package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.*;

import java.util.Date;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String sex;
    private ImageModel pfp;
    private Integer age;
    private CountryModel country;
    private Set<ChannelModel> channels;
    private Set<UserModel> subscriptions;
    private Set<CommentModel> comments;
    private Set<VideoModel> likedVideos;
    private Set<VideoModel> dislikedVideos;
    private Set<PlaylistModel> playlists;
    private Date created;
    private Date updated;
    private boolean deleted;
}