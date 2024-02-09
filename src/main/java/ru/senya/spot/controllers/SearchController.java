package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.es.EsVideoModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.es.ElasticVideoRepository;
import ru.senya.spot.repos.jpa.VideoRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    private final ElasticVideoRepository elasticVideoRepository;
    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;

    public SearchController(ElasticVideoRepository elasticVideoRepository, VideoRepository videoRepository) {
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoRepository = videoRepository;
        modelMapper = new ModelMapper();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "q") String query,
                                    @RequestParam(value = "type", required = false, defaultValue = "v") String type,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        try {
            Page<EsVideoModel> searchResultPage = elasticVideoRepository.find(query, pageRequest);

            List<Long> videoIds = searchResultPage.getContent().stream()
                    .map(esVideoModel -> Long.valueOf(esVideoModel.getId()))
                    .collect(Collectors.toList());

            List<VideoModel> foundVideos = videoRepository.findAllById(videoIds);

            List<VideoDto> videoDtos = foundVideos.stream()
                    .map(videoModel -> modelMapper.map(videoModel, VideoDto.class))
                    .collect(Collectors.toList());

            PageImpl<VideoDto> resultPage = new PageImpl<>(videoDtos, pageRequest, searchResultPage.getTotalElements());

            return ResponseEntity.ok(resultPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during search.");
        }
    }

}
