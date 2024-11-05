package ru.practicum.explorewithme.server.adminapi.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.server.adminapi.compilations.dao.CompilationRepository;
import ru.practicum.explorewithme.server.adminapi.compilations.model.Compilation;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.mappers.CompilationMapper;
import ru.practicum.explorewithme.server.privateapi.events.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    public static final Sort ORDER_BY_EVENT_DAY = Sort.by(Sort.Direction.ASC, "eventDate");

    @Override
    public CompilationDto createCompilation(NewCompilationRequest compilationRequest) {
        log.info("Запрос на создание новой подборки {}", compilationRequest);
        Set<Event> events = new HashSet<>();
        if (compilationRequest.getEvents() != null) {
            events = eventRepository.findEventsByIdIn(compilationRequest.getEvents(), ORDER_BY_EVENT_DAY);
        }
        Compilation compilation = CompilationMapper.mapToNewCompilation(compilationRequest, events);
        compilation = compilationRepository.save(compilation);
        log.info("Подборка успешно создана: {}", compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Запрос на обноление подборки с id {}", compId);
        Compilation compilation = getCompilationById(compId);
        Set<Event> newEvents = new TreeSet<>(Comparator.comparing(Event::getEventDate));
        newEvents.addAll(compilation.getEvents());
        if (updateCompilationRequest.getEvents() != null) {
            newEvents.addAll(eventRepository.findEventsByIdIn(updateCompilationRequest.getEvents(), ORDER_BY_EVENT_DAY));
        }
        compilation = compilationRepository
                .save(CompilationMapper.mapToUpdateCompilation(updateCompilationRequest, compilation, newEvents));
        log.info("Обновленная подборка событий: {}", compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        log.info("Запрос на удаление подборки с id {}", compId);
        Compilation compilation = getCompilationById(compId);
        compilationRepository.delete(compilation);
        log.info("Подборка с id {} была удалена",  compId);
    }

    private Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error("Подборка с id {} отсутствует", compId);
                    return new NotFoundException(String.format("Подборки с идентификатором = '%s' не найдено", compId));
                });
    }
}
