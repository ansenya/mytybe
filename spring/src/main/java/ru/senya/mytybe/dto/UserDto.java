package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.*;

import java.util.Date;
import java.util.List;
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
    private CountryModel country;
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
}