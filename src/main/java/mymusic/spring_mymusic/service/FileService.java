package mymusic.spring_mymusic.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mymusic.spring_mymusic.constant.FileType;
import mymusic.spring_mymusic.entity.FileEntity;
import mymusic.spring_mymusic.exception.FileException;
import mymusic.spring_mymusic.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j(topic = "FILE-SERVICE")
@RequiredArgsConstructor
@Service
public class FileService {

    private final Cloudinary cloudinary;
    private final FileRepository fileRepository;

    private static final List<String> IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final List<String> VIDEO_TYPES = Arrays.asList(
            // Video types
            "video/mp4",
            "video/avi",
            "video/mov",
            "video/mkv",

            // Audio types
            "audio/mpeg",   // .mp3
            "audio/wav",    // .wav
            "audio/x-wav",
            "audio/ogg",    // .ogg
            "audio/webm",   // .webm
            "audio/mp4",    // .m4a
            "audio/x-aac",  // .aac
            "audio/flac"    // .flac
    );

    @Value("${cloud.folder-image}")
    private String folderImage;

    @Value("${cloud.max-size-image}")
    private String maxSizeImage;

    @Value("${cloud.folder-video}")
    private String folderVideo;

    @Value("${cloud.max-size-video}")
    private String maxSizeVideo;



    private long parseSize(String size) {
        size = size.toUpperCase();
        return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
    }


    public FileEntity uploadFile(MultipartFile file, FileType type) throws IOException, FileException {
        if (file == null || file.isEmpty()) {
            throw new FileException("File trống. Không thể lưu trữ file");
        }
        String folder = determineUploadFolder(file, type);

        Map<String, Object> options = ObjectUtils.asMap("folder", folder);

        if (type == FileType.VIDEO) {
            options.put("resource_type", "video");
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        FileEntity fileEntity = FileEntity.builder()
                .id(uploadResult.get("public_id").toString())
                .fileName(file.getOriginalFilename())
                .type(type.name())
                .url(uploadResult.get("url").toString())
                .build();

        return fileRepository.save(fileEntity);
    }

    private String determineUploadFolder(MultipartFile file, FileType type) throws FileException {
        switch (type){
            case IMAGE -> {
                validateFile(file, IMAGE_TYPES, maxSizeImage, "Ảnh");
                return folderImage;
            }
            case VIDEO -> {
                validateFile(file, VIDEO_TYPES, maxSizeVideo, "Video");
                return folderVideo;
            }
            default -> throw new FileException("Loại file không hỗ trợ");
        }
    }

    private void validateFile(MultipartFile file, List<String> validTypes, String maxSize, String fileType) throws FileException {
        if (!validTypes.contains(file.getContentType())) {
            throw new FileException("File " + file.getOriginalFilename() + " không hợp lệ. Định dạng file không được hỗ trợ.");
        }
        if (file.getSize() > parseSize(maxSize)) {
            throw new FileException(fileType + " quá lớn! Chỉ được tối đa " + maxSize + ".");
        }
    }


    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public Boolean deleteFile(String publicId) throws Exception {
        FileEntity fileEntity = fileRepository.findById(publicId)
                .orElseThrow(()-> new FileException("File không tồn tại trong hệ thống"));

        String resourceType = switch (FileType.valueOf(fileEntity.getType())) {
            case IMAGE -> "image";
            case VIDEO -> "video";
        };

        Map<String, Object> options = ObjectUtils.asMap("resource_type", resourceType);
        cloudinary.uploader().destroy(publicId, options);

        fileRepository.delete(fileEntity);
        return true;
    }

}

