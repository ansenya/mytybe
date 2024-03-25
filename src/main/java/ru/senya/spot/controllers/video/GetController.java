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

        if (video.isDeleted()) {
            return ResponseEntity.notFound().build();
        }

        VideoDto videoDto = modelMapper.map(video, VideoDto.class);

        if (authentication != null) {
            var user = userRepository.findByUsername(authentication.getName());
            user.getLastViewed().add(video);
            userRepository.save(user);
            videoDto.setLikedByThisUser(video.getLikedByUser().contains(user));
            videoDto.setDislikedByThisUser(video.getDislikedByUser().contains(user));
            videoDto.getChannel().setFollowedByThisUser(video.getChannel().getFollowers().contains(user));
        } else {
            System.out.println("user == null");
        }

        return ResponseEntity.ok().body(videoDto);
    }
}
