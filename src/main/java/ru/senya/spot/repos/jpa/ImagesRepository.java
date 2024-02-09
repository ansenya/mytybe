package ru.senya.spot.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.spot.models.jpa.ImageModel;

public interface ImagesRepository extends JpaRepository<ImageModel, Long> {
}
