package ru.senya.spot.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.UserModel;

import java.util.List;

public interface ChannelRepository extends JpaRepository<ChannelModel, Long> {

    Page<ChannelModel> findAllByUserId(Long id, Pageable pageable);

    Page<ChannelModel> findAllByFollowersContaining(UserModel userModel, Pageable pageable);

    List<ChannelModel> findAllByNameContaining(String query);
}
