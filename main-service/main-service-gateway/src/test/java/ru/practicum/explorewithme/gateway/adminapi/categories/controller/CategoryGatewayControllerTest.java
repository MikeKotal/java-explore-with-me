package ru.practicum.explorewithme.gateway.adminapi.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.gateway.adminapi.categories.CategoryClient;

import java.nio.charset.StandardCharsets;

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
import static ru.practicum.explorewithme.TestData.createCategoryDto;
import static ru.practicum.explorewithme.TestData.createNewCategoryRequest;

@WebMvcTest(controllers = CategoryGatewayController.class)
public class CategoryGatewayControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CategoryClient categoryClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCreatedNewCategoryThenReturnNewCategory() throws Exception {
        when(categoryClient.createCategory(any()))
                .thenReturn(new ResponseEntity<>(createCategoryDto(), HttpStatusCode.valueOf(201)));

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(createNewCategoryRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createCategoryDto().getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createCategoryDto().getName()), String.class));
    }

    @Test
    public void whenUpdateCategoryThenCategoryIsUpdated() throws Exception {
        when(categoryClient.updateCategory(any(), anyLong()))
                .thenReturn(new ResponseEntity<>(createCategoryDto(), HttpStatusCode.valueOf(200)));

        mockMvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(createNewCategoryRequest()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createCategoryDto().getId()), Long.class))
                .andExpect(jsonPath("$.name", is(createCategoryDto().getName()), String.class));
    }

    @Test
    public void whenDeleteCategoryThenReturn204() throws Exception {
        doNothing().when(categoryClient).deleteCategory(anyLong());

        mockMvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void checkInternalServerError500() throws Exception {
        when(categoryClient.createCategory(any())).thenThrow(new RuntimeException("Тест"));

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(createNewCategoryRequest()))
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
    public void checkCategoryEmptyNameValidation() throws Exception {
        NewCategoryRequest categoryRequest = new NewCategoryRequest(null);

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(categoryRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Категория не должна быть пустой"), String.class))
                .andExpect(jsonPath("$.reason", is("Некорректно заполнены входные параметры"), String.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name()), String.class))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
