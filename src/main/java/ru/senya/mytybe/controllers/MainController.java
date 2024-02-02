package ru.senya.mytybe.controllers;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.mytybe.models.dto.ImageDto;
import ru.senya.mytybe.models.dto.UserDto;
import ru.senya.mytybe.models.es.EsVideoModel;
import ru.senya.mytybe.models.jpa.UserModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.models.redis.RedisVideoModel;
import ru.senya.mytybe.repos.es.ElasticVideoRepository;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.ImagesRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.repos.jpa.VideoRepository;
import ru.senya.mytybe.repos.redis.RedisVideoRepository;

@RestController
public class MainController {

    final
    UserRepository userRepository;

    final
    ChannelRepository channelRepository;
    final
    VideoRepository videoRepository;
    final
    ImagesRepository imagesRepository;
    final
    RedisVideoRepository redisVideoRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    final
    ElasticVideoRepository elasticsearchRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public MainController(UserRepository userRepository, ChannelRepository channelRepository, VideoRepository videoRepository, ImagesRepository imagesRepository, RedisVideoRepository redisVideoRepository, RedisTemplate<String, Object> redisTemplate, ElasticVideoRepository elasticsearchRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.imagesRepository = imagesRepository;
        this.redisVideoRepository = redisVideoRepository;
        this.redisTemplate = redisTemplate;
        this.elasticsearchRepository = elasticsearchRepository;
    }


    @GetMapping("cock")
    public ResponseEntity<?> getTest() {
        var vids = videoRepository.findAll();
        for (var vid : vids) {
            elasticsearchRepository.save(modelMapper.map(vid, EsVideoModel.class));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("test")
    public ResponseEntity<?> postTest() {
        VideoModel videoModel = videoRepository.findById(1L).orElse(null);

        RedisVideoModel redisVideoModel = modelMapper.map(videoModel, RedisVideoModel.class);

        redisVideoRepository.save(redisVideoModel);

        return ResponseEntity.ok(redisVideoModel);
    }

    @DeleteMapping("test")
    public ResponseEntity<?> deleteTest() {
        redisVideoRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    @PostMapping("test2")
    public ResponseEntity<?> pfp(Authentication authentication) {
        UserModel userModel = userRepository.findByUsername(authentication.getName());

//        ImageModel pfp = ImageModel.builder()
//                .type("pfp")
//                .user(userModel)
//                .build();
//        imagesRepository.save(pfp);;
//
//        userModel.setPfp(pfp);
//
//        userRepository.save(userModel);

        System.out.println(new Gson().toJson(modelMapper.map(userModel.getPfp(), ImageDto.class)));

        UserDto userDto = modelMapper.map(userModel, UserDto.class);

        System.out.println(new Gson().toJson(userDto));

//        System.out.println(userDto.getPfp());

        return ResponseEntity.ok(userDto);
    }

}
