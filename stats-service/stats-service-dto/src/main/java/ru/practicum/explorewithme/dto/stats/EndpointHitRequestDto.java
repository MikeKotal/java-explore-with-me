package ru.practicum.explorewithme.dto.stats;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitRequestDto {

    @NotBlank(message = "Наименование сервиса не должно быть пустым")
    private String app;

    @NotBlank(message = "URI запроса не должен быть пустым")
    private String uri;

    @NotBlank(message = "IP-адрес пользователя не должен быть пустым")
    private String ip;
}
