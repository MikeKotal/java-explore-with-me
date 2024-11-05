package ru.practicum.explorewithme.server.publicapi.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.server.publicapi.compilations.service.PublicCompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getCompilationsByPublicUser(@RequestParam(required = false) Boolean pinned,
                                                            @RequestParam Integer from,
                                                            @RequestParam Integer size) {
        return publicCompilationService.getCompilationsByPublicUser(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationByIdByPublicUser(@PathVariable Long compId) {
        return publicCompilationService.getCompilationByIdByPublicUser(compId);
    }
}
