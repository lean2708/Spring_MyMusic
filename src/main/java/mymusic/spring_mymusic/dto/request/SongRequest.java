package mymusic.spring_mymusic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "artists không được để trống")
    Set<Long> artists;

    Long genre;

}
