package ru.practicum.explorewithme.server.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;
import ru.practicum.explorewithme.server.stats.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@RequestBody EndpointHitRequestDto requestDto) {
        statsService.createHit(requestDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatsByPeriodAndUris(@RequestParam String start,
                                                      @RequestParam String end,
                                                      @RequestParam(required = false) List<String> uris,
                                                      @RequestParam Boolean unique) {
        return statsService.getStatsByPeriodAndUris(start, end, uris, unique);
    }
}
