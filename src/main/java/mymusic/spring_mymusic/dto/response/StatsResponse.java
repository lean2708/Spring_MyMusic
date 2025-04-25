package mymusic.spring_mymusic.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {
    long totalUsers;
    long totalAlbums;
    long totalArtists;
    long totalSongs;
    long totalPlaylists;
}
