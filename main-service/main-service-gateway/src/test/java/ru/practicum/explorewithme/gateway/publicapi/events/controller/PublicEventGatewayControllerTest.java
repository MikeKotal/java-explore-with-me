package ru.practicum.explorewithme.gateway.publicapi.events.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.gateway.publicapi.events.PublicEventClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.TestData.createEventFullDto;
import static ru.practicum.explorewithme.TestData.createEventShortDto;

@WebMvcTest(controllers = PublicEventGatewayController.class)
public class PublicEventGatewayControllerTest {

    @MockBean
    PublicEventClient publicEventClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetFilteredEventsByUserThenReturnListSize1() throws Exception {
        EventShortDto eventShortDto = createEventShortDto();
        when(publicEventClient.getFilteredEventsByPublicUser(anyString(), anyList(), anyBoolean(), anyString(),
                anyString(), anyBoolean(), anyString(), anyInt(), anyInt(), any()))
                .thenReturn(new ResponseEntity<>(List.of(eventShortDto), HttpStatusCode.valueOf(200)));

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("categories", "1", "2")
                        .param("paid", "true")
                        .param("rangeStart", "2024-01-01 23:59:59")
                        .param("rangeEnd", "2024-01-02 23:59:59")
                        .param("onlyAvailable", "true")
                        .param("sort", "VIEWS")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventShortDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(eventShortDto.getTitle()), String.class))
                .andExpect(jsonPath("$[0].annotation", is(eventShortDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$[0].category", is(eventShortDto.getCategory()), CategoryDto.class))
                .andExpect(jsonPath("$[0].confirmedRequests", is(eventShortDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$[0].eventDate", is(eventShortDto.getEventDate().toString()), String.class))
                .andExpect(jsonPath("$[0].initiator", is(eventShortDto.getInitiator()), UserShortDto.class))
                .andExpect(jsonPath("$[0].paid", is(eventShortDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$[0].views", is(eventShortDto.getViews()), Long.class));
    }

    @Test
    public void whenGetEventByIdByPublicUserThenReturnEvent() throws Exception {
        EventFullDto eventFullDto = createEventFullDto();
        when(publicEventClient.getEventByIdByPublicUser(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(eventFullDto, HttpStatusCode.valueOf(200)));

        mockMvc.perform(get("/events/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.title", is(eventFullDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.category", is(eventFullDto.getCategory()), CategoryDto.class))
                .andExpect(jsonPath("$.confirmedRequests", is(eventFullDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.createdOn", is(eventFullDto.getCreatedOn().toString()), String.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription()), String.class))
                .andExpect(jsonPath("$.eventDate", is(eventFullDto.getEventDate().toString()), String.class))
                .andExpect(jsonPath("$.location", is(eventFullDto.getLocation()), LocationDto.class))
                .andExpect(jsonPath("$.paid", is(eventFullDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$.participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$.publishedOn", is(eventFullDto.getPublishedOn().toString()), String.class))
                .andExpect(jsonPath("$.requestModeration", is(eventFullDto.getRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$.state", is(eventFullDto.getState().name()), String.class))
                .andExpect(jsonPath("$.views", is(eventFullDto.getViews()), Long.class));
    }
}
