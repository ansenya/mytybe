package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "channels")
public class ChannelModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer size;

    private boolean deleted = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private ImageModel pfp;

    @OneToMany(mappedBy = "channel")
    private Set<VideoModel> videos;

    @OneToMany(mappedBy = "channel")
    private Set<PostModel> posts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToMany(mappedBy = "subscriptions")
    private Set<UserModel> followers;
}
