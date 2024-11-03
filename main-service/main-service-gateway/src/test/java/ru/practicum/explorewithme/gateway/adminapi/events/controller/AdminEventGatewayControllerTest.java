package ru.practicum.explorewithme.gateway.adminapi.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.gateway.adminapi.events.AdminEventClient;

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
import static ru.practicum.explorewithme.TestData.createEventFullDto;
import static ru.practicum.explorewithme.TestData.createUpdateEventUserRequest;
import static ru.practicum.explorewithme.gateway.TestAsserts.checkValidationErrorResponse;

@WebMvcTest(controllers = AdminEventGatewayController.class)
public class AdminEventGatewayControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AdminEventClient adminEventClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetEventsByFilterThenReturnListEventsSize1() throws Exception {
        EventFullDto eventFullDto = createEventFullDto();
        when(adminEventClient.getEventsByFilter(anyList(), anyList(), anyList(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(eventFullDto), HttpStatusCode.valueOf(200)));

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
                .andExpect(jsonPath("$[0].confirmedRequests", is(eventFullDto.getConfirmedRequests()), Long.class))
                .andExpect(jsonPath("$[0].createdOn", is(eventFullDto.getCreatedOn().toString()), String.class))
                .andExpect(jsonPath("$[0].description", is(eventFullDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].eventDate", is(eventFullDto.getEventDate().toString()), String.class))
                .andExpect(jsonPath("$[0].location", is(eventFullDto.getLocation()), LocationDto.class))
                .andExpect(jsonPath("$[0].paid", is(eventFullDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$[0].participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$[0].publishedOn", is(eventFullDto.getPublishedOn().toString()), String.class))
                .andExpect(jsonPath("$[0].requestModeration", is(eventFullDto.getRequestModeration()), Boolean.class))
                .andExpect(jsonPath("$[0].state", is(eventFullDto.getState().name()), String.class))
                .andExpect(jsonPath("$[0].views", is(eventFullDto.getViews()), Long.class));
    }

    @Test
    public void whenUpdateEventByAdminThenReturnUpdatedEvent() throws Exception {
        EventFullDto eventFullDto = createEventFullDto();
        when(adminEventClient.updateEventByAdmin(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(eventFullDto, HttpStatusCode.valueOf(200)));

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
                .andExpect(jsonPath("$.confirmedRequests", is(eventFullDto.getConfirmedRequests()), Long.class))
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

    @Test
    public void checkUpdateEventUserRequestDateTimeFormatValidation() throws Exception {
        UpdateEventRequest updateEventRequest = createUpdateEventUserRequest();
        updateEventRequest.setEventDate("Test");

        ResultActions resultActions = mockMvc.perform(patch("/admin/events/{eventId}", 1)
                .content(mapper.writeValueAsString(updateEventRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        checkValidationErrorResponse(resultActions,
                "Неверный формат даты и времени. Ожидается yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void checkUpdateEventUserRequestStateActionValidation() throws Exception {
        UpdateEventRequest updateEventRequest = createUpdateEventUserRequest();
        updateEventRequest.setStateAction("Test");

        ResultActions resultActions = mockMvc.perform(patch("/admin/events/{eventId}", 1)
                .content(mapper.writeValueAsString(updateEventRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        checkValidationErrorResponse(resultActions,
                "Для stateAction доступно: SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT");
    }
}
