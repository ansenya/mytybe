package ru.senya.mytybe.models.dto;

import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

import static ru.senya.mytybe.MytybeApplication.*;

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
        return "http://" + STORAGE_IP + ":" + STORAGE_PORT + "/api/img?filename=" + path;
    }
}
