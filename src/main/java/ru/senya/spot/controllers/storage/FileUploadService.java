package ru.senya.spot.controllers.storage;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static ru.senya.spot.MytybeApplication.SERVER_TOKEN;
import static ru.senya.spot.MytybeApplication.STORAGE_HOST;

@Service
public class FileUploadService {


    public void sendToStorage(String uuid, String type, String endpoint, MultipartFile file) {
        String url = STORAGE_HOST + endpoint + "/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(SERVER_TOKEN);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        new RestTemplate()
                .exchange(url + "?uuid=" + uuid + "&type=" + type,
                        HttpMethod.POST,
                        requestEntity,
                        String.class);
    }
}
