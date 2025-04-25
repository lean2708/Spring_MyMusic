package mymusic.spring_mymusic.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "tbl_artist")
public class Artist{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
     String name;
     long follower;
    String imageURL;

    @Builder.Default
    @ManyToMany(mappedBy = "artists", fetch = FetchType.EAGER)
    Set<Song> songs = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "artists")
    Set<Album> albums = new HashSet<>();
}
