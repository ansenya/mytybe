package ru.senya.mytybe.repos.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.redis.RedisVideoModel;

@Repository

public interface RedisVideoRepository extends CrudRepository<RedisVideoModel, Long> {
}
