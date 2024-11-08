package ru.practicum.explorewithme.ewm.server.services;

import ru.practicum.explorewithme.dto.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilationsByPublicUser(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationByIdByPublicUser(Long compId);
}
