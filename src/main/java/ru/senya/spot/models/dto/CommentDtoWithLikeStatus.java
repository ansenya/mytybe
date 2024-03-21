package ru.senya.spot.models.dto;

import lombok.Data;

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

    public List<CommentDto> getNextComments() {
        try {
            List<CommentDto> commentDtoList = new ArrayList<>(nextComments.stream().toList());
            commentDtoList.sort(Comparator.comparing(CommentDto::getId));
            return commentDtoList;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}
