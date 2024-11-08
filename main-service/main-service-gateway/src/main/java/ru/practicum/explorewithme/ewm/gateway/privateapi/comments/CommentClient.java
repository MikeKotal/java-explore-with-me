package ru.practicum.explorewithme.ewm.gateway.privateapi.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.comment.CommentRequest;
import ru.practicum.explorewithme.ewm.gateway.client.BaseClient;

import java.util.Map;

@Service
public class CommentClient extends BaseClient {

    @Value("${main-server.comment}")
    private String commentsUrl;

    @Autowired
    public CommentClient(@Value("${main-server.url}") String serverUrl,
                         @Value("${main-server.users}") String usersUrl,
                         RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + usersUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getCommentByComId(Long userId, Long comId) {
        return get(String.format("/%s/%s?userId={userId}", commentsUrl, comId), Map.of("userId", userId));
    }

    public ResponseEntity<Object> getCommentsById(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(String.format("/%s/%s?from={from}&size={size}", userId, commentsUrl), parameters);
    }

    public ResponseEntity<Object> createComment(Long userId, Long eventId, CommentRequest commentRequest) {
        return post(String.format("/%s/%s?eventId={eventId}", userId, commentsUrl), Map.of("eventId", eventId), commentRequest);
    }

    public ResponseEntity<Object> updateComment(Long userId, Long comId, CommentRequest commentRequest) {
        return patch(String.format("/%s/%s/%s", userId, commentsUrl, comId), commentRequest);
    }

    public ResponseEntity<Object> deleteComment(Long userId, Long comId) {
        return delete(String.format("/%s/%s/%s", userId, commentsUrl, comId));
    }
}
