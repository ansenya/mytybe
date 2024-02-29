package ru.senya.storage.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.senya.storage.controller.Utils.save;

@RestController
public class PhotoController {

    @GetMapping("img")
    public ResponseEntity<byte[]> serveImg(@RequestParam(value = "filename") String imgFileName) throws IOException {
        Path filepath = Paths.get("imgs", imgFileName);

        if (!filepath.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imgData = Files.readAllBytes(filepath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/jpeg"));

        return new ResponseEntity<>(imgData, headers, HttpStatus.OK);
    }

    @PostMapping("img/upload")
    public ResponseEntity<?> uploadImg(@RequestParam("file") MultipartFile img, @RequestParam("uuid") String uuid, @RequestParam("type") String type) {
        try {
            savePhoto(img, uuid, type);
        } catch (IOException exception) {
            return ResponseEntity.status(123).body("не сохранилось");
        }
        return ResponseEntity.ok().build();
    }

    private void savePhoto(MultipartFile multipartFile, String uuid, String type) throws IOException {
        File file = new File("imgs/" + uuid + "." + type);
        save(multipartFile, file);
    }
}
