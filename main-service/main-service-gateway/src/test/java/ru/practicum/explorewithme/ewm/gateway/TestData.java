package ru.practicum.explorewithme.ewm.gateway;

import org.apache.commons.lang3.RandomStringUtils;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationRequest;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateResultDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

public class TestData {

    public static String FORMATTED_DATE_TIME = "2030-10-01 12:01:01";
    public static LocalDateTime DATE_TIME = LocalDateTime.parse(FORMATTED_DATE_TIME, FORMATTER);

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
                .eventDate(FORMATTED_DATE_TIME)
                .location(createLocationRequest())
                .paid(Boolean.TRUE)
                .participantLimit(1)
                .requestModeration(Boolean.TRUE)
                .build();
    }

    public static UpdateEventRequest createUpdateEventUserRequest() {
        return UpdateEventRequest.builder()
                .title("Тестовый")
                .annotation(RandomStringUtils.randomAlphabetic(30))
                .category(2L)
                .description(RandomStringUtils.randomAlphabetic(30))
                .eventDate(FORMATTED_DATE_TIME)
                .location(createLocationRequest())
                .paid(Boolean.FALSE)
                .participantLimit(1)
                .requestModeration(Boolean.FALSE)
                .stateAction(StateAction.SEND_TO_REVIEW.name())
                .build();
    }

    public static EventFullDto createEventFullDto() {
        return EventFullDto.builder()
                .id(1L)
                .title("Тестовый")
                .annotation(RandomStringUtils.randomAlphabetic(30))
                .category(createCategoryDto())
                .confirmedRequests(1)
                .createdOn(FORMATTED_DATE_TIME)
                .description(RandomStringUtils.randomAlphabetic(30))
                .eventDate(FORMATTED_DATE_TIME)
                .location(createLocationDto())
                .paid(Boolean.TRUE)
                .participantLimit(1)
                .publishedOn(FORMATTED_DATE_TIME)
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
                .confirmedRequests(1)
                .eventDate(FORMATTED_DATE_TIME)
                .initiator(createUserShortDto())
                .paid(Boolean.TRUE)
                .views(1L)
                .build();
    }

    public static ParticipationRequestDto createRequestDto() {
        return ParticipationRequestDto.builder()
                .id(1L)
                .event(1L)
                .requester(1L)
                .created(DATE_TIME)
                .status(Status.PENDING)
                .build();
    }

    public static EventRequestStatusUpdateRequest createStatusUpdateRequest() {
        return EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L, 2L))
                .status(Status.CONFIRMED.name())
                .build();
    }

    public static EventRequestStatusUpdateResultDto createStatusUpdateResultDto() {
        ParticipationRequestDto rejected = createRequestDto();
        ParticipationRequestDto confirmed = createRequestDto();
        rejected.setStatus(Status.CANCELED);
        confirmed.setStatus(Status.CONFIRMED);
        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(List.of(confirmed))
                .rejectedRequests(List.of(rejected))
                .build();
    }

    public static NewCompilationRequest createCompilationRequest() {
        return NewCompilationRequest.builder()
                .events(List.of(1L))
                .pinned(Boolean.TRUE)
                .title("Мегаподпорка")
                .build();
    }

    public static UpdateCompilationRequest createUpdateCompilationRequest() {
        return UpdateCompilationRequest.builder()
                .events(List.of(2L))
                .pinned(Boolean.FALSE)
                .title("Немегаподборка")
                .build();
    }

    public static CompilationDto createCompilationDto() {
        return CompilationDto.builder()
                .events(List.of(createEventShortDto()))
                .id(1L)
                .pinned(Boolean.TRUE)
                .title("Просто подборка")
                .build();
    }
}
