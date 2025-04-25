package mymusic.spring_mymusic.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.dto.response.SearchResponse;
import mymusic.spring_mymusic.entity.Album;
import mymusic.spring_mymusic.entity.Artist;
import mymusic.spring_mymusic.entity.Playlist;
import mymusic.spring_mymusic.entity.Song;
import mymusic.spring_mymusic.repository.AlbumRepository;
import mymusic.spring_mymusic.repository.ArtistRepository;
import mymusic.spring_mymusic.repository.PlaylistRepository;
import mymusic.spring_mymusic.repository.SongRepository;
import mymusic.spring_mymusic.specification.AlbumSpecification;
import mymusic.spring_mymusic.specification.ArtistSpecification;
import mymusic.spring_mymusic.specification.PlaylistSpecification;
import mymusic.spring_mymusic.specification.SongSpecification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchByPriorityService {
    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final PlaylistService playlistService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final SongService songService;

    public PageResponse<SearchResponse> searchAll(String name, int pageNo, int pageSize) {
        List<SearchResponse> allResults = new ArrayList<>();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // Tìm kiếm Playlist
        Page<Playlist> playlistPage = playlistRepository.findAll(
                Specification.where(PlaylistSpecification.hasNameContainingIgnoreCase(name))
                        .and(PlaylistSpecification.sortByNamePriority(name)), pageable);

        allResults.addAll(playlistPage.getContent().stream()
                .map(item -> new SearchResponse("Playlist", playlistService.convertPlaylistResponse(item), calculatePriority(item.getTitle(), name)))
                .collect(Collectors.toList()));

        // Tìm kiếm Album
        Page<Album> albumPage = albumRepository.findAll(
                Specification.where(AlbumSpecification.hasNameContainingIgnoreCase(name))
                        .and(AlbumSpecification.sortByNamePriority(name)), pageable);

        allResults.addAll(albumPage.getContent().stream()
                .map(item -> new SearchResponse("Album", albumService.convertAlbumResponse(item), calculatePriority(item.getName(), name)))
                .collect(Collectors.toList()));

        // Tìm kiếm Artist
        Page<Artist> artistPage = artistRepository.findAll(
                Specification.where(ArtistSpecification.hasNameContainingIgnoreCase(name))
                        .and(ArtistSpecification.sortByNamePriority(name)), pageable);

        allResults.addAll(artistPage.getContent().stream()
                .map(item -> new SearchResponse("Artist", artistService.convertArtistToResponse(item), calculatePriority(item.getName(), name)))
                .collect(Collectors.toList()));

        // Tìm kiếm Song
        Page<Song> songPage = songRepository.findAll(
                Specification.where(SongSpecification.hasNameContainingIgnoreCase(name))
                        .and(SongSpecification.sortByNamePriority(name)), pageable);

        allResults.addAll(songPage.getContent().stream()
                .map(item -> new SearchResponse("Song", songService.convertSongResponse(item), calculatePriority(item.getName(), name)))
                .collect(Collectors.toList()));

        // Sắp xếp tất cả kết quả theo mức độ ưu tiên
        allResults.sort(Comparator.comparingInt(SearchResponse::getPriority));

        return PageResponse.<SearchResponse>builder()
                .page(pageNo)
                .size(allResults.size())
                .totalPages((int) Math.ceil((double) allResults.size() / pageSize))
                .totalItems(allResults.size())
                .items(allResults)
                .build();
    }

    public int calculatePriority(String objectName, String searchName) {
        int position = objectName.toLowerCase().indexOf(searchName.toLowerCase());

        if (position == -1) {
            return 100000;
        }

        if (position == 0) {
            return 1;
        }
        return position + 1;
    }

}
