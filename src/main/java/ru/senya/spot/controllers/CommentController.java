package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.models.dto.CommentDto;
import ru.senya.spot.models.dto.CommentDtoWithLikeStatus;
import ru.senya.spot.models.dto.VideoDto;
import ru.senya.spot.models.jpa.CommentModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.VideoModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.CommentRepository;
import ru.senya.spot.repos.jpa.UserRepository;
import ru.senya.spot.repos.jpa.VideoRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;


@RequestMapping("comments")
@RestController
public class CommentController {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final ChannelRepository channelRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentController(UserRepository userRepository, VideoRepository videoRepository, CommentRepository commentRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.channelRepository = channelRepository;
        modelMapper = new ModelMapper();
    }


    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication,
                                    @RequestParam(value = "page", required = false) Integer pageNum,
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
            var comments = commentRepository.findAllByVideoIdAndDeletedFalse(videoId);
            List<?> commentsDto;
            if (authentication != null) {
                commentsDto = comments.stream().map(
                        comment -> {
                            var dto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
                            dto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
                            dto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));
                            return dto;
                        }
                ).toList();
            } else {
                commentsDto = comments.stream().map(
                        comment -> {
                            var dto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
                            return dto;
                        }
                ).toList();
            }
            int size = commentsDto.size();

            int fromIndex = (int) page.getOffset();
            int toIndex = Math.min((fromIndex + page.getPageSize()), commentsDto.size());

            return ResponseEntity.ok(new PageImpl<>(commentsDto.subList(fromIndex, toIndex), page, size));
        } else {
            if (!commentRepository.existsById(commentID)) {
                return ResponseEntity.status(404).body("comment does not exist");
            }
            List<CommentDtoWithLikeStatus> comments;
            if (authentication != null) {
                comments =
                        commentRepository
                                .findByIdAndDeletedFalse(commentRepository.getReferenceById(commentID).getId()).get()
                                .getNextComments()
                                .stream()
                                .filter(comment -> !comment.isDeleted())
                                .map(comment -> {
                                    var dto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
                                    dto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
                                    dto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));
                                    return dto;
                                }).toList();
            } else {
                comments =
                        commentRepository
                                .findByIdAndDeletedFalse(commentRepository.getReferenceById(commentID).getId()).get()
                                .getNextComments()
                                .stream()
                                .filter(comment -> !comment.isDeleted())
                                .map(comment -> modelMapper.map(comment, CommentDtoWithLikeStatus.class)).toList();
            }

            int size = comments.size();

            int fromIndex = (int) page.getOffset();
            int toIndex = Math.min((fromIndex + page.getPageSize()), comments.size());

            return ResponseEntity.ok(new PageImpl<>(comments.subList(fromIndex, toIndex), page, size));
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id,
                                        Authentication authentication) {
        var optComment = commentRepository.findByIdAndDeletedFalse(id);

        if (optComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var comment = optComment.get();

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        if (userRepository.existsByUsername(authentication.getName())) {
            var user = userRepository.findByUsername(authentication.getName());
            var commentDto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
            commentDto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
            commentDto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));

            return ResponseEntity.ok(commentDto);
        } else {
            return ResponseEntity.status(404).body("user not found");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        var comment = commentRepository.findByIdAndDeletedFalse(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        var prevComment = commentRepository.findByNextComments(comment);
        prevComment.ifPresent(commentModel -> {
            commentModel.getNextComments().remove(comment);
            commentRepository.save(prevComment.get());
        });
        comment.delete();
        commentRepository.saveAll(comment.getNextComments());
        commentRepository.save(comment);

        return ResponseEntity.ok().build();
    }

    @PostMapping("like/{id}")
    public ResponseEntity<?> likeComment(@PathVariable Long id,
                                         Authentication authentication) {
        var optComment = commentRepository.findByIdAndDeletedFalse(id);

        if (optComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var comment = optComment.get();
        UserModel user = null;
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            user = userRepository.findByUsername(authentication.getName());
            if (comment.getLikedByUser().contains(user)) {
                comment.getLikedByUser().remove(user);
                user.getLikedComments().remove(comment);
            } else {
                comment.getLikedByUser().add(user);
                user.getLikedComments().add(comment);

                comment.getDislikedByUser().remove(user);
                user.getDislikedComments().remove(comment);
            }
        } else {
            return ResponseEntity.status(401).build();
        }
        commentRepository.save(comment);
        userRepository.save(user);
        var commentDto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
        commentDto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
        commentDto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));

        return ResponseEntity.ok().body(commentDto);
    }

    @PostMapping("dislike/{id}")
    public ResponseEntity<?> dislikeComment(@PathVariable Long id,
                                            Authentication authentication) {
        var optComment = commentRepository.findByIdAndDeletedFalse(id);

        if (optComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var comment = optComment.get();
        UserModel user;
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            user = userRepository.findByUsername(authentication.getName());
            if (comment.getDislikedByUser().contains(user)) {
                comment.getDislikedByUser().remove(user);
                user.getDislikedComments().remove(comment);
            } else {
                comment.getDislikedByUser().add(user);
                user.getDislikedComments().add(comment);

                comment.getLikedByUser().remove(user);
                user.getLikedComments().remove(comment);
            }
        } else {
            return ResponseEntity.status(401).build();
        }
        commentRepository.save(comment);
        userRepository.save(user);
        var commentDto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
        commentDto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
        commentDto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));

        return ResponseEntity.ok().body(commentDto);
    }

    @PostMapping("create")
    public ResponseEntity<?> leaveComment(@RequestParam(value = "text", required = false) String text,
                                          @RequestParam(value = "commentId", required = false) Long commentId,
                                          @RequestParam(value = "videoId", required = false) Long videoId,
                                          Authentication authentication) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("text is required");
        }
        if (videoId == null && commentId == null) {
            return ResponseEntity.badRequest().body("either videoId or commentId is required");
        }
        UserModel userModel = userRepository.findByUsername(authentication.getName());
        if (videoId != null) {
            VideoModel videoModel = videoRepository.findById(videoId).orElse(null);
            if (videoModel == null) {
                return ResponseEntity.badRequest().body("video is null");
            }
            CommentModel comment = CommentModel.builder()
                    .video(videoModel)
                    .user(userModel)
                    .likedByUser(new HashSet<>())
                    .dislikedByUser(new HashSet<>())
                    .text(text)
                    .build();

            try {
                comment = commentRepository.save(comment);
            } catch (DataIntegrityViolationException exception) {
                return ResponseEntity.status(400).body("text too long");
            }

            videoModel.getComments().add(comment);

            videoRepository.save(videoModel);

            var commentDto = modelMapper.map(comment, CommentDtoWithLikeStatus.class);
            commentDto.setLikedByThisUser(comment.getLikedByUser().contains(userRepository.findByUsername(authentication.getName())));
            commentDto.setDislikedByThisUser(comment.getDislikedByUser().contains(userRepository.findByUsername(authentication.getName())));

            return ResponseEntity.ok(commentDto);
        } else {

            CommentModel commentModel = commentRepository.findByIdAndDeletedFalse(commentId).orElse(null);

            if (commentModel == null) {
                return ResponseEntity.status(404).body("comment does not exist");
            }

            CommentModel nextComment = CommentModel.builder()
                    .video(null)
                    .user(userModel)
                    .prevComment(commentModel)
                    .likedByUser(new HashSet<>())
                    .dislikedByUser(new HashSet<>())
                    .text(text)
                    .build();

            nextComment = commentRepository.save(nextComment);

            commentModel.getNextComments().add(nextComment);

            commentModel = commentRepository.save(commentModel);


            return ResponseEntity.ok(modelMapper.map(nextComment, CommentDto.class));
        }
    }


}
