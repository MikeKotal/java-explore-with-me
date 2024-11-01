package ru.practicum.explorewithme.server.adminapi.categories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.server.adminapi.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
