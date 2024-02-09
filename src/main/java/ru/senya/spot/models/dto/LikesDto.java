package ru.senya.spot.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
public class LikesDto {
    private Set<UserDtoWithoutChannels> likedByUser;

    private Set<UserDtoWithoutChannels> dislikedByUser;

    @Getter
    int page;

    @Getter
    int pageSize;

    public List<UserDtoWithoutChannels> getLikedByUser() {
        return getItemsFromSet(likedByUser, page);
    }

    public List<UserDtoWithoutChannels> getDislikedByUser() {
        return getItemsFromSet(dislikedByUser, page);
    }

    private List<UserDtoWithoutChannels> getItemsFromSet(Set<UserDtoWithoutChannels> set, int page) {
        return set.stream()
                .skip((long) pageSize * (page - 1))
                .limit(pageSize)
                .collect(Collectors.toList());
    }
}
