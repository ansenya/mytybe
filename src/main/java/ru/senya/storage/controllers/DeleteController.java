package ru.senya.storage.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@RestController
public class DeleteController {

//    @DeleteMapping("videos/{id}")
//    public ResponseEntity<?> deleteVideoById(@PathVariable String id) {
//
//    }

    private int findMaxQuality(String name) {
        try {
            int maxQuality = 0;
            try (Stream<Path> paths = Files.walk(Paths.get("vids"))) {
                maxQuality = paths
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(video -> video.startsWith(name))
                        .map(video -> Integer.parseInt(video.split("\\.")[0].split("_")[1]))
                        .max(Integer::compare)
                        .orElse(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return maxQuality;
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }
}
