package ru.senya.mytybe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "tags")
public class TagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<VideoModel> videos;

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}