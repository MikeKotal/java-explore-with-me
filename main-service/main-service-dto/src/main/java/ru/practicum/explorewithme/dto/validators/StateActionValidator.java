package ru.practicum.explorewithme.dto.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.explorewithme.dto.annotations.ValidStateAction;
import ru.practicum.explorewithme.dto.event.StateAction;

public class StateActionValidator implements ConstraintValidator<ValidStateAction, String> {

    @Override
    public void initialize(ValidStateAction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        try {
            StateAction.valueOf(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
