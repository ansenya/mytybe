package ru.senya.mytybe.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.TagModel;

public interface TagRepository extends JpaRepository<TagModel, Long> {
    TagModel findByTag(String tag);
}
