package ru.senya.spot.models.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@Table(name = "tasks")
public class ProcessingTaskModel {

    @Id
    private Long id;

    private String uuid;

    @CurrentTimestamp
    private Date created;
    @UpdateTimestamp
    private Date updated;
}
