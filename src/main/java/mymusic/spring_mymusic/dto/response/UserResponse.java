package mymusic.spring_mymusic.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
     long id;
     String name;
     String email;
     String imageURL;

     LocalDate dob;
     Set<PlaylistResponse> createdPlaylists;
     @JsonInclude(JsonInclude.Include.NON_NULL)
     List<Long> savedPlaylistId;


      LocalDate createdAt;

      LocalDate updatedAt;

     Set<RoleResponse> roles;

     boolean premiumStatus;

     @JsonInclude(JsonInclude.Include.NON_NULL)
     LocalDate premiumExpiryDate;
}
