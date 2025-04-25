package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.AlbumBasic;
import mymusic.spring_mymusic.dto.request.AlbumRequest;
import mymusic.spring_mymusic.dto.response.AlbumResponse;
import mymusic.spring_mymusic.entity.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    Album toAlbum(AlbumRequest request);

    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    AlbumResponse toAlbumResponse(Album album);


    AlbumBasic toAlbumBasic(Album album);

    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "songs", ignore = true)
    Album update(@MappingTarget Album  album, AlbumRequest request);
}
