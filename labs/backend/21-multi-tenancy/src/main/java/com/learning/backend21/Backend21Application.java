package com.learning.backend21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Backend21Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend21Application.class, args);
        System.out.println("=== Multi-Tenancy Lab is running ===");
    }
}
