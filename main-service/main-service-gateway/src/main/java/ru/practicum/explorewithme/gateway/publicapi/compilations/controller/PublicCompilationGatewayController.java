package ru.practicum.explorewithme.gateway.publicapi.compilations.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.gateway.publicapi.compilations.PublicCompilationClient;

import static ru.practicum.explorewithme.gateway.adminapi.compilations.controller.CompilationGatewayController.VALIDATION_COMP_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationGatewayController {

    private final PublicCompilationClient publicCompilationClient;

    @GetMapping
    public ResponseEntity<Object> getCompilationsByPublicUser(@RequestParam(required = false) Boolean pinned,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return publicCompilationClient.getCompilationsByPublicUser(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<Object> getCompilationByIdByPublicUser(@PathVariable @Positive(message = VALIDATION_COMP_ID) Long compId) {
        return publicCompilationClient.getCompilationByIdByPublicUser(compId);
    }
}
