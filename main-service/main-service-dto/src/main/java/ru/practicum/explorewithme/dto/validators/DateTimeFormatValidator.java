package ru.practicum.explorewithme.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.explorewithme.dto.annotations.ValidDateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatValidator implements ConstraintValidator<ValidDateTime, String> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        try {
            LocalDateTime.parse(s, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
