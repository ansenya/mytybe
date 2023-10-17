package ru.senya.mytybe.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.mytybe.dto.ChannelDto;
import ru.senya.mytybe.dto.LikesDto;
import ru.senya.mytybe.dto.VideoDto;
import ru.senya.mytybe.models.ChannelModel;
import ru.senya.mytybe.models.TagModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.VideoModel;
import ru.senya.mytybe.repos.ChannelRepository;
import ru.senya.mytybe.repos.TagRepository;
import ru.senya.mytybe.repos.UserRepository;
import ru.senya.mytybe.repos.VideoRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("v")
public class VideoController {

    final
    UserRepository userRepository;
    final
    VideoRepository videoRepository;
    final
    ChannelRepository channelRepository;
    final
    TagRepository tagRepository;

    final Gson gson = new Gson();

    ModelMapper modelMapper = new ModelMapper();


    public VideoController(UserRepository userRepository, VideoRepository videoRepository, ChannelRepository channelRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
        this.tagRepository = tagRepository;
    }


    @GetMapping("all")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort,
                                    @RequestParam(value = "channelId", required = false, defaultValue = "-1") Long channelId) {
        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        if (channelId == -1) {
            Sort.Direction direction;

            if (Objects.equals(sort, "desc")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
            Page<VideoModel> videoPage = videoRepository.findAll(page);
            Page<VideoDto> videoDtoPage = videoPage.map(videoModel -> modelMapper.map(videoModel, VideoDto.class));

            return ResponseEntity.ok(videoDtoPage);
        } else {
            Sort.Direction direction;

            if (Objects.equals(sort, "desc")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
            Page<VideoModel> videoPage = videoRepository.findAllByChannelId(channelId, page);
            Page<VideoDto> videoDtoPage = videoPage.map(videoModel -> modelMapper.map(videoModel, VideoDto.class));

            return ResponseEntity.ok(videoDtoPage);
        }

    }

    @PostMapping("video")
    public ResponseEntity<?> upload(@RequestParam(value = "video", required = false) MultipartFile file,
                                    @RequestParam(value = "channelId", required = false) Long channelId,
                                    @RequestParam(value = "videoName", required = false) String videoName,
                                    @RequestParam(value = "videoDescription", required = false) String description,
                                    Authentication authentication) {

        if (channelId == null) {
            return ResponseEntity.badRequest().body("channel id is null");
        }
        if (videoName == null) {
            return ResponseEntity.badRequest().body("video name is null");
        }

        ChannelModel channel = channelRepository.findById(channelId).orElse(null);
        UserModel user = userRepository.findByUsername(authentication.getName());

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

        File destFile;
        try {
            destFile = convertMultipartFileToFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String uuid = String.valueOf(UUID.randomUUID());

        HashMap<String, String> result = gson.fromJson(processVideo(destFile.getPath(), uuid),
                new TypeToken<HashMap<String, String>>() {
                }.getType());

        VideoModel video = VideoModel.builder()
                .name(videoName)
                .channel(channel)
                .description(description)
                .vid_uuid(uuid)
                .path("http://localhost:8642/video?id=" + result.get("id"))
                .build();

        video = videoRepository.save(video);
        user = userRepository.save(user);

        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }

    @GetMapping("video/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {

        VideoModel video = videoRepository.findById(id).orElse(null);

        if (video == null) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok().body(modelMapper.map(video, VideoDto.class));
    }


    @PutMapping("video/{id}")
    public ResponseEntity<?> like(@PathVariable Long id, @RequestParam("like") Boolean like, Authentication authentication) {
        VideoModel video = videoRepository.findById(id).orElse(null);
        UserModel user = userRepository.findByUsername(authentication.getName());

        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        if (like) {
            user.getLikedVideos().add(video);
            user.getDislikedVideos().remove(video);

            video.getLikedByUser().add(user);
            video.getDislikedByUser().remove(user);
        }
        else {
            user.getDislikedVideos().add(video);
            user.getLikedVideos().remove(video);

            video.getDislikedByUser().add(user);
            video.getLikedByUser().remove(user);
        }

        user = userRepository.save(user);

        video = videoRepository.save(video);

        return ResponseEntity.ok(modelMapper.map(video, VideoDto.class));
    }

    @GetMapping("video/{id}/likes")
    public ResponseEntity<?> getLiked(@PathVariable Long id, @RequestParam("page") int page, @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize){
        VideoModel video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        LikesDto likesDto = modelMapper.map(video, LikesDto.class);
        likesDto.setPage(1);
        likesDto.setPageSize(pageSize);

        return ResponseEntity.ok(likesDto);
    }


    @GetMapping("eta")
    public ResponseEntity<?> getEta(@RequestParam(value = "id") Long id) {
        VideoModel videoModel = videoRepository.findById(id).orElse(null);

        if (videoModel == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return ResponseEntity.ok().headers(headers).body(requestEta(videoModel.getVid_uuid()));
    }

    @PostMapping("tag")
    public ResponseEntity<?> setTag(@RequestParam(value = "tag", required = false) String tag,
                                    @RequestParam(value = "id", required = false) Long id) {

        TagModel tagModel = tagRepository.findByTag(tag);
        System.out.println(tag);
        VideoModel videoModel = videoRepository.findById(id).orElse(null);

        System.out.println(tagModel == null);
        System.out.println(videoModel == null);

        if (tagModel == null || videoModel == null) {
            return ResponseEntity.status(404).build();
        }

        tagModel.getVideos().add(videoModel);
        tagModel = tagRepository.save(tagModel);


        videoModel.getTags().add(tagModel);
        videoModel = videoRepository.save(videoModel);

        return ResponseEntity.ok(videoModel);
    }

    private String requestEta(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8642/progress?id=" + uuid, String.class);

        return response.getBody();
    }

    private String processVideo(String path, String uuid) {
        RestTemplate restTemplate = new RestTemplate();

        String serverUrl = "http://localhost:8642/process?uuid=" + uuid;

        FileSystemResource fileResource = new FileSystemResource(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Файл успешно отправлен.");
        } else {
            System.out.println("Ошибка при отправке файла. Код ответа: " + response.getStatusCode());
        }

        return response.getBody();
    }


    private boolean checkType(String name) {
        return name.endsWith(".mp4") || name.endsWith(".avi");
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File("src/main/resources/videos/" + multipartFile.getOriginalFilename());

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