package org.example;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class RequestModel {
    private String model = "general";
    private GenerationOptions generationOptions = new GenerationOptions();
    private ArrayList<Message> messages = new ArrayList<>();
    private String instructionText = "";

    public void setTemp(Double temp) {
        generationOptions.setTemperature(temp);
    }

    public Double getTemp() {
        return generationOptions.getTemperature();
    }

    @Data
    static class GenerationOptions {
        private boolean partialResults = true;
        private double temperature = 0.7;
        private int maxTokens = 7400;
    }

    @Data
    public static class Message {

        public Message(String role, String text) {
            this.role = role;
            this.text = text;
        }

        String role;
        String text;
    }

    public RequestModel(String instructionText) {
        this.instructionText = instructionText;
    }

}
