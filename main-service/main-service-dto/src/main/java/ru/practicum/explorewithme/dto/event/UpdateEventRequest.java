package ru.practicum.explorewithme.dto.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.annotations.ValidDateTime;
import ru.practicum.explorewithme.dto.annotations.ValidStateAction;
import ru.practicum.explorewithme.dto.location.LocationRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @Size(min = 3, max = 120, message = "Недопустимое количество символов для поля title, min = 3, max = 120")
    private String title;

    @Size(min = 20, max = 2000, message = "Недопустимое количество символов для поля annotation, min = 20, max = 2000")
    private String annotation;

    @Positive(message = "Идентификатор категории должен быть больше нуля")
    private Long category;

    @Size(min = 20, max = 7000, message = "Недопустимое количество символов для поля description, min = 20, max = 7000")
    private String description;

    @ValidDateTime
    private String eventDate;

    private LocationRequest location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @ValidStateAction
    private String stateAction;
}
