package ru.senya.spot.controllers.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
public class ProcessingUtils {
    Logger logger = LoggerFactory.getLogger(ProcessingUtils.class);

    public String processVideo(MultipartFile videoFile, String uuid) {
        int timeout = 10_000;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout);

        RestTemplate restTemplate = new RestTemplate(factory);

        FileSystemResource fileResource = new FileSystemResource(convert(videoFile));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", fileResource);

        String serverUrl = "http://localhost:8642/process?uuid=" + uuid;

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        } catch (Exception e) {
            response = restTemplate.postForEntity("http://176.99.146.176:8642/process?uuid=" + uuid, requestEntity, String.class);
        }

        return response.getBody();
    }

    private File convert(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            logger.error("error with processing", e);
        }
        return convFile;
    }
}
