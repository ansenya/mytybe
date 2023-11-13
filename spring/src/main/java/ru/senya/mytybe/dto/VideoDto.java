package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.CategoryModel;
import ru.senya.mytybe.models.jpa.ImageModel;
import ru.senya.mytybe.models.jpa.TagModel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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

//    private LinkedList<CommentDto> comments;

    private Set<TagModel> tags;

//    private Set<PlaylistModel> playlists;

//    private Set<UserDto> likedByUser;

//    private Set<UserModel> dislikedByUser;

//    private String vid_uuid;

    private boolean processed;

    private boolean explicit;

    private boolean deleted;

    private boolean stream;

    private Date created;

    private Date updated;

    public String getThumbnail() {
        return thumbnail.getPath();
    }

    private List<String> getTags() {
        List<String> good_tags = new LinkedList<>();
        for (TagModel tagModel : tags) {
            good_tags.add(tagModel.getEnTag());
            good_tags.add(tagModel.getRuTag());
        }
        return good_tags;
    }
}
