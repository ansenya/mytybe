package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "videos")
public class VideoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;


    private Long duration;

    private Long views = 0L;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private ImageModel thumbnail;

    @OneToMany(mappedBy = "video")
    private Set<CommentModel> comments;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelModel channel;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryModel category;

    private String path;

    @ManyToMany
    @JoinTable(
            name = "videos_tags",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagModel> tags;

    @ManyToMany(mappedBy = "videos")
    private Set<PlaylistModel> playlists;

    @ManyToMany(mappedBy = "likedVideos")
    private Set<UserModel> likedByUser;

    @ManyToMany(mappedBy = "dislikedVideos")
    private Set<UserModel> dislikedByUser;



    private boolean explicit = false;

    private boolean deleted = false;

    private boolean stream = false;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;
}
