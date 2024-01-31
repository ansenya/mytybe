package ru.senya.mytybe.models.dto;

import lombok.Data;

import java.util.*;

@Data
public class CommentDto {
    private Long id;

    private String text;

    private boolean deleted = false;


    private Date created;

    private Date updated;

    private Set<CommentDto> nextComments;

    private UserDtoWithoutChannels user;

    private ChannelDto channel;

    public List<CommentDto> getNextComments() {
        try {
            List<CommentDto> commentDtoList = new ArrayList<>(nextComments.stream().toList());
            commentDtoList.sort(Comparator.comparing(o -> o.id));
            return commentDtoList;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

//    private UserDto getUser() {
//        return null;
//    }
//
//    private ChannelDto getChannel() {
//        return null;
//    }

//    public Object getOwner() {
//        if (channel != null) {
//            return channel;
//        }
//        return user;
//    }
}
