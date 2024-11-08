package ru.practicum.explorewithme.ewm.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, null, null);
    }

    protected ResponseEntity<Object> get(String path, String ip) {
        return makeAndSendRequest(HttpMethod.GET, path, ip, null, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters, null);
    }

    protected ResponseEntity<Object> get(String path, String ip, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, ip, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.POST, path, null, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        ResponseEntity<Object> objectResponseEntity = makeAndSendRequest(HttpMethod.POST, path, null, null, body);
        log.info("Ответ от метода post: {}", objectResponseEntity);
        return objectResponseEntity;
    }

    protected ResponseEntity<Object> patch(String path) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, null, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, String ip, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(ip));

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                log.info("Метод запроса: {} URL запроса: {}, Параметры запроса: {}, тело запроса: {}", method, path, parameters, requestEntity);
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                log.info("Метод запроса: {} URL запроса: {}, тело запроса: {}", method, path, requestEntity);
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.info("Произошла ошибка: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsByteArray());
        }
        ResponseEntity<Object> objectResponseEntity = prepareGatewayResponse(statsServerResponse);
        log.info("Ответ от makeAndSendRequest: {}", objectResponseEntity);
        return objectResponseEntity;
    }

    private HttpHeaders defaultHeaders(String ip) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (ip != null && !ip.isBlank()) {
            headers.set("X-Forwarded-For", ip);
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        log.info("Ответ в prepareGatewayResponse: {}", response);

        HttpHeaders headers = new HttpHeaders();
        response.getHeaders().forEach((key, value) -> {
            if (!key.equalsIgnoreCase("Transfer-Encoding")) {
                headers.put(key, value);
            }
        });

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode()).headers(headers);

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        log.info("Обновленный ответ от prepareGatewayResponse: {}", responseBuilder);
        return responseBuilder.build();
    }
}
