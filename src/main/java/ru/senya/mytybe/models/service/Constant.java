package ru.senya.mytybe.models.service;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "constants")
public class Constant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String k, val;
}
