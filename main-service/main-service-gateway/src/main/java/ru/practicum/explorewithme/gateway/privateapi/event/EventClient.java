package ru.practicum.explorewithme.gateway.privateapi.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.gateway.client.BaseClient;

import java.util.Map;

@Service
public class EventClient extends BaseClient {

    @Value("${stats-server.events}")
    private String eventsUrl;

    @Value("${stats-server.requests}")
    private String requestsUrl;

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl,
                      @Value("${stats-server.users}") String usersUrl,
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

    public ResponseEntity<Object> updateEvent(UpdateEventUserRequest updateRequest, Long userId, Long eventId) {
        return patch(String.format("/%s/%s/%s", userId, eventsUrl, eventId), updateRequest);
    }
}
