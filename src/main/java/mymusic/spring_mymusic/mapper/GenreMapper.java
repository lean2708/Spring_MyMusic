package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.request.GenreRequest;
import mymusic.spring_mymusic.dto.response.GenreResponse;
import mymusic.spring_mymusic.entity.Genre;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GenreMapper {

    Genre toGenre(GenreRequest request);

    GenreResponse toGenreResponse(Genre genre);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Genre update(@MappingTarget Genre genre,GenreRequest request);
}
