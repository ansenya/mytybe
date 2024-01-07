package ru.senya.mytybe.models.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("user")
@Data
public class RedisUserModel {
    @Id
    private Long id;

    private String name;

    private Integer videosAmount;

    private Integer followersAmount = 0;

    private boolean deleted = false;

    private RedisImageModel pfp;
}
