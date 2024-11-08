package ru.practicum.explorewithme.ewm.server.adminapi.events.service;

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
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.ewm.server.dao.CategoryRepository;
import ru.practicum.explorewithme.ewm.server.models.Category;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.exceptions.ConditionException;
import ru.practicum.explorewithme.ewm.server.exceptions.ValidationException;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.models.Location;
import ru.practicum.explorewithme.ewm.server.services.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AdminEventServiceImplTest {

    @Autowired
    AdminEventService adminEventService;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User initiator;

    @Mock
    UpdateEventRequest eventUserRequest;

    @Mock
    Location location;

    @Test
    public void checkGetEventsByFilterAndFullPeriod() {
        when(eventRepository.findAllEventsByFilterAndPeriod(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        adminEventService.getEventsByFilter(List.of(1L), List.of(State.PUBLISHED), List.of(1L),
                "2024-10-01T00:00:00", "2024-10-02T00:00:00", 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllEventsByFilterAndPeriod(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class));
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetEventsByFilterAndRangeStart() {
        when(eventRepository.findAllEventsByFilterAndRangeStart(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        adminEventService.getEventsByFilter(List.of(1L), List.of(State.PUBLISHED), List.of(1L),
                "2024-10-01T00:00:00", null, 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllEventsByFilterAndRangeStart(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                        any(Pageable.class));
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetEventsByFilterAndRangeEnd() {
        when(eventRepository.findAllEventsByFilterAndRangeEnd(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                any(Pageable.class))).thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        adminEventService.getEventsByFilter(List.of(1L), List.of(State.PUBLISHED), List.of(1L),
                null, "2024-10-01T00:00:00", 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllEventsByFilterAndRangeEnd(anyList(), anyList(), anyList(), any(LocalDateTime.class),
                        any(Pageable.class));
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetEventsByFilterAndWithoutPeriod() {
        when(eventRepository.findAllEventsByFilter(anyList(), anyList(), anyList(), any(Pageable.class)))
                .thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        adminEventService.getEventsByFilter(List.of(1L), List.of(State.PUBLISHED), List.of(1L),
                null, null, 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllEventsByFilter(anyList(), anyList(), anyList(), any(Pageable.class));
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetEventsIncorrectDateTimeValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> adminEventService.getEventsByFilter(List.of(1L), List.of(State.PUBLISHED), List.of(1L),
                        "2024-10-02T00:00:00", "2024-10-01T00:00:00", 0, 10));

        String expectedMessage = "Даты не могут быть равны или дата окончания не может быть раньше даты начала";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkUpdateEventByAdmin() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(1L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(event.getState()).thenReturn(State.PENDING);
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.PUBLISH_EVENT.name());
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        when(eventRepository.save(any())).thenReturn(event);
        adminEventService.updateEventByAdmin(1L, eventUserRequest);

        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkPublicationWithUnavailableStatus() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(null);
        when(event.getCategory()).thenReturn(category);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.PUBLISH_EVENT.name());
        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> adminEventService.updateEventByAdmin(1L, eventUserRequest));

        String expectedMessage = "Допустимо публиковать события находящиеся только в статусе PENDING";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkRejectionWithUnavailableStatus() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(null);
        when(event.getCategory()).thenReturn(category);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.REJECT_EVENT.name());
        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> adminEventService.updateEventByAdmin(1L, eventUserRequest));

        String expectedMessage = "Допустимо отклонять события находящиеся не в статусе PUBLISHED";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
