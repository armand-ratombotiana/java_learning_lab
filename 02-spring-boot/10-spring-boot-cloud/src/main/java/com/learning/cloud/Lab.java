package com.learning.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class Lab {
    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }
}

@RestController
@RequestMapping("/api/cloud")
class CloudController {

    @GetMapping("/discovery")
    public Map<String, String> discovery() {
        return Map.of(
            "pattern", "Service Discovery",
            "tools", "Eureka, Consul, Kubernetes",
            "description", "Services register with a registry and discover each other"
        );
    }

    @GetMapping("/config")
    public Map<String, String> config() {
        return Map.of(
            "pattern", "Configuration Server",
            "tools", "Spring Cloud Config Server",
            "backends", "Git, Vault, JDBC"
        );
    }

    @GetMapping("/gateway")
    public Map<String, String> gateway() {
        return Map.of(
            "pattern", "API Gateway",
            "tools", "Spring Cloud Gateway, Zuul",
            "features", "Routing, Filtering, Rate Limiting"
        );
    }

    @GetMapping("/circuit-breaker")
    public Map<String, String> circuitBreaker() {
        return Map.of(
            "pattern", "Circuit Breaker",
            "tools", "Resilience4j, Hystrix",
            "states", "CLOSED, OPEN, HALF_OPEN"
        );
    }

    @GetMapping("/tracing")
    public Map<String, String> tracing() {
        return Map.of(
            "pattern", "Distributed Tracing",
            "tools", "Micrometer Tracing, Zipkin, Jaeger",
            "concepts", "Trace ID, Span ID, Baggage"
        );
    }
}
