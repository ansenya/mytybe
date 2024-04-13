package ru.senya.spot.models.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

import static ru.senya.spot.MytybeApplication.MAIN_HOST;

@RedisHash("image")
@Data
public class RedisImageModel {
    @Id
    private Long id;

    private String path;

    private String type;
    private String vid_uuid;

    private Date created;

    private Date updated;

    private boolean deleted = false;

    public String getFalsePath() {
        return MAIN_HOST + "/api/static/img?fileName=" + path;
    }
}
