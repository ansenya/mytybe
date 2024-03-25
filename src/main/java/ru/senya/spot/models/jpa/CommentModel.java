package ru.senya.spot.models.jpa;

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


    @Column(length = 1000)
    private String text;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoModel video;

    @OneToMany(mappedBy = "prevComment")
    private Set<CommentModel> nextComments;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentModel prevComment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelModel channel;

    @ManyToMany(mappedBy = "likedComments")
    private Set<UserModel> likedByUser;

    @ManyToMany(mappedBy = "dislikedComments")
    private Set<UserModel> dislikedByUser;

    @CurrentTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    public void delete() {
        this.deleted = true;
        for (var comment : nextComments) {
            comment.delete();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
