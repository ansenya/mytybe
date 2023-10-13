package ru.senya.mytybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.senya.mytybe.jwt.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class MytybeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MytybeApplication.class, args);
    }

}
