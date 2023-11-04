package ru.senya.mytybe.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.jpa.TagModel;

public interface TagRepository extends JpaRepository<TagModel, Long> {
    TagModel findByEnTag(String tag);
    TagModel findByRuTag(String tag);
}
