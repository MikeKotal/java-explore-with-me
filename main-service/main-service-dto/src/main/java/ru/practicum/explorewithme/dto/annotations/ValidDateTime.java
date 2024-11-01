package ru.practicum.explorewithme.dto.annotations;

import jakarta.validation.Constraint;
import ru.practicum.explorewithme.dto.validators.DateTimeFormatValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateTimeFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateTime {
    String message() default "Неверный формат даты и времени. Ожидается yyyy-MM-dd HH:mm:ss";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
