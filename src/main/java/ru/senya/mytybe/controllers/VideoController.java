package ru.senya.mytybe.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.mytybe.models.ChannelModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.VideoModel;
import ru.senya.mytybe.repos.ChannelRepository;
import ru.senya.mytybe.repos.UserRepository;
import ru.senya.mytybe.repos.VideoRepository;

import java.io.*;
import java.util.Objects;

@RestController
@RequestMapping("v")
public class VideoController {

    final
    UserRepository userRepository;

    final
    VideoRepository videoRepository;
    final
    ChannelRepository channelRepository;

    public VideoController(UserRepository userRepository, VideoRepository videoRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam(value = "video", required = false) MultipartFile file,
                                    @RequestParam(value = "channelId", required = false) Long channelId,
                                    @RequestParam(value = "videoName", required = false) String videoName,
                                    @RequestParam(value = "videoDescription", required = false) String description,
                                    Authentication authentication) {

        if (channelId == null) {
            return ResponseEntity.badRequest().body("channel id is null");
        }
        if (videoName == null) {
            return ResponseEntity.badRequest().body("video name is null");
        }

        ChannelModel channel = channelRepository.findById(channelId).orElse(null);
        UserModel user = userRepository.findByUsername(authentication.getName());

        if (channel == null) {
            return ResponseEntity.badRequest().body("channel does not exist");
        }
        if (!user.getChannels().contains(channel)) {
            return ResponseEntity.badRequest().body("user is not the owner of the channel " + channel.getName());
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("video is empty");
        }
        if (!checkType(Objects.requireNonNull(file.getOriginalFilename()))) {
            return ResponseEntity.badRequest().body("not a video");
        }

        File destFile;
        try {
            destFile = convertMultipartFileToFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        VideoModel video = VideoModel.builder()
                .name(videoName)
                .channel(channel)
                .description(description)
                .path("src/main/resources/videos/" + file.getOriginalFilename())
                .build();

        video = videoRepository.save(video);
        user = userRepository.save(user);

        return ResponseEntity.ok(video);
    }


    private boolean checkType(String name) {
        return name.endsWith(".mp4") || name.endsWith(".avi");
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File("src/main/resources/videos/" + multipartFile.getOriginalFilename());

        try (OutputStream os = new FileOutputStream(file); InputStream is = multipartFile.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }

        return file;
    }
}
