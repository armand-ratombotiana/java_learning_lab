package com.learning.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@EnableCaching
public class RedisCacheLab {
    public static void main(String[] args) {
        SpringApplication.run(RedisCacheLab.class, args);
    }
}

@RestController
class CacheController {

    @GetMapping("/product/{id}")
    @Cacheable("products")
    public String getProduct(@PathVariable Long id) {
        System.out.println("Fetching from database for id: " + id);
        return "Product-" + id + " (Price: $99.99)";
    }

    @PostMapping("/cache/clear")
    public String clearCache() {
        return "Cache cleared";
    }
}