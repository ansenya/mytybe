package ru.senya.mytybe.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "images")
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private ImageType type;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostModel post;

    @OneToOne(mappedBy = "pfp")
    private UserModel user;

    @OneToOne(mappedBy = "pfp")
    private ChannelModel channel;

    @OneToOne(mappedBy = "thumbnail")
    private VideoModel video;

    private boolean deleted = false;

    @Data
    @Entity
    @Table(name = "types")
    static class ImageType {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        private String type;

        @OneToMany(mappedBy = "type")
        private Set<ImageModel> image;
    }
}
