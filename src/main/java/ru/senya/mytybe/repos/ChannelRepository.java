package ru.senya.mytybe.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.ChannelModel;

public interface ChannelRepository extends JpaRepository<ChannelModel, Long> {
}
