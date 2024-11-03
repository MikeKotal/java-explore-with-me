package ru.practicum.explorewithme.server.privateapi.requests.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.server.privateapi.requests.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllById(Long userId, Sort sort);

    List<Request> findRequestsByEventId(Long eventId, Sort sort);

    List<Request> findRequestsByIdIn(List<Long> requestIds);
}
