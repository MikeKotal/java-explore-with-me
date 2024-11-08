package ru.practicum.explorewithme.ewm.server.publicapi.events.service;

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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.practicum.explorewithme.dto.event.SortType;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.ewm.server.publicapi.client.StatsGatewayClient;
import ru.practicum.explorewithme.ewm.server.services.PublicEventService;
import ru.practicum.explorewithme.ewm.server.models.Category;
import ru.practicum.explorewithme.ewm.server.models.User;
import ru.practicum.explorewithme.ewm.server.exceptions.NotFoundException;
import ru.practicum.explorewithme.ewm.server.exceptions.ValidationException;
import ru.practicum.explorewithme.ewm.server.dao.EventRepository;
import ru.practicum.explorewithme.ewm.server.models.Event;
import ru.practicum.explorewithme.ewm.server.models.Location;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static ru.practicum.explorewithme.ewm.server.TestData.createViewStatsDto;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PublicEventServiceImplTest {

    @Autowired
    PublicEventService publicEventService;

    @MockBean
    EventRepository eventRepository;

    @MockBean
    StatsGatewayClient statsGatewayClient;

    @Mock
    Event event;

    @Mock
    Category category;

    @Mock
    User user;

    @Mock
    Location location;

    @Test
    public void checkGetFilteredEventsByFullPeriod() {
        when(eventRepository.findAllPublishedEventsByFilterAndPeriod(anyString(), anyList(), anyBoolean(),
                any(LocalDateTime.class), any(LocalDateTime.class), anyBoolean(), any(Pageable.class)))
                .thenReturn(List.of(event));
        doNothing().when(statsGatewayClient).post(any());
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        publicEventService.getFilteredEventsByPublicUser("Text", List.of(1L, 2L), Boolean.TRUE,
                "2024-10-02T00:00:00", "2024-10-03T00:00:00", Boolean.TRUE, SortType.VIEWS.name(),
                0, 10, "1:1:1:1", new MockHttpServletRequest());

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllPublishedEventsByFilterAndPeriod(anyString(), anyList(), anyBoolean(),
                        any(LocalDateTime.class), any(LocalDateTime.class), anyBoolean(), any(Pageable.class));
        Mockito.verify(statsGatewayClient, Mockito.times(1)).post(any());
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetFilteredEventsByRangeStart() {
        when(eventRepository.findAllPublishedEventsByFilterAndRangeStart(anyString(), anyList(), anyBoolean(),
                any(LocalDateTime.class), anyBoolean(), any(Pageable.class)))
                .thenReturn(List.of(event));
        doNothing().when(statsGatewayClient).post(any());
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        publicEventService.getFilteredEventsByPublicUser("Text", List.of(1L, 2L), Boolean.TRUE,
                "2024-10-02T00:00:00", null, Boolean.TRUE, SortType.VIEWS.name(),
                0, 10, "1:1:1:1", new MockHttpServletRequest());

        Mockito.verify(eventRepository, Mockito.times(1))
                .findAllPublishedEventsByFilterAndRangeStart(anyString(), anyList(), anyBoolean(),
                        any(LocalDateTime.class), anyBoolean(), any(Pageable.class));
        Mockito.verify(statsGatewayClient, Mockito.times(1)).post(any());
        Assertions.assertEquals(1, Mockito.mockingDetails(eventRepository).getInvocations().size(),
                "Объект eventRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void checkGetEventsIncorrectDateTimeValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> publicEventService.getFilteredEventsByPublicUser("Text", List.of(1L, 2L), Boolean.TRUE,
                        "2024-10-04T00:00:00", "2024-10-03T00:00:00", Boolean.TRUE, SortType.VIEWS.name(),
                        0, 10, "1:1:1:1", new MockHttpServletRequest()));

        String expectedMessage = "Даты не могут быть равны или дата окончания не может быть раньше даты начала";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkGetEventByIdByPublicUser() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getState()).thenReturn(State.PUBLISHED);
        doNothing().when(statsGatewayClient).post(any());
        when(event.getId()).thenReturn(1L);
        when(event.getPublishedOn()).thenReturn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        when(statsGatewayClient.get(anyString(), anyMap())).thenReturn(new ResponseEntity<>(List.of(createViewStatsDto()),
                HttpStatusCode.valueOf(200)));
        when(eventRepository.save(any())).thenReturn(event);
        when(event.getCategory()).thenReturn(category);
        when(event.getInitiator()).thenReturn(user);
        when(event.getLocation()).thenReturn(location);
        publicEventService.getEventByIdByPublicUser(1L, "1:1:1:1", new MockHttpServletRequest());

        Mockito.verify(eventRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(statsGatewayClient, Mockito.times(1)).post(any());
        Mockito.verify(statsGatewayClient, Mockito.times(1)).get(anyString(), anyMap());
        Mockito.verify(eventRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void checkEmptyEventValidation() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> publicEventService.getEventByIdByPublicUser(1L, "1:1:1:1", new MockHttpServletRequest()));

        String expectedMessage = "События с идентификатором = '1' не найдено";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkInvalidStateEventValidation() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(event.getState()).thenReturn(State.CANCELED);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> publicEventService.getEventByIdByPublicUser(1L, "1:1:1:1", new MockHttpServletRequest()));

        String expectedMessage = "Можно просматривать только опубликованные события";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
