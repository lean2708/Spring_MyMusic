package mymusic.spring_mymusic.repository;

import mymusic.spring_mymusic.entity.Role;
import mymusic.spring_mymusic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllByRoles_Name(String roleName);


    @Query("SELECT u FROM User u JOIN u.savedPlaylists p WHERE p.id = :playlistId")
    List<User> findAllBySavedPlaylists_Id(@Param("playlistId") Long playlistId);


}
