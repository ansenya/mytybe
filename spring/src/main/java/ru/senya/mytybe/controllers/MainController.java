package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.models.es.EsVideoModel;
import ru.senya.mytybe.repos.es.ElasticVideoRepository;
import ru.senya.mytybe.repos.jpa.*;

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
    ElasticVideoRepository elasticsearchRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public MainController(UserRepository userRepository, ChannelRepository channelRepository, VideoRepository videoRepository, ImagesRepository imagesRepository, ElasticVideoRepository elasticsearchRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.imagesRepository = imagesRepository;
        this.elasticsearchRepository = elasticsearchRepository;
    }


//    @GetMapping("penis")
//    public ResponseEntity<?> penis() {
//        ImageModel.ImageType imageType = ImageModel.ImageType.builder()
//                .type("thumbnail")
//                .build();
//
//        return ResponseEntity.ok(imageTypeRepository.save(imageType));
//    }

//    @GetMapping("test")
//    public ResponseEntity<?> test() {
//
//        ImageModel.ImageType imageType = imageTypeRepository.findById(1L).get();
//
//
//        List<VideoModel> videoModels = videoRepository.getAll();
//
//        for (VideoModel videoModel : videoModels) {
//
//            ImageModel imageModel = ImageModel.builder()
//                    .type(imageType)
//                    .path("def")
//                    .video(videoModel)
//                    .build();
//
//            videoModel.setThumbnail(imageModel);
//
//            imagesRepository.save(imageModel);
//
//            videoRepository.save(videoModel);
//        }
//
//
//        return ResponseEntity.ok(videoModels.stream().map(videoModel -> modelMapper.map(videoModel, VideoDto.class)));
//    }


    @GetMapping("test2")
    public ResponseEntity<?> test2() {
        EsVideoModel esVideoModel;

        VideoModel videoModel = videoRepository.findById(6L).get();

        esVideoModel = modelMapper.map(videoModel, EsVideoModel.class);

        elasticsearchRepository.save(esVideoModel);

        return ResponseEntity.ok(esVideoModel);
    }

    @GetMapping("test3")
    public ResponseEntity<?> test3() {
        return ResponseEntity.ok(elasticsearchRepository.find("фв", PageRequest.of(0, 10)));
    }

}
