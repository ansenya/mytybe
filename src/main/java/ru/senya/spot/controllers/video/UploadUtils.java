package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
public class UploadUtils {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final VideoService videoService;
    private final StorageApiUtils storageApiUtils;
    private final ProcessingUtils processingUtils;
    private final ModelMapper modelMapper = new ModelMapper();

    public UploadUtils(UserRepository userRepository, ChannelRepository channelRepository, VideoService videoService, StorageApiUtils storageApiUtils, ProcessingUtils processingUtils) {
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

            if (!storageApiUtils.sendToStorage(uuid, videoContentType, "vid", videoFile)) {
                return ResponseEntity.status(418).body("Upload is not available (cannot save)");
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                storageApiUtils.sendToStorage(uuid, videoContentType, "img", imageFile);
            }

            processingUtils.processVideo(videoFile, uuid);

            if (videoName.isEmpty()) {
                videoName = videoFile.getOriginalFilename();
            }

            ImageModel thumbnail = createThumbnail(imageFile, uuid);
            VideoModel video = createVideoEntity(videoName, description, channelRepository.findById(channelId).orElse(null), uuid, thumbnail);

            video = videoService.save(video);

            return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));

        } catch (Exception exception) {
            return ResponseEntity.status(451).body("Error while processing");
        }
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
                    .path(uuid + "." + Objects.requireNonNull(imageFile.getContentType()).split("/")[1])
                    .build();
        }
    }

    private VideoModel createVideoEntity(String videoName, String description, ChannelModel channel, String uuid, ImageModel thumbnail) {
        return VideoModel.builder()
                .name(videoName)
                .channel(channel)
                .description(description)
                .path(uuid)
                .thumbnail(thumbnail)
                .tags(new HashSet<>())
                .streamStatus(0)
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
