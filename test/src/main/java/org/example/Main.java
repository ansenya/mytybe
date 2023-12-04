package org.example;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;


public class Main {
    protected RestTemplate template = new RestTemplate();
    private static final HttpHeaders headers = new HttpHeaders();
    public static final String X_FOLDER_ID = "b1g919216h0usde9431e";
    public static String YANDEX_TOKEN;
    private final static Gson gson = new Gson();
    public static final String URL = "https://llm.api.cloud.yandex.net/llm/v1alpha/chat";


    static {
        generateToken();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-folder-id", X_FOLDER_ID);
        headers.set("Authorization", "Bearer " + YANDEX_TOKEN);
    }

    public static void main(String[] args) {
        RestTemplate template = new RestTemplate();

        RequestModel requestModel = new RequestModel("Привет, напиши большой текст про машины");
        requestModel.setTemp(1.0);

        headers.set("Authorization", "Bearer " + YANDEX_TOKEN);

        System.out.println(gson.toJson(requestModel));

        HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestModel), headers);

        ResponseEntity<String> response = template.postForEntity(URL, request, String.class);

        JsonFactory jsonFactory = new JsonFactory();

        try (JsonParser jsonParser = jsonFactory.createParser(response.getBody())) {
            while (jsonParser.nextToken() != null) {
                if (JsonToken.FIELD_NAME.equals(jsonParser.getCurrentToken())) {
                    String fieldName = jsonParser.getCurrentName();

                    if ("text".equals(fieldName)) {
                        // Парсим значение поля "text"
                        jsonParser.nextToken(); // перемещаемся на значение
                        String textValue = jsonParser.getText();
                        System.out.println("Text: " + textValue);
                    } else if ("num_tokens".equals(fieldName)) {
                        // Парсим значение поля "num_tokens"
                        jsonParser.nextToken(); // перемещаемся на значение
                        String numTokensValue = jsonParser.getText();
                        System.out.println("Num Tokens: " + numTokensValue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response.getBody());
    }

    public static void generateToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
        String body = """
                {
                    "yandexPassportOauthToken": "y0_AgAAAAArbTLAAATuwQAAAADpLndikDIdjcsCRBafZ_UeQXn2eR5-gRk"
                }""";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        TokenModel model = gson.fromJson(response.getBody(), TokenModel.class);
        YANDEX_TOKEN = model.getIamToken();
        headers.set("Authorization", "Bearer " + YANDEX_TOKEN);

        System.out.println("got token! " + YANDEX_TOKEN);
    }
}