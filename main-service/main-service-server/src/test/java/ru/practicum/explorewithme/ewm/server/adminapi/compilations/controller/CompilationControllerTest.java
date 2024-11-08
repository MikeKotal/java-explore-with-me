package ru.practicum.explorewithme.ewm.server.adminapi.compilations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.ewm.server.services.CompilationService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.server.TestData.createCompilationDto;
import static ru.practicum.explorewithme.ewm.server.TestData.createCompilationRequest;
import static ru.practicum.explorewithme.ewm.server.TestData.createUpdateCompilationRequest;

@WebMvcTest(controllers = CompilationController.class)
public class CompilationControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CompilationService compilationService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCreateCompilationThenReturnNewCompilation() throws Exception {
        NewCompilationRequest compilationRequest = createCompilationRequest();
        CompilationDto compilationDto = createCompilationDto();
        when(compilationService.createCompilation(any())).thenReturn(compilationDto);

        ResultActions resultActions = mockMvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(compilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        checkCompilationDtoResponse(resultActions, compilationDto);
    }

    @Test
    public void whenUpdateCompilationThenReturnUpdatedCompilation() throws Exception {
        UpdateCompilationRequest updateCompilationRequest = createUpdateCompilationRequest();
        CompilationDto compilationDto = createCompilationDto();
        when(compilationService.updateCompilation(anyLong(), any())).thenReturn(compilationDto);

        ResultActions resultActions = mockMvc.perform(patch("/admin/compilations/{compId}", 1)
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        checkCompilationDtoResponse(resultActions, compilationDto);
    }

    @Test
    public void whenDeleteCompilationThenReturn204() throws Exception {
        doNothing().when(compilationService).deleteCompilation(anyLong());

        mockMvc.perform(delete("/admin/compilations/{compId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private void checkCompilationDtoResponse(ResultActions resultActions, CompilationDto compilationDto) throws Exception {
        resultActions
                .andExpect(jsonPath("$.events", notNullValue()))
                .andExpect(jsonPath("$.events", hasSize(1)))
                .andExpect(jsonPath("$.id", is(compilationDto.getId()), Long.class))
                .andExpect(jsonPath("$.pinned", is(compilationDto.getPinned()), Boolean.class))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle()), String.class));
    }
}
