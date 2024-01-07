package ru.senya.mytybe.models.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("channel")
@Data
public class RedisChannelModel {

    @Id
    private Long id;

    private String name;

    private Integer videosAmount;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private RedisImageModel chp;

    private RedisUserModel user;

    public String getChp() {
        return chp.getFalsePath();
    }
}
