package ru.practicum.explorewithme.ewm.server.publicapi.categories.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.ewm.server.dao.CategoryRepository;
import ru.practicum.explorewithme.ewm.server.models.Category;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.services.PublicCategoryService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PublicCategoryServiceImplTest {

    @Autowired
    PublicCategoryService publicCategoryService;

    @MockBean
    CategoryRepository categoryRepository;

    @Mock
    Page<Category> categories;

    @Mock
    Category category;

    @Test
    public void checkSuccessGetCategoriesByPublicUser() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categories);
        publicCategoryService.getCategoriesByPublicUser(0, 10);

        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void checkSuccessGetCategoryByIdByPublicUser() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        publicCategoryService.getCategoryByIdByPublicUser(1L);

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    public void checkEmptyCategoryValidation() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> publicCategoryService.getCategoryByIdByPublicUser(1L));

        String expectedMessage = "Категории с идентификатором = '1' не найдено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
