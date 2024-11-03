package ru.practicum.explorewithme.gateway.adminapi.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;

import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AdminEventClientTest {

    @Autowired
    AdminEventClient adminEventClient;

    @Test
    public void checkRangeStartValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> adminEventClient.getEventsByFilter(null, null, null, "2024-01-01",
                        null, 0, 10));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkRangeEndValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> adminEventClient.getEventsByFilter(null, null, null, null,
                        "2024-01-01", 0, 10));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkInvalidStateValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> adminEventClient.getEventsByFilter(null, List.of("PUBLISHED", "Петя курочкин", "CANCELED"),
                        null, "2024-01-01 23:59:59", "2024-01-01 23:59:59", 0, 10));

        String expectedMessage = "Был передан невалидный state для фильтрации: Петя курочкин";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
