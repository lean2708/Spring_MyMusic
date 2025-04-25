package mymusic.spring_mymusic.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import mymusic.spring_mymusic.entity.Artist;
import mymusic.spring_mymusic.entity.Genre;
import mymusic.spring_mymusic.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song,Long>, JpaSpecificationExecutor<Song> {
    boolean existsByName(String name);

    List<Song> findAllByIdIn(Set<Long> idSet);
    List<Song> findAllByGenre(Genre genre);
}
