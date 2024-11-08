package ru.practicum.explorewithme.ewm.gateway.adminapi.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.ewm.gateway.client.BaseClient;

@Service
public class AdminCommentClient extends BaseClient {

    @Autowired
    public AdminCommentClient(@Value("${main-server.url}") String serverUrl,
                         @Value("${main-server.admin}") String adminUrl,
                              @Value("${main-server.comment}") String commentsUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(String.format("%s%s/%s", serverUrl, adminUrl, commentsUrl)))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> deleteCommentByAdmin(Long comId) {
        return delete(String.format("/%s", comId));
    }
}
