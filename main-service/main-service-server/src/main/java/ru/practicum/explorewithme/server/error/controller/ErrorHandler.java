package ru.practicum.explorewithme.server.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.server.error.model.ApiError;
import ru.practicum.explorewithme.server.exceptions.ConditionException;
import ru.practicum.explorewithme.server.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.explorewithme")
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .reason("Объект не был найден")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionException(final ConditionException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .reason("Ошибка при соблюдении условий")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleSqlException(final ConstraintViolationException e) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .reason("Нарушение целостности данных")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalError(final Throwable e) {
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message(e.getMessage())
                .reason("Произошла непредвиденная ошибка.")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}
