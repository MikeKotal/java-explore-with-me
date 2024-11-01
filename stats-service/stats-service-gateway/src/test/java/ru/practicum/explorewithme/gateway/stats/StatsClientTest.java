package ru.practicum.explorewithme.gateway.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.explorewithme.gateway.exceptions.ValidationException;

import java.util.ArrayList;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class StatsClientTest {

    @Autowired
    StatsClient statsClient;

    @Test
    public void checkDateValidation() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> statsClient.getStats("2024-01-01", "2024-01-01",
                        new ArrayList<>(), Boolean.TRUE));

        String expectedMessage = "Период времени необходимо передавать в формате 'yyyy-MM-dd HH:mm:ss'";
        Assertions.assertEquals(expectedMessage, exception.getMessage(), "Некорректное сообщение об ошибке");
    }
}
