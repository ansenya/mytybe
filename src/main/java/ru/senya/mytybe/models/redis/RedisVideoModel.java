package ru.senya.mytybe.models.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;


@RedisHash("video")
@Data
public class RedisVideoModel {

    @Id
    private Long id;

    private String name;

    private String description;

    private Long duration;

    private Long views;

    private RedisImageModel thumbnail;

    private RedisChannelModel channel;

//    private CategoryModel category; //todo

    private String path;

    private boolean processed;

    private boolean explicit;

    private boolean deleted;

    private boolean stream;

    private Date created;

    private Date updated;

//    public String getThumbnail() {
//        return thumbnail.getFalsePath();
//    }
}
