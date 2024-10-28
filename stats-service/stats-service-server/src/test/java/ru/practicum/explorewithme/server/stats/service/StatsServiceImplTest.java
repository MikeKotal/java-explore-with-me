package ru.practicum.explorewithme.server.stats.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.explorewithme.server.exceptions.ValidationException;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.server.stats.dao.StatsRepository;
import ru.practicum.explorewithme.server.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StatsServiceImplTest {

    @Autowired
    StatsService statsService;

    @MockBean
    StatsRepository statsRepository;

    @Mock
    EndpointHitRequestDto endpointHitRequestDto;

    @Mock
    EndpointHit endpointHit;

    @Test
    public void createHitTest() {
        Mockito.when(statsRepository.save(any())).thenReturn(endpointHit);
        statsService.createHit(endpointHitRequestDto);

        Mockito.verify(statsRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void getUniqStatsByPeriodAndUrisTest() {
        Mockito.when(statsRepository.getUniqStatsByUris(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(new ArrayList<>());
        statsService.getStatsByPeriodAndUris("2024-01-01 00:00:00", "2024-01-02 00:00:00", new ArrayList<>(),
                Boolean.TRUE);

        Mockito.verify(statsRepository, Mockito.times(1))
                .getUniqStatsByUris(any(LocalDateTime.class), any(LocalDateTime.class), anyList());
        Assertions.assertEquals(1, Mockito.mockingDetails(statsRepository).getInvocations().size(),
                "Объект statsRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void getAllStatsByPeriodAndUrisTest() {
        Mockito.when(statsRepository.getAllStatsByUris(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(new ArrayList<>());
        statsService.getStatsByPeriodAndUris("2024-01-01 00:00:00", "2024-01-02 00:00:00", new ArrayList<>(),
                Boolean.FALSE);

        Mockito.verify(statsRepository, Mockito.times(1))
                .getAllStatsByUris(any(LocalDateTime.class), any(LocalDateTime.class), anyList());
        Assertions.assertEquals(1, Mockito.mockingDetails(statsRepository).getInvocations().size(),
                "Объект statsRepository должен был быть вызван ровно 1 раз");
    }

    @Test
    public void whenEndIsBeforeStartThenReturnValidationException() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> statsService.getStatsByPeriodAndUris("2024-01-01 00:00:00", "2023-12-31 00:00:00",
                        new ArrayList<>(), Boolean.TRUE));

        String expectedMessage = "Даты не могут быть равны или дата окончания не может быть раньше даты начала";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void whenEndAndStartEqualsThenReturnValidationException() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> statsService.getStatsByPeriodAndUris("2024-01-01 00:00:00", "2024-01-01 00:00:00",
                        new ArrayList<>(), Boolean.TRUE));

        String expectedMessage = "Даты не могут быть равны или дата окончания не может быть раньше даты начала";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkDateValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> statsService.getStatsByPeriodAndUris("2024-01-01", "2024-01-01",
                        new ArrayList<>(), Boolean.TRUE));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
