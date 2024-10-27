package ru.practicum.explorewithme.server.stats;

import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;

public class TestData {

    public static final EndpointHitRequestDto ENDPOINT_HIT_REQUEST_DTO = EndpointHitRequestDto.builder()
            .app("new")
            .uri("/events/new")
            .ip("1.2.3.4")
            .build();

    public static final ViewStatsDto VIEW_STATS_DTO = ViewStatsDto.builder()
            .app("new")
            .uri("/events/new")
            .hits(1L)
            .build();
}
