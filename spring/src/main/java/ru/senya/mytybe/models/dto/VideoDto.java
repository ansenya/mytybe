package ru.senya.mytybe.models.dto;

import lombok.Data;
import ru.senya.mytybe.models.jpa.CategoryModel;
import ru.senya.mytybe.models.jpa.ImageModel;

import java.util.Date;

import static ru.senya.mytybe.MytybeApplication.*;

@Data
public class VideoDto {

    private Long id;

    private String name;

    private String description;

    private Long duration;

    private Long views = 0L;

    private String path;

    private ImageDto thumbnail;

    private ChannelDto channel;

    private CategoryModel category;

//    private LinkedList<CommentDto> comments;

//    private Set<TagModel> tags;

//    private Set<PlaylistModel> playlists;

//    private Set<UserDto> likedByUser;

//    private Set<UserModel> dislikedByUser;

//    private String vid_uuid;

//    private boolean processed;

    private boolean explicit;

//    private boolean deleted;

    private boolean stream;

    private Date created;

    private Date updated;

    public String getThumbnail() {
        try {
            return thumbnail.getFalsePath();
        } catch (Exception e){
            return "http://" + IP + ":" + PORT + "/api/static/img?fileName=" + "def.jpg";
        }
    }

    public String getPath() {
        if (stream) {
            return "http://" + IP + ":" + HLS_PORT + "/hls/" + path + ".m3u8";
        } else {
            return "http://" + IP + ":" + PORT + "/api/static/vid?fileName=" + path + ".mp4";
        }
    }


//    public List<String> getTags() {
//        List<String> good_tags = new LinkedList<>();
//        for (TagModel tagModel : tags) {
//            good_tags.add(tagModel.getEnTag());
//            good_tags.add(tagModel.getRuTag());
//        }
//        return good_tags;
//    }
}
