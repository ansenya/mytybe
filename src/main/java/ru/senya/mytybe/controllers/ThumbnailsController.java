package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.mytybe.models.dto.VideoDto;
import ru.senya.mytybe.models.jpa.ImageModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.repos.jpa.ImagesRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;

import java.io.*;


@RestController
@RequestMapping("videos")
public class ThumbnailsController {

    final VideoService videoService;
    final UserRepository userRepository;
    final ImagesRepository imagesRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public ThumbnailsController(VideoService videoService, UserRepository userRepository, ImagesRepository imagesRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
    }

    @PostMapping("{id}/th")
    public ResponseEntity<?> setThumbnail(@RequestParam(value = "th", required = false) MultipartFile th,
                                          @PathVariable Long id,
                                          Authentication authentication) {
        VideoModel videoModel = videoService.findById(id);

        if (videoModel == null) {
            return ResponseEntity.status(400).body("video is null");
        }

        if (th == null || th.getContentType() == null) {
            return ResponseEntity.status(400).body("thumbnail is null");
        }

        if (!videoModel.getChannel().getUser().getId().equals(userRepository.findByUsername(authentication.getName()).getId())) {
            return ResponseEntity.status(403).build();
        }

        if (th.getContentType().equals(".mp4")) {
            return ResponseEntity.status(404).body("th should be the photo, not the video");
        }

        try {
            if (videoModel.getThumbnail().getPath().equals(videoModel.getVid_uuid())) {
                File old = new File("src/main/resources/images", videoModel.getThumbnail().getPath());
                old.delete();
            }
        } catch (NullPointerException ignored) {

        }

        File destFile;
        try {
            destFile = convertMultipartFileToFile(th, videoModel.getPath() + "." + th.getContentType().split("/")[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageModel thumbnail = ImageModel.builder()
                .video(videoModel)
                .type("th")
                .path(videoModel.getPath() + "." + th.getContentType().split("/")[1])
                .build();

        thumbnail = imagesRepository.save(thumbnail);
        videoModel.setThumbnail(thumbnail);
        videoService.save(videoModel);

        return ResponseEntity.ok(modelMapper.map(videoModel, VideoDto.class));
    }

    public static File convertMultipartFileToFile(MultipartFile multipartFile, String uuid) throws IOException {
        File file = new File("src/main/resources/images/" + uuid);

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
