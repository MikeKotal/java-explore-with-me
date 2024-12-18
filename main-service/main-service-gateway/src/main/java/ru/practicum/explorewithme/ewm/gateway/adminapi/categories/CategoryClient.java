package ru.practicum.explorewithme.ewm.gateway.adminapi.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.ewm.gateway.client.BaseClient;

@Service
public class CategoryClient extends BaseClient {

    @Autowired
    public CategoryClient(@Value("${main-server.url}") String serverUrl,
                      @Value("${main-server.admin}") String adminUrl,
                      @Value("${main-server.categories}") String categoriesUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(String.format("%s%s/%s", serverUrl, adminUrl, categoriesUrl)))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createCategory(NewCategoryRequest categoryRequest) {
        return post("", categoryRequest);
    }

    public ResponseEntity<Object> updateCategory(NewCategoryRequest categoryRequest, Long catId) {
        return patch(String.format("/%s", catId), categoryRequest);
    }

    public ResponseEntity<Object> deleteCategory(Long catId) {
        return delete(String.format("/%s", catId));
    }
}
