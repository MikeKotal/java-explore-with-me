package ru.practicum.explorewithme.ewm.server.adminapi.categories.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.ewm.server.models.Category;
import ru.practicum.explorewithme.ewm.server.services.CategoryService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.ewm.server.TestData.createNewCategoryRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceIntegrationTest {

    private final EntityManager em;
    private final CategoryService categoryService;

    @Test
    public void checkSuccessCreateCategory() {
        NewCategoryRequest categoryRequest = createNewCategoryRequest();
        CategoryDto newCategory = categoryService.createCategory(categoryRequest);

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.id = :id", Category.class);
        Category category = query.setParameter("id", newCategory.getId()).getSingleResult();

        assertThat(category.getId(), equalTo(newCategory.getId()));
        assertThat(category.getName(), equalTo(categoryRequest.getName()));
    }

    @Test
    public void checkSuccessUpdateCategory() {
        NewCategoryRequest categoryRequest = createNewCategoryRequest();
        CategoryDto updatedCategory = categoryService.updateCategory(categoryRequest, 1L);

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.id = :id", Category.class);
        Category category = query.setParameter("id", updatedCategory.getId()).getSingleResult();

        assertThat(category.getId(), equalTo(updatedCategory.getId()));
        assertThat(category.getName(), equalTo(categoryRequest.getName()));
    }

    @Test
    public void checkSuccessDeleteCategory() {
        CategoryDto newCategory = categoryService.createCategory(createNewCategoryRequest());

        assertThat(newCategory, notNullValue());
        categoryService.deleteCategory(newCategory.getId());

        TypedQuery<Category> query = em.createQuery("Select c from Category c where c.id = :id", Category.class);

        int count = query.setParameter("id", newCategory.getId()).getResultList().size();
        assertThat(count, equalTo(0));
    }
}
