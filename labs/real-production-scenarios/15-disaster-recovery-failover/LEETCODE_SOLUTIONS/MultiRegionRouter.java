package com.prod.solutions.disasterrecovery;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Routes traffic to the healthiest region based on health check results.
 * Implements a simple latency-based or health-based routing strategy.
 *
 * In production: Use Route53 (AWS) with latency/geoproximity routing,
 * or a global load balancer with health check-based traffic steering.
 */
public class MultiRegionRouter {

    static class RegionalEndpoint {
        final String region;
        final String endpoint;
        volatile boolean healthy;
        volatile long latencyMs;
        final AtomicInteger activeRequests = new AtomicInteger(0);
        final AtomicInteger failedRequests = new AtomicInteger(0);

        RegionalEndpoint(String region, String endpoint, boolean healthy, long latencyMs) {
            this.region = region;
            this.endpoint = endpoint;
            this.healthy = healthy;
            this.latencyMs = latencyMs;
        }

        void recordSuccess() {
            activeRequests.decrementAndGet();
        }

        void recordFailure() {
            activeRequests.decrementAndGet();
            failedRequests.incrementAndGet();
        }

        void setHealthy(boolean h) { this.healthy = h; }
        void setLatency(long ms) { this.latencyMs = ms; }
    }

    static class Router {
        private final List<RegionalEndpoint> endpoints = new CopyOnWriteArrayList<>();

        Router(List<RegionalEndpoint> endpoints) {
            this.endpoints.addAll(endpoints);
        }

        RegionalEndpoint routeTraffic() {
            // Filter healthy endpoints
            List<RegionalEndpoint> healthy = endpoints.stream()
                    .filter(e -> e.healthy)
                    .toList();

            if (healthy.isEmpty()) {
                System.out.println("  >>> ALL REGIONS DOWN! <<<");
                return null;
            }

            // Route to the lowest-latency healthy endpoint
            RegionalEndpoint best = healthy.stream()
                    .min(Comparator.comparingLong(e -> e.latencyMs))
                    .orElse(healthy.get(0));

            best.activeRequests.incrementAndGet();
            return best;
        }

        void simulateRequest() {
            RegionalEndpoint endpoint = routeTraffic();
            if (endpoint == null) {
                System.out.println("  Request FAILED: no healthy regions");
                return;
            }

            // Simulate success/failure
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                endpoint.recordFailure();
                System.out.printf("  Request to %s FAILED (latency: %d ms)%n",
                        endpoint.region, endpoint.latencyMs);
            } else {
                endpoint.recordSuccess();
                System.out.printf("  Request routed to %s (latency: %d ms, active: %d)%n",
                        endpoint.region, endpoint.latencyMs, endpoint.activeRequests.get());
            }
        }

        void printRegionStatus() {
            System.out.println("\n--- Region Status ---");
            for (RegionalEndpoint e : endpoints) {
                System.out.printf("  %-15s healthy=%-5b latency=%4dms active=%d failed=%d%n",
                        e.region, e.healthy, e.latencyMs,
                        e.activeRequests.get(), e.failedRequests.get());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Multi-Region Traffic Router Demo ===\n");

        List<RegionalEndpoint> regions = new ArrayList<>();
        regions.add(new RegionalEndpoint("us-east-1", "api.us-east-1.com", true, 25));
        regions.add(new RegionalEndpoint("us-west-2", "api.us-west-2.com", true, 40));
        regions.add(new RegionalEndpoint("eu-west-1", "api.eu-west-1.com", true, 80));
        regions.add(new RegionalEndpoint("ap-southeast-1", "api.ap-southeast-1.com", true, 150));

        Router router = new Router(regions);

        // Normal traffic
        System.out.println("--- Normal traffic distribution ---");
        for (int i = 0; i < 10; i++) {
            router.simulateRequest();
            Thread.sleep(100);
        }

        // US East fails
        System.out.println("\n--- US East region failure ---");
        regions.get(0).setHealthy(false);

        for (int i = 0; i < 5; i++) {
            router.simulateRequest();
            Thread.sleep(100);
        }

        router.printRegionStatus();

        // US East recovers
        System.out.println("\n--- US East recovers, latency increased ---");
        regions.get(0).setHealthy(true);
        regions.get(0).setLatency(60); // Was 25, now 60 (cold start)

        for (int i = 0; i < 5; i++) {
            router.simulateRequest();
            Thread.sleep(100);
        }

        router.printRegionStatus();

        System.out.printf("%nMulti-region routing ensures:%n");
        System.out.println("  - Traffic always goes to a healthy region");
        System.out.println("  - Lowest-latency region handles most traffic");
        System.out.println("  - Failover is transparent to users");
        System.out.println("  - Capacity is distributed across regions");
    }
}
