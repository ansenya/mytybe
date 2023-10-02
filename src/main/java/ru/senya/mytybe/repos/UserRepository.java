package ru.senya.mytybe.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.senya.mytybe.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
//    Page<UserModel> getALl(PageRequest page);
}
