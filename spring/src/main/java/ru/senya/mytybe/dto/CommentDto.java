package ru.senya.mytybe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.senya.mytybe.models.UserModel;
import ru.senya.mytybe.models.VideoModel;

import java.util.Date;

@Data
public class CommentDto {
    private Long id;

    private String text;

    private boolean deleted = false;


    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDtoWithoutChannels user;
}
