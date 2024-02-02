package ru.senya.mytybe.models.jpa;

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

    private String type;

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

    @OneToOne(mappedBy = "chp")
    @JsonIgnore
    private ChannelModel channel;

    @OneToOne(mappedBy = "thumbnail")
    @JsonIgnore
    private VideoModel video;

    private boolean deleted = false;

    public static ImageModelBuilder builder() {
        return new ImageModelBuilder().path("def.png");
    }

//    public String getPath() {
//        return "http://" + IP + ":" + PORT + "/api/static/img?fileName=" + path;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
