package ru.practicum.explorewithme.gateway.privateapi.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.gateway.privateapi.event.EventClient;

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
import static ru.practicum.explorewithme.TestData.createEventFullDto;
import static ru.practicum.explorewithme.TestData.createEventShortDto;
import static ru.practicum.explorewithme.TestData.createNewEventRequest;
import static ru.practicum.explorewithme.TestData.createUpdateEventUserRequest;

@WebMvcTest(controllers = EventGatewayController.class)
public class EventGatewayControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    EventClient eventClient;

    @Autowired
    MockMvc mockMvc;

    private EventFullDto eventFullDto;

    @BeforeEach
    public void setUp() {
        eventFullDto = createEventFullDto();
    }

    @Test
    public void whenCreatedEventThenReturnNewEvent() throws Exception {
        when(eventClient.createEvent(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(eventFullDto, HttpStatusCode.valueOf(201)));

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
        when(eventClient.getEventsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(eventShortDto), HttpStatusCode.valueOf(200)));

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
        when(eventClient.getEventByUserAndEventId(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(eventFullDto, HttpStatusCode.valueOf(200)));

        ResultActions resultActions = mockMvc.perform(get("/users/{userId}/events/{eventId}", 1, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkEventFullDtoResponse(resultActions, eventFullDto);
    }

    @Test
    public void whenUpdateEventThenReturnUpdatedEvent() throws Exception {
        when(eventClient.updateEvent(any(), anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(eventFullDto, HttpStatusCode.valueOf(200)));

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
        when(eventClient.createEvent(any(), anyLong())).thenThrow(new RuntimeException("Тест"));

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

    @Test
    public void checkNewEventRequestDateTimeFormatValidation() throws Exception {
        NewEventRequest newEventRequest = createNewEventRequest();
        newEventRequest.setEventDate("Test");

        ResultActions resultActions = mockMvc.perform(post("/users/{userId}/events", 1)
                        .content(mapper.writeValueAsString(newEventRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));
        checkValidationErrorResponse(resultActions,
                "Неверный формат даты и времени. Ожидается yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void checkUpdateEventUserRequestDateTimeFormatValidation() throws Exception {
        UpdateEventUserRequest updateEventUserRequest = createUpdateEventUserRequest();
        updateEventUserRequest.setEventDate("Test");

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}/events/{eventId}", 1, 1)
                .content(mapper.writeValueAsString(updateEventUserRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        checkValidationErrorResponse(resultActions,
                "Неверный формат даты и времени. Ожидается yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void checkUpdateEventUserRequestStateActionValidation() throws Exception {
        UpdateEventUserRequest updateEventUserRequest = createUpdateEventUserRequest();
        updateEventUserRequest.setStateAction("Test");

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}/events/{eventId}", 1, 1)
                .content(mapper.writeValueAsString(updateEventUserRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        checkValidationErrorResponse(resultActions,
                "Для stateAction доступно: SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT");
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

    private void checkValidationErrorResponse(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage),
                        String.class))
                .andExpect(jsonPath("$.reason", is("Некорректно заполнены входные параметры"), String.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name()), String.class))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
