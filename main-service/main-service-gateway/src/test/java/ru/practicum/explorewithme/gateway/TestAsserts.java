package ru.practicum.explorewithme.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestAsserts {

    public static void checkValidationErrorResponse(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage),
                        String.class))
                .andExpect(jsonPath("$.reason", is("Некорректно заполнены входные параметры"), String.class))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name()), String.class))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
