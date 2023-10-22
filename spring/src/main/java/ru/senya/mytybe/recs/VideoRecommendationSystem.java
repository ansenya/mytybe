package ru.senya.mytybe.recs;

import ru.senya.mytybe.models.TagModel;
import ru.senya.mytybe.models.VideoModel;

import java.util.*;
import java.util.stream.Collectors;

public class VideoRecommendationSystem {

    private final List<VideoModel> videoPool;

    public VideoRecommendationSystem(List<VideoModel> videoPool) {
        this.videoPool = videoPool;
    }

    public List<VideoModel> recommendVideos(List<VideoModel> userHistory) {
        userHistory = getLastNElements(userHistory, 100);

        Set<TagModel> userTags = userHistory.stream()
                        .flatMap(video -> video.getTags().stream())
                        .collect(Collectors.toSet());

        Map<TagModel, Integer> tagPopularity = new HashMap<>();
        for (VideoModel video : videoPool) {
            for (TagModel tag : video.getTags()) {
                tagPopularity.put(tag, tagPopularity.getOrDefault(tag, 0) + 1);
            }
        }

        List<TagModel> sortedTags = userTags.stream()
                .sorted(Comparator.comparingInt(tagPopularity::get).reversed())
                .toList();

        Set<VideoModel> recommendedVideos = new HashSet<>();
        for (TagModel tag : sortedTags) {
            for (VideoModel video : videoPool) {
                if (video.getTags().contains(tag) && !userHistory.contains(video)) {
                    recommendedVideos.add(video);
                }
            }
        }

        System.out.println(recommendedVideos.size());

        return recommendedVideos.stream().toList();
    }

    List<VideoModel> getLastNElements(List<VideoModel> list, int n) {
        int size = list.size();
        if (size <= n) {
            return list;
        } else {
            return list.subList(size - n, size);
        }
    }

}
