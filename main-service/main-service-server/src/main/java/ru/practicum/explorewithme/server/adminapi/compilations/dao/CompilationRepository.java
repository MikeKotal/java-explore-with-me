package ru.practicum.explorewithme.server.adminapi.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.server.adminapi.compilations.model.Compilation;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findCompilationsByPinned(Boolean pinned, Pageable pageable);
}
