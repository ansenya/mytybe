package ru.senya.storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.senya.storage.models.UserModel;
import ru.senya.storage.repos.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserModel save(UserModel user) {
        return userRepository.save(user);
    }
}
