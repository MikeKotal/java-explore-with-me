package ru.practicum.explorewithme.ewm.server.services;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventRequest eventRequest);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserAndEventId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest userRequest);

    List<ParticipationRequestDto> getRequestsByOwnerEventAndEventId(Long ownerId, Long eventId);

    EventRequestStatusUpdateResultDto approveRequestByOwnerId(Long ownerId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest);
}
