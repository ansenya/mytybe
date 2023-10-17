package ru.senya.mytybe.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.VideoModel;

public interface VideoRepository extends JpaRepository<VideoModel, Long> {
    Page<VideoModel> findAllByChannelId(Long id, Pageable pageable);
}
