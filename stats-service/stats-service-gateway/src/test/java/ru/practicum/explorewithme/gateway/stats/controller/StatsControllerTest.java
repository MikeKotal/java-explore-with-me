package ru.practicum.explorewithme.gateway.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.gateway.stats.StatsClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.TestData.ENDPOINT_HIT_REQUEST_DTO;
import static ru.practicum.explorewithme.TestData.VIEW_STATS_DTO;

@WebMvcTest(controllers = StatsGatewayController.class)
public class StatsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatsClient statsClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCallCreateHitThenReturn201() throws Exception {
        doNothing().when(statsClient).createHit(ENDPOINT_HIT_REQUEST_DTO);

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(ENDPOINT_HIT_REQUEST_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void checkInternalServerError500() throws Exception {
        doThrow(new RuntimeException("Тест")).when(statsClient).createHit(ENDPOINT_HIT_REQUEST_DTO);

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(ENDPOINT_HIT_REQUEST_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка."), String.class));
    }

    @Test
    public void whenCallGetStatsThenReturnListSize1() throws Exception {
        when(statsClient.getStats(anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(List.of(VIEW_STATS_DTO), HttpStatusCode.valueOf(200)));

        mockMvc.perform(get(String.format("/stats?start=%s&end=%s&uris=%s&unique=%s",
                        LocalDateTime.now(), LocalDateTime.now(), Collections.EMPTY_LIST, Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(VIEW_STATS_DTO.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(VIEW_STATS_DTO.getUri()), String.class))
                .andExpect(jsonPath("$[0].hits", is(VIEW_STATS_DTO.getHits()), Long.class));
    }

    @Test
    public void checkDateValidationException() throws Exception {
        when(statsClient.getStats(anyString(), anyString(), anyList(), anyBoolean()))
                .thenThrow(new ValidationException("Тест"));

        mockMvc.perform(get(String.format("/stats?start=%s&end=%s&uris=%s&unique=%s",
                        LocalDateTime.now(), LocalDateTime.now(), Collections.EMPTY_LIST, Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("Тест"),
                        String.class));
    }

    @Test
    public void checkFieldAppValidationError400() throws Exception {
        EndpointHitRequestDto requestDto = new EndpointHitRequestDto("", "uri", "ip");

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("Наименование сервиса не должно быть пустым"),
                        String.class));
    }

    @Test
    public void checkFieldUriValidationError400() throws Exception {
        EndpointHitRequestDto requestDto = new EndpointHitRequestDto("app", "", "ip");

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("URI запроса не должен быть пустым"),
                        String.class));
    }

    @Test
    public void checkFieldIpValidationError400() throws Exception {
        EndpointHitRequestDto requestDto = new EndpointHitRequestDto("app", "uri", "");

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("IP-адрес пользователя не должен быть пустым"),
                        String.class));
    }
}
