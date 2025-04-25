package mymusic.spring_mymusic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    private String type;
    private Object response;
    private int priority;
}
