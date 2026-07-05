package com.learning.backend13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point with caching enabled.
 *
 * @EnableCaching activates Spring's annotation-driven cache management.
 * Spring Boot auto-configures a suitable CacheManager based on classpath
 * (ConcurrentHashMap-based by default, or Redis, Caffeine, etc.).
 */
@SpringBootApplication
@EnableCaching
public class Backend13Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend13Application.class, args);
        System.out.println("=== Caching Lab is running ===");
    }
}
