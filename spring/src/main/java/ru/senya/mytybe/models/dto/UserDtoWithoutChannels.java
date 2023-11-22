package ru.senya.mytybe.models.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.ImageModel;

import java.util.Date;

@Data
public class UserDtoWithoutChannels {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String role;
    private String sex;
    private ImageDto pfp;
    private Integer age;
    private String country;
//    private Set<ChannelDto> channels;
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
