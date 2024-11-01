package ru.practicum.explorewithme.server.error.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiError {
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
