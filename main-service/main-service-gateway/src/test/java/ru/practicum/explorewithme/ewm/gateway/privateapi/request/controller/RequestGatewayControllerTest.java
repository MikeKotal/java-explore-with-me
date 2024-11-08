package ru.practicum.explorewithme.ewm.gateway.privateapi.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.ewm.gateway.privateapi.requests.RequestClient;
import ru.practicum.explorewithme.ewm.gateway.privateapi.requests.controller.RequestGatewayController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.gateway.TestData.createRequestDto;

@WebMvcTest(controllers = RequestGatewayController.class)
public class RequestGatewayControllerTest {

    @MockBean
    RequestClient requestClient;

    @Autowired
    MockMvc mockMvc;

    private ParticipationRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        requestDto = createRequestDto();
    }

    @Test
    public void whenCreateNewRequestThenReturnNewRequest() throws Exception {
        when(requestClient.createRequest(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(requestDto,
                HttpStatusCode.valueOf(201)));

        ResultActions resultActions = mockMvc.perform(post("/users/{userId}/requests", 1)
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        checkRequestDtoResponse(resultActions, requestDto);
    }

    @Test
    public void whenGetRequestsByUserIdThenReturnListSize1() throws Exception {
        when(requestClient.getRequestsByUser(anyLong()))
                .thenReturn(new ResponseEntity<>(List.of(requestDto), HttpStatusCode.valueOf(200)));

        mockMvc.perform(get("/users/{userId}/requests", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].event", is(requestDto.getEvent()), Long.class))
                .andExpect(jsonPath("$[0].requester", is(requestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().toString()), String.class))
                .andExpect(jsonPath("$[0].status", is(requestDto.getStatus().name()), String.class));
    }

    @Test
    public void whenCancelRequestThenReturnUpdatedRequest() throws Exception {
        when(requestClient.cancelRequest(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(requestDto, HttpStatusCode.valueOf(200)));

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel",
                        1, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkRequestDtoResponse(resultActions, requestDto);
    }

    private void checkRequestDtoResponse(ResultActions resultActions, ParticipationRequestDto requestDto) throws Exception {
        resultActions
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.event", is(requestDto.getEvent()), Long.class))
                .andExpect(jsonPath("$.requester", is(requestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString()), String.class))
                .andExpect(jsonPath("$.status", is(requestDto.getStatus().name()), String.class));
    }
}
