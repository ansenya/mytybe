package ru.senya.mytybe.repos.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.senya.mytybe.models.service.Constant;

import java.util.Optional;

@Repository
public interface ConstantsRepository extends JpaRepository<Constant, Long> {
    Optional<Constant> findByK(String k);
}
