package ru.practicum.explorewithme.gateway.publicapi.compilations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.gateway.client.BaseClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class PublicCompilationClient extends BaseClient {

    @Autowired
    public PublicCompilationClient(@Value("${main-server.url}") String serverUrl,
                             @Value("${main-server.compilation}") String compilationsUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + compilationsUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getCompilationsByPublicUser(Boolean pinned, Integer from, Integer size) {
        StringBuilder defaultPath = new StringBuilder()
                .append("?from={from}")
                .append("&size={size}");
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "from", from,
                "size", size
        ));
        if (pinned != null) {
            parameters.put("pinned", pinned);
            defaultPath.append("&pinned={pinned}");
        }
        return get(defaultPath.toString(), parameters);
    }

    public ResponseEntity<Object> getCompilationByIdByPublicUser(Long compId) {
        return get(String.format("/%s", compId));
    }
}
