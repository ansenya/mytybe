package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.models.dto.LikesDto;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.recs.VideoRecommendationSystem;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;

import java.util.List;

@RestController
@RequestMapping("videos")
@CrossOrigin(origins = "*")
public class GetController {

    private final UserRepository userRepository;
    private final VideoService videoService;
    private final ModelMapper modelMapper;

    public GetController(UserRepository userRepository, VideoService videoService) {
        this.userRepository = userRepository;
        this.videoService = videoService;
        modelMapper = new ModelMapper();
    }


    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "page") Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "channelId", required = false, defaultValue = "-1") Long channelId,
                                    Authentication authentication) {
        if (authentication != null) {
            UserModel userModel = userRepository.findByUsername(authentication.getName());

            VideoRecommendationSystem recommendationSystem = new VideoRecommendationSystem(videoService.getAll());

            List<VideoDto> recs = recommendationSystem.recommendVideos(userModel).stream().map(videoModel -> modelMapper.map(videoModel, VideoDto.class)).toList();

            PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.ASC, "created"));

            Page<VideoModel> videoPage;
            if (channelId == -1) {
                videoPage = videoService.getAll(recs.stream().map(VideoDto::getId).toList(), page);
            } else {
                videoPage = videoService.findAllByChannelId(channelId, page);
            }

            Page<VideoDto> videoDtoPage = videoPage.map(videoModel -> modelMapper.map(videoModel, VideoDto.class));

            return ResponseEntity.ok(videoDtoPage);
        } else {
            var all = videoService.findAll(PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "id")))
                    .map(videoModel -> modelMapper.map(videoModel, VideoDto.class));
            return ResponseEntity.ok(all);
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id,
                                    Authentication authentication) {
        var video = videoService.findById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        video.setViews(video.getViews() + 1);
        video = videoService.save(video);

        if (authentication != null) {
            var user = userRepository.findByUsername(authentication.getName());
            user.getLastViewed().add(video);
            userRepository.save(user);
        }

        return ResponseEntity.ok().body(modelMapper.map(video, VideoDto.class));
    }


    @PutMapping("{id}")
    public ResponseEntity<?> like(@PathVariable Long id, @RequestParam("like") Boolean like, Authentication
            authentication) {
        VideoModel video = videoService.findById(id);
        UserModel user = userRepository.findByUsername(authentication.getName());

        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        if (like) {
            user.getLikedVideos().add(video);
            user.getDislikedVideos().remove(video);

            video.getLikedByUser().add(user);
            video.getDislikedByUser().remove(user);
        } else {
            user.getDislikedVideos().add(video);
            user.getLikedVideos().remove(video);

            video.getDislikedByUser().add(user);
            video.getLikedByUser().remove(user);
        }

        user = userRepository.save(user);

        video = videoService.save(video);

        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }

    @GetMapping("{id}/likes")
    public ResponseEntity<?> getLiked(@PathVariable Long id,
                                      @RequestParam("page") int page,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        VideoModel video = videoService.findById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        LikesDto likesDto = modelMapper.map(video, LikesDto.class);
        likesDto.setPage(1);
        likesDto.setPageSize(pageSize);

        return ResponseEntity.ok(likesDto);
    }

}
