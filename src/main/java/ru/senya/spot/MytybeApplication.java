package ru.senya.spot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.senya.spot.env.RsaKeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableJpaRepositories(basePackages = "ru.senya.spot.repos.jpa")
@EnableRedisRepositories(basePackages = "ru.senya.spot.repos.redis")
@EnableElasticsearchRepositories(basePackages = "ru.senya.spot.repos.es")
public class MytybeApplication {

    public static String MAIN_HOST = "https://video-spot.ru";
        public static String STORAGE_HOST = "https://video-spot.ru/storage/";
//    public static String STORAGE_HOST = "http://st:1986/storage/";
    public static String SERVER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoic2VydmVyIiwiZXhwIjoxNzI3MTA3OTA1LCJpYXQiOjE3MTE1NTU5MDUsInNjb3BlIjoiVVNFUiJ9.VEorBvJGmnyWkXNjqUtdGWIHwkQB7GHaPhtWVVaIWHhsAQhRDnIJDBjV9_QtCY5jooqyoWJ6G-YDNTIMr4BidymN2hy8A-eaTSg5Kh4WrGNH3KEr5dQcoy5qAKmJO5N4Hwb-JvvbivfZPcE1kRdGIsO-w5dDU7mDn2EsnF_xOZaKPvZZHHSbcjd45jZWiKnhWvlGUHuo0uHNyRk7irEbL8WgMsnABZibkzJzZgtuy53DzuRUBiLHkTr8Rc27Ktyi_zlmdBMuLFQduRlrEYXnGt6MUvdtJmSBv8IwAJstrZlpjB4pC0KcpfIE_QcrUHn_bHIwGQTlUL5CA0bctcPVfKb0yZltx-lQzrAPnCiARQJtNaRcearnkcoAOHMQQsxNrdItw9-KykXCB9TX7Y-KXMaMANg-6wYGrePE8_pCv0c8Itn7nhqcGPz0ZMPDhBP8tzxjVrNg78mrrT47J3NCgweEubr0rMfNwO-mIdZFcgR3W8QMKsmEVrSNrlhCR-S_ymX4Pb2nyHVc5X9Wxm8CdUMIWA-B7uR2Z4WJQ7rDiGHdjS83tdHJMBAr0wcwF4UNUA5nJn2FVZui8oZZFFve5B6z3cQ-000GOOZBotx6UCNr4U5Ck7G_DprExVMSZrT8Xtk1GHJJo9zsk7j2auE98iOPY8n04XuixcRZwv_jI5E";

    public static void main(String[] args) {
        SpringApplication.run(MytybeApplication.class, args);
    }
}
