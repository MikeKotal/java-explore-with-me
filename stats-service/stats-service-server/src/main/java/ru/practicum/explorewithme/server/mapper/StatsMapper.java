package ru.practicum.explorewithme.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;
import ru.practicum.explorewithme.server.stats.model.EndpointHit;
import ru.practicum.explorewithme.server.stats.model.ViewStats;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsMapper {

    public static EndpointHit mapToEndpointHit(EndpointHitRequestDto requestDto) {
        log.info("EndpointHitRequest в маппер: {}", requestDto);
        EndpointHit endpointHit = EndpointHit.builder()
                .app(requestDto.getApp())
                .uri(requestDto.getUri())
                .ip(requestDto.getIp())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("EndpointHit из маппера: {}", endpointHit);
        return endpointHit;
    }

    public static ViewStatsDto mapToViewStatsDto(ViewStats viewStats) {
        log.info("ViewStats в маппер: {}", viewStats);
        ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
        log.info("ViewStatsDto из маппера: {}", viewStatsDto);
        return viewStatsDto;
    }
}
