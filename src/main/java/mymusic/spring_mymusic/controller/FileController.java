package mymusic.spring_mymusic.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.constant.FileType;
import mymusic.spring_mymusic.dto.response.ApiResponse;
import mymusic.spring_mymusic.entity.FileEntity;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@Slf4j(topic = "FILE-CONTROLLER")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileEntity> uploadImage(@RequestParam("fileImage") MultipartFile file) throws IOException, FileException, FileException {
        return new ResponseEntity<>(fileService.uploadFile(file, FileType.IMAGE), HttpStatus.OK);
    }

    @PostMapping(value = "/upload/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileEntity> uploadVideo(@RequestParam("fileVideo") MultipartFile file) throws IOException, FileException {
        return new ResponseEntity<>(fileService.uploadFile(file, FileType.VIDEO), HttpStatus.OK);
    }



    @GetMapping("/all")
    public ResponseEntity<List<FileEntity>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }



    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) throws Exception {
        boolean isDeleted = fileService.deleteFile(id);
        if (isDeleted){
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("Xóa thành công file")
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message("File không tồn tại")
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
    }
}
