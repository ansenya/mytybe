package ru.senya.mytybe.services;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.senya.mytybe.models.jpa.VideoModel;
import ru.senya.mytybe.models.redis.RedisVideoModel;
import ru.senya.mytybe.repos.jpa.*;
import ru.senya.mytybe.repos.redis.RedisVideoRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final RedisVideoRepository redisVideoRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private Long lastAddedTime = System.currentTimeMillis();

    public VideoService(VideoRepository videoRepository, RedisVideoRepository redisVideoRepository) {
        this.videoRepository = videoRepository;
        this.redisVideoRepository = redisVideoRepository;
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

    public List<VideoModel> getAll() {
        return videoRepository.getAll();
    }

    public Page<VideoModel> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    public Page<VideoModel> findAllByChannelId(Long channelId, Pageable pageable) {
        return videoRepository.findAllByChannelId(channelId, pageable);
    }

    public VideoModel findById(Long id) {

        if (System.currentTimeMillis() - lastAddedTime > 15 * 60 * 1000) {
            addToRedis();
        }

        RedisVideoModel videoModel = redisVideoRepository.findById(id).orElse(null);
        if (videoModel == null) {
            return videoRepository.findById(id).orElse(null);
        }
        return modelMapper.map(videoModel, VideoModel.class);
    }

    public Optional<VideoModel> findByLink(String path) {
        return videoRepository.findByPath(path);
    }

    public VideoModel save(VideoModel videoModel) {
        return videoRepository.save(videoModel);
    }

    public Page<VideoModel> getPopular(Pageable pageable) {
        List<RedisVideoModel> redisVideoModelList = new LinkedList<>();
        for (RedisVideoModel r : redisVideoRepository.findAll()) {
            redisVideoModelList.add(r);
        }
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min((fromIndex + pageable.getPageSize()), redisVideoModelList.size());

        List<VideoModel> resultList = redisVideoModelList.subList(fromIndex, toIndex).stream().map(redisVideoModel -> modelMapper.map(redisVideoModel, VideoModel.class)).toList();

        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    public void addToRedis() {
        List<RedisVideoModel> popular = videoRepository.getTheMostPopular().stream().map(videoModel -> modelMapper.map(videoModel, RedisVideoModel.class)).toList();
        new Thread(() -> redisVideoRepository.saveAll(popular)).start();
    }

}
