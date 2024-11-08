package ru.practicum.explorewithme.ewm.server.services;

import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest userRequest);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
