package ru.practicum.explorewithme.gateway.privateapi.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.gateway.privateapi.event.EventClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventGatewayController {

    private final EventClient eventClient;
    public static final String VALIDATION_USER_ID = "Идентификатор пользователя должен быть больше нуля";
    public static final String VALIDATION_EVENT_ID = "Идентификатор события должен быть больше нуля";

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createEvent(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                              @Valid @RequestBody NewEventRequest eventRequest) {
        return eventClient.createEvent(eventRequest, userId);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEventsByUserId(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return eventClient.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventByUserAndEventId(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                                           @PathVariable @Positive(message = VALIDATION_EVENT_ID) Long eventId) {
        return eventClient.getEventByUserAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                              @PathVariable @Positive(message = VALIDATION_EVENT_ID) Long eventId,
                                              @RequestBody @Valid UpdateEventUserRequest updateRequest) {
        return eventClient.updateEvent(updateRequest, userId, eventId);
    }
}
