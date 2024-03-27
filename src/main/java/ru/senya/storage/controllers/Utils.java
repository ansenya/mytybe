package ru.senya.storage.controllers;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {

    public static void save(MultipartFile multipartFile, File file) throws IOException {
        try (OutputStream os = Files.newOutputStream(file.toPath()); InputStream is = multipartFile.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    public static String findVideoPath(String fileName, String quality) {
        String name = fileName.split("\\.")[0];
        String path;
        if (quality.isBlank()) {
            int maxQuality = findMaxQuality(name);
            if (maxQuality == -1) {
                path = name;
            } else {
                path = name + "_" + maxQuality;
            }
        } else {
            path = name + "_" + quality;
        }
        return path;
    }

    private static int findMaxQuality(String name) {
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
