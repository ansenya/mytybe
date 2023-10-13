package ru.senya.mytybe.dto;

import lombok.Data;
import ru.senya.mytybe.models.CountryModel;
import ru.senya.mytybe.models.ImageModel;

import java.util.Date;

@Data
public class UserDtoWithoutChannels {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String role;
    private String sex;
    private ImageModel pfp;
    private Integer age;
    private CountryModel country;
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
}
