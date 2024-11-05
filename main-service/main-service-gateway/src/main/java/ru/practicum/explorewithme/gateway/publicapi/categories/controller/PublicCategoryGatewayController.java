package ru.practicum.explorewithme.gateway.publicapi.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.gateway.publicapi.categories.PublicCategoryClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryGatewayController {

    private final PublicCategoryClient publicCategoryClient;

    @GetMapping
    public ResponseEntity<Object> getCategoriesByPublicUser(@RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        return publicCategoryClient.getCategoriesByPublicUser(from, size);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategoryByIdByPublicUser(@PathVariable Long catId) {
        return publicCategoryClient.getCategoryByIdByPublicUser(catId);
    }
}
