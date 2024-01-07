package ru.senya.mytybe.models.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "tags")
public class TagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String enTag;

    private String ruTag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<VideoModel> videos;

    @Override
    public int hashCode() {
        return Objects.hash(enTag);
    }
}