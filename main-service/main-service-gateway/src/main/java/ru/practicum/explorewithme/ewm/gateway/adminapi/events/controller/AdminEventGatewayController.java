package ru.practicum.explorewithme.ewm.gateway.adminapi.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.ewm.gateway.adminapi.events.AdminEventClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventGatewayController {

    private final AdminEventClient adminEventClient;

    @GetMapping
    public ResponseEntity<Object> getEventsByFilter(@RequestParam(required = false) List<String> users,
                                                    @RequestParam(required = false) List<String> states,
                                                    @RequestParam(required = false) List<String> categories,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return adminEventClient.getEventsByFilter(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEventByAdmin(@PathVariable Long eventId,
                                                     @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        return adminEventClient.updateEventByAdmin(updateEventRequest, eventId);
    }
}
