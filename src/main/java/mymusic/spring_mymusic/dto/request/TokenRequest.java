package mymusic.spring_mymusic.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequest {
    @NotBlank(message = "Token không được để trống")
    String accessToken;
}
