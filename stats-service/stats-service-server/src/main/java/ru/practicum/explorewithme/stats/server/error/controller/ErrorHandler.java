package ru.practicum.explorewithme.stats.server.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.stats.server.error.model.ErrorResponse;
import ru.practicum.explorewithme.stats.server.exceptions.ValidationException;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.explorewithme")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidated(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(final Throwable e) {
        log.error("Что-то пошло не так {}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
