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
public class ArtistRequest {
    @NotBlank(message = "Name không được để trống")
    String name;
    long follower;
    String imageURL;

    Set<Long> songs;

    Set<Long> albums;
}
