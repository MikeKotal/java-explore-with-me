package ru.practicum.explorewithme.ewm.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.ewm.server.models.Compilation;
import ru.practicum.explorewithme.ewm.server.models.Event;

import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        log.info("Compilation в маппер: {}", compilation);
        CompilationDto compilationDto = CompilationDto.builder()
                .events(compilation.getEvents().stream()
                        .map(EventMapper::mapToEventShortDto)
                        .toList())
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
        log.info("CompilationDto из маппера: {}", compilationDto);
        return compilationDto;
    }

    public static Compilation mapToNewCompilation(NewCompilationRequest compilationRequest, Set<Event> events) {
        log.info("NewCompilationRequest в маппер: {}", compilationRequest);
        Compilation compilation = Compilation.builder()
                .events(events)
                .pinned(compilationRequest.getPinned() == null ? Boolean.FALSE : compilationRequest.getPinned())
                .title(compilationRequest.getTitle())
                .build();
        log.info("Compilation из маппера: {}", compilation);
        return compilation;
    }

    public static Compilation mapToUpdateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                                     Compilation oldCompilation,
                                                     Set<Event> newEvents) {
        log.info("UpdateCompilationRequest в маппер: {}", updateCompilationRequest);
        Compilation compilation = Compilation.builder()
                .id(oldCompilation.getId())
                .events(newEvents)
                .pinned(updateCompilationRequest.getPinned() == null ? oldCompilation.getPinned()
                        : updateCompilationRequest.getPinned())
                .title(updateCompilationRequest.getTitle() == null ? oldCompilation.getTitle()
                        : updateCompilationRequest.getTitle())
                .build();
        log.info("Обновленный Compilation из маппера: {}", compilation);
        return compilation;
    }
}
