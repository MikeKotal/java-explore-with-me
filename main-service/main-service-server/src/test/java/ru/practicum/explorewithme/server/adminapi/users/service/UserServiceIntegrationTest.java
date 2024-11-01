package ru.practicum.explorewithme.server.adminapi.users.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.server.adminapi.users.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.server.TestData.createNewUserRequest;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    public void checkSuccessCreateUser() {
        NewUserRequest userRequest = createNewUserRequest();
        UserDto newUser = userService.createUser(userRequest);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", newUser.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(newUser.getId()));
        assertThat(user.getName(), equalTo(userRequest.getName()));
        assertThat(user.getEmail(), equalTo(userRequest.getEmail()));
    }

    @Test
    public void checkGetUsersByIds() {
        List<UserDto> userDtos = userService.getUsersByIds(List.of(1L, 2L, 3L), 0, 10);
        assertThat(userDtos.size(), equalTo(3));
    }

    @Test
    public void checkGetUsersByPagination() {
        List<UserDto> userDtos = userService.getUsersByIds(null, 3, 2);
        assertThat(userDtos.size(), equalTo(2));
        assertThat(userDtos.getFirst().getId(), equalTo(3L));
        assertThat(userDtos.getFirst().getName(), equalTo("Name2"));
        assertThat(userDtos.getFirst().getEmail(), equalTo("test2@test.ru"));
    }

    @Test
    public void checkSuccessDeleteUser() {
        UserDto newUser = userService.createUser(createNewUserRequest());

        assertThat(newUser, notNullValue());
        userService.deleteUser(newUser.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);

        int count = query.setParameter("id", newUser.getId()).getFirstResult();
        assertThat(count, equalTo(0));
    }
}
