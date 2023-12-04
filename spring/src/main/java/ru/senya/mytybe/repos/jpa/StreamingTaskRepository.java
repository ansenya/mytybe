package ru.senya.mytybe.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.jpa.StreamingTaskModel;

import java.util.Optional;

@Repository
public interface StreamingTaskRepository extends JpaRepository<StreamingTaskModel, Long> {

    Optional<StreamingTaskModel> findByLink(String link);
}
