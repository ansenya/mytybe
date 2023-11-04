package ru.senya.mytybe.models.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
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

    private Integer videosAmount;

    private Integer followersAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private ImageModel chp;

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

    private boolean deleted = false;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;


    public Integer getFollowersAmount() {
        try {
            return followers.size();
        } catch (Exception e){
            return 0;
        }
    }

    public Integer getVideosAmount() {
        try {
            return videos.size();
        } catch (NullPointerException e){
            return 0;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
