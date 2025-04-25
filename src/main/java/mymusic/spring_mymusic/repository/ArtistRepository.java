package mymusic.spring_mymusic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import mymusic.spring_mymusic.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArtistRepository extends JpaRepository<Artist,Long>, JpaSpecificationExecutor<Artist> {
    boolean existsByName(String name);

    List<Artist> findAllByIdIn(Set<Long> idSet);



}
