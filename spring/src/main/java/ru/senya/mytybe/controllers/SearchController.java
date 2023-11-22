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

@RestController
public class SearchController extends BaseController {

    final ElasticVideoRepository elasticVideoRepository;
    final VideoRepository videoRepository;

    final ModelMapper modelMapper = new ModelMapper();


    public SearchController(ElasticVideoRepository elasticVideoRepository, VideoRepository videoRepository) {
        this.elasticVideoRepository = elasticVideoRepository;
        this.videoRepository = videoRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "text") String text) {

        PageRequest page = PageRequest.of(0, 10);

        Page<EsVideoModel> got = elasticVideoRepository.find(text, page);

        elasticVideoRepository.findAll(page);

        List<VideoModel> found = new LinkedList<>();

        for (EsVideoModel esVideoModel : got) {
            found.add(videoRepository.findById(Long.valueOf(esVideoModel.getId())).get());
        }

        return ResponseEntity.ok(new PageImpl<>(found.stream().map(videoModel -> modelMapper.map(videoModel, VideoDto.class)).toList(), page, found.size()));
    }
}
