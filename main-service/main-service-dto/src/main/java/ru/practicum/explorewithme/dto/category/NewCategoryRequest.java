package ru.practicum.explorewithme.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryRequest {

    @NotBlank(message = "Категория не должна быть пустой")
    @Size(min = 1, max = 50, message = "Недопустимое количество символов для поля name, min = 1, max = 50")
    private String name;
}
