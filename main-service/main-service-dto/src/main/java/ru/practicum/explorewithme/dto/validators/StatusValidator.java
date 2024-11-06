package ru.practicum.explorewithme.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.explorewithme.dto.annotations.ValidStatus;
import ru.practicum.explorewithme.dto.request.Status;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

    @Override
    public void initialize(ValidStatus constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.equals(Status.CONFIRMED.name()) || s.equals(Status.CANCELED.name());
    }
}
