package ru.senya.mytybe.models.dto;

import lombok.Data;

@Data
public class ChannelDto {
    private Long id;

    private String name;

    private Integer videosAmount;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private ImageDto chp;

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

    private UserDtoWithoutChannels user;

    public String getChp() {
        return chp.getFalsePath();
    }

    //    private Set<UserModel> followers = new HashSet<>();
}
