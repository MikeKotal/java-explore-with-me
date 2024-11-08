package ru.practicum.explorewithme.stats.server.stats.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.stats.EndpointHitRequestDto;
import ru.practicum.explorewithme.dto.stats.ViewStatsDto;
import ru.practicum.explorewithme.stats.server.stats.model.EndpointHit;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.explorewithme.stats.server.stats.TestData.ENDPOINT_HIT_REQUEST_DTO;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceIntegrationTest {

    private final EntityManager em;
    private final StatsService statsService;

    @Test
    public void checkSuccessCreateHit() {
        EndpointHitRequestDto requestDto = ENDPOINT_HIT_REQUEST_DTO;
        statsService.createHit(requestDto);

        TypedQuery<EndpointHit> query = em.createQuery("Select eh from EndpointHit eh where eh.id = :id", EndpointHit.class);
        EndpointHit endpointHit = query.setParameter("id", 5).getSingleResult();

        assertThat(endpointHit.getId(), equalTo(5));
        assertThat(endpointHit.getApp(), equalTo(requestDto.getApp()));
        assertThat(endpointHit.getUri(), equalTo(requestDto.getUri()));
        assertThat(endpointHit.getIp(), equalTo(requestDto.getIp()));
        assertThat(endpointHit.getTimestamp(), notNullValue());
    }

    @Test
    public void checkSuccessGetUniqStatsByPeriodAndUris() {
        List<ViewStatsDto> viewStatsDtos = statsService.getStatsByPeriodAndUris("2024-01-01T00:00:00",
                "2024-01-04T00:00:00", List.of("/event/test2"), Boolean.TRUE);

        assertThat(viewStatsDtos.size(), equalTo(1));
        assertThat(viewStatsDtos.getFirst().getApp(), equalTo("test2"));
        assertThat(viewStatsDtos.getFirst().getUri(), equalTo("/event/test2"));
        assertThat(viewStatsDtos.getFirst().getHits(), equalTo(1L));
    }

    @Test
    public void checkSuccessGetAllStatsByPeriodAndUris() {
        List<ViewStatsDto> viewStatsDtos = statsService.getStatsByPeriodAndUris("2024-01-01T00:00:00",
                "2024-01-04T00:00:00", null, Boolean.FALSE);

        assertThat(viewStatsDtos.size(), equalTo(3));
        assertThat(viewStatsDtos.getFirst().getApp(), equalTo("test2"));
        assertThat(viewStatsDtos.getFirst().getUri(), equalTo("/event/test2"));
        assertThat(viewStatsDtos.getFirst().getHits(), equalTo(2L));
    }
}
