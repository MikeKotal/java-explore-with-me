package ru.practicum.explorewithme.server.privateapi.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.mappers.EventMapper;
import ru.practicum.explorewithme.server.privateapi.event.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.event.model.Event;

import java.time.LocalDateTime;
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
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest userRequest) {
        log.info("Запрос от пользователя {} на изменение события {}", userId, eventId);
        checkUserExists(userId);
        Event event = getEventById(eventId);
        Category category = userRequest.getCategory() == null ? event.getCategory() : getCategoryById(userRequest.getCategory());
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
        if (LocalDateTime.parse(eventDate, FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Время события некорректно");
            throw new ConditionException("Дата и время не может быть раньше, чем через два часа от текущего момента");
        }
    }
}
