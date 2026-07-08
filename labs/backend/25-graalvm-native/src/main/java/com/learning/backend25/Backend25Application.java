package com.learning.backend25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Backend25Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend25Application.class, args);
        System.out.println("=== GraalVM Native Lab is running ===");
    }
}
