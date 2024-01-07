package ru.senya.mytybe.models.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

import static ru.senya.mytybe.MytybeApplication.IP;
import static ru.senya.mytybe.MytybeApplication.PORT;

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
        return "http://" + IP + ":" + PORT + "/api/static/img?fileName=" + path;
    }
}
