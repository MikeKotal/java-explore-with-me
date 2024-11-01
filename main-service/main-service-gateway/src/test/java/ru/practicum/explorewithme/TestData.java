package ru.practicum.explorewithme;

import org.apache.commons.lang3.RandomStringUtils;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

public class TestData {

    public static LocalDateTime DATE_TIME = LocalDateTime.parse("2024-10-01 12:01:01", FORMATTER);

    public static NewUserRequest createNewUserRequest() {
        return NewUserRequest.builder()
                .email("test@test.ru")
                .name("Имя")
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .email("test@test.ru")
                .name("Имя")
                .build();
    }

    public static UserShortDto createUserShortDto() {
        return UserShortDto.builder()
                .email("test@test.com")
                .name("Супер Имя")
                .build();
    }

    public static NewCategoryRequest createNewCategoryRequest() {
        return NewCategoryRequest.builder()
                .name("Категория")
                .build();
    }

    public static CategoryDto createCategoryDto() {
        return CategoryDto.builder()
                .id(1L)
                .name("Категория")
                .build();
    }

    public static LocationRequest createLocationRequest() {
        return LocationRequest.builder()
                .lat(1.0)
                .lon(2.0)
                .build();
    }

    public static LocationDto createLocationDto() {
        return LocationDto.builder()
                .lat(1.0)
                .lon(1.0)
                .build();
    }

    public static NewEventRequest createNewEventRequest() {
        return NewEventRequest.builder()
                .title("Тестовый")
                .annotation(RandomStringUtils.randomAlphabetic(30))
                .category(1L)
                .description(RandomStringUtils.randomAlphabetic(30))
                .eventDate("2024-10-01 12:00:00")
                .location(createLocationRequest())
                .paid(Boolean.TRUE)
                .participantLimit(1)
                .requestModeration(Boolean.TRUE)
                .build();
    }

    public static UpdateEventUserRequest createUpdateEventUserRequest() {
        return UpdateEventUserRequest.builder()
                .title("Тестовый")
                .annotation(RandomStringUtils.randomAlphabetic(30))
                .category(1L)
                .description(RandomStringUtils.randomAlphabetic(30))
                .eventDate("2024-10-01 12:00:00")
                .location(createLocationRequest())
                .paid(Boolean.TRUE)
                .participantLimit(1)
                .requestModeration(Boolean.TRUE)
                .stateAction(StateAction.PUBLISH_EVENT.name())
                .build();
    }

    public static EventFullDto createEventFullDto() {
        return EventFullDto.builder()
                .id(1L)
                .title("Тестовый")
                .annotation(RandomStringUtils.randomAlphabetic(30))
                .category(createCategoryDto())
                .confirmedRequests(1L)
                .createdOn(DATE_TIME)
                .description(RandomStringUtils.randomAlphabetic(30))
                .eventDate(DATE_TIME)
                .location(createLocationDto())
                .paid(Boolean.TRUE)
                .participantLimit(1)
                .publishedOn(DATE_TIME)
                .requestModeration(Boolean.TRUE)
                .state(State.PUBLISHED)
                .views(1L)
                .build();
    }

    public static EventShortDto createEventShortDto() {
        return EventShortDto.builder()
                .id(1L)
                .title("Title")
                .annotation("Annotation")
                .category(createCategoryDto())
                .confirmedRequests(1L)
                .eventDate(DATE_TIME)
                .initiator(createUserShortDto())
                .paid(Boolean.TRUE)
                .views(1L)
                .build();
    }
}
