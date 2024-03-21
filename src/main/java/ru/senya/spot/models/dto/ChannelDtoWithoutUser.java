package ru.senya.spot.models.dto;

import lombok.Data;
import ru.senya.spot.models.jpa.UserModel;

import java.util.Set;

@Data
public class ChannelDtoWithoutUser {
    private Long id;

    private String name;

    private Integer videosAmount;

    private boolean deleted = false;

    private ImageDto chp;

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

//    private UserDtoWithoutChannels user;

    private Set<UserModel> followers;

    public String getChp() {
        return chp.getFalsePath();
    }

    private String getFollowers() {
        return "";
    }

    public Integer getFollowersAmount() {
        return followers.size();
    }
}
