package ru.practicum.explorewithme.gateway.privateapi.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.gateway.privateapi.events.EventClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventGatewayController {

    private final EventClient eventClient;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createEvent(@PathVariable Long userId,
                                              @Valid @RequestBody NewEventRequest eventRequest) {
        return eventClient.createEvent(eventRequest, userId);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEventsByUserId(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return eventClient.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventByUserAndEventId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        return eventClient.getEventByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @RequestBody @Valid UpdateEventRequest updateRequest) {
        return eventClient.updateEvent(updateRequest, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsByOwnerEventAndEventId(@PathVariable Long userId,
                                                                    @PathVariable Long eventId) {
        return eventClient.getRequestsByOwnerEventAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> approveRequestByOwnerId(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody @Valid EventRequestStatusUpdateRequest statusUpdateRequest) {
        return eventClient.approveRequestByOwnerId(userId, eventId, statusUpdateRequest);
    }
}
