package ru.practicum.explorewithme.ewm.server.adminapi.events.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.ewm.server.services.AdminEventService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.ewm.server.TestData.createUpdateEventUserRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminEventIntegrationTest {

    private final AdminEventService adminEventService;

    @Test
    public void checkSuccessGetEventsByAllFiltersAndPeriod() {
        List<EventFullDto> eventFullDtos = adminEventService.getEventsByFilter(List.of(1L), List.of(State.CANCELED),
                List.of(1L), "2024-10-01T23:59:58", "2024-10-02T00:00:00", 0, 1);

        assertThat(eventFullDtos.size(), equalTo(1));

        EventFullDto eventFullDto = eventFullDtos.getFirst();
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
    public void checkSuccessUpdateEvent() {
        UpdateEventRequest updateEventRequest = createUpdateEventUserRequest();
        EventFullDto eventFullDto = adminEventService.updateEventByAdmin(1L, updateEventRequest);

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
}
