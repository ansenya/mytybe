package ru.senya.mytybe.models.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.redis.core.RedisHash;

import java.util.*;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String name, surname;

    private String sex = "none";

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel pfp;

    private Integer age;

    private String role = "user";

    private String country;

    @OneToMany(mappedBy = "user")
    private Set<ChannelModel> channels = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id"))
    private Set<ChannelModel> subscriptions;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<CommentModel> comments;

    @ManyToMany
    @JoinTable(
            name = "videos_liked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    @JsonIgnore
    private Set<VideoModel> likedVideos;

    @ManyToMany
    @JoinTable(
            name = "videos_disliked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> dislikedVideos;

    @ManyToMany
    @JoinTable(
            name = "videos_watched",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    @JsonIgnore
    private Set<VideoModel> lastViewed;

    @OneToMany(mappedBy = "owner")
    private Set<PlaylistModel> playlists;

    @ManyToMany
    @JoinTable(
            name = "recommendations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id"))
    private Set<VideoModel> recommendedVideos;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    private boolean deleted = false;
    private boolean locked = false;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return deleted == userModel.deleted && locked == userModel.locked && Objects.equals(id, userModel.id) && Objects.equals(username, userModel.username) && Objects.equals(password, userModel.password) && Objects.equals(name, userModel.name) && Objects.equals(surname, userModel.surname) && Objects.equals(sex, userModel.sex) && Objects.equals(pfp, userModel.pfp) && Objects.equals(age, userModel.age) && Objects.equals(role, userModel.role) && Objects.equals(country, userModel.country) && Objects.equals(channels, userModel.channels) && Objects.equals(subscriptions, userModel.subscriptions) && Objects.equals(comments, userModel.comments) && Objects.equals(likedVideos, userModel.likedVideos) && Objects.equals(dislikedVideos, userModel.dislikedVideos) && Objects.equals(lastViewed, userModel.lastViewed) && Objects.equals(playlists, userModel.playlists) && Objects.equals(recommendedVideos, userModel.recommendedVideos) && Objects.equals(created, userModel.created) && Objects.equals(updated, userModel.updated);
    }
}
