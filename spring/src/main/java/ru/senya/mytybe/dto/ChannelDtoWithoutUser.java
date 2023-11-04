package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.ImageModel;

@Data
public class ChannelDtoWithoutUser {
    private Long id;

    private String name;

    private Integer videosAmount;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private ImageModel pfp; //todo: change to chp

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

//    private UserDtoWithoutChannels user;

//    private Set<UserModel> followers = new HashSet<>();
}
