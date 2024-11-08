package ru.practicum.explorewithme.ewm.gateway.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
