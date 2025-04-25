package mymusic.spring_mymusic.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import mymusic.spring_mymusic.dto.basic.AlbumBasic;
import mymusic.spring_mymusic.dto.basic.ArtistBasic;
import mymusic.spring_mymusic.entity.Genre;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongResponse {
    long id;
    String name;
    String description;
    double duration;
    long listener;
    String imageURL;
    String fileSongURL;

     LocalDate createdAt;
     LocalDate updatedAt;

    AlbumBasic album;

    Set<ArtistBasic> artists;

    Genre genre;
}
