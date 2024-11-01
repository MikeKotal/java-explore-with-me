package ru.practicum.explorewithme.server.adminapi.users.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Mock
    NewUserRequest userRequest;

    @Mock
    User user;

    @Test
    public void checkCreateUser() {
        Mockito.when(userRepository.save(any())).thenReturn(user);
        userService.createUser(userRequest);

        Mockito.verify(userRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkGetUsersByIds() {
        Mockito.when(userRepository.findUserByIds(anyList(), any(Pageable.class))).thenReturn(List.of(user));
        userService.getUsersByIds(new ArrayList<>(), 0, 10);

        Mockito.verify(userRepository, Mockito.times(1))
                .findUserByIds(anyList(), any(Pageable.class));
    }

    @Test
    public void checkDeleteUserById() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(any());
        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).delete(any());
    }

    @Test
    public void checkDeleteNotFoundException404() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteUser(1L));

        String expectedMessage = "Пользователя с идентификатором = '1' не найдено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
