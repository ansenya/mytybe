package ru.senya.mytybe.models.dto;

import lombok.Data;

import java.util.Date;
import java.util.Set;

import static ru.senya.mytybe.MytybeApplication.IP;
import static ru.senya.mytybe.MytybeApplication.PORT;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String role;
    private String sex;
    private ImageDto pfp;
    private Integer age;
    private String country;
//    private String streamLink;
    private Set<ChannelDtoWithoutUser> channels;
//    private List<VideoModel> lastViewed;

    //    private Set<UserModel> subscriptions;
//    private Set<CommentModel> comments;
//    private Set<VideoModel> likedVideos;
//    private Set<VideoModel> dislikedVideos;
//    private Set<PlaylistModel> playlists;
    private boolean deleted;
    private boolean locked;
    private Date created;
    private Date updated;

    public String getPfp() {
        return pfp.getFalsePath();
    }
}