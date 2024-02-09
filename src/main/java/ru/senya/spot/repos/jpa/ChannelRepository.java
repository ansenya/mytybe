package ru.senya.spot.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.spot.models.jpa.ChannelModel;

public interface ChannelRepository extends JpaRepository<ChannelModel, Long> {

    Page<ChannelModel> getAllByUserId(Long id, Pageable pageable);
}
