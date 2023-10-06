package ru.senya.mytybe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "channels")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer videosAmount = videos.size();

    private Integer followersAmount = 0;

    private boolean deleted = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private ImageModel pfp; //todo: change to chp

    @OneToMany(mappedBy = "channel")
    private Set<VideoModel> videos = new HashSet<>();

    @OneToMany(mappedBy = "channel")
    private Set<PostModel> posts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserModel user;

    @ManyToMany(mappedBy = "subscriptions")
    @JsonIgnore
    private Set<UserModel> followers = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
