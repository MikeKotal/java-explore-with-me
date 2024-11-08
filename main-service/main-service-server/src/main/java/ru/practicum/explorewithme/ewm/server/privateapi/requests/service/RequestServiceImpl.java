package ru.practicum.explorewithme.ewm.server.privateapi.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.ewm.server.dao.UserRepository;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.exceptions.ConditionException;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.mappers.RequestMapper;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.dao.RequestRepository;
import ru.practicum.explorewithme.ewm.server.models.Request;
import ru.practicum.explorewithme.ewm.server.services.RequestService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Запрос от пользователя {} на запрос на участие в событии {}", userId, eventId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConditionException("Нельзя создать повторный запрос на то же событие");
        }
        User requester = getUserById(userId);
        Event event = getEventById(eventId);
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConditionException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConditionException("Нельзя участвовать в неопубликованном событии");
        }
        if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConditionException("Был достигнут лимит запросов на участие в событии");
        }
        Request request = RequestMapper.mapToRequest(event, requester);
        if (event.getParticipantLimit().equals(0) || (!event.getParticipantLimit().equals(0) && !event.getRequestModeration())) {
            incrementConfirmationEvent(request, event);
        }
        request = requestRepository.save(request);
        log.info("Запрос успешно сохранен: {}", request);
        return RequestMapper.mapToRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        log.info("Запрос от пользователя {} на получение его запросов", userId);
        getUserById(userId);
        Sort orderByCreated = Sort.by(Sort.Direction.DESC, "created");
        List<Request> requests = requestRepository.findAllByRequesterId(userId, orderByCreated);
        log.info("Все запросы пользователя на участия в событиях: {}", requests);
        return requests.stream()
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Запрос от пользователя {} на отмену запроса на участие {}", userId, requestId);
        getUserById(userId);
        Request request = getRequestById(requestId);
        if (!userId.equals(request.getRequester().getId())) {
            throw new ConditionException("Отменить запрос на участии в событии может только владелец запроса");
        }
        if (request.getStatus().equals(Status.CANCELED)) {
            throw new ConditionException("Событие уже отменено");
        }
        request.setStatus(Status.CANCELED);
        request = requestRepository.save(request);
        log.info("Запрос успешно отменен: {}", request);
        return RequestMapper.mapToRequestDto(request);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} отсутствует", id);
                    return new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", id));
                });
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id {} отсутствует", eventId);
                    return new NotFoundException(String.format("События с идентификатором = '%s' не найдено", eventId));
                });
    }

    private Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Запроса на участие с id {} отсутствует", requestId);
                    return new NotFoundException(String.format("Запроса на участие с идентификатором = '%s' не найдено",
                            requestId));
                });
    }

    private void incrementConfirmationEvent(Request request, Event event) {
        request.setStatus(Status.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
    }
}
