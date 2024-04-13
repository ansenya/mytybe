package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.spot.controllers.storage.FileUploadService;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.es.EsVideoModel;
import ru.senya.spot.models.jpa.ChannelModel;
import ru.senya.spot.models.jpa.ImageModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.es.ElasticVideoRepository;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;

import javax.swing.text.TableView;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static ru.senya.spot.MytybeApplication.SERVER_TOKEN;
import static ru.senya.spot.MytybeApplication.STORAGE_HOST;

@RestController
@RequestMapping("videos")
@CrossOrigin(origins = "*")
public class UploadController {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ElasticVideoRepository elasticVideoRepository;
    private final VideoService videoService;
    private final FileUploadService storageApiUtils;
    private final ProcessingUtils processingUtils;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Logger logger = LoggerFactory.getLogger(UploadController.class);

    public UploadController(UserRepository userRepository, ChannelRepository channelRepository, ElasticVideoRepository elasticVideoRepository, VideoService videoService, FileUploadService storageApiUtils, ProcessingUtils processingUtils) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoService = videoService;
        this.storageApiUtils = storageApiUtils;
        this.processingUtils = processingUtils;
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                    @RequestParam(value = "channelId", required = false) Long channelId,
                                    @RequestParam(value = "videoName", required = false, defaultValue = "") String videoName,
                                    @RequestParam(value = "videoDescription", required = false) String description,
                                    Authentication authentication) {

        var verdict = check(channelId, authentication);
        if (verdict != null) {
            return verdict;
        }

        try {
            String uuid = UUID.randomUUID().toString();

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageContentType = Objects.requireNonNull(imageFile.getContentType()).split("/")[1];
                try {
                    storageApiUtils.sendToStorage(uuid, imageContentType, "img", imageFile);
                } catch (HttpClientErrorException e) {
                    logger.error(e.getMessage());
                }
            }
//            try {
//                processingUtils.processVideo(videoFile, uuid);
//            } catch (Exception e) {
//                logger.error(e.getMessage());
//            }

            var thumbnail = createThumbnail(imageFile, uuid);
            var video = createVideoEntity(videoName,
                    description,
                    channelRepository.findById(channelId).get(),
                    uuid,
                    thumbnail);

            video = videoService.save(video);
            try {
                allowToUpload(uuid, authentication);
            } catch (HttpClientErrorException e) {
                logger.error("allow to upload error: " + e.getMessage());
            }

            var uuidData = new HashMap<>();
            uuidData.put("uuid", uuid);
            return ResponseEntity.ok(new Object[]{modelMapper.map(video, VideoDto.class), uuidData});
        } catch (Exception e) {
            logger.error("error", e);
            return ResponseEntity.status(451).body("Error while processing");
        }
    }

    private void allowToUpload(String uuid, Authentication authentication) {
        var restTemplate = new RestTemplate();
        var url = STORAGE_HOST + "vid/allow";

        var headers = new HttpHeaders();
        headers.setBearerAuth(SERVER_TOKEN);

        var body = new LinkedMultiValueMap<>();
        body.add("uuid", uuid);
        body.add("user", authentication.getName());

        var request = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.POST, request, String.class);
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
        var esVideo = modelMapper.map(video, EsVideoModel.class);
        elasticVideoRepository.save(esVideo);

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

    private ResponseEntity<?> check(Long channelId, Authentication authentication) {
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

        return null;
    }
}
