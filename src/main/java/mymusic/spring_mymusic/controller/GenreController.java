package mymusic.spring_mymusic.controller;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.request.GenreRequest;
import mymusic.spring_mymusic.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mymusic.spring_mymusic.service.GenreService;
import mymusic.spring_mymusic.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Slf4j(topic = "GENRE-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class GenreController {

    private final GenreService genreService;
    private final SongService songService;

    @PostMapping("/genres")
    public ApiResponse<GenreResponse> create(@Valid @RequestBody GenreRequest request){
        return ApiResponse.<GenreResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(genreService.create(request))
                .message("Create Genre")
                .build();
    }

    @GetMapping("/genres/{id}")
    public ApiResponse<GenreResponse> fetchById(@PathVariable long id){
        return ApiResponse.<GenreResponse>builder()
                .code(HttpStatus.OK.value())
                .result(genreService.fetchById(id))
                .message("Fetch Genre By Id")
                .build();
    }

    @GetMapping("/genres")
    public ApiResponse<PageResponse<GenreResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                              @RequestParam(defaultValue = "1") int pageNo,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<GenreResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(genreService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Genres With Pagination")
                .build();
    }

    @PutMapping("/genres/{id}")
    public ApiResponse<GenreResponse> update(@PathVariable long id,@Valid @RequestBody GenreRequest request){
        return ApiResponse.<GenreResponse>builder()
                .code(HttpStatus.OK.value())
                .result(genreService.update(id, request))
                .message("Update Genre")
                .build();
    }

    @DeleteMapping("/genres/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        genreService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Genre")
                .build();
    }

    @GetMapping("/genres/{genreId}/songs")
    public ApiResponse<List<SongResponse>> fetchSongsByGenre(@PathVariable long genreId) {

        return ApiResponse.<List<SongResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(songService.fetchSongsByGenre(genreId))
                .message("Fetched Songs by Genre")
                .build();
    }
}
