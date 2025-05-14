package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.SongBasic;
import mymusic.spring_mymusic.dto.request.SongRequest;
import mymusic.spring_mymusic.dto.response.SongResponse;
import mymusic.spring_mymusic.entity.Song;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SongMapper {
    @Mapping(target = "album", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "artists", ignore = true)
    Song toSong(SongRequest request);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "artists", ignore = true)
    SongResponse toSongResponse(Song song);

    SongBasic toSongBasic(Song song);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Song update(@MappingTarget Song song, SongRequest request);
}
