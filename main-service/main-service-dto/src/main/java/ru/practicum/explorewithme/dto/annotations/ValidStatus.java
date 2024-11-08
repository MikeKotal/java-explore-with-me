package ru.practicum.explorewithme.dto.annotations;

import jakarta.validation.Constraint;
import ru.practicum.explorewithme.dto.validators.StatusValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {
    String message() default "Для status доступно: CONFIRMED, REJECTED";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
