package ru.senya.spot.models.dto;

import lombok.Data;
import ru.senya.spot.models.jpa.UserModel;

import java.util.Set;

@Data
public class ChannelDtoWithoutUser {
    private Long id;

    private String name;
    private String description;


    private Integer videosAmount;

    private boolean deleted = false;
    private boolean followedByThisUser;
    private ImageDto bigBlackCock;


    private ImageDto chp;

    private Set<UserModel> followers;

    public String getChp() {
        return chp.getFalsePath();
    }

    public String getBigBlackCock() {
        return bigBlackCock.getFalsePath();
    }


    private String getFollowers() {
        return "";
    }

    public Integer getFollowersAmount() {
        return followers.size();
    }
}
