package ru.senya.storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.senya.storage.confs.Log;
import ru.senya.storage.service.VideoStreamService;
import ru.senya.storage.utils.UserRequestTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static ru.senya.storage.controller.Utils.save;

@RestController
@RequestMapping("/api")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;
    private final UserRequestTracker userRequestTracker;

    private final Logger log = LoggerFactory.getLogger(Log.class);

    @Autowired
    public VideoStreamController(VideoStreamService videoStreamService, UserRequestTracker userRequestTracker) {
        this.videoStreamService = videoStreamService;
        this.userRequestTracker = userRequestTracker;
    }

    @GetMapping("vid")
    public Mono<ResponseEntity<byte[]>> streamVideo(HttpServletRequest request,
                                                    @RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @RequestParam(value = "filename") String fileName,
                                                    @RequestParam(value = "q", required = false, defaultValue = "") String quality) {
        if (userRequestTracker.isRequestInProgress(request.getRemoteAddr())) {
            log.error("429 from {}", request.getRemoteAddr());
            return Mono.just(ResponseEntity.status(429).build());
        } else {
            try {
                userRequestTracker.setRequestInProgress(request.getRemoteAddr(), 1);
                log.error("set progress from {}", request.getRemoteAddr());
                String path = findVideoPath(fileName, quality);
                String type = determineFileType(fileName);
                if (!new File("vids/" + path + "." + type).exists()) {
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                }
                return Mono.just(videoStreamService.prepareContent(path, type, httpRangeList));
            } finally {
                log.error("unset progress from {}", request.getRemoteAddr());
                userRequestTracker.setRequestInProgress(request.getRemoteAddr(), 1);
            }
        }
    }

    @PostMapping("vid/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile video, @RequestParam("uuid") String uuid, @RequestParam("type") String type) {
        try {
            saveVideo(video, uuid, type);
        } catch (IOException exception) {
            return ResponseEntity.status(123).body("не сохранилось");
        }
        return ResponseEntity.ok().build();
    }

    private void saveVideo(MultipartFile multipartFile, String uuid, String type) throws IOException {
        String path = "vids/" + uuid + "." + type;
        File file = new File(path);
        save(multipartFile, file);
        new Thread(() -> {
            encode(path);
            if (!new File(path).delete()) {
                log.error("could not delete file: " + path);
            }
        }).start();
    }

    private void encode(String filename) {
        try {
            ProcessBuilder pb = new ProcessBuilder("src/main/resources/scripts/encode.sh", filename);
            pb.redirectErrorStream(true);
            pb.redirectErrorStream(true);
            Process process = pb.start();

//            // Получаем вывод от процесса
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("encode error", e);
        }
    }

    private String findVideoPath(String fileName, String quality) {
        String name = fileName.split("\\.")[0];
        String type = fileName.split("\\.")[1];
        String path;
        if (quality.isBlank()) {
            int maxQuality = findMaxQuality(name);
            path = name + "_" + maxQuality;
        } else {
            path = name + "_" + quality;
        }
        return path;
    }

    private int findMaxQuality(String name) {
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
    }

    private String determineFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
