package ru.senya.mytybe.controllers;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.mytybe.models.ChannelModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.VideoModel;
import ru.senya.mytybe.repos.ChannelRepository;
import ru.senya.mytybe.repos.UserRepository;
import ru.senya.mytybe.repos.VideoRepository;

@RestController
public class MainController {

    final
    UserRepository userRepository;

    final
    ChannelRepository channelRepository;
    final
    VideoRepository videoRepository;

    Gson gson = new Gson();

    @Autowired
    public MainController(UserRepository userRepository, ChannelRepository channelRepository, VideoRepository videoRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
    }

    @GetMapping("/watch")
    public String watchVideo(@RequestParam String video, Model model) {
        model.addAttribute("video", video);
        return "video";
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserModel>> getAll() {
        PageRequest page = PageRequest.of(0, 10);
        return ResponseEntity.ok(userRepository.findAll(page));
    }

    @GetMapping("/channels")
    public ResponseEntity<Page<ChannelModel>> getChanese() {
        PageRequest page = PageRequest.of(0, 10);
        return ResponseEntity.ok(channelRepository.findAll(page));
    }

    @PostMapping("/create")
    public UserModel create() {
        UserModel user = userRepository.findById(1L).orElse(null);
        ChannelModel[] channels = user.getChannels().toArray(ChannelModel[]::new);

        VideoModel video = VideoModel.builder()
                .channel(channels[0])
                .duration(123897123L)
                .explicit(false)
                .path("/path_to_video")
                .stream(false)
                .build();

        videoRepository.save(video);


        channels[0].getVideos().add(video);
        channelRepository.save(channels[0]);
//
//        ChannelModel channel = ChannelModel.builder()
//                .name("Название канала")
//                .size(0)
//                .user(user)
//                .build();
//        channelRepository.save(channel);
//
//        user.getChannels().add(channel);
//
//        user = userRepository.save(user);

        return user;
    }


}
