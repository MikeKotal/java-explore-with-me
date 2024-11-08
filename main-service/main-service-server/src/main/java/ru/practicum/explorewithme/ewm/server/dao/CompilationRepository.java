package ru.practicum.explorewithme.ewm.server.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewm.server.models.Compilation;

import java.util.List;

import org.springframework.data.domain.Pageable;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findCompilationsByPinned(Boolean pinned, Pageable pageable);
}
