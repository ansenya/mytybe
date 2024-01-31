package com.ask.home.videostream.controller;

import com.ask.home.videostream.service.VideoStreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    @GetMapping("/static/vid")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @RequestParam(value = "filename") String fileName) {
        return Mono.just(videoStreamService.prepareContent(fileName.split("\\.")[0], fileName.split("\\.")[1], httpRangeList));
    }

    @PostMapping("upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile video, @RequestParam("uuid") String uuid, @RequestParam("type") String type) {
        try {
            convertMultipartFileToFile(video, uuid, type);
        } catch (IOException exception) {
            return ResponseEntity.status(123).body("не сохранилось");
        }
        return ResponseEntity.ok().build();
    }

    private void convertMultipartFileToFile(MultipartFile multipartFile, String uuid, String type) throws IOException {
        File file = new File("src/main/resources/video/" + uuid + "." + type);

        try (OutputStream os = Files.newOutputStream(file.toPath()); InputStream is = multipartFile.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
