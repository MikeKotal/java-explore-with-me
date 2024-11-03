package ru.practicum.explorewithme.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto mapToRequestDto(Request request) {
        log.info("Request в маппер: {}", request);
        ParticipationRequestDto requestDto = ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
        log.info("ParticipationRequestDto из маппера: {}", requestDto);
        return requestDto;
    }

    public static Request mapToRequest(Event event, User requester) {
        log.info("Для Request в маппер Event: {} и Requester: {}", event, requester);
        Request request = Request.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED)
                .build();
        log.info("Request из маппера: {}", request);
        return request;
    }

    public static EventRequestStatusUpdateResultDto mapToStatusUpdateResultDto(List<Request> requests) {
        log.info("Список Request в маппер для сортировки: {}", requests);
        EventRequestStatusUpdateResultDto statusUpdateResultDto = EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(getRequestDtoByStatus(requests, Status.CONFIRMED))
                .rejectedRequests(getRequestDtoByStatus(requests, Status.REJECTED))
                .build();
        log.info("Сортированный список Request из маппера: {}", statusUpdateResultDto);
        return statusUpdateResultDto;
    }

    private static List<ParticipationRequestDto> getRequestDtoByStatus(List<Request> requests, Status status) {
        return requests.stream()
                .filter(request -> request.getStatus().equals(status))
                .map(RequestMapper::mapToRequestDto)
                .toList();
    }
}
