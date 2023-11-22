package ru.senya.mytybe.models.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.CategoryModel;

import java.util.Date;

@Data
public class VideoDto {

    private Long id;

    private String name;

    private String description;

    private Long duration;

    private Long views = 0L;

    private ImageDto thumbnail;

    private ChannelDto channel;

    private CategoryModel category;

    private String path;

//    private LinkedList<CommentDto> comments;

//    private Set<TagModel> tags;

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
        return thumbnail.getFalsePath();
    }

//    public List<String> getTags() {
//        List<String> good_tags = new LinkedList<>();
//        for (TagModel tagModel : tags) {
//            good_tags.add(tagModel.getEnTag());
//            good_tags.add(tagModel.getRuTag());
//        }
//        return good_tags;
//    }
}
