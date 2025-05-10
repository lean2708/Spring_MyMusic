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
public class SongRequest {
    @NotBlank(message = "Name không được để trống")
     String name;
     String description;
    String imageURL;
    String fileSongURL;
    double duration;
    long listener;

    Long album;

    Set<Long> artists;

    Long genre;

}
