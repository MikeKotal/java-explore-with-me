package ru.practicum.explorewithme.ewm.server.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import ru.practicum.explorewithme.ewm.server.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User as u
            where (?1 is null or u.id in ?1)
            """)
    List<User> findUserByIds(List<Long> ids, Pageable pageable);

    Optional<UserShortDto> findUserById(Long id);
}
