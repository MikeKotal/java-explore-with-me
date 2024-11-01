package ru.practicum.explorewithme.server.adminapi.users.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.server.adminapi.users.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User as u
            where (?1 is null or u.id in ?1)
            """)
    List<User> findUserByIds(List<Long> ids, Pageable pageable);

    Optional<UserShortDto> findUserById(Long id);
}
