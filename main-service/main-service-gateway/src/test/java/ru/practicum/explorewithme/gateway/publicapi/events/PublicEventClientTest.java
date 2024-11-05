package ru.practicum.explorewithme.gateway.publicapi.events;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;

import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PublicEventClientTest {

    @Autowired
    PublicEventClient publicEventClient;

    @Test
    public void checkRangeStartValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> publicEventClient.getFilteredEventsByPublicUser(null, null, null, "2024-01-01",
                        null, Boolean.FALSE, null, 0, 10, new MockHttpServletRequest()));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkRangeEndValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> publicEventClient.getFilteredEventsByPublicUser(null, null, null, null,
                        "2024-01-01", Boolean.FALSE, null, 0, 10, new MockHttpServletRequest()));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }

    @Test
    public void checkIncorrectSortTypeValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> publicEventClient.getFilteredEventsByPublicUser("Test", List.of("1", "2"), Boolean.TRUE, "2024-01-01 00:00:00",
                        "2024-01-02 00:00:00", Boolean.FALSE, "Мощный", 0, 10, new MockHttpServletRequest()));

        String expectedMessage = "Был передан невалидный sort для фильтрации: Мощный";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
