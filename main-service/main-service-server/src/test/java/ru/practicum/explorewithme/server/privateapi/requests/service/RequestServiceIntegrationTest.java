package ru.practicum.explorewithme.server.privateapi.requests.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.Status;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceIntegrationTest {

    private final EntityManager em;
    private final RequestService requestService;

    @Test
    public void checkSuccessCreateRequest() {
        ParticipationRequestDto requestDto = requestService.createRequest(3L, 2L);

        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.id = :id", Request.class);
        Request request = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertThat(request.getId(), equalTo(requestDto.getId()));
        assertThat(request.getEvent(), notNullValue());
        assertThat(request.getEvent().getId(), equalTo(requestDto.getEvent()));
        assertThat(request.getRequester(), notNullValue());
        assertThat(request.getRequester().getId(), equalTo(requestDto.getRequester()));
        assertThat(request.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(request.getStatus(), equalTo(requestDto.getStatus()));
    }

    @Test
    public void checkSuccessGetRequestsByUserId() {
        List<ParticipationRequestDto> requestDtos = requestService.getRequestsByUserId(1L);

        assertThat(requestDtos.size(), equalTo(1));

        ParticipationRequestDto requestDto = requestDtos.getFirst();
        assertThat(requestDto.getId(), equalTo(1L));
        assertThat(requestDto.getEvent(), equalTo(1L));
        assertThat(requestDto.getRequester(), equalTo(1L));
        assertThat(requestDto.getCreated(), equalTo(LocalDateTime.parse("2024-10-01T23:59:59")));
        assertThat(requestDto.getStatus(), equalTo(Status.PENDING));
    }

    @Test
    public void checkSuccessCancelRequest() {
        ParticipationRequestDto requestDto = requestService.cancelRequest(1L, 1L);

        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.id = :id", Request.class);
        Request request = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertThat(request.getId(), equalTo(requestDto.getId()));
        assertThat(request.getEvent(), notNullValue());
        assertThat(request.getEvent().getId(), equalTo(requestDto.getEvent()));
        assertThat(request.getRequester(), notNullValue());
        assertThat(request.getRequester().getId(), equalTo(requestDto.getRequester()));
        assertThat(request.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(request.getStatus(), equalTo(Status.REJECTED));
    }
}
