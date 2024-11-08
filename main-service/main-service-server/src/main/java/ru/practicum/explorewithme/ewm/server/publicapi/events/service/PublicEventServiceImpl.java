package ru.practicum.explorewithme.ewm.server.publicapi.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.SortType;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;
import ru.practicum.explorewithme.ewm.server.publicapi.client.StatsGatewayClient;
import ru.practicum.explorewithme.ewm.server.services.PublicEventService;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.exceptions.ValidationException;
import ru.practicum.explorewithme.ewm.server.mappers.EndpointHitRequestMapper;
import ru.practicum.explorewithme.ewm.server.mappers.EventMapper;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.models.Event;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final StatsGatewayClient statsGatewayClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<EventShortDto> getFilteredEventsByPublicUser(String text, List<Long> categories, Boolean paid,
                                                             String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                             String sort, Integer from, Integer size, String ip,
                                                             HttpServletRequest request) {
        log.info("Публичный запрос на получение событий по множественным фильтрам");
        Pageable pageable;
        if (sort != null) {
            String sortField = sort.equals(SortType.EVENT_DATE.name()) ? "eventDate" : "views";
            pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sortField).descending());
        } else {
            pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        }
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8));
        LocalDateTime endDate = null;
        if (rangeEnd != null) {
            endDate = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8));
        }
        List<Event> events;
        if (endDate != null) {
            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                throw new ValidationException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
            }
            events = eventRepository.findAllPublishedEventsByFilterAndPeriod(text, categories, paid, startDate, endDate, onlyAvailable, pageable);
        } else {
            events = eventRepository.findAllPublishedEventsByFilterAndRangeStart(text, categories, paid, startDate, onlyAvailable, pageable);
        }
        statsGatewayClient.post(EndpointHitRequestMapper.mapToEndpointHitRequest(request, ip));
        log.info("Публичные события согласно фильтрации: {}", events);
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getEventByIdByPublicUser(Long id, String ip, HttpServletRequest request) {
        log.info("Публичный запрос на получение события с id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Событие с id {} отсутствует", id);
                    return new NotFoundException(String.format("События с идентификатором = '%s' не найдено", id));
                });
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Можно просматривать только опубликованные события");
        }
        statsGatewayClient.post(EndpointHitRequestMapper.mapToEndpointHitRequest(request, ip));
        List<ViewStatsDto> stats = getStats(event);
        event.setViews(stats.stream().mapToLong(ViewStatsDto::getHits).sum());
        event = eventRepository.save(event);
        log.info("Публичное событие получено: {}", event);
        return EventMapper.mapToEventDto(event);
    }

    public List<ViewStatsDto> getStats(Event event) {
        Map<String, Object> parameters = Map.of(
                "start", URLEncoder.encode(event.getPublishedOn().format(FORMATTER), StandardCharsets.UTF_8),
                "end", URLEncoder.encode(LocalDateTime.now().plusSeconds(1).truncatedTo(ChronoUnit.SECONDS).format(FORMATTER),
                        StandardCharsets.UTF_8),
                "unique", Boolean.TRUE,
                "uris", String.format("/events/%s", event.getId()));
        ResponseEntity<Object> responseEntity = statsGatewayClient.get("?start={start}&end={end}&unique={unique}&uris={uris}",parameters);
        return objectMapper.convertValue(responseEntity.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ViewStatsDto.class));
    }
}
