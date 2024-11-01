package ru.practicum.explorewithme.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(Category category) {
        log.info("Category в маппер: {}", category);
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        log.info("CategoryDto из маппера: {}", categoryDto);
        return categoryDto;
    }

    public static Category mapToCategory(NewCategoryRequest categoryRequest) {
        log.info("NewCategoryRequest в маппер: {}", categoryRequest);
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        log.info("Category из маппера: {}", category);
        return category;
    }
}
