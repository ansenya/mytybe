package ru.senya.spot.models.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "streaming_tasks")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StreamingTaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UserModel user;

    @ManyToOne
    private ChannelModel channel;

    private String link;

    private String name;

    private String description;

    private Integer status;
    // 0 - created, but not initialized
    // 1 - created and started
    // 2 - finished
}
