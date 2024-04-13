package ru.senya.spot.controllers.video;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.repos.es.ElasticVideoRepository;
import ru.senya.spot.repos.jpa.CommentRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.services.VideoService;

@RestController
@RequestMapping("videos")
public class DeleteController {

    private final VideoService videoService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ElasticVideoRepository elasticVideoRepository;


    @Autowired
    public DeleteController(VideoService videoService, UserRepository userRepository, CommentRepository commentRepository, ElasticVideoRepository elasticVideoRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.elasticVideoRepository = elasticVideoRepository;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id,
                                       Authentication authentication) {
        var video = videoService.findById(id);
        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        var user = userRepository.findByUsername(authentication.getName());
        if (video.getChannel().getUser().getId().equals(user.getId()) || user.getUsername().equals("admin")) {
            video.setDeleted(true);
            for (var comment : video.getComments()) {
                comment.delete();
                commentRepository.save(comment);
            }
            videoService.save(video);
            elasticVideoRepository.deleteById(String.valueOf(video.getId()));
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(401).build();
    }

    private void sendDeleteRequestToStorage(String uuid) {
        String url = "http://st:1984/api/videos/delete/" + uuid;
        new RestTemplate().exchange(url, HttpMethod.POST, null, String.class);
    }
}
