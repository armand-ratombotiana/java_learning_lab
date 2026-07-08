package com.learning.backend23;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Backend23Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend23Application.class, args);
        System.out.println("=== CQRS Axon Lab is running ===");
    }
}
