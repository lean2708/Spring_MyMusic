package mymusic.spring_mymusic.dto.basic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistBasic {
    long id;
    String title;
    String description;
    long follower;
    String creator;
    String imageURL;
    double totalHours;
}
