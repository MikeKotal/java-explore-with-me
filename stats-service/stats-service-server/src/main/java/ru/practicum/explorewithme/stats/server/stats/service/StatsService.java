package ru.practicum.explorewithme.stats.server.stats.service;

import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void createHit(EndpointHitRequestDto requestDto);

    List<ViewStatsDto> getStatsByPeriodAndUris(String start, String end, List<String> uris, Boolean unique);
}