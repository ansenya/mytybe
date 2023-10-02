package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class PostModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chanel_id")
    private ChannelModel channel;

    private String title;
    private String content;

    @OneToMany(mappedBy = "post")
    private Set<ImageModel> image;

    @OneToOne(mappedBy = "post")
    private PollModel polls;

}
