package ru.senya.spot.models.dto;

import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

import static ru.senya.spot.MytybeApplication.*;

@Data
public class ImageDto {
    @Id
    private Long id;

    private String path;

    private String type;

    private Date created;

    private Date updated;

    private boolean deleted = false;

    public String getFalsePath() {
        return "http://" + STORAGE_HOST + "/img?filename=" + path;
    }
}
