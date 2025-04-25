package mymusic.spring_mymusic.dto.response;

import mymusic.spring_mymusic.dto.basic.ArtistBasic;
import mymusic.spring_mymusic.dto.basic.SongBasic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumResponse {
    long id;
    String name;
    String description;
    int totalTracks;
    long follower;
    String imageURL;
    double totalHours;


     LocalDate createdAt;
     LocalDate updatedAt;

    Set<ArtistBasic> artists;

    Set<SongBasic> songs;
}
