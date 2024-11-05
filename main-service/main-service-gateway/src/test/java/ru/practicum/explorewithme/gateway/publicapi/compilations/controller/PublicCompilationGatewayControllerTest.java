package ru.practicum.explorewithme.gateway.publicapi.compilations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.gateway.publicapi.compilations.PublicCompilationClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.TestData.createCompilationDto;

@WebMvcTest(controllers = PublicCompilationGatewayController.class)
public class PublicCompilationGatewayControllerTest {

    @MockBean
    PublicCompilationClient publicCompilationClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetCompilationsByPublicUserThenReturnListSize1() throws Exception {
        CompilationDto compilationDto = createCompilationDto();
        when(publicCompilationClient.getCompilationsByPublicUser(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(List.of(compilationDto), HttpStatusCode.valueOf(200)));

        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(compilationDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].pinned", is(compilationDto.getPinned()), Boolean.class))
                .andExpect(jsonPath("$[0].title", is(compilationDto.getTitle()), String.class))
                .andExpect(jsonPath("$[0].events", notNullValue()))
                .andExpect(jsonPath("$[0].events", hasSize(1)))
                .andExpect(jsonPath("$[0].events[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].events[0].title", is("Title"), String.class));
    }

    @Test
    public void whenGetCompilationByIdByPublicUserThenReturnCompilation() throws Exception {
        CompilationDto compilationDto = createCompilationDto();
        when(publicCompilationClient.getCompilationByIdByPublicUser(anyLong()))
                .thenReturn(new ResponseEntity<>(compilationDto, HttpStatusCode.valueOf(200)));

        mockMvc.perform(get("/compilations/{compId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compilationDto.getId()), Long.class))
                .andExpect(jsonPath("$.pinned", is(compilationDto.getPinned()), Boolean.class))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle()), String.class))
                .andExpect(jsonPath("$.events", notNullValue()))
                .andExpect(jsonPath("$.events", hasSize(1)));
    }
}
