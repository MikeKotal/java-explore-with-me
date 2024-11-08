package ru.practicum.explorewithme.ewm.server.publicapi.categories.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.ewm.server.services.PublicCategoryService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicCategoryServiceIntegrationTest {

    private final PublicCategoryService publicCategoryService;

    @Test
    public void checkSuccessGetCategoriesByPublicUser() {
        List<CategoryDto> categoryDtos = publicCategoryService.getCategoriesByPublicUser(0, 10);

        assertThat(categoryDtos.size(), equalTo(3));

        CategoryDto categoryDto = categoryDtos.getFirst();
        assertThat(categoryDto.getId(), equalTo(1L));
        assertThat(categoryDto.getName(), equalTo("ТестКатегория"));
    }

    @Test
    public void checkSuccessGetCategoryByIdByPublicUser() {
        CategoryDto categoryDto = publicCategoryService.getCategoryByIdByPublicUser(1L);
        assertThat(categoryDto.getId(), equalTo(1L));
        assertThat(categoryDto.getName(), equalTo("ТестКатегория"));
    }
}
