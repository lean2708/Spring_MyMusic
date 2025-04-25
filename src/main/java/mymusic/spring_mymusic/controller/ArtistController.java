package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import mymusic.spring_mymusic.dto.request.ArtistRequest;
import mymusic.spring_mymusic.dto.response.*;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Validated
@Slf4j(topic = "ARTIST-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ArtistController {

private final ArtistService artistService;

    @PostMapping(value = "/artists")
    public ApiResponse<ArtistResponse> create(@Valid @RequestBody ArtistRequest request) throws FileException, IOException, SAXException {
        return ApiResponse.<ArtistResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(artistService.create(request))
                .message("Create Artist")
                .build();
    }

    @GetMapping("/artists/{id}")
    public ApiResponse<ArtistResponse> fetchById(@PathVariable long id){
        return ApiResponse.<ArtistResponse>builder()
                .code(HttpStatus.OK.value())
                .result(artistService.fetchById(id))
                .message("Fetch Artist By Id")
                .build();
    }

    @GetMapping("/artists")
    public ApiResponse<PageResponse<ArtistResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                             @RequestParam(defaultValue = "1") int pageNo,
                                                             @RequestParam(defaultValue = "10") int pageSize,
                                                             @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<ArtistResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(artistService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Artists With Pagination")
                .build();
    }

    @Operation(summary = "Search artists based on attributes with pagination",
            description = "Giá trị của search: field~value hoặc field>value hoặc field<value")
    @GetMapping("/artists/search")
    public ApiResponse<PageResponse<ArtistResponse>> searchAlbums(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                 @RequestParam(defaultValue = "1") int pageNo,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @RequestParam(required = false) String sortBy,
                                                                 @RequestParam(required = false) List<String> search){
        return ApiResponse.<PageResponse<ArtistResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(artistService.searchArtists(pageNo, pageSize, sortBy, search))
                .message("Search artists based on attributes with pagination")
                .build();
    }

    @PutMapping(value = "/artists/{id}")
    public ApiResponse<ArtistResponse> update(@PathVariable long id,@Valid @RequestBody ArtistRequest request){
        return ApiResponse.<ArtistResponse>builder()
                .code(HttpStatus.OK.value())
                .result(artistService.update(id, request))
                .message("Update Artist")
                .build();
    }
    @DeleteMapping("/artists/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        artistService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Artist")
                .build();
    }
}
