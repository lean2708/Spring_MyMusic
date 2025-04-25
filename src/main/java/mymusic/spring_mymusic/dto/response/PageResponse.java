package mymusic.spring_mymusic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalPages;
    private long totalItems;
    private List<T> items;
}
