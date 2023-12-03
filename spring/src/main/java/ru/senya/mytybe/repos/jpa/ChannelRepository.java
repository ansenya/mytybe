package ru.senya.mytybe.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.jpa.ChannelModel;

public interface ChannelRepository extends JpaRepository<ChannelModel, Long> {

    Page<ChannelModel> getAllByUserId(Long id, Pageable pageable);
}