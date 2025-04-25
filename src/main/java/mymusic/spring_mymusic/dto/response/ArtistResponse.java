package mymusic.spring_mymusic.dto.response;

import mymusic.spring_mymusic.dto.basic.AlbumBasic;
import mymusic.spring_mymusic.dto.basic.SongBasic;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistResponse {
    long id;
    String name;
    long follower;
    String imageURL;

    Set<SongBasic> songs;

    Set<AlbumBasic> albums;
}
