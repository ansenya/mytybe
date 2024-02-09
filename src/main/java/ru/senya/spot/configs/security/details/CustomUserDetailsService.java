package ru.senya.spot.configs.security.details;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.repos.jpa.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUserDetails(user);
    }
}
