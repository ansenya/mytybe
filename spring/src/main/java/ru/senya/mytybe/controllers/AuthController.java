package ru.senya.mytybe.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.dto.UserDto;
import ru.senya.mytybe.security.TokenService;
import ru.senya.mytybe.models.TokenModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.UserRequest;
import ru.senya.mytybe.repos.UserRepository;


@RequestMapping("/u/auth")
@RestController
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    final TokenService tokenService;
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    ModelMapper modelMapper = new ModelMapper();

    public AuthController(TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public ResponseEntity<?> token(Authentication authentication, HttpServletRequest request) {
        logger.info("GET request from '{}' to '/auth/token'", request.getRemoteAddr());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");

        if (!authentication.isAuthenticated()) return ResponseEntity.status(401).headers(headers).build();


        return ResponseEntity.ok().headers(headers).body(new Object[]{modelMapper.map(userRepository.findByUsername(authentication.getName()), UserDto.class),
                new TokenModel(tokenService.generateToken(authentication))});
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {

        if (userRequest == null || userRequest.isEmpty())
            return ResponseEntity.badRequest().body("Empty body");
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(409).build();
        }
        if (userRequest.getPassword().length() < 7) {
            return ResponseEntity.status(406).body("Password must contain than 7 or more letters");
        }

        UserModel user = new UserModel();

        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setSurname(userRequest.getSex());
        user.setAge(userRequest.getAge());
//        user.setCountry(userRequest.getCountry());

        user = userRepository.save(user);

        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }
}
