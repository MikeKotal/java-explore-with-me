package ru.practicum.explorewithme.server.privateapi.requests.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.server.adminapi.users.dao.UserRepository;
import ru.practicum.explorewithme.server.adminapi.users.model.User;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.privateapi.events.dao.EventRepository;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;
import ru.practicum.explorewithme.server.privateapi.requests.dao.RequestRepository;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Autowired
    RequestService requestService;

    @MockBean
    RequestRepository requestRepository;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    UserRepository userRepository;

    @Mock
    Event event;

    @Mock
    User requester;

    @Mock
    Request request;

    @Test
    public void checkCreateRequest() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(event.getConfirmedRequests()).thenReturn(1);
        when(event.getParticipantLimit()).thenReturn(2);
        when(event.getRequestModeration()).thenReturn(Boolean.TRUE);
        when(request.getEvent()).thenReturn(event);
        when(request.getRequester()).thenReturn(requester);
        when(requestRepository.save(any())).thenReturn(request);
        requestService.createRequest(1L, 1L);

        Mockito.verify(requestRepository, Mockito.times(1))
                .existsByRequesterIdAndEventId(anyLong(), anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkCreateRequestWithAutoApprove() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(event.getConfirmedRequests()).thenReturn(0);
        when(event.getParticipantLimit()).thenReturn(0);
        when(event.getRequestModeration()).thenReturn(Boolean.TRUE);
        when(request.getEvent()).thenReturn(event);
        when(eventRepository.save(any())).thenReturn(event);
        when(request.getRequester()).thenReturn(requester);
        when(requestRepository.save(any())).thenReturn(request);
        requestService.createRequest(1L, 1L);

        Mockito.verify(requestRepository, Mockito.times(1))
                .existsByRequesterIdAndEventId(anyLong(), anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
        Mockito.verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkAutoRejectRequest() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(event.getConfirmedRequests()).thenReturn(0);
        when(event.getParticipantLimit()).thenReturn(0);
        when(event.getRequestModeration()).thenReturn(Boolean.TRUE);
        when(request.getEvent()).thenReturn(event);
        when(eventRepository.save(any())).thenReturn(event);
        when(request.getRequester()).thenReturn(requester);
        when(requestRepository.save(any())).thenReturn(request);
        requestService.createRequest(1L, 1L);

        Mockito.verify(requestRepository, Mockito.times(1))
                .existsByRequesterIdAndEventId(anyLong(), anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
        Mockito.verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkValidationUserIdCreateRequest() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(1L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> requestService.createRequest(1L, 1L));

        String expectedMessage = "Инициатор события не может добавить запрос на участие в своём событии";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkValidationStatusEventCreateRequest() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);
        when(event.getState()).thenReturn(State.CANCELED);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> requestService.createRequest(1L, 1L));

        String expectedMessage = "Нельзя участвовать в неопубликованном событии";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkValidationConfirmedRequests() {
        when(requestRepository.existsByRequesterIdAndEventId(anyLong(), anyLong())).thenReturn(Boolean.FALSE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getInitiator()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);
        when(event.getState()).thenReturn(State.PUBLISHED);
        when(event.getConfirmedRequests()).thenReturn(1);
        when(event.getParticipantLimit()).thenReturn(1);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> requestService.createRequest(1L, 1L));

        String expectedMessage = "Был достигнут лимит запросов на участие в событии";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkGetRequestsByUserId() {
        Sort orderByCreated = Sort.by(Sort.Direction.DESC, "created");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(requestRepository.findAllByRequesterId(1L, orderByCreated)).thenReturn(List.of(request));
        when(request.getEvent()).thenReturn(event);
        when(request.getRequester()).thenReturn(requester);
        requestService.getRequestsByUserId(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(requestRepository, Mockito.times(1)).findAllByRequesterId(1L, orderByCreated);
    }

    @Test
    public void checkSuccessCancelRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(requester.getId()).thenReturn(1L);
        when(request.getStatus()).thenReturn(Status.PENDING);
        when(requestRepository.save(any())).thenReturn(request);
        when(request.getEvent()).thenReturn(event);
        when(request.getRequester()).thenReturn(requester);
        requestService.cancelRequest(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(requestRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkCancelOtherUserRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(request.getRequester()).thenReturn(requester);
        when(requester.getId()).thenReturn(2L);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> requestService.cancelRequest(1L, 1L));

        String expectedMessage = "Отменить запрос на участии в событии может только владелец запроса";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkCancelRejectedRequestException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(request.getRequester()).thenReturn(requester);
        when(requester.getId()).thenReturn(1L);
        when(request.getStatus()).thenReturn(Status.CANCELED);

        ConditionException exception = Assertions.assertThrows(ConditionException.class,
                () -> requestService.cancelRequest(1L, 1L));

        String expectedMessage = "Событие уже отменено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
