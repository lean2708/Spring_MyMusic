package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.ArtistBasic;
import mymusic.spring_mymusic.dto.request.ArtistRequest;
import mymusic.spring_mymusic.dto.response.ArtistResponse;
import mymusic.spring_mymusic.entity.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @Mapping(target = "songs", ignore = true)
    @Mapping(target = "albums", ignore = true)
    Artist toArtist(ArtistRequest request);

    @Mapping(target = "songs", ignore = true)
    @Mapping(target = "albums", ignore = true)
    ArtistResponse toArtistResponse(Artist artist);

    ArtistBasic toArtistBasic(Artist artist);

    @Mapping(target = "songs", ignore = true)
    @Mapping(target = "albums", ignore = true)
    Artist update(@MappingTarget Artist artist, ArtistRequest request);
}
