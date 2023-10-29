package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.dto.ChannelDto;
import ru.senya.mytybe.dto.UserDtoWithoutChannels;
import ru.senya.mytybe.models.ChannelModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.repos.ChannelRepository;
import ru.senya.mytybe.repos.UserRepository;

import java.util.Objects;


@RequestMapping("c")
@RestController
public class ChannelController extends BaseController {
    final
    ChannelRepository channelRepository;
    final
    UserRepository userRepository;
    ModelMapper modelMapper = new ModelMapper();

    Logger logger = LoggerFactory.getLogger(ChannelController.class);

    public ChannelController(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("channel/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {

        if (id == null) {
            return ResponseEntity.badRequest().body("id is empty");
        }
        ChannelModel channel = channelRepository.findById(id).orElse(null);

        if (channel == null) {
            return ResponseEntity.notFound().build();
        }

        ChannelDto channelDto = modelMapper.map(channel, ChannelDto.class);
        channelDto.setUser(modelMapper.map(channel.getUser(), UserDtoWithoutChannels.class));

        return ResponseEntity.ok(channelDto);
    }

    @GetMapping("all")
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

    @PostMapping("channel")
    public ResponseEntity<?> create(@RequestParam(value = "name") String name, Authentication authentication) {
        UserModel userModel = userRepository.findByUsername(authentication.getName());

        ChannelModel channel = ChannelModel.builder()
                .name(name)
                .user(userModel)
                .build();

        channel = channelRepository.save(channel);

        return ResponseEntity.ok(modelMapper.map(channel, ChannelDto.class));
    }
}
