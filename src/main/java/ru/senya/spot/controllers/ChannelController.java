package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.models.dto.ChannelDto;
import ru.senya.spot.models.dto.UserDtoWithoutChannels;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.ImagesRepository;
import ru.senya.spot.repos.jpa.UserRepository;

import java.util.Objects;


@RequestMapping("channels")
@RestController
public class ChannelController {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ImagesRepository imagesRepository;
    private final ModelMapper modelMapper;

    public ChannelController(ChannelRepository channelRepository, UserRepository userRepository, ImagesRepository imagesRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
        modelMapper = new ModelMapper();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is empty");
        }

        var channel = channelRepository.findById(id).orElse(null);
        if (channel == null) {
            return ResponseEntity.notFound().build();
        }

        var channelDto = modelMapper.map(channel, ChannelDto.class);
        channelDto.setUser(modelMapper.map(channel.getUser(), UserDtoWithoutChannels.class));

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

            Page<ChannelModel> channelPage = channelRepository.getAllByUserId(uid, page);

            if (channelPage == null) {
                return ResponseEntity.notFound().build();
            }

            Page<ChannelDto> channelDtoPage = channelPage.map(user -> modelMapper.map(user, ChannelDto.class));

            return ResponseEntity.ok(channelDtoPage);
        }
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestParam(value = "name") String name, Authentication authentication) {
        UserModel userModel = userRepository.findByUsername(authentication.getName());


        ImageModel chp = ImageModel.builder()
                .type("th")
                .build();

        ChannelModel channel = ChannelModel.builder()
                .name(name)
                .user(userModel)
                .chp(chp)
                .build();

        channel = channelRepository.save(channel);
        imagesRepository.save(chp);

        return ResponseEntity.ok(modelMapper.map(channel, ChannelDto.class));
    }
}
