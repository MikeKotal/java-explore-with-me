package ru.practicum.explorewithme.ewm.gateway.adminapi.compilations.controller;

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
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.ewm.gateway.adminapi.compilations.CompilationClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationGatewayController {

    private final CompilationClient compilationClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCompilation(@RequestBody @Valid NewCompilationRequest compilationRequest) {
        return compilationClient.createCompilation(compilationRequest);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<Object> updateCompilation(@PathVariable Long compId,
                                                    @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return compilationClient.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationClient.deleteCompilation(compId);
    }
}
