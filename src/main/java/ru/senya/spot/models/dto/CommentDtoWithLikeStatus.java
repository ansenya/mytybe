package ru.senya.spot.models.dto;

import jakarta.persistence.ManyToMany;
import lombok.Data;
import ru.senya.spot.models.jpa.UserModel;

import java.util.*;

@Data
public class CommentDtoWithLikeStatus {
    private Long id;

    private String text;

    private boolean deleted = false;


    private Date created;

    private Date updated;

    private Set<CommentDto> nextComments;

    private UserDtoWithoutChannels user;

    private boolean likedByThisUser;

    private boolean dislikedByThisUser;

    private List<UserDtoWithoutChannels> likedByUser;

    private List<UserDtoWithoutChannels> dislikedByUser;

    private List<UserDtoWithoutChannels> getLikedByUser() {
        return null;
    }

    private List<UserDtoWithoutChannels> getDislikedByUser() {
        return null;
    }

    public int getLikes() {
        return likedByUser.size();
    }

    public int getDislikes() {
        return dislikedByUser.size();
    }

    public List<Long> getNextComments() {
        try {
            return nextComments.stream().sorted(Comparator.comparing(CommentDto::getId)).map(CommentDto::getId).toList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}
