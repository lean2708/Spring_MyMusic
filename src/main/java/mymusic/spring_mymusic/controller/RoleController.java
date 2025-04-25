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
import mymusic.spring_mymusic.dto.request.RoleRequest;
import mymusic.spring_mymusic.service.RoleService;

import java.util.List;

@Validated
@Slf4j(topic = "ROLE-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/roles")
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(roleService.create(request))
                .message("Create Role")
                .build();
    }

    @GetMapping("/roles/{id}")
    public ApiResponse<RoleResponse> fetchById(@PathVariable long id){
        return ApiResponse.<RoleResponse>builder()
                .code(HttpStatus.OK.value())
                .result(roleService.fetchById(id))
                .message("Fetch role By Id")
                .build();
    }

    @GetMapping("/roles")
    public ApiResponse<PageResponse<RoleResponse>> fetchAll(@Min(value = 1, message = "pageNo phải lớn hơn 0")
                                                                @RequestParam(defaultValue = "1") int pageNo,
                                                                @RequestParam(defaultValue = "10") int pageSize,
                                                                @RequestParam(required = false) String sortBy){
        return ApiResponse.<PageResponse<RoleResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(roleService.fetchAll(pageNo, pageSize, sortBy))
                .message("Fetch All Roles With Pagination")
                .build();
    }

    @DeleteMapping("/roles/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        roleService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Delete Role")
                .build();
    }
}
