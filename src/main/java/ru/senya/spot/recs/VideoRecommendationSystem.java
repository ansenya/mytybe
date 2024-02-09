package ru.senya.spot.recs;

import ru.senya.spot.models.jpa.TagModel;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.models.jpa.VideoModel;

import java.util.*;
import java.util.stream.Collectors;

public class VideoRecommendationSystem {

    private final List<VideoModel> videoPool;

    public VideoRecommendationSystem(List<VideoModel> videoPool) {
        this.videoPool = videoPool;
    }

    public List<VideoModel> recommendVideos(UserModel user) {

        List<VideoModel> userHistory = getLastNElements(user.getLastViewed().stream().toList(), 100); // последние 100 видео из истории просмотров. на их основе формируются рекомендации.
        List<VideoModel> likedVideos = getLastNElements(user.getLikedVideos().stream().toList(), 100);
        List<VideoModel> dislikedVideos = getLastNElements(user.getDislikedVideos().stream().toList(), 100);


        Set<TagModel> tagsFromUserHistory = userHistory.stream()
                .flatMap(video -> video.getTags().stream())
                .collect(Collectors.toSet());

        Set<TagModel> tagsFromLikedVideos = likedVideos.stream()
                .flatMap(video -> video.getTags().stream())
                .collect(Collectors.toSet());

        Set<TagModel> tagsFromDislikedVideos = dislikedVideos.stream()
                .flatMap(video -> video.getTags().stream())
                .collect(Collectors.toSet());

        /*
            выше мы в hashSet записываем все теги видоео, которые находятся в истории просмотров.
            то есть, допустим, если в истории 2 видоса с тегами:
                1. машина, ананас, пенис
                2. машина, пенис
            то в сете будет "машина, пенис, ананас".
         */

        Map<TagModel, Integer> tagPopularity = new HashMap<>();
        for (VideoModel video : videoPool) {
            for (TagModel tag : video.getTags()) {
                tagPopularity.put(tag, tagPopularity.getOrDefault(tag, 0) + 1);
            }
        }

        // todo: момент загрузки

        /*
            здесь я использую словарь. в него я записываю все теги, которые у меня в целом есть.
            videoPool - все видео, которые вообще есть в базе данных.

            теги хранятся по "тег" - "популярность"
         */

        List<TagModel> sortedTags = tagsFromUserHistory.stream()
                .sorted(Comparator.comparingInt(tagPopularity::get).reversed())
                .toList();

        /*
            теги из последних просмотренных видео пользователя сортируются по убыванию

            это нужно, чтобы в конечном списке рекомендаций видео были от самого подходящего, к менее подходящему
         */


        Set<VideoModel> recommendedVideos = new HashSet<>();
        for (TagModel tag : sortedTags) {
            for (VideoModel video : videoPool) {
                if (video.getTags().contains(tag) && !userHistory.contains(video) && !dislikedVideos.contains(video)) {
                    recommendedVideos.add(video);
                }
            }
        }

        /*
            тут просто проходим по тегам пользователя и по всем видео в базе данных
            если в видео есть теги из отсортированных тегов И этого видео нет в истории, то он рекомендуется

            в конечном итоге в recommendedVideos будут все видео, которые подходят пользователю. на первом месте будут самые подходящие, а на последнем - наименее подходящие
         */

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
