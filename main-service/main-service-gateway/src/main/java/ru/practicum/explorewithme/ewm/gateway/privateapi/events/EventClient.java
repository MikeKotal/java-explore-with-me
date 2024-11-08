package ru.practicum.explorewithme.ewm.gateway.privateapi.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.ewm.gateway.client.BaseClient;

import java.util.Map;

@Service
public class EventClient extends BaseClient {

    @Value("${main-server.events}")
    private String eventsUrl;

    @Value("${main-server.requests}")
    private String requestsUrl;

    @Autowired
    public EventClient(@Value("${main-server.url}") String serverUrl,
                       @Value("${main-server.users}") String usersUrl,
                       RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + usersUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createEvent(NewEventRequest eventRequest, Long userId) {
        return post(String.format("/%s/%s", userId, eventsUrl), eventRequest);
    }

    public ResponseEntity<Object> getEventsByUserId(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(String.format("/%s/%s?from={from}&size={size}", userId, eventsUrl), parameters);
    }

    public ResponseEntity<Object> getEventByUserAndEventId(Long userId, Long eventId) {
        return get(String.format("/%s/%s/%s", userId, eventsUrl, eventId));
    }

    public ResponseEntity<Object> updateEvent(UpdateEventRequest updateRequest, Long userId, Long eventId) {
        return patch(String.format("/%s/%s/%s", userId, eventsUrl, eventId), updateRequest);
    }

    public ResponseEntity<Object> getRequestsByOwnerEventAndEventId(Long ownerId, Long eventId) {
        return get(String.format("/%s/%s/%s/%s", ownerId, eventsUrl, eventId, requestsUrl));
    }

    public ResponseEntity<Object> approveRequestByOwnerId(Long ownerId,
                                                          Long eventId,
                                                          EventRequestStatusUpdateRequest statusUpdateRequest) {
        return patch(String.format("/%s/%s/%s/%s", ownerId, eventsUrl, eventId, requestsUrl), statusUpdateRequest);
    }
}
