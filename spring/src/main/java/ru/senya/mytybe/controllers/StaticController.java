package ru.senya.mytybe.controllers;

import com.nimbusds.jose.util.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("static")
public class StaticController extends BaseController{

    @GetMapping("vid")
    public ResponseEntity<byte[]> serveVideo(@RequestParam(value = "fileName") String videoFileName) throws IOException {
        Path videoPath = Path.of("src/main/resources/videos", videoFileName + ".mp4");

        if (!videoPath.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] videoData = Files.readAllBytes(videoPath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.setContentLength(videoData.length);
        headers.setContentDispositionFormData(videoFileName, videoFileName);

        return new ResponseEntity<>(videoData, headers, HttpStatus.OK);
    }

    @GetMapping("img")
    public ResponseEntity<byte[]> serveImg(@RequestParam(value = "fileName") String imgFileName) throws IOException {
        Path filepath = Paths.get("src/main/resources/images", imgFileName);

        if (!filepath.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imgData = Files.readAllBytes(filepath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/jpeg"));

        return new ResponseEntity<>(imgData, headers, HttpStatus.OK);
    }
}
