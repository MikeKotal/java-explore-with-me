package ru.practicum.explorewithme.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static final Integer DEFAULT_VALUE = 0;

    public static EventFullDto mapToEventDto(Event event) {
        log.info("Event в маппер: {}", event);
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(getParsedDateTime(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(getParsedDateTime(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(getParsedDateTime(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(event.getViews())
                .build();
        log.info("EventFullDto из маппера: {}", eventFullDto);
        return eventFullDto;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        log.info("Event для Short в маппер: {}", event);
        EventShortDto eventShortDto = EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(getParsedDateTime(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .views(event.getViews())
                .build();
        log.info("EventShortFullDto из маппера: {}", eventShortDto);
        return eventShortDto;
    }

    public static Event mapToEvent(NewEventRequest eventRequest, Category category, User initiator) {
        log.info("NewEventRequest в маппер: {}", eventRequest);
        Event event = Event.builder()
                .annotation(eventRequest.getAnnotation())
                .category(category)
                .description(eventRequest.getDescription())
                .createdOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .eventDate(LocalDateTime.parse(eventRequest.getEventDate(), FORMATTER))
                .location(LocationMapper.mapToLocation(eventRequest.getLocation()))
                .paid(eventRequest.getPaid() == null ? Boolean.FALSE : eventRequest.getPaid())
                .participantLimit(eventRequest.getParticipantLimit() == null ? DEFAULT_VALUE : eventRequest.getParticipantLimit())
                .requestModeration(eventRequest.getRequestModeration() == null ? Boolean.TRUE : eventRequest.getRequestModeration())
                .title(eventRequest.getTitle())
                .confirmedRequests(DEFAULT_VALUE)
                .initiator(initiator)
                .views(Long.valueOf(DEFAULT_VALUE))
                .build();
        log.info("Event из маппера: {}", event);
        return event;
    }

    public static Event mapToUpdatedEvent(UpdateEventRequest updateRequest, Category newCategory, Event oldEvent) {
        log.info("Новое событие: {} и старое событие: {} в маппер", updateRequest, oldEvent);
        State newState = oldEvent.getState();
        if (updateRequest.getStateAction() != null) {
            switch (StateAction.valueOf(updateRequest.getStateAction())) {
                case SEND_TO_REVIEW -> newState = State.PENDING;
                case REJECT_EVENT, CANCEL_REVIEW -> newState = State.CANCELED;
                case PUBLISH_EVENT -> newState = State.PUBLISHED;
            }
        }
        oldEvent.setTitle(updateRequest.getTitle() == null ? oldEvent.getTitle() : updateRequest.getTitle());
        oldEvent.setAnnotation(updateRequest.getAnnotation() == null ? oldEvent.getAnnotation() : updateRequest.getAnnotation());
        oldEvent.setCategory(newCategory);
        oldEvent.setDescription(updateRequest.getDescription() == null ? oldEvent.getDescription() : updateRequest.getDescription());
        oldEvent.setEventDate(updateRequest.getEventDate() == null ? oldEvent.getEventDate()
                : LocalDateTime.parse(updateRequest.getEventDate(), FORMATTER));
        oldEvent.setLocation(updateRequest.getLocation() == null ? oldEvent.getLocation()
                : LocationMapper.mapToLocation(updateRequest.getLocation()));
        oldEvent.setPaid(updateRequest.getPaid() == null ? oldEvent.getPaid() : updateRequest.getPaid());
        oldEvent.setParticipantLimit(updateRequest.getParticipantLimit() == null ? oldEvent.getParticipantLimit()
                : updateRequest.getParticipantLimit());
        oldEvent.setRequestModeration(updateRequest.getRequestModeration() == null ? oldEvent.getRequestModeration()
                : updateRequest.getRequestModeration());
        oldEvent.setState(newState);
        log.info("Обновленное событие из маппера: {}", oldEvent);
        return oldEvent;
    }

    private static String getParsedDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(FORMATTER);
    }
}
