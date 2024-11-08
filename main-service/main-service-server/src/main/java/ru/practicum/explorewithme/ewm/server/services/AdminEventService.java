package ru.practicum.explorewithme.ewm.server.services;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEventsByFilter(List<Long> users, List<State> states, List<Long> categories,
                                         String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest userRequest);
}
