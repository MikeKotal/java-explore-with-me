package ru.practicum.explorewithme.gateway.privateapi.requests.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.gateway.privateapi.requests.RequestClient;

import static ru.practicum.explorewithme.gateway.privateapi.events.controller.EventGatewayController.VALIDATION_EVENT_ID;
import static ru.practicum.explorewithme.gateway.privateapi.events.controller.EventGatewayController.VALIDATION_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class RequestGatewayController {

    private final RequestClient requestClient;
    public static final String VALIDATION_REQUEST_ID = "Идентификатор запроса на участие должен быть больше нуля";

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                                @RequestParam @Positive(message = VALIDATION_EVENT_ID) Long eventId) {
        return requestClient.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequestsByUser(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId) {
        return requestClient.getRequestsByUser(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable @Positive(message = VALIDATION_USER_ID) Long userId,
                                                @PathVariable @Positive(message = VALIDATION_REQUEST_ID) Long requestId) {
        return requestClient.cancelRequest(userId, requestId);
    }
}
