package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.dto.CommentDto;
import ru.senya.mytybe.dto.VideoDto;
import ru.senya.mytybe.models.CommentModel;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.VideoModel;
import ru.senya.mytybe.repos.CommentRepository;
import ru.senya.mytybe.repos.UserRepository;
import ru.senya.mytybe.repos.VideoRepository;

import java.util.Objects;


@RequestMapping("com")
@RestController
public class CommentController extends BaseController{

    final UserRepository userRepository;
    final VideoRepository videoRepository;
    final CommentRepository commentRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public CommentController(UserRepository userRepository, VideoRepository videoRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort,
                                    @RequestParam(value = "videoId", required = false, defaultValue = "-1") Long videoId) {
        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        if (videoId == null) {
            return ResponseEntity.badRequest().body("videoId is null");
        }

        Sort.Direction direction;

        if (Objects.equals(sort, "desc")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
        Page<CommentModel> commentPage = commentRepository.findAll(page);
        Page<CommentDto> commentDtoPage = commentPage.map(commentModel -> modelMapper.map(commentModel, CommentDto.class));

        return ResponseEntity.ok(commentDtoPage);
    }


    @GetMapping("/comment")
    public ResponseEntity<?> getComment(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is null");
        }

        CommentModel comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(modelMapper.map(comment, CommentDto.class));
    }

    @PostMapping("/comment")
    public ResponseEntity<?> leaveComment(@RequestParam(value = "text", required = false) String text,
                                          @RequestParam(value = "videoId", required = false) Long id,
                                          Authentication authentication) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("text is required");
        }

        if (id == null) {
            return ResponseEntity.badRequest().body("id is required");
        }

        UserModel userModels = userRepository.findByUsername(authentication.getName());

        VideoModel videoModel = videoRepository.findById(id).orElse(null);

        if (videoModel == null) {
            return ResponseEntity.badRequest().body("video is null");
        }

        CommentModel commentModel = CommentModel.builder()
                .video(videoModel)
                .user(userModels)
                .text(text)
                .build();


        videoModel.getComments().add(commentModel);

        videoRepository.save(videoModel);
        commentModel = commentRepository.save(commentModel);

        return ResponseEntity.ok(modelMapper.map(commentModel, CommentDto.class));
    }

}
