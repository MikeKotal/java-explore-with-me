package ru.practicum.explorewithme.gateway.adminapi.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.gateway.client.BaseClient;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Service
public class AdminEventClient extends BaseClient {

    @Value("${main-server.events}")
    private String eventsUrl;

    @Autowired
    public AdminEventClient(@Value("${main-server.url}") String serverUrl,
                            @Value("${main-server.admin}") String adminUrl,
                            RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + adminUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getEventsByFilter(List<String> users, List<String> states, List<String> categories,
                                                    String rangeStart, String rangeEnd, Integer from, Integer size) {
        StringBuilder defaultPath = new StringBuilder("/")
                .append(eventsUrl)
                .append("?from={from}")
                .append("&size={size}");
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "from", from,
                "size", size
        ));
        try {
            if (rangeStart != null) {
                rangeStart = LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), FORMATTER)
                        .format(DateTimeFormatter.ISO_DATE_TIME);
                parameters.put("rangeStart", URLEncoder.encode(rangeStart, StandardCharsets.UTF_8));
                defaultPath.append("&rangeStart={rangeStart}");
            }
            if (rangeEnd != null) {
                rangeEnd = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), FORMATTER)
                        .format(DateTimeFormatter.ISO_DATE_TIME);
                parameters.put("rangeEnd", URLEncoder.encode(rangeEnd, StandardCharsets.UTF_8));
                defaultPath.append("&rangeEnd={rangeEnd}");
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'");
        }
        if (checkEmptyList(users)) {
            addQueryParam(users, "users", parameters, defaultPath);
        }
        if (checkEmptyList(states)) {
            states.forEach(state -> {
                try {
                    State.valueOf(state);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException(String.format("Был передан невалидный state для фильтрации: %s", state));
                }
            });
            addQueryParam(states, "states", parameters, defaultPath);
        }
        if (checkEmptyList(categories)) {
            addQueryParam(categories, "categories", parameters, defaultPath);
        }
        return get(defaultPath.toString(), parameters);
    }

    public ResponseEntity<Object> updateEventByAdmin(UpdateEventRequest updateRequest, Long eventId) {
        return patch(String.format("/%s/%s", eventsUrl, eventId), updateRequest);
    }

    private Boolean checkEmptyList(List<String> params) {
        return params != null && !params.isEmpty();
    }

    private void addQueryParam(List<String> params, String paramName, Map<String, Object> parameters, StringBuilder builder) {
        String strParams = String.join(",", params);
        parameters.put(paramName, strParams);
        builder.append(String.format("&%s={%s}", paramName, paramName));
    }
}
