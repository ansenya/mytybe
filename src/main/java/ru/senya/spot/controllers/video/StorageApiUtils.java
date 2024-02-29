package ru.senya.spot.controllers.video;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@CrossOrigin(origins = "*")
public class StorageApiUtils {

    public boolean sendToStorage(String uuid, String type, String endpoint, MultipartFile file) {
        String url = "http://st:1986/storage/" + endpoint + "/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestOperations restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url + "?uuid=" + uuid + "&type=" + type, HttpMethod.POST, requestEntity, String.class);

        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode == HttpStatus.OK;
    }
}
