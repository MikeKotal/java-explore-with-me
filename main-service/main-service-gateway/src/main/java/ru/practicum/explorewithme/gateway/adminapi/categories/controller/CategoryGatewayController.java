package ru.practicum.explorewithme.gateway.adminapi.categories.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.gateway.adminapi.categories.CategoryClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryGatewayController {

    private final CategoryClient categoryClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCategory(@Valid @RequestBody NewCategoryRequest categoryRequest) {
        return categoryClient.createCategory(categoryRequest);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategory(@Valid @RequestBody NewCategoryRequest categoryRequest,
                                                 @PathVariable Long catId) {
        return categoryClient.updateCategory(categoryRequest, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteCategory(@PathVariable Long catId) {
        return categoryClient.deleteCategory(catId);
    }
}
