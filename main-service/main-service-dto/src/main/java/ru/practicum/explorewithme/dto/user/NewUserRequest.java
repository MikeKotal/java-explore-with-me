package ru.practicum.explorewithme.dto.user;

import jakarta.validation.constraints.Email;
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
public class NewUserRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Email имеет некорректный формат")
    @Size(min = 6, max = 254, message = "Недопустимое количество символов для поля email, min = 6, max = 254")
    private String email;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 250, message = "Недопустимое количество символов для поля name, min = 2, max = 250")
    private String name;
}
