package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.ImageModel;

@Data
public class ChannelDto {
    private Long id;

    private String name;

    private Integer videosAmount;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private ImageModel chp;

//    private Set<VideoModel> videos = new HashSet<>();

//    private Set<PostModel> posts = new HashSet<>();

    private UserDtoWithoutChannels user;

    public String getChp() {
        return chp.getPath();
    }

    //    private Set<UserModel> followers = new HashSet<>();
}
