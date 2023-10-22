package ru.senya.mytybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.senya.mytybe.jwt.RsaKeyProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class MytybeApplication {

    public static String IP;

    static {
        try {
            URL url = new URL("https://api64.ipify.org?format=text");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            IP = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MytybeApplication.class, args);
    }
}
