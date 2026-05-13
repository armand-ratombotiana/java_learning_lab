package com.mlplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.mlplatform.repository")
public class MLPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(MLPlatformApplication.class, args);
    }
}