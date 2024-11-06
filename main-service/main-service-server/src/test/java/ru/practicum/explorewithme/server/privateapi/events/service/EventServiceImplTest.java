package ru.practicum.explorewithme.server.privateapi.events.service;

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
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.event.StateAction;
import ru.practicum.explorewithme.dto.event.UpdateEventRequest;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.server.adminapi.categories.dao.CategoryRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.server.privateapi.events.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;
import ru.practicum.explorewithme.server.privateapi.location.model.Location;
import ru.practicum.explorewithme.server.privateapi.requests.dao.RequestRepository;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @MockBean
    RequestRepository requestRepository;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User initiator;

    @Mock
    NewEventRequest newEventRequest;

    @Mock
    UpdateEventRequest eventUserRequest;

    @Mock
    LocationRequest locationRequest;

    @Mock
    Location location;

    @Mock
    Request request;

    @Mock
    EventRequestStatusUpdateRequest statusUpdateRequest;

    @Test
    public void checkCreateEvent() {
        when(newEventRequest.getEventDate()).thenReturn(LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS).plusHours(2).format(FORMATTER));
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
        when(eventRepository.findAllByInitiatorId(anyLong(), any(Pageable.class))).thenReturn(List.of(event));
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(initiator);
        eventService.getEventsByUserId(1L, 0, 10);

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllByInitiatorId(anyLong(), any(Pageable.class));
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
    public void checkEventTimeValidation() {
        when(newEventRequest.getEventDate())
                .thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusHours(2).minusMinutes(1)
                .truncatedTo(ChronoUnit.SECONDS).format(FORMATTER));
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> eventService.createEvent(1L, newEventRequest));

        String expectedMessage = "Дата и время не может быть раньше, чем через два часа от текущего момента";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkUpdateEvent() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventUserRequest.getCategory()).thenReturn(1L);
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.SEND_TO_REVIEW.name());
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
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.SEND_TO_REVIEW.name());
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
        when(eventUserRequest.getStateAction()).thenReturn(StateAction.SEND_TO_REVIEW.name());
        when(event.getCategory()).thenReturn(category);
        when(event.getState()).thenReturn(State.CANCELED);
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.updateEvent(1L, 1L, eventUserRequest));

        String expectedMessage = "Обновлять информацию о событии может только собственник";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkGetRequestsByOwnerEventAndEventId() {
        Sort orderByCreated = Sort.by(Sort.Direction.DESC, "created");
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(requestRepository.findRequestsByEventId(1L, orderByCreated)).thenReturn(List.of(request));
        when(request.getEvent()).thenReturn(event);
        when(request.getRequester()).thenReturn(initiator);
        eventService.getRequestsByOwnerEventAndEventId(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(requestRepository, Mockito.times(1)).findRequestsByEventId(1L, orderByCreated);
    }

    @Test
    public void checkForbiddenRequestsValidation() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.getRequestsByOwnerEventAndEventId(1L, 1L));

        String expectedMessage = "Просматривать список запросов к событию может только собственник";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkApproveNeedConfirmRequestByOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getParticipantLimit()).thenReturn(1);
        when(event.getRequestModeration()).thenReturn(Boolean.TRUE);
        when(event.getConfirmedRequests()).thenReturn(2);
        when(statusUpdateRequest.getRequestIds()).thenReturn(List.of(1L));
        when(requestRepository.findRequestsByIdIn(anyList())).thenReturn(List.of(request));
        when(request.getStatus()).thenReturn(Status.PENDING);
        when(statusUpdateRequest.getStatus()).thenReturn(Status.CONFIRMED.name());
        when(requestRepository.save(any())).thenReturn(request);
        when(eventRepository.save(any())).thenReturn(event);
        eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(requestRepository, Mockito.times(1)).findRequestsByIdIn(List.of(1L));
        Mockito.verify(requestRepository, Mockito.times(1)).save(any());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
        Assertions.assertEquals(2, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 2 раза");
        Assertions.assertEquals(1, Mockito.mockingDetails(userRepository).getInvocations().size(),
                "Объект userRepository должен был быть вызван ровно 1 раз");
        Assertions.assertEquals(2, Mockito.mockingDetails(requestRepository).getInvocations().size(),
                "Объект requestRepository должен был быть вызван ровно 2 раза");
    }

    @Test
    public void checkRejectRequestByOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getParticipantLimit()).thenReturn(1);
        when(event.getRequestModeration()).thenReturn(Boolean.TRUE);
        when(event.getConfirmedRequests()).thenReturn(2);
        when(statusUpdateRequest.getRequestIds()).thenReturn(List.of(1L));
        when(requestRepository.findRequestsByIdIn(anyList())).thenReturn(List.of(request));
        when(request.getStatus()).thenReturn(Status.PENDING);
        when(statusUpdateRequest.getStatus()).thenReturn(Status.REJECTED.name());
        when(requestRepository.saveAll(any())).thenReturn(List.of(request));
        when(eventRepository.save(any())).thenReturn(event);
        eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(requestRepository, Mockito.times(1)).findRequestsByIdIn(List.of(1L));
        Mockito.verify(requestRepository, Mockito.times(1)).saveAll(any());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
        Assertions.assertEquals(2, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 2 раза");
        Assertions.assertEquals(1, Mockito.mockingDetails(userRepository).getInvocations().size(),
                "Объект userRepository должен был быть вызван ровно 1 раз");
        Assertions.assertEquals(2, Mockito.mockingDetails(requestRepository).getInvocations().size(),
                "Объект requestRepository должен был быть вызван ровно 2 раза");
    }

    @Test
    public void checkValidationRejectedStatusRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getParticipantLimit()).thenReturn(1);
        when(event.getConfirmedRequests()).thenReturn(2);
        when(statusUpdateRequest.getRequestIds()).thenReturn(List.of(1L));
        when(requestRepository.findRequestsByIdIn(anyList())).thenReturn(List.of(request));
        when(request.getStatus()).thenReturn(Status.CANCELED);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest));

        String expectedMessage = "Статус можно изменить только у заявок, находящихся в состоянии ожидания";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkNotFountRequestsValidation() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getParticipantLimit()).thenReturn(1);
        when(event.getConfirmedRequests()).thenReturn(2);
        when(statusUpdateRequest.getRequestIds()).thenReturn(List.of(1L));
        when(requestRepository.findRequestsByIdIn(anyList())).thenReturn(new ArrayList<>());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest));

        String expectedMessage = "Запросов по таким идентификаторам нет";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkValidationParticipantLimitRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(1L);
        when(event.getParticipantLimit()).thenReturn(2);
        when(event.getConfirmedRequests()).thenReturn(2);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest));

        String expectedMessage = "Заявки не принимаются, достигнут лимит по заявкам на данное событие";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkValidationNotOwnerApprove() {
        when(userRepository.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(initiator);
        when(initiator.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> eventService.approveRequestByOwnerId(1L, 1L, statusUpdateRequest));

        String expectedMessage = "Подтверждать запросы на участие в событии может только собственник";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
