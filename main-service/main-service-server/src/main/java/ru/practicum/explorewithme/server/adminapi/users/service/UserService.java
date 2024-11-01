package ru.practicum.explorewithme.server.adminapi.users.service;

import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest userRequest);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
