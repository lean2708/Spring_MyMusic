package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.PlaylistBasic;
import mymusic.spring_mymusic.dto.request.PlaylistRequest;
import mymusic.spring_mymusic.dto.response.PlaylistResponse;
import mymusic.spring_mymusic.entity.Playlist;
import mymusic.spring_mymusic.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PlaylistMapper {

    @Mapping(target = "songs", ignore = true)
    Playlist toPlaylist(PlaylistRequest request);
    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "songs", ignore = true)
    PlaylistResponse toPlaylistResponse(Playlist playlist);

    PlaylistBasic toPlaylistBasic(Playlist playlist);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "songs", ignore = true)
    Playlist update(@MappingTarget Playlist playlist, PlaylistRequest request);

    default String map(User user) {
        return user != null ? user.getName() : null;
    }
}
