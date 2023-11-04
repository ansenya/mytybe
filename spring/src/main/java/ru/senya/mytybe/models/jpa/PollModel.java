package ru.senya.mytybe.models.jpa;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "polls")
public class PollModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @OneToMany(mappedBy = "poll")
    private Set<Element> element;

    @OneToOne
    @JoinColumn(name = "post_id")
    private PostModel post;

    @Data
    @Entity
    static class Element {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String answer;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private UserModel user;

        @ManyToOne
        @JoinColumn(name = "poll_id")
        private PollModel poll;
    }
}