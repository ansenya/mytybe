package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.models.jpa.ImageModel;
import ru.senya.mytybe.models.jpa.StreamingTaskModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.StreamingTaskRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;

import java.util.UUID;

@RestController
@RequestMapping("stream")
public class StreamingController {

    final UserRepository userRepository;
    final ChannelRepository channelRepository;
    final StreamingTaskRepository streamingTaskRepository;
    final VideoService videoService;
    final ModelMapper modelMapper = new ModelMapper();


    @Autowired
    public StreamingController(UserRepository userRepository, ChannelRepository channelRepository, StreamingTaskRepository streamingTaskRepository, VideoService videoService) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.streamingTaskRepository = streamingTaskRepository;
        this.videoService = videoService;
    }

    @PostMapping("create")
    public ResponseEntity<?> createStream(Authentication authentication,
                                          @RequestParam(name = "channelId") Long channelId,
                                          @RequestParam(required = false, name = "name", defaultValue = "стрим без названия") String videoName,
                                          @RequestParam(required = false, name = "description", defaultValue = "") String description) {
        var user = userRepository.findByUsername(authentication.getName());
        var channel = channelRepository.findById(channelId);

        if (channel.isEmpty() || !user.getChannels().contains(channel.get())) {
            return ResponseEntity.status(403).body("not the owner");
        }

        var link = String.valueOf(UUID.randomUUID());

        var streamTask = StreamingTaskModel.builder()
                .user(user)
                .channel(channel.get())
                .link(link)
                .status(0)
                .name(videoName)
                .description(description)
                .build();

        streamingTaskRepository.save(streamTask);

        return ResponseEntity.ok(link);
    }


    @PostMapping("validate")
    public ResponseEntity<?> validate(@RequestParam("name") String link) {
        var task = streamingTaskRepository.findByLink(link);

        if (task.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        if (task.get().getStatus() != 0) {
            return ResponseEntity.status(403).build();
        }

        task.get().setStatus(1);
        streamingTaskRepository.save(task.get());


        var stream = videoService.save(VideoModel.builder()
                .stream(true)
                .channel(task.get().getChannel())
                .name(task.get().getName())
                .description(task.get().getDescription())
                .path(link)
                .build());

        return ResponseEntity.ok().build();
    }

    @PostMapping("done")
    public ResponseEntity<?> done(@RequestParam("name") String link) {
        var task = streamingTaskRepository.findByLink(link);

        if (task.isEmpty()) {
            return ResponseEntity.status(404).body("no link");
        }

        task.get().setStatus(2);
        streamingTaskRepository.save(task.get());

        return ResponseEntity.ok("");
    }

    @GetMapping("path/{path}")
    public ResponseEntity<?> path(@PathVariable String path) {
        return ResponseEntity.ok(UUID.randomUUID());
    }
}
