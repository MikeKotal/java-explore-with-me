package ru.practicum.explorewithme.stats.gateway.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.stats.gateway.client.BaseClient;
import ru.practicum.explorewithme.stats.gateway.exceptions.ValidationException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${stats-server.hit}")
    private String hitUrl;

    @Value("${stats-server.stats}")
    private String statsUrl;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public void createHit(EndpointHitRequestDto requestDto) {
        post(hitUrl, requestDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        try {
            start = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
            end = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER)
                    .format(DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'");
        }
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", URLEncoder.encode(start, StandardCharsets.UTF_8),
                "end", URLEncoder.encode(end, StandardCharsets.UTF_8),
                "unique", unique
        ));

        if (uris != null && !uris.isEmpty()) {
            String urisString = String.join(",", uris);
            parameters.put("uris", urisString);
            return get(String.format("%s?start={start}&end={end}&uris={uris}&unique={unique}", statsUrl), parameters);
        }

        return get(String.format("%s?start={start}&end={end}&unique={unique}", statsUrl), parameters);
    }
}
