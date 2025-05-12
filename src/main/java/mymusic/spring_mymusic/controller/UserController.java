package mymusic.spring_mymusic.controller;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.response.*;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import mymusic.spring_mymusic.dto.basic.PlaylistBasic;
import mymusic.spring_mymusic.dto.request.UserRequest;
import mymusic.spring_mymusic.dto.request.UserUpdateRequest;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Validated
@Slf4j(topic = "USER-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserRequest request){
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(userService.create(request))
                .message("Create User")
                .build();
    }
    @GetMapping("/users/{id}")
    public ApiResponse<UserResponse> fetchById(@PathVariable long id){

        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .result(userService.fetchById(id))
                .message("Get User By Id")
                .build();
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                            @RequestParam(defaultValue = "1") int pageNo,
                                                            @RequestParam(defaultValue = "10") int pageSize,
                                                            @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(userService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Users With Pagination")
                .build();
    }

    @PutMapping(value = "/users/{id}")
    public ApiResponse<UserResponse> update(@PathVariable long id,@Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .result(userService.update(id, request))
                .message("Update User")
                .build();
    }
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> delete(@PathVariable long id){
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete User")
                .build();
    }

    @PostMapping("/users/saved-playlist/{playlistId}")
    public ApiResponse<PlaylistResponse> createSavedPlaylists(@PathVariable long playlistId){
        return ApiResponse.<PlaylistResponse>builder()
                .code(HttpStatus.OK.value())
                .result(userService.createSavedPlaylists(playlistId))
                .message("Save Playlist With User")
                .build();
    }
    @GetMapping("/users/saved-playlists")
    public ApiResponse<PageResponse<PlaylistResponse>> fetchSavedPlaylists(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                       @RequestParam(defaultValue = "1") int pageNo,
                                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                                   @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<PlaylistResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(userService.fetchSavedPlaylists(pageNo, pageSize, sortBy))
                .message("Fetch Playlist Save with User")
                .build();
    }
    @DeleteMapping("/users/saved-playlist/{playlistId}")
    public ApiResponse<Void> removeSavedPlaylist(@PathVariable long playlistId) {
        userService.removeSavedPlaylist( playlistId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Playlist Save From User")
                .build();
    }

}
