package com.learning.backend15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Backend15Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend15Application.class, args);
        System.out.println("=== WebFlux Reactive Lab is running ===");
    }
}
