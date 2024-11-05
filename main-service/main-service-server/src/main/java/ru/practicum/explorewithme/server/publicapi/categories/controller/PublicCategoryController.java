package ru.practicum.explorewithme.server.publicapi.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.server.publicapi.categories.service.PublicCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> getCategoriesByPublicUser(@RequestParam Integer from,
                                                       @RequestParam Integer size) {
        return publicCategoryService.getCategoriesByPublicUser(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryByIdByPublicUser(@PathVariable Long catId) {
        return publicCategoryService.getCategoryByIdByPublicUser(catId);
    }
}
