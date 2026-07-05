package com.net.discovery;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class HealthCheck {

    public enum HealthStatus { HEALTHY, DEGRADED, UNHEALTHY }

    public static class HealthResult {
        public final HealthStatus status;
        public final String details;
        public final long responseTimeMs;

        public HealthResult(HealthStatus status, String details, long responseTimeMs) {
            this.status = status;
            this.details = details;
            this.responseTimeMs = responseTimeMs;
        }

        @Override
        public String toString() {
            return status + " (" + responseTimeMs + "ms): " + details;
        }
    }

    public static class HealthCheckEndpoint {
        private final String name;
        private final Supplier<HealthResult> check;
        private HealthResult lastResult;

        public HealthCheckEndpoint(String name, Supplier<HealthResult> check) {
            this.name = name;
            this.check = check;
        }

        public HealthResult execute() {
            long start = System.currentTimeMillis();
            lastResult = check.get();
            return lastResult;
        }
    }

    public static class HealthChecker {
        private final List<HealthCheckEndpoint> endpoints = new ArrayList<>();
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        public void addEndpoint(HealthCheckEndpoint endpoint) {
            endpoints.add(endpoint);
        }

        public void startPeriodicCheck(long intervalMs) {
            scheduler.scheduleAtFixedRate(() -> {
                System.out.println("\n=== Health Check ===");
                for (HealthCheckEndpoint ep : endpoints) {
                    HealthResult result = ep.execute();
                    System.out.println("  " + ep.name + ": " + result);
                }
            }, 0, intervalMs, TimeUnit.MILLISECONDS);
        }

        public List<HealthResult> checkAll() {
            List<HealthResult> results = new ArrayList<>();
            for (HealthCheckEndpoint ep : endpoints) {
                results.add(ep.execute());
            }
            return results;
        }

        public boolean isSystemHealthy() {
            return endpoints.stream().allMatch(ep -> {
                HealthResult r = ep.execute();
                return r.status == HealthStatus.HEALTHY || r.status == HealthStatus.DEGRADED;
            });
        }

        public void shutdown() { scheduler.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        HealthChecker checker = new HealthChecker();

        checker.addEndpoint(new HealthCheckEndpoint("database", () -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            return new HealthResult(HealthStatus.HEALTHY, "DB connected", 50);
        }));

        checker.addEndpoint(new HealthCheckEndpoint("cache", () -> {
            try { Thread.sleep(20); } catch (InterruptedException e) {}
            return new HealthResult(HealthStatus.HEALTHY, "Redis connected", 20);
        }));

        checker.addEndpoint(new HealthCheckEndpoint("disk", () ->
            new HealthResult(HealthStatus.DEGRADED, "85% disk usage", 10)));

        System.out.println("System healthy: " + checker.isSystemHealthy());
        checker.startPeriodicCheck(2000);

        Thread.sleep(500);
        checker.shutdown();
    }
}
