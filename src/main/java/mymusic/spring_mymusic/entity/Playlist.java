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
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "tbl_playlist")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     String title;
     String description;
    int totalTracks;
    long follower;

    String imageURL;
    long listener;
    double totalHours;

    @CreationTimestamp
    LocalDate createdAt;
    @UpdateTimestamp
    LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    User creator;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    Set<Song> songs = new HashSet<>();

}
