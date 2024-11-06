package ru.practicum.explorewithme.server.publicapi.events.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.SortType;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.server.publicapi.client.StatsGatewayClient;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static ru.practicum.explorewithme.server.TestData.createViewStatsDto;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class PublicEventServiceIntegrationTest {

    private final PublicEventService publicEventService;

    @MockBean
    StatsGatewayClient statsGatewayClient;

    @Test
    public void checkSuccessGetEventsByFilteredAndPublicUser() {
        doNothing().when(statsGatewayClient).post(any());
        List<EventShortDto> eventShortDtos = publicEventService.getFilteredEventsByPublicUser("запр", List.of(2L),
                Boolean.TRUE, "2024-10-01T23:59:58", "2024-10-02T00:00:00", Boolean.TRUE,
                SortType.EVENT_DATE.name(), 0, 10, "1:1:1:1", new MockHttpServletRequest());

        assertThat(eventShortDtos.size(), equalTo(1));

        EventShortDto eventShortDto = eventShortDtos.getFirst();

        assertThat(eventShortDto.getId(), equalTo(2L));
        assertThat(eventShortDto.getTitle(), equalTo("Мегатитл"));
        assertThat(eventShortDto.getAnnotation(), equalTo("Для запросов"));
        assertThat(eventShortDto.getCategory(), notNullValue());
        assertThat(eventShortDto.getCategory().getId(), equalTo(2L));
        assertThat(eventShortDto.getCategory().getName(), equalTo("ТестКатегория1"));
        assertThat(eventShortDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventShortDto.getEventDate(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventShortDto.getInitiator(), notNullValue());
        assertThat(eventShortDto.getInitiator().getName(), equalTo("Name1"));
        assertThat(eventShortDto.getInitiator().getEmail(), equalTo("test1@test.ru"));
        assertThat(eventShortDto.getPaid(), is(Boolean.TRUE));
        assertThat(eventShortDto.getViews(), equalTo(0L));
    }

    @Test
    public void checkSuccessGetEventByIdAndPublicUser() {
        doNothing().when(statsGatewayClient).post(any());
        when(statsGatewayClient.get(anyString(), anyMap())).thenReturn(new ResponseEntity<>(List.of(createViewStatsDto()),
                HttpStatusCode.valueOf(200)));

        EventFullDto eventFullDto
                = publicEventService.getEventByIdByPublicUser(2L, "1:1:1:1", new MockHttpServletRequest());

        assertThat(eventFullDto.getId(), equalTo(2L));
        assertThat(eventFullDto.getTitle(), equalTo("Мегатитл"));
        assertThat(eventFullDto.getAnnotation(), equalTo("Для запросов"));
        assertThat(eventFullDto.getCategory(), notNullValue());
        assertThat(eventFullDto.getCategory().getId(), equalTo(2L));
        assertThat(eventFullDto.getCategory().getName(), equalTo("ТестКатегория1"));
        assertThat(eventFullDto.getConfirmedRequests(), equalTo(0));
        assertThat(eventFullDto.getCreatedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getDescription(), equalTo("Запросный"));
        assertThat(eventFullDto.getEventDate(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getInitiator(), notNullValue());
        assertThat(eventFullDto.getInitiator().getName(), equalTo("Name1"));
        assertThat(eventFullDto.getInitiator().getEmail(), equalTo("test1@test.ru"));
        assertThat(eventFullDto.getLocation(), notNullValue());
        assertThat(eventFullDto.getLocation().getLat(), equalTo(1.1));
        assertThat(eventFullDto.getLocation().getLon(), equalTo(1.2));
        assertThat(eventFullDto.getPaid(), is(Boolean.TRUE));
        assertThat(eventFullDto.getParticipantLimit(), equalTo(2));
        assertThat(eventFullDto.getPublishedOn(), equalTo("2024-10-01 23:59:59"));
        assertThat(eventFullDto.getRequestModeration(), is(Boolean.TRUE));
        assertThat(eventFullDto.getState(), equalTo(State.PUBLISHED));
        assertThat(eventFullDto.getViews(), equalTo(10L));
    }
}
