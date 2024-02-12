package ru.senya.spot.controllers.video;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.senya.spot.models.es.EsVideoModel;
import ru.senya.spot.models.jpa.TagModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.es.ElasticVideoRepository;
import ru.senya.spot.repos.jpa.TagRepository;
import ru.senya.spot.services.VideoService;

@RestController
@RequestMapping("video")
@CrossOrigin(origins = "*")
public class ApiController {

    private final VideoService videoService;
    private final TagRepository tagRepository;
    private final ElasticVideoRepository elasticVideoRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;

    public ApiController(VideoService videoService, TagRepository tagRepository, ElasticVideoRepository elasticVideoRepository) {
        this.videoService = videoService;
        this.tagRepository = tagRepository;
        this.elasticVideoRepository = elasticVideoRepository;
        gson = new Gson();
        modelMapper = new ModelMapper();
    }

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


    @GetMapping("eta")
    public ResponseEntity<?> getEta(@RequestParam(value = "id") Long id) {
        VideoModel videoModel = videoService.findById(id);

        if (videoModel == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return ResponseEntity.ok().headers(headers).body(requestEta(videoModel.getVid_uuid()));
    }


    private String requestEta(String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8642/progress?id=" + uuid, String.class);

        return response.getBody();
    }

}
