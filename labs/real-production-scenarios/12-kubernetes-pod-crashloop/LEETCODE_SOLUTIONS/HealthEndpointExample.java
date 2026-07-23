package com.prod.solutions.kubernetes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulates a Spring Boot health endpoint with liveness and readiness
 * probes. In production, Spring Boot Actuator provides /actuator/health
 * with customizable health indicators.
 *
 * Liveness: Is the app alive? (If no, K8s restarts the pod)
 * Readiness: Is the app ready to serve traffic? (If no, K8s stops sending traffic)
 */
public class HealthEndpointExample {

    static class HealthIndicator {
        private final String name;
        private final AtomicBoolean healthy = new AtomicBoolean(true);
        private String detail = "OK";

        HealthIndicator(String name) { this.name = name; }

        void setHealthy(boolean healthy, String detail) {
            this.healthy.set(healthy);
            this.detail = detail;
        }

        Map<String, Object> getHealth() {
            return Map.of(
                "name", name,
                "status", healthy.get() ? "UP" : "DOWN",
                "detail", detail
            );
        }
    }

    static class HealthRegistry {
        private final Map<String, HealthIndicator> indicators = new ConcurrentHashMap<>();

        void register(HealthIndicator indicator) {
            indicators.put(indicator.name, indicator);
        }

        Map<String, Object> checkLiveness() {
            // Liveness checks if the process is alive
            boolean allUp = indicators.values().stream()
                    .allMatch(i -> i.healthy.get());
            return Map.of(
                "status", allUp ? "UP" : "DOWN",
                "indicators", indicators.values().stream()
                        .map(HealthIndicator::getHealth)
                        .toList()
            );
        }

        Map<String, Object> checkReadiness() {
            // Readiness checks if the service can handle requests
            boolean allReady = indicators.values().stream()
                    .allMatch(i -> i.healthy.get());
            return Map.of(
                "status", allReady ? "READY" : "NOT_READY",
                "indicators", indicators.values().stream()
                        .map(HealthIndicator::getHealth)
                        .toList()
            );
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Kubernetes Health Probe Demo ===\n");

        HealthRegistry registry = new HealthRegistry();

        HealthIndicator db = new HealthIndicator("database");
        HealthIndicator cache = new HealthIndicator("redis-cache");
        HealthIndicator disk = new HealthIndicator("disk-space");

        registry.register(db);
        registry.register(cache);
        registry.register(disk);

        // Initial state: healthy
        System.out.println("--- Initial State (Healthy) ---");
        System.out.println("Liveness:  " + registry.checkLiveness());
        System.out.println("Readiness: " + registry.checkReadiness());

        // Database becomes unavailable
        System.out.println("\n--- Database Connection Lost ---");
        db.setHealthy(false, "Cannot connect to database at host:5432");

        System.out.println("Liveness:  " + registry.checkLiveness());
        System.out.println("Readiness: " + registry.checkReadiness());
        System.out.println(">>> K8s stops sending traffic to this pod (readiness fails)");

        // Database recovered
        System.out.println("\n--- Database Recovered ---");
        db.setHealthy(true, "Connected");

        System.out.println("Liveness:  " + registry.checkLiveness());
        System.out.println("Readiness: " + registry.checkReadiness());
        System.out.println(">>> K8s resumes sending traffic");

        System.out.printf("%nKubernetes probe behavior:%n");
        System.out.println("  Liveness fails  → pod is killed and restarted");
        System.out.println("  Readiness fails → pod is removed from Service endpoints");
        System.out.println("  Startup probe   → delays liveness/readiness checks during startup");
    }
}
