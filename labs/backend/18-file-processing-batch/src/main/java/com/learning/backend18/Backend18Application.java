package com.learning.backend18;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Backend18Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend18Application.class, args);
        System.out.println("=== Spring Batch Lab is running ===");
    }
}
