package mymusic.spring_mymusic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreResponse {
    long id;
    String keyGenre;
    String name;

}
