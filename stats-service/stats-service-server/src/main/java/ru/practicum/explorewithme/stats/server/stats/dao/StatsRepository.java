package ru.practicum.explorewithme.stats.server.stats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.stats.server.stats.model.EndpointHit;
import ru.practicum.explorewithme.stats.server.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("""
            select new ru.practicum.explorewithme.stats.server.stats.model.ViewStats(eh.app, eh.uri, count(distinct eh.ip))
            from EndpointHit as eh
            where eh.timestamp between ?1 and ?2
            and (?3 is null or eh.uri in ?3)
            group by eh.app, eh.uri
            order by count(distinct eh.ip) desc
            """)
    List<ViewStats> getUniqStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            select new ru.practicum.explorewithme.stats.server.stats.model.ViewStats(eh.app, eh.uri, count(eh.ip))
            from EndpointHit as eh
            where eh.timestamp between ?1 and ?2
            and (?3 is null or eh.uri in ?3)
            group by eh.app, eh.uri
            order by count(eh.ip) desc
            """)
    List<ViewStats> getAllStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
