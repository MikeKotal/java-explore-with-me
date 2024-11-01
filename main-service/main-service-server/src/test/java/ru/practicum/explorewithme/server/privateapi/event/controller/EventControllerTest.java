package ru.practicum.explorewithme.server.privateapi.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.server.privateapi.event.service.EventService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.server.TestData.createEventFullDto;
import static ru.practicum.explorewithme.server.TestData.createEventShortDto;
import static ru.practicum.explorewithme.server.TestData.createNewEventRequest;
import static ru.practicum.explorewithme.server.TestData.createUpdateEventUserRequest;

@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    EventService eventService;

    @Autowired
    MockMvc mockMvc;

    private EventFullDto eventFullDto;

    @BeforeEach
    public void setUp() {
        eventFullDto = createEventFullDto();
    }

    @Test
    public void whenCreatedEventThenReturnNewEvent() throws Exception {
        when(eventService.createEvent(anyLong(), any())).thenReturn(eventFullDto);

        ResultActions resultActions = mockMvc.perform(post("/users/{userId}/events", 1)
                        .content(mapper.writeValueAsString(createNewEventRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        checkEventFullDtoResponse(resultActions, eventFullDto);
    }

    @Test
    public void whenGetEventsByUserIdThenReturnEventsSize1() throws Exception {
        EventShortDto eventShortDto = createEventShortDto();
        when(eventService.getEventsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/users/{userId}/events", 1)
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
                .andExpect(jsonPath("$[0].confirmedRequests", is(eventShortDto.getConfirmedRequests()), Long.class))
                .andExpect(jsonPath("$[0].eventDate", is(eventShortDto.getEventDate().toString()), String.class))
                .andExpect(jsonPath("$[0].initiator", is(eventShortDto.getInitiator()), UserShortDto.class))
                .andExpect(jsonPath("$[0].paid", is(eventShortDto.getPaid()), Boolean.class))
                .andExpect(jsonPath("$[0].views", is(eventShortDto.getViews()), Long.class));
    }

    @Test
    public void whenGetEventByUserAndEventIdThenReturnEvent() throws Exception {
        when(eventService.getEventByUserAndEventId(anyLong(), anyLong())).thenReturn(eventFullDto);

        ResultActions resultActions = mockMvc.perform(get("/users/{userId}/events/{eventId}", 1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkEventFullDtoResponse(resultActions, eventFullDto);
    }

    @Test
    public void whenUpdateEventThenReturnUpdatedEvent() throws Exception {
        when(eventService.updateEvent(anyLong(), anyLong(), any())).thenReturn(eventFullDto);

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}/events/{eventId}", 1, 1)
                        .content(mapper.writeValueAsString(createUpdateEventUserRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkEventFullDtoResponse(resultActions, eventFullDto);
    }

    @Test
    public void checkInternalServerError500() throws Exception {
        when(eventService.createEvent(anyLong(), any())).thenThrow(new RuntimeException("Тест"));

        mockMvc.perform(post("/users/{userId}/events", 1)
                        .content(mapper.writeValueAsString(createNewEventRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Тест"), String.class))
                .andExpect(jsonPath("$.reason", is("Произошла непредвиденная ошибка."), String.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.name()), String.class))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    private void checkEventFullDtoResponse(ResultActions resultActions, EventFullDto eventFullDto) throws Exception {
        resultActions
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
}
