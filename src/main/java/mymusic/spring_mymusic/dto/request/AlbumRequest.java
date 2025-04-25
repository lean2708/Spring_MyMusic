package mymusic.spring_mymusic.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumRequest {
    @NotBlank(message = "Name không được để trống")
    String name;
    String description;
    String imageURL;
    long follower;

    Set<Long> artists;

    Set<Long> songs;

}
