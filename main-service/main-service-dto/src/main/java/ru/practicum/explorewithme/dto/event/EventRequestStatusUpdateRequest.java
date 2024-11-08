package ru.practicum.explorewithme.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.annotations.ValidStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotEmpty(message = "В запросе должны присутствовать идентификаторы запросов")
    private List<Long> requestIds;

    @NotBlank(message = "Статус заявки должен присутствовать в запросе")
    @ValidStatus
    private String status;
}
