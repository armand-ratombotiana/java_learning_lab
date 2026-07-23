package com.prod.solutions.disasterrecovery;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates a circuit breaker with automatic failover to a backup
 * service or region when the primary fails.
 *
 * This is a core DR pattern: when a service or region becomes unhealthy,
 * traffic is automatically redirected to a healthy backup.
 */
public class CircuitBreakerFailover {

    static class Region {
        final String name;
        final boolean initiallyHealthy;
        private volatile boolean healthy;

        Region(String name, boolean healthy) {
            this.name = name;
            this.initiallyHealthy = healthy;
            this.healthy = healthy;
        }

        String call() throws Exception {
            if (!healthy) {
                throw new Exception(name + " is DOWN");
            }
            return name + "-response";
        }

        void setHealthy(boolean h) { this.healthy = h; }
        boolean isHealthy() { return healthy; }
    }

    static class FailoverRouter {
        private final Region primary;
        private final Region secondary;
        private final AtomicInteger failoverCount = new AtomicInteger(0);
        private volatile Region activeRegion;

        FailoverRouter(Region primary, Region secondary) {
            this.primary = primary;
            this.secondary = secondary;
            this.activeRegion = primary.isHealthy() ? primary : secondary;
        }

        String call() throws Exception {
            Region current = activeRegion;
            try {
                return current.call();
            } catch (Exception e) {
                // Failover to other region
                Region other = (current == primary) ? secondary : primary;
                if (other.isHealthy()) {
                    System.out.printf("  FAILOVER: %s -> %s%n", current.name, other.name);
                    activeRegion = other;
                    failoverCount.incrementAndGet();
                    return other.call();
                }
                throw new Exception("ALL REGIONS DOWN");
            }
        }

        int getFailoverCount() { return failoverCount.get(); }
        Region getActiveRegion() { return activeRegion; }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Circuit Breaker with Failover Demo ===\n");

        Region primary = new Region("us-east-1", true);
        Region secondary = new Region("us-west-2", true);

        FailoverRouter router = new FailoverRouter(primary, secondary);

        // Normal operation
        System.out.println("--- Normal operation (US East active) ---");
        for (int i = 0; i < 3; i++) {
            System.out.printf("  Call %d: %s%n", i + 1, router.call());
        }

        // Primary fails
        System.out.println("\n--- US East region failure ---");
        primary.setHealthy(false);

        try {
            System.out.printf("  Call: %s%n", router.call());
            System.out.printf("  Active region: %s%n", router.getActiveRegion().name);
            System.out.printf("  Failovers: %d%n", router.getFailoverCount());
        } catch (Exception e) {
            System.out.printf("  FAILED: %s%n", e.getMessage());
        }

        // Primary recovers
        System.out.println("\n--- US East recovers (manual failback) ---");
        primary.setHealthy(true);
        // In production: health check would detect recovery and route back
        System.out.printf("  Primary healthy again. Manual failback required (or health check).%n");

        System.out.printf("%nDR failover strategy:%n");
        System.out.println("  - Active-Passive: secondary is standby");
        System.out.println("  - Detect failure via health checks");
        System.out.println("  - Failover transparent to callers");
        System.out.println("  - Failback requires verification of primary health");
    }
}
