package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.response.*;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import mymusic.spring_mymusic.dto.request.SongRequest;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Validated
@Slf4j(topic = "SONG-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SongController {
    private final SongService songService;

    @PostMapping(value = "/songs")
    public ApiResponse<SongResponse> create(@Valid  @RequestBody SongRequest songRequest) {
        return ApiResponse.<SongResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(songService.create(songRequest))
                .message("Create Song")
                .build();
    }

    @GetMapping("songs/{id}")
    public ApiResponse<SongResponse> fetchById(@PathVariable long id){
        return ApiResponse.<SongResponse>builder()
                .code(HttpStatus.OK.value())
                .result(songService.fetchById(id))
                .message("Fetch Song By Id")
                .build();
    }

    @GetMapping("/songs")
    public ApiResponse<PageResponse<SongResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<SongResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(songService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Songs With Pagination")
                .build();
    }

    @Operation(summary = "Search songs based on attributes with pagination",
            description = "Giá trị của search: field~value hoặc field>value hoặc field<value")
    @GetMapping("/songs/search")
    public ApiResponse<PageResponse<SongResponse>> searchSongs(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                                       @RequestParam(required = false) String sortBy,
                                                                       @RequestParam(required = false) List<String> search){
        return ApiResponse.<PageResponse<SongResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(songService.searchSongs(pageNo, pageSize, sortBy, search))
                .message("Search playlists based on attributes with pagination")
                .build();
    }

    @PutMapping(value = "/songs/{id}")
    public ApiResponse<SongResponse> update(@PathVariable long id, @Valid @RequestBody SongRequest request) {

        return ApiResponse.<SongResponse>builder()
                .code(HttpStatus.OK.value())
                .result(songService.update(id, request))
                .message("Update Song")
                .build();
    }
    @DeleteMapping("/songs/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        songService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Song")
                .build();
    }


}
