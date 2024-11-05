package ru.practicum.explorewithme.server.publicapi.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.mappers.CategoryMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategoriesByPublicUser(Integer from, Integer size) {
        log.info("Публичный запрос на получение списка категорий");
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        log.info("Получен список категорий по запросу публичного пользователя: {}", categories);
        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryByIdByPublicUser(Long catId) {
        log.info("Публичный запрос на получение категории по id: {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.error("Категория с id {} отсутствует", catId);
                    return new NotFoundException(String.format("Категории с идентификатором = '%s' не найдено", catId));
                });
        log.info("Категория {} получена публичным пользователем", category);
        return CategoryMapper.mapToCategoryDto(category);
    }
}
