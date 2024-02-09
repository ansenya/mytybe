package ru.senya.spot.models.jpa;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "playlists")

public class PlaylistModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel owner;

    @ManyToMany
    @JoinTable(name = "videos_playlists",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> videos;
}
