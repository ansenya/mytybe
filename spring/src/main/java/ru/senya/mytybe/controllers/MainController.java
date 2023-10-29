package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.dto.VideoDto;
import ru.senya.mytybe.models.ImageModel;
import ru.senya.mytybe.models.VideoModel;
import ru.senya.mytybe.repos.*;

import java.util.List;

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
    ImageTypeRepository imageTypeRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public MainController(UserRepository userRepository, ChannelRepository channelRepository, VideoRepository videoRepository, ImagesRepository imagesRepository, ImageTypeRepository imageTypeRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.videoRepository = videoRepository;
        this.imagesRepository = imagesRepository;
        this.imageTypeRepository = imageTypeRepository;
    }


    @GetMapping("penis")
    public ResponseEntity<?> penis() {
        ImageModel.ImageType imageType = ImageModel.ImageType.builder()
                .type("thumbnail")
                .build();

        return ResponseEntity.ok(imageTypeRepository.save(imageType));
    }

    @GetMapping("test")
    public ResponseEntity<?> test() {

        ImageModel.ImageType imageType = imageTypeRepository.findById(1L).get();


        List<VideoModel> videoModels = videoRepository.getAll();

        for (VideoModel videoModel : videoModels) {

            ImageModel imageModel = ImageModel.builder()
                    .type(imageType)
                    .path("def")
                    .video(videoModel)
                    .build();

            videoModel.setThumbnail(imageModel);

            imagesRepository.save(imageModel);

            videoRepository.save(videoModel);
        }


        return ResponseEntity.ok(videoModels.stream().map(videoModel -> modelMapper.map(videoModel, VideoDto.class)));
    }


}
