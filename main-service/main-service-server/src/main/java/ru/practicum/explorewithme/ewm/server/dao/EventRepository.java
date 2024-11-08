package ru.practicum.explorewithme.ewm.server.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.dto.event.State;
import ru.practicum.explorewithme.ewm.server.models.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

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

    @Query("""
            select e
            from Event as e
            where (?1 is null or (e.annotation ilike %?1% or e.description ilike %?1%))
            and (?2 is null or e.category.id in ?2)
            and (?3 is null or e.paid = ?3)
            and e.eventDate between ?4 and ?5
            and (?6 = false or e.confirmedRequests < e.participantLimit)
            and e.state = 'PUBLISHED'
            """)
    List<Event> findAllPublishedEventsByFilterAndPeriod(String text, List<Long> categories, Boolean paid,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                        Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or (e.annotation ilike %?1% or e.description ilike %?1%))
            and (?2 is null or e.category.id in ?2)
            and (?3 is null or e.paid = ?3)
            and e.eventDate >= ?4
            and (?5 = false or e.confirmedRequests < e.participantLimit)
            and e.state = 'PUBLISHED'
            """)
    List<Event> findAllPublishedEventsByFilterAndRangeStart(String text, List<Long> categories, Boolean paid,
                                                            LocalDateTime rangeStart, Boolean onlyAvailable,
                                                            Pageable pageable);
}
