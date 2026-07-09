package com.learning.backend26;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ActuatorInternals {

    public static class CustomHealthIndicator implements HealthIndicator {
        private volatile boolean healthy = true;

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }

        @Override
        public Health health() {
            if (healthy) {
                return Health.up()
                        .withDetail("service", "custom-check")
                        .withDetail("uptime", System.currentTimeMillis())
                        .build();
            }
            return Health.down()
                    .withDetail("service", "custom-check")
                    .withDetail("error", "Simulated failure")
                    .build();
        }
    }

    public static class CustomMetrics {
        private final Counter requestCounter;
        private final AtomicLong activeSessions = new AtomicLong(0);

        private final Map<String, Long> customGauges = new ConcurrentHashMap<>();

        public CustomMetrics(MeterRegistry registry) {
            this.requestCounter = Counter.builder("custom.requests.total")
                    .description("Total custom requests")
                    .tag("source", "actuator-internals")
                    .register(registry);
        }

        public void recordRequest() {
            requestCounter.increment();
        }

        public void incrementSessions() {
            activeSessions.incrementAndGet();
        }

        public void decrementSessions() {
            activeSessions.decrementAndGet();
        }

        public long getActiveSessions() {
            return activeSessions.get();
        }
    }

    @Endpoint(id = "custom-features")
    public static class CustomFeaturesEndpoint {

        private final Map<String, Boolean> features = new ConcurrentHashMap<>(Map.of(
                "feature-a", true,
                "feature-b", false,
                "feature-c", true
        ));

        @ReadOperation
        public Map<String, Object> read() {
            return Map.of(
                    "features", features,
                    "total", features.size(),
                    "enabledCount", features.values().stream().filter(v -> v).count()
            );
        }

        @WriteOperation
        public void update(String featureName, boolean enabled) {
            features.put(featureName, enabled);
        }
    }
}