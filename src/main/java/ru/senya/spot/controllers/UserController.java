package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.controllers.storage.FileUploadService;
import ru.senya.spot.models.dto.ChannelDto;
import ru.senya.spot.models.dto.UserDto;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.UserRepository;

import java.util.Objects;
import java.util.UUID;


@RestController
@RequestMapping("users")
public class UserController {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    ModelMapper modelMapper = new ModelMapper();
    private final FileUploadService storageApiUtils;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    public UserController(UserRepository userRepository, ChannelRepository channelRepository, FileUploadService storageApiUtils) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.storageApiUtils = storageApiUtils;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is null");
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editOne(@PathVariable Long id,
                                     @RequestParam(name = "pfp", required = false) MultipartFile pfp) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is null");
        }
        if (pfp == null || pfp.isEmpty()) {
            return ResponseEntity.badRequest().body("pfp is null");
        }
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        String uuid = String.valueOf(UUID.randomUUID());
        String imageContentType = Objects.requireNonNull(pfp.getContentType()).split("/")[1];

        try {
            storageApiUtils.sendToStorage(uuid, imageContentType, "img", pfp);
        } catch (Exception e) {
            logger.error("user edit error", e);
            return ResponseEntity.status(500).build();
        }
        user.getPfp().setPath(uuid + "." + imageContentType);

        userRepository.save(user);

        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @GetMapping
    public ResponseEntity<?> getALlUsers(@RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNum,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort) {

        Sort.Direction direction;
        if (Objects.equals(sort, "desc")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        var page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
        Page<UserModel> userPage = userRepository.findAll(page);
        Page<UserDto> userDtoPage = userPage.map(user -> modelMapper.map(user, UserDto.class));
        return ResponseEntity.ok(userDtoPage);
    }

    @GetMapping("followings")
    public ResponseEntity<?> getFollowings(Authentication authentication,
                                           @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNum,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                           @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort) {
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            var user = userRepository.findByUsername(authentication.getName());
            Sort.Direction direction;
            if (Objects.equals(sort, "desc")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            return ResponseEntity.ok(channelRepository
                    .findAllByFollowersContaining(user, PageRequest.of(pageNum, pageSize, Sort.by(direction, "created")))
                    .map(channel -> modelMapper.map(channel, ChannelDto.class)));
        } else {
            return ResponseEntity.status(401).build();
        }

    }
}
