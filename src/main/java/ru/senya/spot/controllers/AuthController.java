package ru.senya.spot.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.configs.security.TokenService;
import ru.senya.spot.models.dto.UserDto;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.models.jpa.TokenModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.UserRequest;
import ru.senya.spot.repos.jpa.ImagesRepository;
import ru.senya.spot.repos.jpa.UserRepository;

import java.util.HashSet;
import java.util.UUID;


@RestController
@RequestMapping("auth")
public class AuthController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ImagesRepository imagesRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    public AuthController(TokenService tokenService, UserRepository userRepository, ImagesRepository imagesRepository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
        this.passwordEncoder = passwordEncoder;
        modelMapper = new ModelMapper();
    }

    @GetMapping("/login")
    public ResponseEntity<?> token(Authentication authentication) {
        if (!authentication.isAuthenticated()) return ResponseEntity.status(401).build();

        return ResponseEntity.ok().body(new Object[]{modelMapper.map(userRepository.findByUsername(authentication.getName()), UserDto.class),
                new TokenModel(tokenService.generateToken(authentication))});
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        var validationResponse = validateUserRequest(userRequest);
        if (validationResponse != null) {
            return validationResponse;
        }

        UserModel user = new UserModel();

        ImageModel pfp = ImageModel.builder()
                .type("pfp")
                .user(user)
                .build();

        createUser(userRequest, user, pfp);

        user = userRepository.save(user);

        imagesRepository.save(pfp);

        return ResponseEntity.ok().body(new Object[]{modelMapper.map(user, UserDto.class),
                new TokenModel(tokenService.generateToken(user.getUsername()))});
    }

    private ResponseEntity<?> validateUserRequest(UserRequest userRequest) {
        if (userRequest == null || userRequest.isEmpty())
            return ResponseEntity.badRequest().body("Empty body");
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(409).build();
        }
        if (userRequest.getPassword().length() < 7) {
            return ResponseEntity.status(406).body("Password must contain than 7 or more letters");
        }
        return null;
    }

    private void createUser(UserRequest userRequest, UserModel user, ImageModel pfp) {
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setSurname(userRequest.getSex());
        user.setAge(userRequest.getAge());
        user.setCountry("ru");
        user.setSex(userRequest.getSex());
        user.setPfp(pfp);
        user.setPublicStreamLink(String.valueOf(UUID.randomUUID()));
        user.setLastViewed(new HashSet<>());
    }
}
