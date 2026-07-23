package com.prod.solutions.disasterrecovery;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Aggregates health checks from multiple regions/services and provides
 * a unified health endpoint. In production, this is exposed via
 * Spring Boot Actuator or a custom endpoint for load balancer health checks.
 */
public class HealthCheckEndpoint {

    static class HealthCheck {
        private final String name;
        private final CheckFunction check;
        private volatile HealthResult lastResult;
        private volatile long lastCheckTime;

        @FunctionalInterface
        interface CheckFunction {
            HealthResult check() throws Exception;
        }

        HealthCheck(String name, CheckFunction check) {
            this.name = name;
            this.check = check;
            this.lastResult = new HealthResult(false, "Not checked yet");
        }

        HealthResult execute() {
            try {
                lastResult = check.check();
                lastCheckTime = System.currentTimeMillis();
            } catch (Exception e) {
                lastResult = new HealthResult(false, e.getMessage());
                lastCheckTime = System.currentTimeMillis();
            }
            return lastResult;
        }

        String getName() { return name; }
        HealthResult getLastResult() { return lastResult; }
    }

    static class HealthResult {
        final boolean healthy;
        final String message;
        final long timestamp;

        HealthResult(boolean healthy, String message) {
            this.healthy = healthy;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }

    static class AggregatedHealth {
        final String status;
        final String summary;
        final List<Map<String, Object>> checks;

        AggregatedHealth(String status, String summary, List<Map<String, Object>> checks) {
            this.status = status;
            this.summary = summary;
            this.checks = checks;
        }

        void print() {
            System.out.printf("""
                    Status:  %s
                    Summary: %s
                    Checks:
                    """, status, summary);
            for (Map<String, Object> c : checks) {
                System.out.printf("  - %s: %s (%s)%n", c.get("name"), c.get("healthy"), c.get("message"));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Health Check Aggregation Demo ===\n");

        List<HealthCheck> checks = new ArrayList<>();

        // Simulate per-region health checks
        checks.add(new HealthCheck("us-east-1-api", () -> {
            Thread.sleep(200);
            return new HealthResult(true, "API responding in 45ms");
        }));

        checks.add(new HealthCheck("us-east-1-db", () -> {
            Thread.sleep(150);
            return new HealthResult(true, "Database connected, replication lag 0ms");
        }));

        checks.add(new HealthCheck("us-west-2-api", () -> {
            Thread.sleep(200);
            return new HealthResult(false, "API timeout after 5s");
        }));

        checks.add(new HealthCheck("eu-west-1-api", () -> {
            Thread.sleep(200);
            return new HealthResult(true, "API responding in 120ms");
        }));

        // Run all checks in parallel
        System.out.println("--- Running health checks (parallel) ---");
        ExecutorService executor = Executors.newFixedThreadPool(checks.size());
        List<Future<HealthResult>> futures = new ArrayList<>();

        long start = System.nanoTime();
        for (HealthCheck check : checks) {
            futures.add(executor.submit(check::execute));
        }

        // Wait for all results
        List<Map<String, Object>> checkResults = new ArrayList<>();
        boolean allHealthy = true;
        int failedCount = 0;

        for (int i = 0; i < checks.size(); i++) {
            try {
                HealthResult result = futures.get(i).get(5, TimeUnit.SECONDS);
                Map<String, Object> cr = new HashMap<>();
                cr.put("name", checks.get(i).getName());
                cr.put("healthy", result.healthy);
                cr.put("message", result.message);
                checkResults.add(cr);

                if (!result.healthy) {
                    allHealthy = false;
                    failedCount++;
                }
            } catch (Exception e) {
                Map<String, Object> cr = new HashMap<>();
                cr.put("name", checks.get(i).getName());
                cr.put("healthy", false);
                cr.put("message", "Check timed out");
                checkResults.add(cr);
                allHealthy = false;
                failedCount++;
            }
        }

        long elapsed = (System.nanoTime() - start) / 1_000_000;

        String status = allHealthy ? "HEALTHY" : "DEGRADED";
        String summary = String.format("%d/%d checks passed (took %d ms)",
                checks.size() - failedCount, checks.size(), elapsed);

        AggregatedHealth health = new AggregatedHealth(status, summary, checkResults);
        health.print();

        executor.shutdown();

        System.out.printf("%nHealth check endpoint design:%n");
        System.out.println("  - Parallel checks for low latency");
        System.out.println("  - Per-region and per-service granularity");
        System.out.println("  - Used by load balancers for traffic routing");
        System.out.println("  - Aggregated status for global view");
    }
}
