package ru.practicum.explorewithme.ewm.server.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.ewm.server.models.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        log.info("User в маппер: {}", user);
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
        log.info("UserDto из маппера: {}", userDto);
        return userDto;
    }

    public static User mapToUser(NewUserRequest userRequest) {
        log.info("NewUserRequest в маппер: {}", userRequest);
        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
        log.info("User из маппера: {}", user);
        return user;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        log.info("User в маппер для UserShort: {}", user);
        UserShortDto userShortDto = UserShortDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
        log.info("UserShortDto из маппера: {}", userShortDto);
        return userShortDto;
    }
}
