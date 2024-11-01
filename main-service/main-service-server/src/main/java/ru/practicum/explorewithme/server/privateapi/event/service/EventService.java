package ru.practicum.explorewithme.server.privateapi.event.service;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventRequest eventRequest);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserAndEventId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest userRequest);
}
