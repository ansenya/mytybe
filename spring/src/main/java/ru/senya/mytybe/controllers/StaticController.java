package ru.senya.mytybe.controllers;

import com.nimbusds.jose.util.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class StaticController {

    @GetMapping("watch")
    public ResponseEntity<byte[]> serveVideo(@RequestParam(value = "fileName") String videoFileName) throws IOException {
        Path videoPath = Path.of("/home/senya/IdeaProjects/mytybe/spring/src/main/resources/videos", videoFileName + ".mp4");

        // Проверьте, существует ли файл
        if (!videoPath.toFile().exists()) {
            // Верните код 404, если файл не найден
            return ResponseEntity.notFound().build();
        }

        byte[] videoData = Files.readAllBytes(videoPath);

        // Определите MIME-тип видео (например, для MP4)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.setContentLength(videoData.length);
        headers.setContentDispositionFormData(videoFileName, videoFileName);

        return new ResponseEntity<>(videoData, headers, 200);
    }
}
