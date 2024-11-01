package ru.practicum.explorewithme.server.privateapi.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.server.privateapi.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventRequest eventRequest) {
        return eventService.createEvent(userId, eventRequest);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserAndEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        return eventService.getEventByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody UpdateEventUserRequest updateRequest) {
        return eventService.updateEvent(userId, eventId, updateRequest);
    }
}
