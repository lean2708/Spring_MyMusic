package mymusic.spring_mymusic.repository.criteria;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    String key;
    String operation;   // : > < ~
    Object value;
}
