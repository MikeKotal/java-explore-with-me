package ru.practicum.explorewithme.ewm.server.publicapi.categories.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.ewm.server.services.PublicCategoryService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.explorewithme.ewm.server.TestData.createCategoryDto;

@WebMvcTest(controllers = PublicCategoryController.class)
public class PublicCategoryControllerTest {

    @MockBean
    PublicCategoryService publicCategoryService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenGetCategoriesByPublicUserThenReturnListSize1() throws Exception {
        CategoryDto categoryDto = createCategoryDto();
        when(publicCategoryService.getCategoriesByPublicUser(anyInt(), anyInt())).thenReturn(List.of(categoryDto));

        mockMvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(categoryDto.getName()), String.class));
    }

    @Test
    public void whenGetCategoryByIdAndPublicUserThenReturnCategory() throws Exception {
        CategoryDto categoryDto = createCategoryDto();
        when(publicCategoryService.getCategoryByIdByPublicUser(anyLong())).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/{catId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName()), String.class));
    }
}
