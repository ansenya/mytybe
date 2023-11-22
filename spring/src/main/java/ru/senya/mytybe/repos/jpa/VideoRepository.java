package ru.senya.mytybe.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.senya.mytybe.models.jpa.VideoModel;

import java.util.List;

public interface

VideoRepository extends JpaRepository<VideoModel, Long> {
    Page<VideoModel> findAllByChannelId(Long id, Pageable pageable);

    @Query(value = "SELECT * FROM videos", nativeQuery = true)
    List<VideoModel> getAll();


    @Query(value = "SELECT * FROM videos vm " +
            "WHERE vm.id IN (:specificIds)" +
            "ORDER BY vm.created DESC",
            nativeQuery = true)
    List<VideoModel> findInSpecificIds(List<Long> specificIds);

    @Query(value = "SELECT * FROM videos vm " +
            "WHERE vm.id NOT IN (:specificIds)" +
            "ORDER BY vm.created ASC",
            nativeQuery = true)
    List<VideoModel> findNotInSpecificIds(List<Long> specificIds);

    @Query(value = "select * from videos order by views DESC limit 100", nativeQuery = true)
    List<VideoModel> getTheMostPopular();
}
