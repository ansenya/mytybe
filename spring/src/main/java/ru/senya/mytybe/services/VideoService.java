package ru.senya.mytybe.services;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.repos.jpa.*;

import java.util.List;

@Service

public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ImagesRepository imagesRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public VideoService(UserRepository userRepository, VideoRepository videoRepository, ChannelRepository channelRepository, ImagesRepository imagesRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
        this.imagesRepository = imagesRepository;
        this.commentRepository = commentRepository;
    }

    public Page<VideoModel> getAll(List<Long> specificIds, Pageable pageable) {
        List<VideoModel> resultList;
        if (specificIds.isEmpty()) {
            return videoRepository.findAll(pageable);
        } else {
            List<VideoModel> inSpecificIds = videoRepository.findInSpecificIds(specificIds);
            List<VideoModel> notInSpecificIds = videoRepository.findNotInSpecificIds(specificIds);

            inSpecificIds.addAll(notInSpecificIds);

            int fromIndex = (int) pageable.getOffset();
            int toIndex = Math.min((fromIndex + pageable.getPageSize()), inSpecificIds.size());

            resultList = inSpecificIds.subList(fromIndex, toIndex);
        }

        return new PageImpl<>(resultList, pageable, resultList.size());
    }
}
