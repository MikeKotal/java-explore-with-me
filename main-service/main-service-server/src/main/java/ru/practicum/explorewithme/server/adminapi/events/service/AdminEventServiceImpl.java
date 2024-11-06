package ru.practicum.explorewithme.server.adminapi.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.server.mappers.EventMapper;
import ru.practicum.explorewithme.server.privateapi.events.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> getEventsByFilter(List<Long> users, List<State> states, List<Long> categories,
                                                String rangeStart, String rangeEnd, Integer from, Integer size) {
        log.info("Запрос на получение событий по множественным фильтрам");
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (rangeStart != null) {
            startDate = LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8));
        }
        if (rangeEnd != null) {
            endDate = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8));
        }
        List<Event> events;
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                throw new ValidationException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
            }
            events = eventRepository.findAllEventsByFilterAndPeriod(users, states, categories, startDate, endDate, pageable);
        } else if (startDate != null) {
            events = eventRepository.findAllEventsByFilterAndRangeStart(users, states, categories, startDate, pageable);
        } else if (endDate != null) {
            events = eventRepository.findAllEventsByFilterAndRangeEnd(users, states, categories, endDate, pageable);
        } else {
            events = eventRepository.findAllEventsByFilter(users, states, categories, pageable);
        }
        log.info("События согласно фильтрации: {}", events);
        return events.stream()
                .map(EventMapper::mapToEventDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest userRequest) {
        log.info("Запрос от администратора на изменение события {}", eventId);
        Event event = getEventById(eventId);
        Category category = userRequest.getCategory() == null ? event.getCategory()
                : getCategoryById(userRequest.getCategory());
        if (!event.getState().equals(State.PENDING) && userRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
            throw new ConditionException("Допустимо публиковать события находящиеся только в статусе PENDING");
        }
        if (event.getState().equals(State.PUBLISHED) && userRequest.getStateAction().equals(StateAction.REJECT_EVENT.name())) {
            throw new ConditionException("Допустимо отклонять события находящиеся не в статусе PUBLISHED");
        }
        if (userRequest.getEventDate() != null) {
            checkFutureEventDateTime(userRequest.getEventDate(), event.getEventDate());
        }
        event = eventRepository.save(EventMapper.mapToUpdatedEvent(userRequest, category, event));
        log.info("Обновленное администратором событие: {}", event);
        return EventMapper.mapToEventDto(event);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id {} отсутствует", eventId);
                    return new NotFoundException(String.format("События с идентификатором = '%s' не найдено", eventId));
                });
    }

    private Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Категория с id {} отсутствует", catId);
                    return new NotFoundException(String.format("Категории с идентификатором = '%s' не найдено", catId));
                });
    }

    private void checkFutureEventDateTime(String eventDate, LocalDateTime currentEventDate) {
        LocalDateTime newEventDate = LocalDateTime.parse(eventDate, FORMATTER).truncatedTo(ChronoUnit.MINUTES);
        if (newEventDate.isBefore(currentEventDate.truncatedTo(ChronoUnit.MINUTES).minusHours(1))) {
            log.error("Расхождение нового времени и старого слишком большое");
            throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
    }
}
