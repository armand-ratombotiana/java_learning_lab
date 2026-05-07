package com.learning.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
public class RedisLab {
    public static void main(String[] args) {
        SpringApplication.run(RedisLab.class, args);
    }
}

@RestController
class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    CacheController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/cache/{key}")
    public String getCached(@PathVariable String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : "Not found";
    }

    @PostMapping("/cache/{key}")
    public String setCached(@PathVariable String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);
        return "Cached: " + value;
    }

    @GetMapping("/product/{id}")
    @Cacheable("products")
    public String getProduct(@PathVariable Long id) {
        System.out.println("Fetching from DB for id: " + id);
        return "Product-" + id + " (Price: $99.99)";
    }
}