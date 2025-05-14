package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.AlbumBasic;
import mymusic.spring_mymusic.dto.request.AlbumRequest;
import mymusic.spring_mymusic.dto.response.AlbumResponse;
import mymusic.spring_mymusic.entity.Album;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Album update(@MappingTarget Album  album, AlbumRequest request);
}
