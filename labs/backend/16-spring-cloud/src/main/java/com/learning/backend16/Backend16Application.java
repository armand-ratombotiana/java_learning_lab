package com.learning.backend16;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Backend16Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend16Application.class, args);
        System.out.println("=== Spring Cloud Lab is running ===");
    }
}
