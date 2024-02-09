package ru.senya.spot.repos.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import ru.senya.spot.models.jpa.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Override
    @NonNull
    @Query("SELECT u FROM UserModel u WHERE u.deleted = false and u.locked = false")
    Page<UserModel> findAll(Pageable page);

    UserModel findByUsername(String username);

    boolean existsByUsername(String username);
//    Page<UserModel> getALl(PageRequest page);
}
