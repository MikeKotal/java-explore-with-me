package ru.practicum.explorewithme.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.annotations.ValidDateTime;
import ru.practicum.explorewithme.dto.location.LocationRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventRequest {

    @NotBlank(message = "Заголовок события не должен быть пустым")
    @Size(min = 3, max = 120, message = "Недопустимое количество символов для поля title, min = 3, max = 120")
    private String title;

    @NotBlank(message = "Краткое описание не должно быть пустым")
    @Size(min = 20, max = 2000, message = "Недопустимое количество символов для поля annotation, min = 20, max = 2000")
    private String annotation;

    @NotNull(message = "Идентификатор категории не должен быть пустым")
    @Positive(message = "Идентификатор категории должен быть больше нуля")
    private Long category;

    @NotBlank(message = "Полное описание не должно быть пустым")
    @Size(min = 20, max = 7000, message = "Недопустимое количество символов для поля description, min = 20, max = 7000")
    private String description;

    @NotBlank(message = "Дата и время события не должно быть пустым")
    @ValidDateTime
    private String eventDate;

    @NotNull(message = "Координаты локации должны быть заполнены")
    private LocationRequest location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;
}
