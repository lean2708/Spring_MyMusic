package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import mymusic.spring_mymusic.dto.request.AlbumRequest;
import mymusic.spring_mymusic.dto.response.AlbumResponse;
import mymusic.spring_mymusic.dto.response.ApiResponse;
import mymusic.spring_mymusic.dto.response.PageResponse;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Validated
@Slf4j(topic = "ALBUM-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AlbumController {

private final AlbumService albumService;

    @PostMapping(value = "/albums")
    public ApiResponse<AlbumResponse> create(@Valid @RequestBody AlbumRequest request) throws FileException, IOException, SAXException {
        return ApiResponse.<AlbumResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(albumService.create(request))
                .message("Create Album")
                .build();
    }

    @GetMapping("/albums/{id}")
    public ApiResponse<AlbumResponse> fetchById(@PathVariable long id){
        return ApiResponse.<AlbumResponse>builder()
                .code(HttpStatus.OK.value())
                .result(albumService.fetchById(id))
                .message("Fetch Album By Id")
                .build();
    }


    @GetMapping("/albums")
    public ApiResponse<PageResponse<AlbumResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                               @RequestParam(defaultValue = "1") int pageNo,
                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                               @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<AlbumResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(albumService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Albums With Pagination")
                .build();
    }

    @Operation(summary = "Search albums based on attributes with pagination",
            description = "Giá trị của search: field~value hoặc field>value hoặc field<value")
    @GetMapping("/albums/search")
    public ApiResponse<PageResponse<AlbumResponse>> searchAlbums(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                    @RequestParam(defaultValue = "1") int pageNo,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(required = false) String sortBy,
                                                                    @RequestParam(required = false) List<String> search){
        return ApiResponse.<PageResponse<AlbumResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(albumService.searchAlbums(pageNo, pageSize, sortBy, search))
                .message("Search albums based on attributes with pagination")
                .build();
    }

    @PutMapping(value = "/albums/{id}")
    public ApiResponse<AlbumResponse> update(@PathVariable long id,@Valid @RequestBody AlbumRequest request){
        return ApiResponse.<AlbumResponse>builder()
                .code(HttpStatus.OK.value())
                .result(albumService.update(id, request))
                .message("Update Album")
                .build();
    }

    @DeleteMapping("/albums/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        albumService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Album")
                .build();
    }

    @DeleteMapping("/albums/{albumId}/songs/{songId}")
    public ApiResponse<Void> deleteSongFromAlbum(@PathVariable long albumId,
                                                  @PathVariable long songId) {
        albumService.deleteSongFromAlbum(albumId, songId);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Deleted song from album")
                .build();
    }
}
