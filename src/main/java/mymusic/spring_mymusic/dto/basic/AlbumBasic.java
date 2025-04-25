package mymusic.spring_mymusic.dto.basic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumBasic {
    long id;
    String name;
    String description;
    int totalTracks;
    long follower;
    double totalHours;
}
