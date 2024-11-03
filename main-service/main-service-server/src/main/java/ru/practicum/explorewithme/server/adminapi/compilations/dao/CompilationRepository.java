package ru.practicum.explorewithme.server.adminapi.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.server.adminapi.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
