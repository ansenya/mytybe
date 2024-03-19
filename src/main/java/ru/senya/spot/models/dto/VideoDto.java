package ru.senya.spot.models.dto;

import jakarta.persistence.ManyToMany;
import lombok.Data;
import ru.senya.spot.models.jpa.TagModel;
import ru.senya.spot.models.jpa.UserModel;

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
    private Set<UserModel> likedByUser;
    private Set<UserModel> dislikedByUser;
    private boolean likedByThisUser;
    private boolean dislikedByThisUser;
    private ChannelDtoWithoutUser channel;
    private boolean explicit;
    private boolean processedQualities;
    private boolean processedAi;
    private Integer streamStatus;
    private Date created;
    private Date updated;

    public Long getLikes() {
        return (long) likedByUser.size();
    }

    public Long getDislikes() {
        return (long) dislikedByUser.size();
    }

    public String getThumbnail() {
        try {
            return thumbnail.getFalsePath();
        } catch (Exception e) {
            return "http://" + STORAGE_HOST + "/img?filename=" + "def_th.png";
        }
    }

    public String getPath() {
        if (streamStatus == 1) {
            return "http://" + STORAGE_HOST + "/hls/" + path + ".m3u8";
        } else if (streamStatus == 2) {
            return "http://" + STORAGE_HOST + "/static/vid?fileName=" + path + ".flv";
        } else {
            return "http://" + STORAGE_HOST + "/vid?filename=" + path + ".mp4";
        }
    }

    public void setQualities(String qualities) {
        this.qualities = qualities.split(",");
    }

    private String getLikedByUser() {
        return "";
    }

    private String getDislikedByUser() {
        return "";
    }
}