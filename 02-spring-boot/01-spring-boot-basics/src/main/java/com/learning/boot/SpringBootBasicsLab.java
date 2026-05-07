package com.learning.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class SpringBootBasicsLab {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootBasicsLab.class, args);
    }

    @Bean
    public String greeting() {
        return "Welcome to Spring Boot!";
    }
}

@RestController
class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/status")
    public Status status() {
        return new Status("UP", System.currentTimeMillis());
    }
}

class Status {
    private String status;
    private long timestamp;
    public Status(String status, long timestamp) {
        this.status = status; this.timestamp = timestamp;
    }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}