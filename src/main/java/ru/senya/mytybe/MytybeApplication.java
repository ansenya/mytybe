package ru.senya.mytybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.senya.mytybe.configs.security.RsaKeyProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableJpaRepositories(basePackages = "ru.senya.mytybe.repos.jpa")
@EnableRedisRepositories(basePackages = "ru.senya.mytybe.repos.redis")
@EnableElasticsearchRepositories(basePackages = "ru.senya.mytybe.repos.es")
public class MytybeApplication {

    public static String IP;
    public static String PORT = "1984";
    public static String STORAGE_PORT = "1986";
    public static String HLS_PORT = "8088";

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
