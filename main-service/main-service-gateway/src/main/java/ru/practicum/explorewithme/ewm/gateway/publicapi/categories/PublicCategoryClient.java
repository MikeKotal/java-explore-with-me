package ru.practicum.explorewithme.ewm.gateway.publicapi.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.ewm.gateway.client.BaseClient;

import java.util.Map;

@Service
public class PublicCategoryClient extends BaseClient {

    @Autowired
    public PublicCategoryClient(@Value("${main-server.url}") String serverUrl,
                                   @Value("${main-server.categories}") String categoriesUrl,
                                   RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + categoriesUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getCategoriesByPublicUser(Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> getCategoryByIdByPublicUser(Long catId) {
        return get(String.format("/%s", catId));
    }
}
