package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.request.GenreRequest;
import mymusic.spring_mymusic.dto.response.GenreResponse;
import mymusic.spring_mymusic.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre toGenre(GenreRequest request);

    GenreResponse toGenreResponse(Genre genre);

    Genre update(@MappingTarget Genre genre,GenreRequest request);
}
