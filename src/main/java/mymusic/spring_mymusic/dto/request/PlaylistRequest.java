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
public class PlaylistRequest {
    @NotBlank(message = "Title không được để trống")
    String title;
    String description;
    String imageURL;
    long follower;
    long listener;

    Set<Long> songs;
}
