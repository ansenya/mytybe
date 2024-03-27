package ru.senya.storage.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.senya.storage.controllers.Utils.findVideoPath;

@RestController
public class DownloadingController {

    @GetMapping("download/{name}?")
    public ResponseEntity<?> download(@PathVariable String name,
                                      @RequestParam(name = "type") String type,
                                      @RequestParam(name = "q", required = false, defaultValue = "") String q) throws IOException {
        if (type.equals("video")) {
            Path path = Path.of(findVideoPath(name + ".mp4", q) + ".mp4");
            System.out.println(path);
            if (!path.toFile().exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] videoData = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentLength(videoData.length);
//            headers.setContentDispositionFormData(videoFileName, videoFileName);

            return new ResponseEntity<>(videoData, headers, 200);
        }
        return ResponseEntity.badRequest().build();

    }

}
