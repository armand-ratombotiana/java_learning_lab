package com.learning.backend14;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point with scheduling enabled.
 *
 * @EnableScheduling activates Spring's task scheduling capability,
 * allowing @Scheduled methods to be executed at configured intervals.
 */
@SpringBootApplication
@EnableScheduling
public class Backend14Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend14Application.class, args);
        System.out.println("=== Scheduling Lab is running ===");
    }
}
