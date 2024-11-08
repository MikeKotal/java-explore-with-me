package ru.practicum.explorewithme.ewm.server.adminapi.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.ewm.server.dao.UserRepository;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.mappers.UserMapper;
import ru.practicum.explorewithme.ewm.server.services.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userRequest) {
        log.info("Запрос на создание пользователя {}", userRequest);
        User newUser = userRepository.save(UserMapper.mapToUser(userRequest));
        log.info("Пользователь успешно создан {}", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        log.info("Получение пользователей с ids = {}, с элемента from {} размера выборки size {}", ids, from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<User> users = userRepository.findUserByIds(ids, pageable);
        log.info("Список пользователей {}", users);
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Запрос на удаление пользователя с id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} отсутствует", userId);
                    return new NotFoundException(String.format("Пользователя с идентификатором = '%s' не найдено", userId));
                });
        userRepository.delete(user);
        log.info("Пользователь с id {} удален", userId);
    }
}
