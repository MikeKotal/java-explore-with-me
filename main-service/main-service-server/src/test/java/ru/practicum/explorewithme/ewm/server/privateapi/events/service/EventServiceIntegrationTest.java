package ru.practicum.explorewithme.ewm.server.privateapi.events.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.CommentShortDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.services.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;
import static ru.practicum.explorewithme.ewm.server.TestData.createNewEventRequest;
import static ru.practicum.explorewithme.ewm.server.TestData.createStatusUpdateRequest;
import static ru.practicum.explorewithme.ewm.server.TestData.createUpdateEventUserRequest;

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
        assertThat(eventShortDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventShortDto.getEventDate(), equalTo("2024-10-01 23:59:59"));
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
        assertThat(eventFullDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventFullDto.getCreatedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getDescription(), equalTo("Мегаописание"));
        assertThat(eventFullDto.getEventDate(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getInitiator(), notNullValue());
        assertThat(eventFullDto.getInitiator().getName(), equalTo("Name"));
        assertThat(eventFullDto.getInitiator().getEmail(), equalTo("test@test.ru"));
        assertThat(eventFullDto.getLocation(), notNullValue());
        assertThat(eventFullDto.getLocation().getLat(), equalTo(1.1));
        assertThat(eventFullDto.getLocation().getLon(), equalTo(1.2));
        assertThat(eventFullDto.getPaid(), is(Boolean.TRUE));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(2));
        assertThat(eventFullDto.getPublishedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getRequestModeration(), is(Boolean.TRUE));
        assertThat(eventFullDto.getState(), equalTo(State.CANCELED));
        assertThat(eventFullDto.getViews(), equalTo(0L));
    }

    @Test
    public void checkSuccessGetCommentEvent() {
        EventFullDto eventFullDto = eventService.getEventByUserAndEventId(1L, 1L);

        assertThat(eventFullDto.getComments(), notNullValue());
        assertThat(eventFullDto.getComments().size(), equalTo(2));

        List<CommentShortDto> commentDtos = eventFullDto.getComments().stream().toList();
        assertThat(commentDtos.getFirst().getCommenter(), equalTo("Name"));
        assertThat(commentDtos.getFirst().getComment(), equalTo("Супер комментарий"));
        assertThat(commentDtos.getFirst().getCreatedAt(), equalTo("2024-10-01 23:59:59"));

        assertThat(commentDtos.getLast().getCommenter(), equalTo("Name1"));
        assertThat(commentDtos.getLast().getComment(), equalTo("Полностью поддерживаю"));
        assertThat(commentDtos.getLast().getCreatedAt(), equalTo("2024-10-02 23:59:59"));
    }

    @Test
    public void checkSuccessUpdateEvent() {
        UpdateEventRequest updateEventRequest = createUpdateEventUserRequest();
        EventFullDto eventFullDto = eventService.updateEvent(1L, 1L, updateEventRequest);

        assertThat(eventFullDto.getId(), equalTo(1L));
        assertThat(eventFullDto.getTitle(), equalTo(updateEventRequest.getTitle()));
        assertThat(eventFullDto.getAnnotation(), equalTo(updateEventRequest.getAnnotation()));
        assertThat(eventFullDto.getCategory(), notNullValue());
        assertThat(eventFullDto.getCategory().getId(), equalTo(updateEventRequest.getCategory()));
        assertThat(eventFullDto.getCategory().getName(), equalTo("ТестКатегория1"));
        assertThat(eventFullDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventFullDto.getCreatedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getDescription(), equalTo(updateEventRequest.getDescription()));
        assertThat(eventFullDto.getEventDate(), equalTo(updateEventRequest.getEventDate()));
        assertThat(eventFullDto.getInitiator(), notNullValue());
        assertThat(eventFullDto.getInitiator().getName(), equalTo("Name"));
        assertThat(eventFullDto.getInitiator().getEmail(), equalTo("test@test.ru"));
        assertThat(eventFullDto.getLocation(), notNullValue());
        assertThat(eventFullDto.getLocation().getLat(), equalTo(updateEventRequest.getLocation().getLat()));
        assertThat(eventFullDto.getLocation().getLon(), equalTo(updateEventRequest.getLocation().getLon()));
        assertThat(eventFullDto.getPaid(), is(updateEventRequest.getPaid()));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(updateEventRequest.getParticipantLimit()));
        assertThat(eventFullDto.getPublishedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getRequestModeration(), is(updateEventRequest.getRequestModeration()));
        assertThat(eventFullDto.getState(), equalTo(State.PENDING));
        assertThat(eventFullDto.getViews(), equalTo(0L));
    }

    @Test
    public void checkSuccessGetRequestsByOwnerEventAndEventId() {
        List<ParticipationRequestDto> participationRequestDtos
                = eventService.getRequestsByOwnerEventAndEventId(1L, 1L);

        assertThat(participationRequestDtos.size(), equalTo(1));

        ParticipationRequestDto requestDto = participationRequestDtos.getFirst();
        assertThat(requestDto.getId(), equalTo(1L));
        assertThat(requestDto.getEvent(), equalTo(1L));
        assertThat(requestDto.getRequester(), equalTo(1L));
        assertThat(requestDto.getCreated(), equalTo(LocalDateTime.parse("2024-10-01T23:59:59")));
        assertThat(requestDto.getStatus(), equalTo(Status.PENDING));
    }

    @Test
    public void checkApproveRequestByOwnerId() {
        EventRequestStatusUpdateRequest statusUpdateRequest = createStatusUpdateRequest();
        EventRequestStatusUpdateResultDto statusUpdateResultDto
                = eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest);

        assertThat(statusUpdateResultDto.getConfirmedRequests(), notNullValue());
        assertThat(statusUpdateResultDto.getConfirmedRequests().size(), equalTo(1));
        assertThat(statusUpdateResultDto.getRejectedRequests(), empty());

        ParticipationRequestDto requestDto = statusUpdateResultDto.getConfirmedRequests().getFirst();
        assertThat(requestDto.getId(), equalTo(1L));
        assertThat(requestDto.getEvent(), equalTo(1L));
        assertThat(requestDto.getRequester(), equalTo(1L));
        assertThat(requestDto.getCreated(), equalTo(LocalDateTime.parse("2024-10-01T23:59:59")));
        assertThat(requestDto.getStatus(), equalTo(Status.CONFIRMED));
    }
}
