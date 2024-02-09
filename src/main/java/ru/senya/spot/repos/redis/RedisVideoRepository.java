package ru.senya.spot.repos.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.senya.spot.models.redis.RedisVideoModel;

@Repository

public interface RedisVideoRepository extends CrudRepository<RedisVideoModel, Long> {
}
