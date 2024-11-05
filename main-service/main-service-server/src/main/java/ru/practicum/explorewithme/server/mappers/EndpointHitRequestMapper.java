package ru.practicum.explorewithme.server.mappers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitRequestMapper {

    public static EndpointHitRequestDto mapToEndpointHitRequest(HttpServletRequest request, String ip) {
        log.info("Данные о запросе в сервис статистики в маппер: {}", request);
        EndpointHitRequestDto endpointHitRequestDto = EndpointHitRequestDto.builder()
                .app("main-service-server")
                .uri(request.getRequestURI())
                .ip(ip)
                .build();
        log.info("EndpointHitRequestDto из маппера: {}", endpointHitRequestDto);
        return endpointHitRequestDto;
    }
}
