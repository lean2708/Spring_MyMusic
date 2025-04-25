package mymusic.spring_mymusic.mapper;

import mymusic.spring_mymusic.dto.basic.PlaylistBasic;
import mymusic.spring_mymusic.dto.request.PlaylistRequest;
import mymusic.spring_mymusic.dto.response.PlaylistResponse;
import mymusic.spring_mymusic.entity.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "songs", ignore = true)
    Playlist toPlaylist(PlaylistRequest request);
    @Mapping(target = "songs", ignore = true)
    PlaylistResponse toPlaylistResponse(Playlist playlist);

    PlaylistBasic toPlaylistBasic(Playlist playlist);
    @Mapping(target = "songs", ignore = true)
    Playlist update(@MappingTarget Playlist playlist, PlaylistRequest request);
}
