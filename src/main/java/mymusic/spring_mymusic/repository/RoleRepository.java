package mymusic.spring_mymusic.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mymusic.spring_mymusic.entity.Role;
import mymusic.spring_mymusic.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(String name);

    List<Role> findAllByIdIn(Set<Long> idSet);

    Optional<Role> findByName(String name);

    Optional<Object> findById(long id, Sort sort, Limit limit);

}
