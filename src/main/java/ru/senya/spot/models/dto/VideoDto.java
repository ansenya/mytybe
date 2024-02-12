package ru.senya.spot.models.dto;

import lombok.Data;
import ru.senya.spot.models.jpa.TagModel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static ru.senya.spot.MytybeApplication.*;

@Data
public class VideoDto {

    private Long id;
    private String name;
    private String description;
    private Long duration;
    private Long views = 0L;
    private String[] qualities;
    private String path;
    private ImageDto thumbnail;
    //    private CategoryModel category;
    private ChannelDtoWithoutUser channel;
    private boolean explicit;
    private Integer streamStatus;
    private Date created;
    private Date updated;

    private Set<TagModel> tags;


    public String getThumbnail() {
        try {
            return thumbnail.getFalsePath();
        } catch (Exception e) {
            return "http://" + MAIN_IP + ":" + STORAGE_PORT + "/api/img?filename=" + "def_th.png";
        }
    }

    public String getPath() {
        if (streamStatus == 1) {
            return "http://" + STORAGE_IP + ":" + HLS_PORT + "/hls/" + path + ".m3u8";
        } else if (streamStatus == 2) {
            return "http://" + STORAGE_IP + ":" + MAIN_PORT + "/api/static/vid?fileName=" + path + ".flv";
        } else {
            return "http://" + STORAGE_IP + ":" + STORAGE_PORT + "/api/vid?filename=" + path + ".mp4";
        }
    }

    public void setQualities(String qualities) {
        this.qualities = qualities.split(" ");
    }

    public List<String> getTags() {
        List<String> good_tags = new LinkedList<>();
        for (TagModel tagModel : tags) {
            good_tags.add(tagModel.getEnTag());
            good_tags.add(tagModel.getRuTag());
        }
        return good_tags;
    }
}

/*
    private LinkedList<CommentDto> comments;


    private Set<PlaylistModel> playlists;

    private Set<UserDto> likedByUser;

    private Set<UserModel> dislikedByUser;

    private String vid_uuid;

    private boolean processed;



 */
