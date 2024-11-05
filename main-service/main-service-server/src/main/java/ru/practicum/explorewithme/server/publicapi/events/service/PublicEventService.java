package ru.practicum.explorewithme.server.publicapi.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getFilteredEventsByPublicUser(String text, List<Long> categories, Boolean paid,
                                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                      String sort, Integer from, Integer size, String ip,
                                                      HttpServletRequest request);

    EventFullDto getEventByIdByPublicUser(Long id, String ip, HttpServletRequest request);
}
