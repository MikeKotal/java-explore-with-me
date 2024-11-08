package ru.practicum.explorewithme.ewm.gateway.privateapi.requests.controller;

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
import ru.practicum.explorewithme.ewm.gateway.privateapi.requests.RequestClient;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class RequestGatewayController {

    private final RequestClient requestClient;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@PathVariable Long userId,
                                                @RequestParam Long eventId) {
        return requestClient.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequestsByUser(@PathVariable Long userId) {
        return requestClient.getRequestsByUser(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@PathVariable Long userId,
                                                @PathVariable Long requestId) {
        return requestClient.cancelRequest(userId, requestId);
    }
}
