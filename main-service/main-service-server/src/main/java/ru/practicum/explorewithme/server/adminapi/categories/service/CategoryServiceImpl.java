package ru.practicum.explorewithme.server.adminapi.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.mappers.CategoryMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryRequest categoryRequest) {
        log.info("Запрос на создание категории {}", categoryRequest);
        Category category = categoryRepository.save(CategoryMapper.mapToCategory(categoryRequest));
        log.info("Категория успешно создана {}", category);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(NewCategoryRequest categoryRequest, Long catId) {
        log.info("Запрос на обновление категории с id {}", catId);
        Category newCat = findCategoryById(catId);
        newCat.setName(categoryRequest.getName());
        newCat = categoryRepository.save(newCat);
        log.info("Категория {} успешно обновлена", newCat);
        return CategoryMapper.mapToCategoryDto(newCat);
    }

    @Override
    public void deleteCategory(Long catId) {
        log.info("Запрос на удаление категории с id {}", catId);
        Category category = findCategoryById(catId);
        categoryRepository.delete(category);
        log.info("Категория с id {} удалена", catId);
    }

    private Category findCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Категория с id {} отсутствует", catId);
                    return new NotFoundException(String.format("Категории с идентификатором = '%s' не найдено", catId));
                });
    }
}