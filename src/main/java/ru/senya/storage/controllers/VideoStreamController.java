package ru.senya.storage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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

import static ru.senya.storage.controllers.Utils.findVideoPath;
import static ru.senya.storage.controllers.Utils.save;

@RestController
public class VideoStreamController {

    private final VideoStreamService videoStreamService;
    private final UserRequestTracker userRequestTracker;


    @Autowired
    public VideoStreamController(VideoStreamService videoStreamService, UserRequestTracker userRequestTracker) {
        this.videoStreamService = videoStreamService;
        this.userRequestTracker = userRequestTracker;
    }

    @GetMapping("vid")
    public Mono<?> streamVideo(HttpServletRequest request,
                               @RequestHeader(value = "Range", required = false) String httpRangeList,
                               @RequestParam(value = "filename") String fileName,
                               @RequestParam(value = "q", required = false, defaultValue = "") String quality) {
        if (userRequestTracker.isRequestInProgress(request.getRemoteAddr())) {
            return Mono.just(ResponseEntity.status(429).build());
        } else {
            try {
                userRequestTracker.setRequestInProgress(request.getRemoteAddr(), 1);
                String path = findVideoPath(fileName, quality);
                String type = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (!new File("vids/" + path + "." + type).exists()) {
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                }
                return Mono.just(videoStreamService.prepareContent(path, type, httpRangeList));
            } finally {
                userRequestTracker.setRequestInProgress(request.getRemoteAddr(), -1);
            }
        }
    }


    @DeleteMapping("vid")
    private void deleteVideo(HttpServletRequest request,
                             @RequestParam(value = "filename") String fileName) {
        System.out.println(request.getRemoteHost());
    }
}
