package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import mymusic.spring_mymusic.dto.request.PlaylistRequest;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Validated
@Slf4j(topic = "PLAYLIST-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping(value = "/playlists")
    public ApiResponse<PlaylistResponse> create(@Valid @RequestBody PlaylistRequest request){
        return ApiResponse.<PlaylistResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(playlistService.create(request))
                .message("Create Playlist")
                .build();
    }

    @GetMapping(value = "/playlists/{id}")
    public ApiResponse<PlaylistResponse> fetchById(@PathVariable long id){
        return ApiResponse.<PlaylistResponse>builder()
                .code(HttpStatus.OK.value())
                .result(playlistService.fetchById(id))
                .message("Fetch Playlist By Id")
                .build();
    }

    @GetMapping("/playlists")
    public ApiResponse<PageResponse<PlaylistResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                  @RequestParam(defaultValue = "1") int pageNo,
                                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                                  @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<PlaylistResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(playlistService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Playlists With Pagination")
                .build();
    }


    @Operation(summary = "Search playlists based on attributes with pagination",
            description = "Giá trị của search: field~value hoặc field>value hoặc field<value")
    @GetMapping("/playlists/search")
    public ApiResponse<PageResponse<PlaylistResponse>> searchPlaylists(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                 @RequestParam(defaultValue = "1") int pageNo,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(required = false) String sortBy,
                                                                 @RequestParam(required = false) List<String> search){
        return ApiResponse.<PageResponse<PlaylistResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(playlistService.searchPlaylists(pageNo, pageSize, sortBy, search))
                .message("Search playlists based on attributes with pagination")
                .build();
    }

    @PutMapping(value = "/playlists/{id}")
    public ApiResponse<PlaylistResponse> update(@PathVariable long id,@Valid @RequestBody PlaylistRequest request) {
        return ApiResponse.<PlaylistResponse>builder()
                .code(HttpStatus.OK.value())
                .result(playlistService.update(id, request))
                .message("Update Playlist")
                .build();
    }
    @DeleteMapping("/playlists/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        playlistService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Playlist")
                .build();
    }

    @DeleteMapping("/playlists/{playlistId}/songs/{songId}")
    public ApiResponse<Void> removeSong(@PathVariable long playlistId, @PathVariable long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Song removed from Playlist")
                .build();
    }
}
