package ru.senya.mytybe.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.CommentModel;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {
}
