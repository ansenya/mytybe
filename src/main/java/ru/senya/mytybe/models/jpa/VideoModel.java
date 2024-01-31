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
import java.util.Set;


@Data
@Entity
@Table(name = "videos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Long duration;

    private Long views;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel thumbnail;

    @OneToMany(mappedBy = "video")
    private Set<CommentModel> comments;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @JsonIgnore
    private ChannelModel channel;

//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private CategoryModel category;

    private String path;

    @ManyToMany
    @JoinTable(
            name = "videos_tags",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagModel> tags = new HashSet<>();

    @ManyToMany(mappedBy = "videos")
    private Set<PlaylistModel> playlists;

    @ManyToMany(mappedBy = "likedVideos")
    private Set<UserModel> likedByUser;

    @ManyToMany(mappedBy = "dislikedVideos")
    private Set<UserModel> dislikedByUser;

    @ManyToMany(mappedBy = "recommendedVideos")
    private Set<UserModel> recommendations;

    private String vid_uuid;

    private boolean processed = false;

    private boolean small = false;

    private boolean explicit = false;

    private boolean deleted = false;

    // private boolean stream = false;

    @Column(columnDefinition = "integer default 0")
    private Integer streamStatus = 0;
    // 0 - not a stream
    // 1 - a stream
    // 2 - finished stream

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    public Long getViews() {
        if (views == null) {
            return 0L;
        }
        return views;
    }
}
