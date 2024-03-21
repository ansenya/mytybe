package ru.senya.spot.repos.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.spot.models.jpa.PlaylistModel;
import ru.senya.spot.models.jpa.UserModel;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistModel, Long> {

    Optional<PlaylistModel> findByTypeAndOwner(String type, UserModel owner);
}
