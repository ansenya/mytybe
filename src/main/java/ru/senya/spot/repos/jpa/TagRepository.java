package ru.senya.spot.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.spot.models.jpa.TagModel;

public interface TagRepository extends JpaRepository<TagModel, Long> {
    TagModel findByEnTag(String tag);

    TagModel findByRuTag(String tag);
}
