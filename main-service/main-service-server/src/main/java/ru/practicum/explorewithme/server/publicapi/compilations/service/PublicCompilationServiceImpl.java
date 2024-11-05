package ru.practicum.explorewithme.server.publicapi.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.server.adminapi.compilations.dao.CompilationRepository;
import ru.practicum.explorewithme.server.adminapi.compilations.model.Compilation;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.mappers.CompilationMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilationsByPublicUser(Boolean pinned, Integer from, Integer size) {
        log.info("Публичный запрос на получение подборок событий по закрепленным подборкам: {}", pinned);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findCompilationsByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        log.info("Публичные подборки получены: {}", compilations);
        return compilations.stream()
                .map(CompilationMapper::mapToCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationByIdByPublicUser(Long compId) {
        log.info("Публичный запрос на получение подборки событий по id: {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error("Подборка с id {} отсутствует", compId);
                    return new NotFoundException(String.format("Подборки с идентификатором = '%s' не найдено", compId));
                });
        log.info("Подборка по публичному запросу получена: {}", compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }
}
