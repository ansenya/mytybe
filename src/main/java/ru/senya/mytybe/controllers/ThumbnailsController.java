package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.mytybe.repos.jpa.ImagesRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.services.VideoService;


@RestController
@RequestMapping("videos")
public class ThumbnailsController {

    final VideoService videoService;
    final UserRepository userRepository;
    final ImagesRepository imagesRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public ThumbnailsController(VideoService videoService, UserRepository userRepository, ImagesRepository imagesRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
    }


}
