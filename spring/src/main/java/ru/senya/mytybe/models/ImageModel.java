package ru.senya.mytybe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static ru.senya.mytybe.MytybeApplication.IP;
import static ru.senya.mytybe.MytybeApplication.PORT;

@Data
@Entity
@Table(name = "images")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
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
    @JsonIgnore
    private PostModel post;

    @OneToOne(mappedBy = "pfp")
    @JsonIgnore
    private UserModel user;

    @OneToOne(mappedBy = "pfp")
    @JsonIgnore
    private ChannelModel channel;

    @OneToOne(mappedBy = "thumbnail")
    @JsonIgnore
    private VideoModel video;

    private boolean deleted = false;



    @Data
    @Entity
    @Table(name = "types")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageType {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        private String type;

        @OneToMany(mappedBy = "type")
        private Set<ImageModel> image;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
