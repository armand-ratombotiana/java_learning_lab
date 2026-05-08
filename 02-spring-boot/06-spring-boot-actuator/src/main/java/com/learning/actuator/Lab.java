package com.learning.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Lab {
    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }
}

@Component
class CustomHealthIndicator implements HealthIndicator {

    private boolean healthy = true;

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public Health health() {
        if (healthy) {
            return Health.up()
                .withDetail("service", "Lab App")
                .withDetail("timestamp", Instant.now())
                .build();
        }
        return Health.down()
            .withDetail("error", "Simulated failure")
            .build();
    }
}

@Component
@Endpoint(id = "requests")
class RequestCounterEndpoint {

    private final AtomicLong counter = new AtomicLong();

    @ReadOperation
    public Map<String, Object> requests() {
        return Map.of(
            "totalRequests", counter.incrementAndGet(),
            "sinceStartup", Instant.now()
        );
    }
}

@RestController
class ActuatorDemoController {

    @GetMapping("/stats")
    public Map<String, String> stats() {
        return Map.of(
            "app", "Spring Boot Actuator Lab",
            "version", "3.3.0",
            "endpoints", "/actuator/health, /actuator/info, /actuator/metrics"
        );
    }
}
