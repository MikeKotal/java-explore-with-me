package ru.practicum.explorewithme.server.publicapi.categories.service;

import ru.practicum.explorewithme.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getCategoriesByPublicUser(Integer from, Integer size);

    CategoryDto getCategoryByIdByPublicUser(Long catId);
}
