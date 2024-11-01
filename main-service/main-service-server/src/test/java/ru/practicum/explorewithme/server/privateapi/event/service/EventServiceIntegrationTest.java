package ru.practicum.explorewithme.server.privateapi.event.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.server.privateapi.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;
import static ru.practicum.explorewithme.server.TestData.createNewEventRequest;
import static ru.practicum.explorewithme.server.TestData.createUpdateEventUserRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceIntegrationTest {

    private final EntityManager em;
    private final EventService eventService;

    @Test
    public void checkSuccessCreateEvent() {
        NewEventRequest newEventRequest = createNewEventRequest();
        EventFullDto eventFullDto = eventService.createEvent(2L, newEventRequest);

        TypedQuery<Event> query = em.createQuery("Select e from Event e where e.id = :id", Event.class);
        Event event = query.setParameter("id", eventFullDto.getId()).getSingleResult();

        assertThat(event.getId(), equalTo(eventFullDto.getId()));
        assertThat(event.getAnnotation(), equalTo(newEventRequest.getAnnotation()));
        assertThat(event.getCategory(), notNullValue());
        assertThat(event.getCategory().getId(), equalTo(newEventRequest.getCategory()));
        assertThat(event.getDescription(), equalTo(newEventRequest.getDescription()));
        assertThat(event.getCreatedOn(), notNullValue());
        assertThat(event.getEventDate().toString(),
                equalTo(LocalDateTime.parse(newEventRequest.getEventDate(), FORMATTER)
                        .format(DateTimeFormatter.ISO_DATE_TIME)));
        assertThat(event.getPublishedOn(), nullValue());
        assertThat(event.getLocation(), notNullValue());
        assertThat(event.getPaid(), is(newEventRequest.getPaid()));
        assertThat(event.getParticipantLimit(), equalTo(newEventRequest.getParticipantLimit()));
        assertThat(event.getRequestModeration(), is(newEventRequest.getRequestModeration()));
        assertThat(event.getTitle(), equalTo(newEventRequest.getTitle()));
        assertThat(event.getConfirmedRequests(), notNullValue());
        assertThat(event.getInitiator(), notNullValue());
        assertThat(event.getViews(), notNullValue());
    }

    @Test
    public void checkSuccessGetEventsByUserId() {
        List<EventShortDto> eventShortDtos = eventService.getEventsByUserId(1L, 0, 10);

        assertThat(eventShortDtos.size(), equalTo(1));

        EventShortDto eventShortDto = eventShortDtos.getFirst();
        assertThat(eventShortDto.getId(), equalTo(1L));
        assertThat(eventShortDto.getTitle(), equalTo("Мегатитл"));
        assertThat(eventShortDto.getAnnotation(), equalTo("Мегааннотация"));
        assertThat(eventShortDto.getCategory(), notNullValue());
        assertThat(eventShortDto.getCategory().getId(), equalTo(1L));
        assertThat(eventShortDto.getCategory().getName(), equalTo("ТестКатегория"));
        assertThat(eventShortDto.getConfirmedRequests(), equalTo(0L));
        assertThat(eventShortDto.getEventDate(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventShortDto.getInitiator(), notNullValue());
        assertThat(eventShortDto.getInitiator().getName(), equalTo("Name"));
        assertThat(eventShortDto.getInitiator().getEmail(), equalTo("test@test.ru"));
        assertThat(eventShortDto.getViews(), equalTo(0L));
    }

    @Test
    public void checkSuccessGetEventByUserAndEventId() {
        EventFullDto eventFullDto = eventService.getEventByUserAndEventId(1L, 1L);

        assertThat(eventFullDto.getId(), equalTo(1L));
        assertThat(eventFullDto.getTitle(), equalTo("Мегатитл"));
        assertThat(eventFullDto.getAnnotation(), equalTo("Мегааннотация"));
        assertThat(eventFullDto.getCategory(), notNullValue());
        assertThat(eventFullDto.getCategory().getId(), equalTo(1L));
        assertThat(eventFullDto.getCategory().getName(), equalTo("ТестКатегория"));
        assertThat(eventFullDto.getConfirmedRequests(), equalTo(0L));
        assertThat(eventFullDto.getCreatedOn(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventFullDto.getDescription(), equalTo("Мегаописание"));
        assertThat(eventFullDto.getEventDate(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventFullDto.getInitiator(), notNullValue());
        assertThat(eventFullDto.getInitiator().getName(), equalTo("Name"));
        assertThat(eventFullDto.getInitiator().getEmail(), equalTo("test@test.ru"));
        assertThat(eventFullDto.getLocation(), notNullValue());
        assertThat(eventFullDto.getLocation().getLat(), equalTo(1.1));
        assertThat(eventFullDto.getLocation().getLon(), equalTo(1.2));
        assertThat(eventFullDto.getPaid(), is(Boolean.TRUE));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(2));
        assertThat(eventFullDto.getPublishedOn(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventFullDto.getRequestModeration(), is(Boolean.TRUE));
        assertThat(eventFullDto.getState(), equalTo(State.CANCELED));
        assertThat(eventFullDto.getViews(), equalTo(0L));
    }

    @Test
    public void checkSuccessUpdateEvent() {
        UpdateEventUserRequest updateEventUserRequest = createUpdateEventUserRequest();
        EventFullDto eventFullDto = eventService.updateEvent(1L, 1L, updateEventUserRequest);

        assertThat(eventFullDto.getId(), equalTo(1L));
        assertThat(eventFullDto.getTitle(), equalTo(updateEventUserRequest.getTitle()));
        assertThat(eventFullDto.getAnnotation(), equalTo(updateEventUserRequest.getAnnotation()));
        assertThat(eventFullDto.getCategory(), notNullValue());
        assertThat(eventFullDto.getCategory().getId(), equalTo(updateEventUserRequest.getCategory()));
        assertThat(eventFullDto.getCategory().getName(), equalTo("ТестКатегория1"));
        assertThat(eventFullDto.getConfirmedRequests(), equalTo(0L));
        assertThat(eventFullDto.getCreatedOn(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventFullDto.getDescription(), equalTo(updateEventUserRequest.getDescription()));
        assertThat(eventFullDto.getEventDate(), equalTo(LocalDateTime.parse(updateEventUserRequest.getEventDate(), FORMATTER)));
        assertThat(eventFullDto.getInitiator(), notNullValue());
        assertThat(eventFullDto.getInitiator().getName(), equalTo("Name"));
        assertThat(eventFullDto.getInitiator().getEmail(), equalTo("test@test.ru"));
        assertThat(eventFullDto.getLocation(), notNullValue());
        assertThat(eventFullDto.getLocation().getLat(), equalTo(updateEventUserRequest.getLocation().getLat()));
        assertThat(eventFullDto.getLocation().getLon(), equalTo(updateEventUserRequest.getLocation().getLon()));
        assertThat(eventFullDto.getPaid(), is(updateEventUserRequest.getPaid()));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(updateEventUserRequest.getParticipantLimit()));
        assertThat(eventFullDto.getPublishedOn(), equalTo(LocalDateTime.parse("2024-10-01 23:59:59", FORMATTER)));
        assertThat(eventFullDto.getRequestModeration(), is(updateEventUserRequest.getRequestModeration()));
        assertThat(eventFullDto.getState(), equalTo(State.PENDING));
        assertThat(eventFullDto.getViews(), equalTo(0L));
    }
}
