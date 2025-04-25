package mymusic.spring_mymusic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyCodeRequest {
    @Email(message = "Email phải có định dạng hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email;
    @NotBlank(message = "VerificationCode không được để trống")
    @Size(min = 6, max = 6, message = "VerificationCode phải có đúng 6 ký tự")
    String verificationCode;
}
