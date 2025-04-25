package mymusic.spring_mymusic.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import mymusic.spring_mymusic.dto.response.ApiResponse;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalException {
    // handle all exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception ex) {
        ApiResponse apiResponse = new ApiResponse();

        log.error("Đã xảy ra lỗi: {}", ex.getMessage());

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler({AppException.class})
    public ResponseEntity<ApiResponse> handlingSpotifyException(AppException exception){
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode = exception.getErrorCode();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(apiResponse);
    }

    // Lỗi khi Validation thuộc tính
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> validationError(MethodArgumentNotValidException ex){
        BindingResult result = ex.getBindingResult();

        final List<FieldError> fieldErrors = result.getFieldErrors();

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());

        List<String> messages = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            messages.add(fieldError.getDefaultMessage());
        }
        String errorMessages = String.join(". ", messages);
        apiResponse.setMessage(errorMessages);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // Lỗi khi phân quyền
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse> handingAccessDeniedException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse response = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(response);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        ApiResponse apiResponse = new ApiResponse();
        String parameterName = exception.getParameterName();

        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage("Tham số bắt buộc : " + parameterName);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiResponse);
    }


    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiResponse);
    }

    // Lỗi file
    @ExceptionHandler(value = {FileException.class})
    public ResponseEntity<ApiResponse<Object>> handleFileUploadException(Exception ex) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
