package ru.practicum.explorewithme.dto.annotations;

import jakarta.validation.Constraint;
import ru.practicum.explorewithme.dto.validators.StateActionValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StateActionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStateAction {
    String message() default "Для stateAction доступно: SEND_TO_REVIEW, CANCEL_REVIEW, PUBLISH_EVENT, REJECT_EVENT";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
