package ru.practicum.explorewithme.server.privateapi.location.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.server.privateapi.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
