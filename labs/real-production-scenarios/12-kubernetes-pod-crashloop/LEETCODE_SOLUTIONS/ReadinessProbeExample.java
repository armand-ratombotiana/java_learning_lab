package com.prod.solutions.kubernetes;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates a sophisticated readiness probe that checks multiple
 * conditions before declaring the pod ready to serve traffic.
 *
 * Conditions checked:
 * - Upstream dependencies available
 * - Cache warmed up
 * - Connection pool initialized
 * - Not in startup grace period
 */
public class ReadinessProbeExample {

    static class ReadinessCheck {
        private final String name;
        private final AtomicBoolean ready = new AtomicBoolean(false);
        private String reason = "Not initialized";

        ReadinessCheck(String name) { this.name = name; }

        void markReady() { this.ready.set(true); this.reason = "Ready"; }
        void markNotReady(String reason) { this.ready.set(false); this.reason = reason; }
        boolean isReady() { return ready.get(); }
        String getReason() { return reason; }
        String getName() { return name; }
    }

    static class ReadinessAggregator {
        private final ReadinessCheck[] checks;
        private final AtomicBoolean lastProbeResult = new AtomicBoolean(false);

        ReadinessAggregator(ReadinessCheck[] checks) {
            this.checks = checks;
        }

        boolean isReady() {
            for (ReadinessCheck check : checks) {
                if (!check.isReady()) {
                    lastProbeResult.set(false);
                    System.out.printf("Readiness probe: NOT READY (%s: %s)%n",
                            check.getName(), check.getReason());
                    return false;
                }
            }
            lastProbeResult.set(true);
            System.out.println("Readiness probe: READY");
            return true;
        }

        AtomicBoolean getLastProbeResult() { return lastProbeResult; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Readiness Probe Demo ===\n");

        ReadinessCheck dbReady = new ReadinessCheck("database");
        ReadinessCheck cacheWarm = new ReadinessCheck("cache-warmup");
        ReadinessCheck poolInit = new ReadinessCheck("connection-pool");

        ReadinessAggregator probe = new ReadinessAggregator(
                new ReadinessCheck[]{dbReady, cacheWarm, poolInit});

        // Initial startup: nothing is ready
        System.out.println("--- Pod Starting Up ---");
        probe.isReady();

        // DB connects first
        System.out.println("\n--- Database connected ---");
        dbReady.markReady();
        probe.isReady();

        // Connection pool initialized
        System.out.println("\n--- Connection pool initialized ---");
        poolInit.markReady();
        probe.isReady();

        // Cache warmed up
        System.out.println("\n--- Cache warm-up complete ---");
        cacheWarm.markReady();
        probe.isReady();

        // Simulate downstream failure
        System.out.println("\n--- Downstream service becomes unhealthy ---");
        dbReady.markNotReady("Downstream API returning 503");
        probe.isReady();

        System.out.printf("%nReadiness probe designed to prevent:%n");
        System.out.println("  - Serving traffic before app is initialized");
        System.out.println("  - Routing traffic to pods with failed dependencies");
        System.out.println("  - Dropping requests during cache cold start");
    }
}
