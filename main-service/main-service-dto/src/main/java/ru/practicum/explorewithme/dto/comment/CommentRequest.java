package ru.practicum.explorewithme.dto.comment;

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
public class CommentRequest {

    @NotBlank(message = "Комментарий к событию не должен быть пустым")
    @Size(min = 3, max = 7000, message = "Недопустимое количество символов для поля comment, min = 3, max = 7000")
    private String comment;
}
