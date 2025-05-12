package mymusic.spring_mymusic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import mymusic.spring_mymusic.entity.Artist;
import mymusic.spring_mymusic.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,Long>, JpaSpecificationExecutor<Playlist> {
    boolean existsByTitle(String title);


    List<Playlist> findAllByIdIn(List<Long> listId);

    Page<Playlist> findAllByIdIn(List<Long> ids, Pageable pageable);

}
