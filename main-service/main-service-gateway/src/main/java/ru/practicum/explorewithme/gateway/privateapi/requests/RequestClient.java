package ru.practicum.explorewithme.gateway.privateapi.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.gateway.client.BaseClient;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    @Value("${main-server.requests}")
    private String requestsUrl;

    @Value("${main-server.cancel}")
    private String cancelUrl;

    @Autowired
    public RequestClient(@Value("${main-server.url}") String serverUrl,
                         @Value("${main-server.users}") String usersUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + usersUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, Long eventId) {
        return post(String.format("/%s/%s", userId, requestsUrl), Map.of("eventId", eventId));
    }

    public ResponseEntity<Object> getRequestsByUser(Long userId) {
        return get(String.format("/%s/%s", userId, requestsUrl));
    }

    public ResponseEntity<Object> cancelRequest(Long userId, Long requestId) {
        return patch(String.format("/%s/%s/%s/%s", userId, requestsUrl, requestId, cancelUrl));
    }
}
