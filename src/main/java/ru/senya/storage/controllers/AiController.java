package ru.senya.storage.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AiController {
    public static String SERVER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoic2VydmVyIiwiZXhwIjoxNzI3MTA3OTA1LCJpYXQiOjE3MTE1NTU5MDUsInNjb3BlIjoiVVNFUiJ9.VEorBvJGmnyWkXNjqUtdGWIHwkQB7GHaPhtWVVaIWHhsAQhRDnIJDBjV9_QtCY5jooqyoWJ6G-YDNTIMr4BidymN2hy8A-eaTSg5Kh4WrGNH3KEr5dQcoy5qAKmJO5N4Hwb-JvvbivfZPcE1kRdGIsO-w5dDU7mDn2EsnF_xOZaKPvZZHHSbcjd45jZWiKnhWvlGUHuo0uHNyRk7irEbL8WgMsnABZibkzJzZgtuy53DzuRUBiLHkTr8Rc27Ktyi_zlmdBMuLFQduRlrEYXnGt6MUvdtJmSBv8IwAJstrZlpjB4pC0KcpfIE_QcrUHn_bHIwGQTlUL5CA0bctcPVfKb0yZltx-lQzrAPnCiARQJtNaRcearnkcoAOHMQQsxNrdItw9-KykXCB9TX7Y-KXMaMANg-6wYGrePE8_pCv0c8Itn7nhqcGPz0ZMPDhBP8tzxjVrNg78mrrT47J3NCgweEubr0rMfNwO-mIdZFcgR3W8QMKsmEVrSNrlhCR-S_ymX4Pb2nyHVc5X9Wxm8CdUMIWA-B7uR2Z4WJQ7rDiGHdjS83tdHJMBAr0wcwF4UNUA5nJn2FVZui8oZZFFve5B6z3cQ-000GOOZBotx6UCNr4U5Ck7G_DprExVMSZrT8Xtk1GHJJo9zsk7j2auE98iOPY8n04XuixcRZwv_jI5E";


    @PostMapping("start_ai")
    public ResponseEntity<?> startAi() {
        File folder = new File("vids");
        File[] files = folder.listFiles();

        List<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }

        for (File file : fileList) {
            try {
                System.out.println(file.getName());
                var name = file.getName().split("\\.")[0];
                if (!name.contains("480")) {
                    continue;
                }
                name = name.split("_")[0];
                String url = "http://176.99.146.176:8642/process?uuid=" + name;


                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                headers.setBearerAuth(SERVER_TOKEN);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("video", new FileSystemResource(file));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                var penis = new RestTemplate()
                        .exchange(url,
                                HttpMethod.POST,
                                requestEntity,
                                String.class);
                System.out.println(penis.getStatusCode());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return ResponseEntity.ok().build();
    }

}
