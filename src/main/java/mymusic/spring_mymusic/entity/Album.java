package mymusic.spring_mymusic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "tbl_album")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     String name;
     String description;
     int totalTracks;
    long follower;
    String imageURL;
    double totalHours;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "album_artist",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    Set<Artist> artists = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "album", fetch = FetchType.EAGER)
    Set<Song> songs = new HashSet<>();

    @CreationTimestamp
    LocalDate createdAt;
    @UpdateTimestamp
    LocalDate updatedAt;
}
