package ru.senya.mytybe.models.jpa;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String link;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
}
