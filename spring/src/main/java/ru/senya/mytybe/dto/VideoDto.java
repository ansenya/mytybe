package ru.senya.mytybe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.senya.mytybe.models.*;

import java.util.Date;
import java.util.Set;

@Data
public class VideoDto {

    private Long id;

    private String name;

    private String description;

    private Long duration;

    private Long views = 0L;

    private ImageModel thumbnail;


    private ChannelDto channel;

    private CategoryModel category;

    private String path;

    private Set<CommentModel> comments;

//    private Set<TagModel> tags;

    private Set<PlaylistModel> playlists;

    private Set<UserModel> likedByUser;

    private Set<UserModel> dislikedByUser;

    private String vid_uuid;

    private boolean processed;

    private boolean explicit;

    private boolean deleted;

    private boolean stream;

    private Date created;

    private Date updated;
}
