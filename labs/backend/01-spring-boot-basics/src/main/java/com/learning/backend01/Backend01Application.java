package com.learning.backend01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot application.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: marks the class as a source of bean definitions
 * - @EnableAutoConfiguration: tells Spring Boot to auto-configure based on
 *   classpath dependencies
 * - @ComponentScan: enables scanning of components in the current package
 *
 * Spring Boot 3.x requires Java 17+ and uses the jakarta.* namespace.
 */
@SpringBootApplication
public class Backend01Application {

    private static final Logger log = LoggerFactory.getLogger(Backend01Application.class);

    public static void main(String[] args) {
        // SpringApplication.run() bootstraps the application, creates the
        // ApplicationContext, and starts the embedded web server.
        SpringApplication.run(Backend01Application.class, args);
        log.info("Backend01Application started successfully!");
        System.out.println("=== Spring Boot Basics Lab is running ===");
    }
}
