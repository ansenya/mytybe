package ru.senya.mytybe.configs.security;


import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

    @Bean
    public RestHighLevelClient client() {
        final ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo(elasticsearchUrl)
                        .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
