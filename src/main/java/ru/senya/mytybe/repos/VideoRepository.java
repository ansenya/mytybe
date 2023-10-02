package ru.senya.mytybe.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.VideoModel;

public interface VideoRepository extends JpaRepository<VideoModel, Long> {
}
