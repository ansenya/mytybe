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
@Table(name = "comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonIgnore
    private VideoModel video;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;


    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
