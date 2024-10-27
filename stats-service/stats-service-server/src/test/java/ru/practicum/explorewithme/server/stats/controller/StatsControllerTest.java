package ru.practicum.explorewithme.server.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.server.stats.service.StatsService;

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
import static ru.practicum.explorewithme.server.stats.TestData.ENDPOINT_HIT_REQUEST_DTO;
import static ru.practicum.explorewithme.server.stats.TestData.VIEW_STATS_DTO;

@WebMvcTest(controllers = StatsController.class)
public class StatsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatsService statsService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCallCreateHitThenReturn201() throws Exception {
        doNothing().when(statsService).createHit(ENDPOINT_HIT_REQUEST_DTO);

        mockMvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(ENDPOINT_HIT_REQUEST_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void checkInternalServerError500() throws Exception {
        doThrow(new RuntimeException("Тест")).when(statsService).createHit(ENDPOINT_HIT_REQUEST_DTO);

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
        when(statsService.getStatsByPeriodAndUris(anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn(List.of(VIEW_STATS_DTO));

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
        when(statsService.getStatsByPeriodAndUris(anyString(), anyString(), anyList(), anyBoolean()))
                .thenThrow(new ValidationException("Даты не могут быть равны или дата окончания не может быть раньше даты начала"));

        mockMvc.perform(get(String.format("/stats?start=%s&end=%s&uris=%s&unique=%s",
                        LocalDateTime.now(), LocalDateTime.now(), Collections.EMPTY_LIST, Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("Даты не могут быть равны или дата окончания не может быть раньше даты начала"),
                        String.class));
    }
}