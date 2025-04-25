package mymusic.spring_mymusic.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import mymusic.spring_mymusic.dto.request.PermissionRequest;
import mymusic.spring_mymusic.service.PermissionService;

import java.util.List;

@Validated
@Slf4j(topic = "PERMISSION-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    public ApiResponse<PermissionResponse> create(@Valid @RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(permissionService.create(request))
                .message("Create Permission")
                .build();
    }

    @GetMapping("/permissions/{id}")
    public ApiResponse<PermissionResponse> fetchById(@PathVariable Long id){
        return ApiResponse.<PermissionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Fetch Permission By Id")
                .result(permissionService.fetchById(id))
                .build();
    }

    @GetMapping("/permissions")
    public ApiResponse<PageResponse<PermissionResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                             @RequestParam(defaultValue = "1") int pageNo,
                                                             @RequestParam(defaultValue = "10") int pageSize,
                                                             @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<PermissionResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(permissionService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Permissions With Pagination")
                .build();
    }

    @DeleteMapping("/permissions/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        permissionService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Permission")
                .build();
    }
}
