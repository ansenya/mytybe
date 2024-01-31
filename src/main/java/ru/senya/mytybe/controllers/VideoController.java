package ru.senya.mytybe.controllers;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.mytybe.models.dto.LikesDto;
import ru.senya.mytybe.models.dto.VideoDto;
import ru.senya.mytybe.models.jpa.ChannelModel;
import ru.senya.mytybe.models.jpa.ImageModel;
import ru.senya.mytybe.models.jpa.UserModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.recs.VideoRecommendationSystem;
import ru.senya.mytybe.repos.es.ElasticVideoRepository;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.TagRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequestMapping("videos")
@RestController
public class VideoController extends BaseController {

    final
    UserRepository userRepository;

    final
    ChannelRepository channelRepository;
    final
    TagRepository tagRepository;
    final
    ElasticVideoRepository elasticVideoRepository;

    final VideoService videoService;

    final Gson gson = new Gson();

    ModelMapper modelMapper = new ModelMapper();


    public VideoController(UserRepository userRepository, ChannelRepository channelRepository, TagRepository tagRepository, ElasticVideoRepository elasticVideoRepository, VideoService videoService) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.tagRepository = tagRepository;
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoService = videoService;
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


    @PostMapping("upload")
    public ResponseEntity<?> upload(@RequestParam(value = "video", required = false) MultipartFile file,
                                    @RequestParam(value = "channelId", required = false) Long channelId,
                                    @RequestParam(value = "videoName", required = false, defaultValue = "") String videoName,
                                    @RequestParam(value = "videoDescription", required = false) String description,
                                    Authentication authentication) {

        if (channelId == null) {
            return ResponseEntity.badRequest().body("channel id is null");
        }

        UserModel user = userRepository.findByUsername(authentication.getName());
        ChannelModel channel = channelRepository.findById(channelId).orElse(null);

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


        String uuid = String.valueOf(UUID.randomUUID());

        if (!sendToStorage(uuid, Objects.requireNonNull(file.getContentType()).split("/")[1], file)) {
            return ResponseEntity.status(418).body("upload is not available (cannot save)");
        }
//
//
//        HashMap<String, String> result;
//        try {
//            result = gson.fromJson(processVideo(destFile.getPath(), uuid),
//                    new TypeToken<HashMap<String, String>>() {
//                    }.getType());
//        } catch (Exception exception) {
//            File delete = new File("src/main/resources/videos/" + uuid + ".mp4");
//            delete.delete();
//            return ResponseEntity.status(418).body("upload is not available");
//        }

        if (videoName.isEmpty()) {
            videoName = file.getOriginalFilename();
        }

        ImageModel thumbnail = ImageModel.builder()
                .type("th")
                .build();

        VideoModel video = VideoModel.builder()
                .name(videoName)
                .channel(channel)
                .description(description)
                .path(uuid)
                .thumbnail(thumbnail)
                .tags(new HashSet<>())
                .streamStatus(0)
                .build();

        video = videoService.save(video);

        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }

    private boolean sendToStorage(String uuid, String type, MultipartFile file) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestOperations restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:1986/api/upload?uuid=" + uuid + "&type=" + type, HttpMethod.POST, requestEntity, String.class);

        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode == HttpStatus.OK;
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


//    @GetMapping("eta")
//    public ResponseEntity<?> getEta(@RequestParam(value = "id") Long id) {
//        VideoModel videoModel = videoService.findById(id);
//
//        if (videoModel == null) {
//            return ResponseEntity.notFound().build();
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//
//        return ResponseEntity.ok().headers(headers).body(requestEta(videoModel.getVid_uuid()));
//    }

//    @PostMapping("tag")
//    public ResponseEntity<?> setTag(@RequestParam(value = "tag", required = false) String tag,
//                                    @RequestParam(value = "id", required = false) Long id) {
//
//        System.out.println(tag);
//        TagModel tagModel = tagRepository.findByEnTag(tag);
//        VideoModel videoModel = videoService.findById(id);
//
//        if (tagModel == null || videoModel == null) {
//            return ResponseEntity.status(404).build();
//        }
//
////        tagModel.getVideos().add(videoModel);
////        tagModel = tagRepository.save(tagModel);
//
//        videoModel.getTags().add(tagModel);
//        videoModel = videoService.save(videoModel);
//
//        return ResponseEntity.ok(videoModel);
//    }
//
//    @PostMapping("done")
//    public ResponseEntity<?> setDone(@RequestParam(value = "id", required = false) Long id) {
//        VideoModel videoModel = videoService.findById(id);
//
//        if (videoModel == null) {
//            return ResponseEntity.status(404).build();
//        }
//
//        EsVideoModel esVideoModel = modelMapper.map(videoModel, EsVideoModel.class);
//
//        esVideoModel = elasticVideoRepository.save(esVideoModel);
//
//        System.out.println(gson.toJson(esVideoModel));
//
//        return ResponseEntity.ok(esVideoModel);
//    }

    private String requestEta(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8642/progress?id=" + uuid, String.class);

        return response.getBody();
    }

    private String processVideo(String path, String uuid) {
        int timeout = 10000;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        String serverUrl = "http://localhost:8642/process?uuid=" + uuid;

        FileSystemResource fileResource = new FileSystemResource(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        return response.getBody();
    }


    private boolean checkType(String name) {
        return name.endsWith(".mp4") || name.endsWith(".avi");
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile, String uuid) throws IOException {
        File file = new File("src/main/resources/videos/" + uuid + ".mp4");

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