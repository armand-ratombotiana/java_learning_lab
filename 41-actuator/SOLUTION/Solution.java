package com.learning.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import java.util.HashMap;
import java.util.Map;

public class ActuatorSolution {

    public Health createHealthStatus(String status, Map<String, Object> details) {
        return Health.up()
            .withDetail("status", status)
            .withDetails(details)
            .build();
    }

    public Health createDownHealth() {
        return Health.down()
            .withDetail("reason", "Service unavailable")
            .build();
    }

    public Map<String, Object> getMetrics(String metricName, Metric<?> metric) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", metricName);
        result.put("measurements", metric.getValue());
        result.put("availableTags", new String[]{});
        return result;
    }

    @Endpoint(id = "custom")
    public static class CustomEndpoint {
        @ReadOperation
        public Map<String, String> getData() {
            Map<String, String> data = new HashMap<>();
            data.put("status", "UP");
            return data;
        }

        @WriteOperation
        public void performAction(String action) {
            System.out.println("Action performed: " + action);
        }
    }

    public static class DatabaseHealthIndicator implements HealthIndicator {
        @Override
        public Health health() {
            return Health.up()
                .withDetail("database", "connected")
                .build();
        }
    }

    public static class CustomHealthIndicator implements HealthIndicator {
        @Override
        public Health health() {
            return Health.status("UP")
                .withDetail("service", "healthy")
                .build();
        }
    }
}