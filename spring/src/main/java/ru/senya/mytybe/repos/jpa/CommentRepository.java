package ru.senya.mytybe.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.jpa.CommentModel;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, Long> {
}
