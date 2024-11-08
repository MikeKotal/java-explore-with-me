package ru.practicum.explorewithme.ewm.gateway.publicapi.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.ewm.gateway.publicapi.events.PublicEventClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventGatewayController {

    private final PublicEventClient publicEventClient;

    @GetMapping
    public ResponseEntity<Object> getFilteredEventsByPublicUser(@RequestParam(required = false) String text,
                                                                @RequestParam(required = false) List<String> categories,
                                                                @RequestParam(required = false) Boolean paid,
                                                                @RequestParam(required = false) String rangeStart,
                                                                @RequestParam(required = false) String rangeEnd,
                                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                @RequestParam(required = false) String sort,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                HttpServletRequest request) {
        return publicEventClient.getFilteredEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEventByIdByPublicUser(@PathVariable Long id,
                                                           HttpServletRequest request) {
        return publicEventClient.getEventByIdByPublicUser(id, request);
    }
}
