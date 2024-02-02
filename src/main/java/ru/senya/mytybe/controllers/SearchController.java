package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.mytybe.models.dto.VideoDto;
import ru.senya.mytybe.models.es.EsVideoModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.repos.es.ElasticVideoRepository;
import ru.senya.mytybe.repos.jpa.VideoRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    final ElasticVideoRepository elasticVideoRepository;
    final VideoRepository videoRepository;

    final ModelMapper modelMapper = new ModelMapper();


    public SearchController(ElasticVideoRepository elasticVideoRepository, VideoRepository videoRepository) {
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoRepository = videoRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "q") String query,
                                    @RequestParam(value = "type", required = false, defaultValue = "v") String type) {
        PageRequest page = PageRequest.of(0, 10);

        Page<EsVideoModel> got = elasticVideoRepository.find(query, page);

        List<Long> videoIds = got.getContent().stream()
                .map(esVideoModel -> Long.valueOf(esVideoModel.getId()))
                .collect(Collectors.toList());

        List<VideoModel> found = videoRepository.findAllById(videoIds);

        PageImpl<VideoDto> resultPage = new PageImpl<>(found.stream()
                .map(videoModel -> modelMapper.map(videoModel, VideoDto.class))
                .toList(), page, found.size());

        return ResponseEntity.ok(resultPage);
    }
}
