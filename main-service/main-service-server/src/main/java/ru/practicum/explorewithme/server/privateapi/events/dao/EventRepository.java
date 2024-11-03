package ru.practicum.explorewithme.server.privateapi.events.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.server.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllById(Long userId, Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or e.initiator.id in ?1)
            and (?2 is null or e.state in ?2)
            and (?3 is null or e.category.id in ?3)
            and e.eventDate between ?4 and ?5
            order by e.eventDate desc
            """)
    List<Event> findAllEventsByFilterAndPeriod(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or e.initiator.id in ?1)
            and (?2 is null or e.state in ?2)
            and (?3 is null or e.category.id in ?3)
            and e.eventDate >= ?4
            order by e.eventDate desc
            """)
    List<Event> findAllEventsByFilterAndRangeStart(List<Long> users, List<State> states, List<Long> categories,
                                                   LocalDateTime rangeStart, Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or e.initiator.id in ?1)
            and (?2 is null or e.state in ?2)
            and (?3 is null or e.category.id in ?3)
            and e.eventDate <= ?4
            order by e.eventDate desc
            """)
    List<Event> findAllEventsByFilterAndRangeEnd(List<Long> users, List<State> states, List<Long> categories,
                                                 LocalDateTime rangeEnd, Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or e.initiator.id in ?1)
            and (?2 is null or e.state in ?2)
            and (?3 is null or e.category.id in ?3)
            order by e.eventDate desc
            """)
    List<Event> findAllEventsByFilter(List<Long> users, List<State> states, List<Long> categories, Pageable pageable);

    Set<Event> findEventsByIdIn(List<Long> events, Sort sort);
}
