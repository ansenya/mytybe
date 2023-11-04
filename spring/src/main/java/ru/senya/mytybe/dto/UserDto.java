package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.ImageModel;

import java.util.Date;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String role;
    private String sex;
    private ImageModel pfp;
    private Integer age;
    private String country;
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
        return pfp.getPath();
    }
}