package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.controllers.storage.FileUploadService;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.repos.jpa.ImagesRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;


@RestController
@RequestMapping("videos")
public class ThumbnailsController {

    final VideoService videoService;
    final UserRepository userRepository;
    final ImagesRepository imagesRepository;
    private final FileUploadService storageApiUtils;
    final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public ThumbnailsController(VideoService videoService, UserRepository userRepository, ImagesRepository imagesRepository, FileUploadService storageApiUtils) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
        this.storageApiUtils = storageApiUtils;
    }

    @PutMapping("{id}/th")
    public ResponseEntity<?> th(@PathVariable Long id,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                Authentication authentication) {
        var user = userRepository.findByUsername(authentication.getName());
        var video = videoService.findById(id);

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("imageFile is null");
        }

        if (video != null && video.getChannel().getUser().equals(user)) {
            storageApiUtils.sendToStorage(video.getPath(), "png", "img", imageFile);
            video.getThumbnail().setPath(video.getPath() + ".png");
        } else {
            return ResponseEntity.status(404).build();
        }

        video = videoService.save(video);
        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }
}
