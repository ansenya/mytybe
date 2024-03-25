package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("videos")
@CrossOrigin(origins = "*")
public class UploadController {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final VideoService videoService;
    private final StorageApiUtils storageApiUtils;
    private final ProcessingUtils processingUtils;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Logger logger = LoggerFactory.getLogger(UploadController.class);

    public UploadController(UserRepository userRepository, ChannelRepository channelRepository, VideoService videoService, StorageApiUtils storageApiUtils, ProcessingUtils processingUtils) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.videoService = videoService;
        this.storageApiUtils = storageApiUtils;
        this.processingUtils = processingUtils;
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                    @RequestParam(value = "channelId", required = false) Long channelId,
                                    @RequestParam(value = "videoName", required = false, defaultValue = "") String videoName,
                                    @RequestParam(value = "videoDescription", required = false) String description,
                                    Authentication authentication) {

        var verdict = check(channelId, authentication, videoFile);
        if (verdict != null) {
            return verdict;
        }

        try {
            String uuid = UUID.randomUUID().toString();
            String videoContentType = Objects.requireNonNull(videoFile.getContentType()).split("/")[1];

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageContentType = Objects.requireNonNull(imageFile.getContentType()).split("/")[1];
                storageApiUtils.sendToStorage(uuid, imageContentType, "img", imageFile);
            }

            try {
                processingUtils.processVideo(videoFile, uuid);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            if (videoName.isEmpty()) {
                videoName = Objects.requireNonNull(videoFile.getOriginalFilename()).replace(".mp4", "");
            }

            ImageModel thumbnail = createThumbnail(imageFile, uuid);
            VideoModel video = createVideoEntity(videoName, description, channelRepository.findById(channelId).orElse(null), uuid, thumbnail);

            if (!storageApiUtils.sendToStorage(uuid, videoContentType, "vid", videoFile)) {
                return ResponseEntity.status(418).body("Upload is not available (cannot save)");
            }

            video = videoService.save(video);

            return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
        } catch (Exception e) {
            logger.error("error", e);
            return ResponseEntity.status(451).body("Error while processing");
        }
    }


    @PostMapping("upload/{uuid}/setQuality")
    public ResponseEntity<?> setQuality(@PathVariable String uuid, @RequestParam(name = "q") String q) {
        var optVideo = videoService.findByUUID(uuid);
        if (optVideo.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        var video = optVideo.get();

        if (video.getQualities().isEmpty()) {
            video.setQualities(q);
        } else {
            video.setQualities(video.getQualities() + ',' + q);
        }

        return ResponseEntity.ok(videoService.save(video));
    }

    @PostMapping("upload/{uuid}/setDoneQualities")
    public ResponseEntity<?> setDoneQualities(@PathVariable String uuid, @RequestParam(name = "status") String status) {
        var optVideo = videoService.findByUUID(uuid);
        if (optVideo.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        var video = optVideo.get();

        video.setProcessedQualities(status.equals("1"));

        return ResponseEntity.ok(videoService.save(video));
    }

    @PostMapping("upload/{uuid}/setDuration")
    public ResponseEntity<?> setTime(@PathVariable String uuid, @RequestParam(name = "duration") String duration) {
        var optVideo = videoService.findByUUID(uuid);
        if (optVideo.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        var video = optVideo.get();

        try {
            video.setDuration(Long.valueOf(duration));
        } catch (NumberFormatException e) {
            logger.error("number format exception in time", e);
        }

        return ResponseEntity.ok(videoService.save(video));
    }

    @PostMapping("upload/{uuid}/setProcessed")
    public ResponseEntity<?> setProcessed(@PathVariable String uuid) {
        var optVideo = videoService.findByUUID(uuid);
        if (optVideo.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        var video = optVideo.get();

        video.setProcessedAi(true);

        return ResponseEntity.ok(videoService.save(video));
    }

    private boolean checkVideoType(String name) {
        return name.endsWith(".mp4") || name.endsWith(".avi");
    }

    private ImageModel createThumbnail(MultipartFile imageFile, String uuid) {
        if (imageFile == null) {
            return ImageModel.builder()
                    .type("th")
                    .path("def_th.png")
                    .build();
        } else {
            return ImageModel.builder()
                    .type("th")
                    .path(uuid + "." + imageFile.getContentType().split("/")[1])
                    .build();
        }
    }

    private VideoModel createVideoEntity(String videoName, String description, ChannelModel channel, String uuid, ImageModel thumbnail) {
        return VideoModel.builder()
                .name(videoName)
                .channel(channel)
                .description(description)
                .path(uuid)
                .qualities("")
                .thumbnail(thumbnail)
                .tags(new HashSet<>())
                .likedByUser(new HashSet<>())
                .dislikedByUser(new HashSet<>())
                .processedQualities(false)
                .streamStatus(0)
                .version(0)
                .build();
    }

    private ResponseEntity<?> check(Long channelId, Authentication authentication, MultipartFile videoFile) {
        if (channelId == null) {
            return ResponseEntity.badRequest().body("Channel ID is null");
        }

        var user = userRepository.findByUsername(authentication.getName());
        var channel = channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return ResponseEntity.badRequest().body("Channel does not exist");
        }

        if (!user.getChannels().contains(channel)) {
            return ResponseEntity.badRequest().body("User is not the owner of the channel " + channel.getName());
        }

        if (videoFile == null || videoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Video is empty");
        }

        if (!checkVideoType(Objects.requireNonNull(videoFile.getOriginalFilename()))) {
            return ResponseEntity.badRequest().body("Unsupported video format");
        }

        return null;
    }
}
