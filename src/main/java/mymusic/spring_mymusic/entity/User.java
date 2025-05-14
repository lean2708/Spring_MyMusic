package mymusic.spring_mymusic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     String name;
     String email;
     @JsonIgnore
     String password;
     String imageURL;
     LocalDate dob;

    @CreationTimestamp
    LocalDate createdAt;
    @UpdateTimestamp
    LocalDate updatedAt;

    @Builder.Default
    @ManyToMany
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    Set<Playlist> createdPlaylists = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_saved_playlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "playlist_id")
    )
    Set<Playlist> savedPlaylists = new HashSet<>();

    boolean premiumStatus;
    LocalDate premiumExpiryDate;

}
