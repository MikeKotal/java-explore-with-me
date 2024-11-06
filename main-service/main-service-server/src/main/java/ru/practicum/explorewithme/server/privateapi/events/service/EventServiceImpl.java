package ru.practicum.explorewithme.server.privateapi.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.server.mappers.EventMapper;
import ru.practicum.explorewithme.server.mappers.RequestMapper;
import ru.practicum.explorewithme.server.privateapi.events.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;
import ru.practicum.explorewithme.server.privateapi.requests.dao.RequestRepository;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventRequest eventRequest) {
        log.info("Запрос от пользователя {} на создание события {}", userId, eventRequest);
        checkFutureEventDateTime(eventRequest.getEventDate());
        User initiator = getUserById(userId);
        Category category = getCategoryById(eventRequest.getCategory());
        Event event = EventMapper.mapToEvent(eventRequest, category, initiator);
        event.setState(State.PENDING);
        event = eventRepository.save(event);
        log.info("Событие успешно создано: {}", event);
        return EventMapper.mapToEventDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя {}, с элемента from {} размера выборки size {}", userId, from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findAllById(userId, pageable);
        log.info("Список событий {}", events);
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getEventByUserAndEventId(Long userId, Long eventId) {
        log.info("Запрос от пользователя {} на просмотр события {}", userId, eventId);
        checkUserExists(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionException("Просматривать полную информацию о событии может только собственник");
        }
        log.info("Полученное событие {}", event);
        return EventMapper.mapToEventDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest userRequest) {
        log.info("Запрос от пользователя {} на изменение события {}", userId, eventId);
        checkUserExists(userId);
        Event event = getEventById(eventId);
        Category category = userRequest.getCategory() == null ? event.getCategory()
                : getCategoryById(userRequest.getCategory());
        if (userRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name())
                || userRequest.getStateAction().equals(StateAction.REJECT_EVENT.name())) {
            throw new ConditionException("Публиковать и отклонять события может только администратор системы");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConditionException("Можно изменять только события находящиеся в статусах: PENDING, CANCELED");
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionException("Обновлять информацию о событии может только собственник");
        }
        if (userRequest.getEventDate() != null) {
            checkFutureEventDateTime(userRequest.getEventDate());
        }

        event = eventRepository.save(EventMapper.mapToUpdatedEvent(userRequest, category, event));
        log.info("Обновленное событие: {}", event);
        return EventMapper.mapToEventDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByOwnerEventAndEventId(Long ownerId, Long eventId) {
        log.info("Запрос от собственника {} на получение запросов к событию {}", ownerId, eventId);
        Sort orderByCreated = Sort.by(Sort.Direction.DESC, "created");
        checkUserExists(ownerId);
        Event event = getEventById(eventId);
        if (!ownerId.equals(event.getInitiator().getId())) {
            throw new ConditionException("Просматривать список запросов к событию может только собственник");
        }
        List<Request> requests = requestRepository.findRequestsByEventId(eventId, orderByCreated);
        log.info("Получен список запросов к событию: {}", requests);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto approveRequestByOwnerId(Long ownerId,
                                                                     Long eventId,
                                                                     EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Запрос от собственника {} на подтверждение запросов к событию {}", ownerId, eventId);
        checkUserExists(ownerId);
        Event event = getEventById(eventId);
        if (!ownerId.equals(event.getInitiator().getId())) {
            throw new ConditionException("Подтверждать запросы на участие в событии может только собственник");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConditionException("Заявки не принимаются, достигнут лимит по заявкам на данное событие");
        }
        List<Request> requests = requestRepository.findRequestsByIdIn(statusUpdateRequest.getRequestIds());
        if (requests.isEmpty()) {
            throw new NotFoundException("Запросов по таким идентификаторам нет");
        }
        checkValidStatusRequest(requests);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()
                || Status.valueOf(statusUpdateRequest.getStatus()).equals(Status.REJECTED)) {
            requests = requestRepository.saveAll(requests.stream()
                    .peek(request -> request.setStatus(Status.valueOf(statusUpdateRequest.getStatus())))
                    .toList());
        } else {
            requests = requests.stream()
                    .map(request -> {
                        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                            request.setStatus(Status.valueOf(statusUpdateRequest.getStatus()));
                            request = requestRepository.save(request);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                            return request;
                        } else {
                            request.setStatus(Status.REJECTED);
                            return requestRepository.save(request);
                        }
                    }).toList();
        }
        eventRepository.save(event);
        log.info("Разобранные запросы на участии в событии: {}", requests);
        return RequestMapper.mapToStatusUpdateResultDto(requests);
    }

    private void checkUserExists(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", ownerId));
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} отсутствует", id);
                    return new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", id));
                });
    }

    private Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Категория с id {} отсутствует", catId);
                    return new NotFoundException(String.format("Категории с идентификатором = '%s' не найдено", catId));
                });
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id {} отсутствует", eventId);
                    return new NotFoundException(String.format("События с идентификатором = '%s' не найдено", eventId));
                });
    }

    private void checkFutureEventDateTime(String eventDate) {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusHours(2);
        if (LocalDateTime.parse(eventDate, FORMATTER).truncatedTo(ChronoUnit.MINUTES).isBefore(currentTime)) {
            log.error("Время события некорректно");
            throw new ValidationException("Дата и время не может быть раньше, чем через два часа от текущего момента");
        }
    }

    private void checkValidStatusRequest(List<Request> requests) {
        requests.forEach(request -> {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConditionException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        });
    }
}
