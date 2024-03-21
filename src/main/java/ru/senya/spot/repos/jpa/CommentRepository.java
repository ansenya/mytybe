package ru.senya.spot.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.spot.models.jpa.CommentModel;
import ru.senya.spot.models.jpa.VideoModel;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {

    Page<CommentModel> findAllByVideoAndDeletedFalse(Pageable pageable, VideoModel videoModel);
    Optional<CommentModel> findByIdAndDeletedFalse(Long id);
}
