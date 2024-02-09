package ru.senya.spot.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    public TaskExecutor myTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}