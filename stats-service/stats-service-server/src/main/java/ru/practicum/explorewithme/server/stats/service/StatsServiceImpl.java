package ru.practicum.explorewithme.server.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.server.stats.dao.StatsRepository;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;
import ru.practicum.explorewithme.server.mapper.StatsMapper;
import ru.practicum.explorewithme.server.stats.model.EndpointHit;
import ru.practicum.explorewithme.server.stats.model.ViewStats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public void createHit(EndpointHitRequestDto requestDto) {
        log.info("Запрос на создание статистики {}", requestDto);
        EndpointHit endpointHit = statsRepository.save(StatsMapper.mapToEndpointHit(requestDto));
        log.info("Запись о статистике успешно создана {}", endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStatsByPeriodAndUris(String start, String end, List<String> uris, Boolean unique) {
        log.info("Запрос на получение статистик для периода {}-{}, по uri {} с уникальностью = {}", start, end, uris, unique);
        try {
            start = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
            end = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'");
        }
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            throw new ValidationException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
        }
        List<ViewStats> viewStats = unique
                ? statsRepository.getUniqStatsByUris(startDate, endDate, uris)
                : statsRepository.getAllStatsByUris(startDate, endDate, uris);
        log.info("Список статистики за период {}-{}: {}", start, end, viewStats);
        return viewStats
                .stream()
                .map(StatsMapper::mapToViewStatsDto)
                .toList();
    }
}
