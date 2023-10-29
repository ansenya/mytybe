package ru.senya.mytybe.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.ImageModel;

public interface ImagesRepository extends JpaRepository<ImageModel, Long> {
}
