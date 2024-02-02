package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.models.dto.CommentDto;
import ru.senya.mytybe.models.jpa.ChannelModel;
import ru.senya.mytybe.models.jpa.CommentModel;
import ru.senya.mytybe.models.jpa.UserModel;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.repos.jpa.ChannelRepository;
import ru.senya.mytybe.repos.jpa.CommentRepository;
import ru.senya.mytybe.repos.jpa.UserRepository;
import ru.senya.mytybe.repos.jpa.VideoRepository;

import java.util.List;
import java.util.Objects;


@RequestMapping("comments")
@RestController
public class CommentController {

    final UserRepository userRepository;
    final VideoRepository videoRepository;
    final CommentRepository commentRepository;
    final ChannelRepository channelRepository;


    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public CommentController(UserRepository userRepository, VideoRepository videoRepository, CommentRepository commentRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.channelRepository = channelRepository;
    }


    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer pageNum,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                    @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort,
                                    @RequestParam(value = "videoId", required = false, defaultValue = "-1") Long videoId,
                                    @RequestParam(value = "commentId", required = false) Long commentID) {

        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        Sort.Direction direction;

        if (Objects.equals(sort, "desc")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));

        if (commentID == null) {
            if (!videoRepository.existsById(videoId)) {
                return ResponseEntity.status(404).build();
            }
            Page<CommentModel> commentPage = commentRepository.findAllByVideo(page, videoRepository.getReferenceById(videoId));
            Page<CommentDto> commentDtoPage = commentPage.map(commentModel -> modelMapper.map(commentModel, CommentDto.class));
            return ResponseEntity.ok(commentDtoPage);
        } else {
            if (!commentRepository.existsById(commentID)) {
                return ResponseEntity.status(404).body("comment is null");
            }

            List<CommentDto> comments =
                    commentRepository.findById(commentRepository.getReferenceById(commentID).getId()).get().getNextComments().stream().map(commentModel -> modelMapper.map(commentModel, CommentDto.class)).toList();

            int fromIndex = (int) page.getOffset();
            int toIndex = Math.min((fromIndex + page.getPageSize()), comments.size());

            return ResponseEntity.ok(new PageImpl<>(comments.subList(fromIndex, toIndex), page, comments.size()));
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("id is null");
        }

        CommentModel comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(modelMapper.map(comment, CommentDto.class));
    }

    @PostMapping("create")
    public ResponseEntity<?> leaveComment(@RequestParam(value = "text", required = false) String text,
                                          @RequestParam(value = "commentId", required = false) Long commentId,
                                          @RequestParam(value = "channelId", required = false) Long channelId,
                                          @RequestParam(value = "videoId", required = false) Long videoId,
                                          Authentication authentication) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("text is required");
        }

        if (videoId == null && commentId == null) {
            return ResponseEntity.badRequest().body("either videoId or commentId is required");
        }

        ChannelModel channelModel = null;
        if (channelId != null) {
            channelModel = channelRepository.findById(channelId).orElse(null);
        }

        UserModel userModel = userRepository.findByUsername(authentication.getName());

        if (videoId != null) {
            VideoModel videoModel = videoRepository.findById(videoId).orElse(null);

            if (videoModel == null) {
                return ResponseEntity.badRequest().body("video is null");
            }


            CommentModel commentModel = CommentModel.builder()
                    .video(videoModel)
                    .user(userModel)
                    .text(text)
                    .build();

            if (channelModel != null) {
                commentModel.setChannel(channelModel);
            }


            videoModel.getComments().add(commentModel);

            videoRepository.save(videoModel);
            commentModel = commentRepository.save(commentModel);

            return ResponseEntity.ok(modelMapper.map(commentModel, CommentDto.class));
        } else {
            if (channelModel == null) {
                return ResponseEntity.badRequest().body("channel is null");
            }

            CommentModel commentModel = commentRepository.findById(commentId).orElse(null);

            if (commentModel == null) {
                return ResponseEntity.badRequest().body("comment is null");
            }

            CommentModel nextComment = CommentModel.builder()
                    .video(null)
                    .user(userModel)
                    .channel(channelModel)
                    .prevComment(commentModel)
                    .text(text)
                    .build();

            nextComment = commentRepository.save(nextComment);

            commentModel.getNextComments().add(nextComment);

            commentModel = commentRepository.save(commentModel);

            return ResponseEntity.ok(modelMapper.map(commentModel, CommentDto.class));

        }
    }


}
