package ru.senya.mytybe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CountryModel country;

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

    @OneToMany(mappedBy = "owner")
    private Set<PlaylistModel> playlists;

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
}
