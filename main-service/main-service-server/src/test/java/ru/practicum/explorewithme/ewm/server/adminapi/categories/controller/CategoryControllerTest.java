package ru.practicum.explorewithme.ewm.server.adminapi.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.ewm.server.services.CategoryService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.server.TestData.createCategoryDto;
import static ru.practicum.explorewithme.ewm.server.TestData.createNewCategoryRequest;

@WebMvcTest(controllers = CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CategoryService categoryService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenCreatedNewCategoryThenReturnNewCategory() throws Exception {
        when(categoryService.createCategory(any())).thenReturn(createCategoryDto());

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
        when(categoryService.updateCategory(any(), anyLong())).thenReturn(createCategoryDto());

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
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
