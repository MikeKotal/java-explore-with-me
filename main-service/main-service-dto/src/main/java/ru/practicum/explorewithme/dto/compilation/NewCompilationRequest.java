package ru.practicum.explorewithme.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @NotBlank(message = "Заголовок подборки не должен быть пустым")
    @Size(min = 1, max = 50, message = "Недопустимое количество символов для поля title, min = 1, max = 50")
    private String title;
}
