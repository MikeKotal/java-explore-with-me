package ru.practicum.explorewithme.ewm.server.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewm.server.models.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByRequesterId(Long userId, Sort sort);

    List<Request> findRequestsByEventId(Long eventId, Sort sort);

    List<Request> findRequestsByIdIn(List<Long> requestIds);
}
