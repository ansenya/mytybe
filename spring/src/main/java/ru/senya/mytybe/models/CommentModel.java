package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoModel video;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
}
