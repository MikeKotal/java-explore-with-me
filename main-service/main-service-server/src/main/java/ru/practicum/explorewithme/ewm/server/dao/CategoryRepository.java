package ru.practicum.explorewithme.ewm.server.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.ewm.server.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
