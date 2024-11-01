package ru.practicum.explorewithme.server.adminapi.categories.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @MockBean
    CategoryRepository categoryRepository;

    @Mock
    NewCategoryRequest categoryRequest;

    @Mock
    Category category;

    @Test
    public void checkCreateCategory() {
        Mockito.when(categoryRepository.save(any())).thenReturn(category);
        categoryService.createCategory(categoryRequest);

        Mockito.verify(categoryRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkUpdateCategory() {
        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.when(categoryRequest.getName()).thenReturn("Тест");
        Mockito.when(categoryRepository.save(any())).thenReturn(category);
        categoryService.updateCategory(categoryRequest, 1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkDeleteCategory() {
        Mockito.when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        Mockito.doNothing().when(categoryRepository).delete(any());
        categoryService.deleteCategory(1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(categoryRepository, Mockito.times(1)).delete(any());
    }
}
