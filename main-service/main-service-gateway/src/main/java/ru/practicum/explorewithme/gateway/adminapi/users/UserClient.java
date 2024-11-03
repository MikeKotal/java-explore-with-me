package ru.practicum.explorewithme.gateway.adminapi.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.gateway.client.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Value("${main-server.url}") String serverUrl,
                      @Value("${main-server.admin}") String adminUrl,
                      @Value("${main-server.users}") String usersUrl,
                       RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(String.format("%s%s/%s", serverUrl, adminUrl, usersUrl)))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(NewUserRequest userRequest) {
        return post("", userRequest);
    }

    public ResponseEntity<Object> getUsers(List<String> ids, Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "from", from,
                "size", size
        ));

        if (ids != null && !ids.isEmpty()) {
            String idsString = String.join(",", ids);
            parameters.put("ids", idsString);
            return get("?ids={ids}&from={from}&size={size}", parameters);
        }
        return get("?from={from}&size={size}", parameters);
    }

    public void deleteUser(Long userId) {
        delete(String.format("/%s", userId));
    }
}
