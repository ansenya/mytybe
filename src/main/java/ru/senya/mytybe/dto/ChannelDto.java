package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.ImageModel;
import ru.senya.mytybe.models.VideoModel;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChannelDto {
    private Long id;

    private String name;

    private Integer videosAmount = 0;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private ImageModel pfp; //todo: change to chp

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

//    private UserModel user;

//    private Set<UserModel> followers = new HashSet<>();
}
