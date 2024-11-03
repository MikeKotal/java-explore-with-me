package ru.practicum.explorewithme.gateway.adminapi.compilations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.gateway.client.BaseClient;

@Service
public class CompilationClient extends BaseClient {

    @Autowired
    public CompilationClient(@Value("${main-server.url}") String serverUrl,
                            @Value("${main-server.admin}") String adminUrl,
                            @Value("${main-server.compilation}") String compilationsUrl,
                            RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(String.format("%s%s/%s", serverUrl, adminUrl, compilationsUrl)))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createCompilation(NewCompilationRequest compilationRequest) {
        return post("", compilationRequest);
    }

    public ResponseEntity<Object> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        return patch(String.format("/%s", compId), updateCompilationRequest);
    }

    public void deleteCompilation(Long compId) {
        delete(String.format("/%s", compId));
    }
}
