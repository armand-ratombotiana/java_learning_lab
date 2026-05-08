package com.learning.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
public class Lab {

    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }

    @Bean
    public String appName() {
        return "SpringBootBasics";
    }
}

@Service
class GreetingService {

    private final String appName;

    GreetingService(String appName) {
        this.appName = appName;
    }

    public String greet(String name) {
        return "Hello, %s! From %s".formatted(name, appName);
    }

    public List<String> getLessons() {
        return List.of(
            "Auto-configuration",
            "Spring Boot Starters",
            "Dependency Injection",
            "Embedded Server",
            "Actuator"
        );
    }
}

@RestController
@RequestMapping("/api")
class DemoController {

    private final GreetingService greetingService;

    DemoController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return greetingService.greet(name);
    }

    @GetMapping("/lessons")
    public List<String> lessons() {
        return greetingService.getLessons();
    }

    @GetMapping("/status")
    public Status status() {
        return new Status("UP", "Spring Boot 3.3.0", System.currentTimeMillis());
    }
}

record Status(String status, String version, long timestamp) {}
