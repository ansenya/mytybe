package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.models.dto.LikesDto;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;

@RestController
@RequestMapping("videos")
@CrossOrigin(origins = "*")
public class LikeDislikeController {
    private final VideoService videoService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LikeDislikeController(VideoService videoService, UserRepository userRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    @PostMapping("like/{id}")
    @Transactional
    public ResponseEntity<?> like(@PathVariable Long id,
                                  Authentication authentication) {
        VideoModel video = videoService.findById(id);
        UserModel user = userRepository.findByUsername(authentication.getName());

        if (video == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getLikedVideos().contains(video)) {
            user.getLikedVideos().remove(video);
            video.getLikedByUser().remove(user);
        } else {
            user.getLikedVideos().add(video);
            user.getDislikedVideos().remove(video);

            video.getLikedByUser().add(user);
            video.getDislikedByUser().remove(user);
        }

        user = userRepository.save(user);
        video = videoService.save(video);
        var videoDto = modelMapper.map(video, VideoDto.class);

        videoDto.setLikedByThisUser(video.getLikedByUser().contains(user));
        videoDto.setDislikedByThisUser(video.getDislikedByUser().contains(user));

        return ResponseEntity.ok(videoDto);
    }

    @PostMapping("dislike/{id}")
    @Transactional
    public ResponseEntity<?> dislike(@PathVariable Long id,
                                     Authentication authentication) {
        VideoModel video = videoService.findById(id);
        UserModel user = userRepository.findByUsername(authentication.getName());

        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.getDislikedVideos().contains(video)) {
            user.getDislikedVideos().remove(video);
            video.getDislikedByUser().remove(user);
        } else {
            user.getDislikedVideos().add(video);
            user.getLikedVideos().remove(video);

            video.getDislikedByUser().add(user);
            video.getLikedByUser().remove(user);
        }
        user = userRepository.save(user);
        video = videoService.save(video);

        var videoDto = modelMapper.map(video, VideoDto.class);

        videoDto.setLikedByThisUser(video.getLikedByUser().contains(user));
        videoDto.setDislikedByThisUser(video.getDislikedByUser().contains(user));

        return ResponseEntity.ok(videoDto);
    }


    @GetMapping("{id}/likes")
    public ResponseEntity<?> getLiked(@PathVariable Long id,
                                      @RequestParam("page") int page,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        VideoModel video = videoService.findById(id);
        if (video == null) {
            return ResponseEntity.status(404).build();
        }

        LikesDto likesDto = modelMapper.map(video, LikesDto.class);
        likesDto.setPage(1);
        likesDto.setPageSize(pageSize);

        return ResponseEntity.ok(likesDto);
    }
}
