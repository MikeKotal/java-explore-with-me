package ru.practicum.explorewithme.stats.gateway.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.stats.gateway.error.model.ErrorResponse;
import ru.practicum.explorewithme.stats.gateway.exceptions.ValidationException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.explorewithme")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidated(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJakartaValidation(final MethodArgumentNotValidException e) {
        return new ErrorResponse(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingFieldsValidation(final MissingRequestValueException e) {
        return new ErrorResponse(String.format("Не переданы обязательные параметры: %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(final Throwable e) {
        log.error("Что-то пошло не так {}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
