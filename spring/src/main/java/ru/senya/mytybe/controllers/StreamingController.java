package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.models.dto.VideoDto;
import ru.senya.mytybe.models.jpa.*;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.UserLinkRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("stream")
public class StreamingController {

    final UserRepository userRepository;
    final ChannelRepository channelRepository;
    final UserLinkRepository userLinkRepository;
    final VideoService videoService;
    final ModelMapper modelMapper = new ModelMapper();


    @Autowired
    public StreamingController(UserRepository userRepository, ChannelRepository channelRepository, UserLinkRepository userLinkRepository, VideoService videoService) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.userLinkRepository = userLinkRepository;
        this.videoService = videoService;
    }

    @PostMapping("create")
    public ResponseEntity<?> createStream(Authentication authentication,
                                          @RequestParam(name = "channelId") Long channelId) {

        UserModel userModel = userRepository.findByUsername(authentication.getName());
        Optional<ChannelModel> channelModel = channelRepository.findById(channelId);

        if (channelModel.isPresent()) {
            if (!userModel.getChannels().contains(channelModel.get())) {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.status(403).build();
        }


        String link = String.valueOf(UUID.randomUUID());


        var ans = new HashMap<String, String>();
        ans.put("key", link);

        var videoModel = videoService.save(VideoModel.builder()
                .stream(true)
                .channel(channelModel.get())
                .thumbnail(ImageModel.builder().build())
                .path(link)
                .build());

        UserLinkEntity userLinkEntity = new UserLinkEntity();
        userLinkEntity.setUser(userModel);
        userLinkEntity.setLink(link);

        userLinkRepository.save(userLinkEntity);

        return ResponseEntity.ok(new Object[]{ans, modelMapper.map(videoModel, VideoDto.class)});
    }


    @PostMapping("validate")
    public ResponseEntity<?> validate(@RequestParam("name") String link) {
        return ResponseEntity.ok("");
    }

    @PostMapping("done")
    public ResponseEntity<?> done(@RequestParam("name") String link) {
        return ResponseEntity.ok("");
    }

    @GetMapping("path/{path}")
    public ResponseEntity<?> path(@PathVariable String path) {
        return ResponseEntity.ok(UUID.randomUUID());
    }
}
