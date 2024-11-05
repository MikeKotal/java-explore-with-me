package ru.practicum.explorewithme.gateway.publicapi.events;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.event.SortType;
import ru.practicum.explorewithme.gateway.client.BaseClient;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@Service
public class PublicEventClient extends BaseClient {

    @Autowired
    public PublicEventClient(@Value("${main-server.url}") String serverUrl,
                             @Value("${main-server.events}") String eventsUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + eventsUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getFilteredEventsByPublicUser(String text, List<String> categories, Boolean paid,
                                                          String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                          String sort, Integer from, Integer size, HttpServletRequest request) {
        StringBuilder defaultPath = new StringBuilder()
                .append("?onlyAvailable={onlyAvailable}")
                .append("&from={from}")
                .append("&size={size}");
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "onlyAvailable", onlyAvailable,
                "from", from,
                "size", size
        ));
        try {
            rangeStart = rangeStart == null ? LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()
                    : LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), FORMATTER)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
            parameters.put("rangeStart", URLEncoder.encode(rangeStart, StandardCharsets.UTF_8));
            defaultPath.append("&rangeStart={rangeStart}");
            if (rangeEnd != null) {
                rangeEnd = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), FORMATTER)
                        .format(DateTimeFormatter.ISO_DATE_TIME);
                parameters.put("rangeEnd", URLEncoder.encode(rangeEnd, StandardCharsets.UTF_8));
                defaultPath.append("&rangeEnd={rangeEnd}");
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'");
        }
        if (categories != null && !categories.isEmpty()) {
            String strParams = String.join(",", categories);
            parameters.put("categories", strParams);
            defaultPath.append("&categories={categories}");
        }
        if (sort != null) {
            try {
                SortType.valueOf(sort);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(String.format("Был передан невалидный sort для фильтрации: %s", sort));
            }
            parameters.put("sort", sort);
            defaultPath.append("&sort={sort}");
        }
        if (text != null) {
            parameters.put("text", text);
            defaultPath.append("&text={text}");
        }
        if (paid != null) {
            parameters.put("paid", paid);
            defaultPath.append("&paid={paid}");
        }
        return get(defaultPath.toString(), request.getRemoteAddr(), parameters);
    }

    public ResponseEntity<Object> getEventByIdByPublicUser(Long id, HttpServletRequest request) {
        return get(String.format("/%s", id), request.getRemoteAddr());
    }
}
