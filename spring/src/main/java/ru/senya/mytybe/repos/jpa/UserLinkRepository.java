package ru.senya.mytybe.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.jpa.UserLinkEntity;

import java.util.Optional;

@Repository
public interface UserLinkRepository extends JpaRepository<UserLinkEntity, Long> {
    Optional<UserLinkEntity> findByLink(String link);
}
