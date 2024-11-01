package ru.practicum.explorewithme.server.privateapi.event.service;

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
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.privateapi.event.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.event.model.Event;
import ru.practicum.explorewithme.server.privateapi.location.model.Location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator.FORMATTER;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Autowired
    EventService eventService;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User initiator;

    @Mock
    NewEventRequest newEventRequest;

    @Mock
    UpdateEventUserRequest eventUserRequest;

    @Mock
    LocationRequest locationRequest;

    @Mock
    Location location;

    @Test
    public void checkCreateEvent() {
        when(newEventRequest.getEventDate()).thenReturn("2030-01-01 00:00:00");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(initiator));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(newEventRequest.getLocation()).thenReturn(locationRequest);
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(initiator);
        when(event.getLocation()).thenReturn(location);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        when(eventRepository.save(any())).thenReturn(event);
        eventService.createEvent(1L, newEventRequest);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkGetEventsByUserId() {
        when(eventRepository.findAllById(anyLong(), any(Pageable.class))).thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(initiator);
        eventService.getEventsByUserId(1L, 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllById(anyLong(), any(Pageable.class));
    }

    @Test
    public void checkGetEventByUserAndEventId() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        eventService.getEventByUserAndEventId(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    public void checkGetEventByInvalidUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(2L);
        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.getEventByUserAndEventId(1L, 1L));

        String expectedMessage = "Просматривать полную информацию о событии может только собственник";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkUpdateEvent() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(1L);
        when(eventUserRequest.getEventDate()).thenReturn(LocalDateTime.now().plusDays(1L).format(FORMATTER));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(event.getState()).thenReturn(State.PENDING);
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getCategory()).thenReturn(category);
        when(event.getLocation()).thenReturn(location);
        when(event.getCreatedOn()).thenReturn(LocalDateTime.now());
        when(event.getEventDate()).thenReturn(LocalDateTime.now());
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now());
        when(eventRepository.save(any())).thenReturn(event);
        eventService.updateEvent(1L, 1L, eventUserRequest);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkExceptionUpdateWithInvalidStatus() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(null);
        when(event.getCategory()).thenReturn(category);
        when(event.getState()).thenReturn(State.PUBLISHED);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.updateEvent(1L, 1L, eventUserRequest));

        String expectedMessage = "Можно изменять только события находящиеся в статусах: PENDING, CANCELED";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkExceptionUpdateWithInvalidUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(null);
        when(event.getCategory()).thenReturn(category);
        when(event.getState()).thenReturn(State.CANCELED);
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.updateEvent(1L, 1L, eventUserRequest));

        String expectedMessage = "Обновлять информацию о событии может только собственник";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
