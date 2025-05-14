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
public class PlaylistRequest {
    String title;
    String description;
    String imageURL;
    long follower;
    long listener;

    @NotEmpty(message = "songs không được để trống")
    Set<Long> songIds;
}
