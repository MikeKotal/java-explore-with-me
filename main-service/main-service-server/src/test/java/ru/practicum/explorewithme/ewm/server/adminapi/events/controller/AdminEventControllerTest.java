package ru.practicum.explorewithme.ewm.server.adminapi.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.ewm.server.services.AdminEventService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.server.TestData.createEventFullDto;
import static ru.practicum.explorewithme.ewm.server.TestData.createUpdateEventUserRequest;

@WebMvcTest(controllers = AdminEventController.class)
public class AdminEventControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AdminEventService adminEventService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetEventsByFilterThenReturnListEventsSize1() throws Exception {
        EventFullDto eventFullDto = createEventFullDto();
        when(adminEventService.getEventsByFilter(anyList(), anyList(), anyList(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(eventFullDto));

        mockMvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PENDING", "PUBLISHED")
                        .param("categories", "1", "2")
                        .param("rangeStart", "2024-01-01 23:59:59")
                        .param("rangeEnd", "2024-01-02 23:59:59")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].title", is(eventFullDto.getTitle()), String.class))
                .andExpect(jsonPath("$[0].annotation", is(eventFullDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$[0].category", is(eventFullDto.getCategory()), CategoryDto.class))
                .andExpect(jsonPath("$[0].confirmedRequests", is(eventFullDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$[0].createdOn", is(eventFullDto.getCreatedOn()), String.class))
                .andExpect(jsonPath("$[0].description", is(eventFullDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].eventDate", is(eventFullDto.getEventDate()), String.class))
                .andExpect(jsonPath("$[0].location", is(eventFullDto.getLocation()), LocationDto.class))
                .andExpect(jsonPath("$[0].paid", is(eventFullDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$[0].participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$[0].publishedOn", is(eventFullDto.getPublishedOn()), String.class))
                .andExpect(jsonPath("$[0].requestModeration", is(eventFullDto.getRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$[0].state", is(eventFullDto.getState().name()), String.class))
                .andExpect(jsonPath("$[0].views", is(eventFullDto.getViews()), Long.class));
    }

    @Test
    public void whenUpdateEventByAdminThenReturnUpdatedEvent() throws Exception {
        EventFullDto eventFullDto = createEventFullDto();
        when(adminEventService.updateEventByAdmin(anyLong(), any()))
                .thenReturn(eventFullDto);

        mockMvc.perform(patch("/admin/events/{eventId}", 1)
                        .content(mapper.writeValueAsString(createUpdateEventUserRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.title", is(eventFullDto.getTitle()), String.class))
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation()), String.class))
                .andExpect(jsonPath("$.category", is(eventFullDto.getCategory()), CategoryDto.class))
                .andExpect(jsonPath("$.confirmedRequests", is(eventFullDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.createdOn", is(eventFullDto.getCreatedOn()), String.class))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription()), String.class))
                .andExpect(jsonPath("$.eventDate", is(eventFullDto.getEventDate()), String.class))
                .andExpect(jsonPath("$.location", is(eventFullDto.getLocation()), LocationDto.class))
                .andExpect(jsonPath("$.paid", is(eventFullDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$.participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$.publishedOn", is(eventFullDto.getPublishedOn()), String.class))
                .andExpect(jsonPath("$.requestModeration", is(eventFullDto.getRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$.state", is(eventFullDto.getState().name()), String.class))
                .andExpect(jsonPath("$.views", is(eventFullDto.getViews()), Long.class));
    }
}
