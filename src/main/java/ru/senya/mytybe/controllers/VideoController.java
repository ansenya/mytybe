package ru.senya.mytybe.controllers;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import ru.senya.mytybe.models.es.EsVideoModel;
import ru.senya.mytybe.models.jpa.*;
import ru.senya.mytybe.recs.VideoRecommendationSystem;
import ru.senya.mytybe.repos.es.ElasticVideoRepository;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.TagRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;

import java.io.*;
import java.util.*;

@RequestMapping("videos")
@RestController
@CrossOrigin(origins = "*")
public class VideoController {

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
    public ResponseEntity<?> upload(@RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
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

        if (videoFile == null || videoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("video is empty");
        }
        if (!checkType(Objects.requireNonNull(videoFile.getOriginalFilename()))) {
            return ResponseEntity.badRequest().body("not a video");
        }


        String uuid = String.valueOf(UUID.randomUUID());

        if (!sendToStorage(uuid, Objects.requireNonNull(videoFile.getContentType()).split("/")[1], "vid", videoFile)) {
            return ResponseEntity.status(418).body("upload is not available (cannot save)");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            sendToStorage(uuid, Objects.requireNonNull(videoFile.getContentType()).split("/")[1], "img", imageFile);
        }

//
        HashMap<String, String> result;
        try {
            result = gson.fromJson(processVideo(videoFile, uuid),
                    new TypeToken<HashMap<String, String>>() {
                    }.getType());
        } catch (Exception exception) {
            System.out.println(exception);
            return ResponseEntity.status(418).body("upload is not available");
        }


        if (videoName.isEmpty()) {
            videoName = videoFile.getOriginalFilename();
        }
        ImageModel thumbnail;
        if (imageFile == null) {
            thumbnail = ImageModel.builder()
                    .type("th")
                    .path("def_th.png")
                    .build();
        } else {
            thumbnail = ImageModel.builder()
                    .type("th")
                    .path(uuid + "." + Objects.requireNonNull(imageFile.getContentType()).split("/")[1])
                    .build();
        }


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

        System.gc();
        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }


    public static boolean sendToStorage(String uuid, String type, String endpoint, MultipartFile file) {
        String url = "http://5.180.174.216:1986/api/" + endpoint + "/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestOperations restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url + "?uuid=" + uuid + "&type=" + type, HttpMethod.POST, requestEntity, String.class);

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

    @PostMapping("tag")
    public ResponseEntity<?> setTag(@RequestParam(value = "tag", required = false) String tag,
                                    @RequestParam(value = "id", required = false) Long id) {

        System.out.println(tag);
        TagModel tagModel = tagRepository.findByEnTag(tag);
        VideoModel videoModel = videoService.findById(id);

        if (tagModel == null || videoModel == null) {
            return ResponseEntity.status(404).build();
        }

//        tagModel.getVideos().add(videoModel);
//        tagModel = tagRepository.save(tagModel);

        videoModel.getTags().add(tagModel);
        videoModel = videoService.save(videoModel);

        return ResponseEntity.ok(videoModel);
    }

    //
    @PostMapping("done")
    public ResponseEntity<?> setDone(@RequestParam(value = "id", required = false) Long id) {
        VideoModel videoModel = videoService.findById(id);

        if (videoModel == null) {
            return ResponseEntity.status(404).build();
        }

        EsVideoModel esVideoModel = modelMapper.map(videoModel, EsVideoModel.class);

        esVideoModel = elasticVideoRepository.save(esVideoModel);

        System.out.println(gson.toJson(esVideoModel));

        return ResponseEntity.ok(esVideoModel);
    }

    private String requestEta(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8642/progress?id=" + uuid, String.class);

        return response.getBody();
    }

//    @PostMapping("process")
//    public ResponseEntity<?> process() {
//        var videos = videoService.getAll();
//
//        for (var video : videos) {
//            processVideo()
//        }
//    }

//    private String processVideo(File videoFile, String uuid) {
//        int timeout = 10_000;
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(timeout);
//        factory.setConnectTimeout(timeout);
//
//        RestTemplate restTemplate = new RestTemplate(factory);
//
//        String serverUrl = "http://192.168.1.2:8642/process?uuid=" + uuid;
//
//        FileSystemResource fileResource = new FileSystemResource(videoFile);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("video", fileResource);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
//        return response.getBody();
//    }

    private String processVideo(MultipartFile videoFile, String uuid) {
        int timeout = 10_000;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        String serverUrl = "http://localhost:8642/process?uuid=" + uuid;

        FileSystemResource fileResource = new FileSystemResource(convert(videoFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        return response.getBody();
    }

    private File convert(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        return convFile;
    }

    private boolean checkType(String name) {
        return name.endsWith(".mp4") || name.endsWith(".avi");
    }
}