package ru.practicum.explorewithme.ewm.gateway.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.explorewithme.ewm.gateway.error.model.ApiError;
import ru.practicum.explorewithme.ewm.gateway.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.explorewithme")
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleJakartaValidation(final MethodArgumentNotValidException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .reason("Некорректно заполнены входные параметры")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleRequestValidation(final ValidationException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .reason("Запрос сформирован некорректно")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleFieldValidation(final HandlerMethodValidationException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message(e.getAllValidationResults().getFirst().getResolvableErrors().getFirst().getDefaultMessage())
                .reason("Некорректно заполнены входные параметры")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleParsingValidation(final NumberFormatException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .reason("Некорректно переданы типы входных параметров")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingFieldsValidation(final MissingRequestValueException e) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .reason("Не переданы обязательные параметры")
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
