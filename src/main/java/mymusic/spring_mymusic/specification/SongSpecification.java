package mymusic.spring_mymusic.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import mymusic.spring_mymusic.entity.Album;
import mymusic.spring_mymusic.entity.Song;

public class SongSpecification {
    public static Specification<Song> hasNameContainingIgnoreCase(String name) {
        return new Specification<Song>() {
            @Override
            public Predicate toPredicate(Root<Song> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
        };
    }

    public static Specification<Song> sortByNamePriority(String name) {
        return (root, query, criteriaBuilder) -> {
            Expression<Integer> position = criteriaBuilder.locate(criteriaBuilder.lower(root.get("name")), name.toLowerCase());

            Predicate containsKeyword = criteriaBuilder.greaterThan(position, 0);

            query.orderBy(criteriaBuilder.asc(position),
                    criteriaBuilder.desc(root.get("listener")));

            return criteriaBuilder.and(containsKeyword);
        };
    }
}
