package ru.practicum.explorewithme.server.publicapi.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.server.publicapi.events.service.PublicEventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> getFilteredEventsByPublicUser(@RequestParam(required = false) String text,
                                                             @RequestParam(required = false) List<Long> categories,
                                                             @RequestParam(required = false) Boolean paid,
                                                             @RequestParam(required = false) String rangeStart,
                                                             @RequestParam(required = false) String rangeEnd,
                                                             @RequestParam Boolean onlyAvailable,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam Integer from,
                                                             @RequestParam Integer size,
                                                             @RequestHeader("X-Forwarded-For") String ip,
                                                             HttpServletRequest request) {
        return publicEventService.getFilteredEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, ip, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdByPublicUser(@PathVariable Long id,
                                                 @RequestHeader("X-Forwarded-For") String ip,
                                                 HttpServletRequest request) {
        return publicEventService.getEventByIdByPublicUser(id, ip, request);
    }
}
