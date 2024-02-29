package ru.senya.spot.repos.jpa;

import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.senya.spot.models.jpa.VideoModel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Where(clause = "deleted = false")
public interface VideoRepository extends JpaRepository<VideoModel, Long> {

    Page<VideoModel> findAllByDeletedIsFalse(@Nonnull    Pageable pageable);
    Page<VideoModel> findAllByChannelIdAndDeletedIsFalse(Long id, Pageable pageable);

    Optional<VideoModel> findByPath(String path);

    @Query(value = "SELECT * FROM videos", nativeQuery = true)
    List<VideoModel> getAll();

    @Query(value = "SELECT * FROM videos vm " +
            "WHERE vm.id IN (:specificIds)" +
            "and vm.stream_status=0 " +
            "and vm.deleted=false " +
            "ORDER BY vm.created DESC",
            nativeQuery = true)
    List<VideoModel> findInSpecificIds(List<Long> specificIds);

    @Query(value = "SELECT * FROM videos vm " +
            "WHERE vm.id NOT IN (:specificIds)" +
            "and vm.stream_status=0 " +
            "and vm.deleted=false " +
            "ORDER BY vm.created DESC",
            nativeQuery = true)
    List<VideoModel> findNotInSpecificIds(List<Long> specificIds);

    @Query(value = "select * from videos order by views DESC limit 100", nativeQuery = true)
    List<VideoModel> getTheMostPopular();
}
