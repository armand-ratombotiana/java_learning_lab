package com.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.rag.repository")
public class RAGApplication {
    public static void main(String[] args) {
        SpringApplication.run(RAGApplication.class, args);
    }
}