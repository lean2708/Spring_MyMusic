package mymusic.spring_mymusic.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Name không được để trống")
    String name;
    @Email(message = "Email phải có định dạng hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email;
    LocalDate dob;

    @Size(min = 6, message = "Password phải từ 6 kí tự trở lên")
    @NotBlank(message = "Password không được để trống")
    String password;
    @Size(min = 6, message = "confirmPassword phải từ 6 kí tự trở lên")
    @NotBlank(message = "confirmPassword không được để trống")
    String confirmPassword;
}
