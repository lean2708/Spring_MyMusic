package mymusic.spring_mymusic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mymusic.spring_mymusic.entity.Permission;
import mymusic.spring_mymusic.entity.Role;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    boolean existsByName(String name);

    List<Permission> findAllByNameIn(Set<String> nameSet);

}
