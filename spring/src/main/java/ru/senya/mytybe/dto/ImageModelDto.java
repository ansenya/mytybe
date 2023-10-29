package ru.senya.mytybe.dto;


import lombok.Data;
import ru.senya.mytybe.models.*;

import java.util.Date;

import static ru.senya.mytybe.MytybeApplication.IP;
import static ru.senya.mytybe.MytybeApplication.PORT;

@Data
public class ImageModelDto {
//    private Long id;

    private String path;

    private ImageTypeDto type;

    private Date created;

    private Date updated;

//    private PostModel post;

//    private UserDto user;

//    private ChannelDto channel;

//    private VideoModel video;

    private boolean deleted = false;

    public String getPath() {
        return "http://" + IP + ":" + PORT + "/api/static/img?fileName=" + path;
    }

    public String getType() {
        return type.getType();
    }

    @Data
    public static class ImageTypeDto {
        Long id;

        private String type;
    }
}
