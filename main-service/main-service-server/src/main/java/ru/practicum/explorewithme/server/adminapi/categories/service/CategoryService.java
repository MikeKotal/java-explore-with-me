package ru.practicum.explorewithme.server.adminapi.categories.service;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryRequest categoryRequest);

    CategoryDto updateCategory(NewCategoryRequest categoryRequest, Long catId);

    void deleteCategory(Long catId);
}
