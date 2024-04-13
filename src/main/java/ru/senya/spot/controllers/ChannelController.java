package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.controllers.storage.FileUploadService;
import ru.senya.spot.models.dto.ChannelDto;
import ru.senya.spot.models.dto.UserDtoWithoutChannels;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.ImagesRepository;
import ru.senya.spot.repos.jpa.UserRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;


@RequestMapping("channels")
@RestController
public class ChannelController {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ImagesRepository imagesRepository;
    private final FileUploadService storageApiUtils;
    private final ModelMapper modelMapper;

    public ChannelController(ChannelRepository channelRepository, UserRepository userRepository, ImagesRepository imagesRepository, FileUploadService storageApiUtils) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
        this.storageApiUtils = storageApiUtils;
        modelMapper = new ModelMapper();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOne(Authentication authentication,
                                    @PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is empty");
        }

        var channel = channelRepository.findById(id).orElse(null);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }

        var channelDto = modelMapper.map(channel, ChannelDto.class);
        channelDto.setUser(modelMapper.map(channel.getUser(), UserDtoWithoutChannels.class));
        if (authentication != null) {
            channelDto.setFollowedByThisUser(channel.getFollowers().contains(userRepository.findByUsername(authentication.getName())));
        }

        return ResponseEntity.ok(channelDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateOne(Authentication authentication,
                                       @PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is empty");
        }

        var channel = channelRepository.findById(id).orElse(null);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }


        var channelDto = modelMapper.map(channel, ChannelDto.class);
        channelDto.setUser(modelMapper.map(channel.getUser(), UserDtoWithoutChannels.class));
        if (authentication != null) {
            channelDto.setFollowedByThisUser(channel.getFollowers().contains(userRepository.findByUsername(authentication.getName())));
        }

        return ResponseEntity.ok(channelDto);
    }

    @GetMapping
    public ResponseEntity<?> getALl(@RequestParam(value = "page", required = false) Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort,
                                    @RequestParam(value = "uid", required = false, defaultValue = "-1") Long uid) {
        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        if (uid == -1) {
            Sort.Direction direction;

            if (Objects.equals(sort, "desc")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
            Page<ChannelModel> channelPage = channelRepository.findAll(page);
            Page<ChannelDto> channelDtoPage = channelPage.map(user -> modelMapper.map(user, ChannelDto.class));
            return ResponseEntity.ok(channelDtoPage);
        } else {
            Sort.Direction direction;

            if (Objects.equals(sort, "desc")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
            Page<ChannelModel> channelPage = channelRepository.findAllByUserId(uid, page);
            if (channelPage == null) {
                return ResponseEntity.notFound().build();
            }
            Page<ChannelDto> channelDtoPage = channelPage.map(user -> modelMapper.map(user, ChannelDto.class));
            return ResponseEntity.ok(channelDtoPage);
        }
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestParam(value = "name") String name,
                                    @RequestParam(value = "description", defaultValue = "", required = false) String description,
                                    @RequestParam(value = "chp", required = false) MultipartFile chpImage,
                                    @RequestParam(value = "bigBlackCock", required = false) MultipartFile bigBlackCock,
                                    Authentication authentication) {
        UserModel userModel = userRepository.findByUsername(authentication.getName());
        if (userModel == null) {
            return ResponseEntity.status(401).build();
        }

        String uuidChp = String.valueOf(UUID.randomUUID());
        String uuidBigBlackCock = String.valueOf(UUID.randomUUID());

        ImageModel chp = ImageModel.builder()
                .type("th")
                .build();

        ImageModel bigBlackCockImage = ImageModel.builder()
                .type("th")
                .build();

        if (chpImage != null && !chpImage.isEmpty()) {
            String imageContentType = Objects.requireNonNull(chpImage.getContentType()).split("/")[1];
            storageApiUtils.sendToStorage(uuidChp, imageContentType, "img", chpImage);
            chp.setPath(uuidChp + "." + imageContentType);
        }

        if (bigBlackCock != null && !bigBlackCock.isEmpty()) {
            String imageContentType = Objects.requireNonNull(bigBlackCock.getContentType()).split("/")[1];
            storageApiUtils.sendToStorage(uuidBigBlackCock, imageContentType, "img", bigBlackCock);
            bigBlackCockImage.setPath(uuidBigBlackCock + "." + imageContentType);
        }


        ChannelModel channel = ChannelModel.builder()
                .name(name)
                .description(description)
                .followers(new HashSet<>())
                .user(userModel)
                .chp(chp)
                .bigBlackCock(bigBlackCockImage)
                .build();

        channel = channelRepository.save(channel);
        imagesRepository.save(chp);
        imagesRepository.save(bigBlackCockImage);

        return ResponseEntity.ok(modelMapper.map(channel, ChannelDto.class));
    }
}
