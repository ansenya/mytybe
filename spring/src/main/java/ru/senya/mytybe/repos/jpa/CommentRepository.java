package ru.senya.mytybe.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.jpa.CommentModel;
import ru.senya.mytybe.models.jpa.VideoModel;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {

    Page<CommentModel> findAllByVideo(Pageable pageable, VideoModel videoModel);
    CommentModel findById(CommentModel commentModel);
}
