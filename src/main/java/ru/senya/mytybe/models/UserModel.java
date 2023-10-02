package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String name, surname;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel pfp;

    private Integer age;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CountryModel country;

    @OneToMany(mappedBy = "user")
    private Set<ChannelModel> channels;

    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<ChannelModel> subscriptions;

    @OneToMany(mappedBy = "user")
    private Set<CommentModel> comments;

    @ManyToMany
    @JoinTable(
            name = "videos_liked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> likedVideos;

    @ManyToMany
    @JoinTable(
            name = "videos_disliked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> dislikedVideos;

    @OneToMany(mappedBy = "owner")
    private Set<PlaylistModel> playlists;

    private boolean deleted = false;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;
}
