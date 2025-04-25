package mymusic.spring_mymusic.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import mymusic.spring_mymusic.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mymusic.spring_mymusic.entity.Artist;

import java.util.List;
import java.util.Set;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long>, JpaSpecificationExecutor<Album> {
    boolean existsByName(String name);
    List<Album> findAllByIdIn(Set<Long> idSet);

}
