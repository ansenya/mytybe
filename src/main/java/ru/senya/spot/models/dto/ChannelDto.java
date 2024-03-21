package ru.senya.spot.models.dto;

import lombok.Data;
import ru.senya.spot.models.jpa.UserModel;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChannelDto {
    private Long id;

    private String name;

    private Integer videosAmount;

    private boolean deleted = false;

    private ImageDto chp;
    private Set<UserModel> followers = new HashSet<>();

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

    private UserDtoWithoutChannels user;

    public String getChp() {
        return chp.getFalsePath();
    }

    private String getFollowers() {
        return "";
    }

    public Integer getFollowersAmount() {
        return followers.size();
    }

    //    private Set<UserModel> followers = new HashSet<>();
}
