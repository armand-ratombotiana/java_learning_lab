package com.devops.deep.lab02;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceMeshLab {
    public static void main(String[] args) {
        var mesh = new ServiceMesh();
        mesh.registerService("frontend", "v1");
        mesh.registerService("backend", "v1");
        mesh.registerService("backend", "v2");

        mesh.setTrafficSplit("frontend", "backend", Map.of("v1", 90, "v2", 10));

        var mTLS = new MutualTLS();
        mTLS.enableMeshWide();

        var circuitBreaker = new CircuitBreaker("backend", 5, 10);

        var results = new HashMap<String, AtomicInteger>();
        results.put("v1", new AtomicInteger(0));
        results.put("v2", new AtomicInteger(0));

        for (int i = 0; i < 100; i++) {
            var version = mesh.route("frontend", "backend");
            if (version != null) {
                results.get(version).incrementAndGet();
                circuitBreaker.recordSuccess(version);
            }
        }

        System.out.println("Traffic split results (90/10 expected):");
        results.forEach((v, c) -> System.out.println("  " + v + ": " + c.get() + " requests"));

        System.out.println("\nmTLS enabled: " + mTLS.isEnabled());
        System.out.println("Circuit breaker state: " + circuitBreaker.getState());

        // Fault injection test
        mesh.injectFault("backend", "delay", 2000);
        System.out.println("\nFault injected on backend: delay=2000ms");
    }
}

class ServiceMesh {
    private final Map<String, Map<String, List<String>>> services = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> trafficSplits = new ConcurrentHashMap<>();
    private final Map<String, FaultRule> faultInjections = new ConcurrentHashMap<>();

    void registerService(String service, String version) {
        services.computeIfAbsent(service, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(version, k -> new ArrayList<>());
    }

    void setTrafficSplit(String source, String target, Map<String, Integer> split) {
        trafficSplits.put(source + "->" + target, split);
    }

    void injectFault(String service, String faultType, int value) {
        faultInjections.put(service, new FaultRule(faultType, value));
    }

    String route(String source, String target) {
        var split = trafficSplits.get(source + "->" + target);
        if (split == null) return null;

        var rand = new Random().nextInt(100);
        int cumulative = 0;
        for (var entry : split.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) return entry.getKey();
        }
        return split.keySet().iterator().next();
    }
}

record FaultRule(String type, int value) {}

class MutualTLS {
    private boolean enabled = false;

    void enableMeshWide() { this.enabled = true; }
    boolean isEnabled() { return enabled; }
}

class CircuitBreaker {
    private final String service;
    private final int maxConnections;
    private final int maxPendingRequests;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile boolean tripped = false;

    CircuitBreaker(String service, int maxConnections, int maxPendingRequests) {
        this.service = service;
        this.maxConnections = maxConnections;
        this.maxPendingRequests = maxPendingRequests;
    }

    void recordSuccess(String version) {
        if (activeConnections.get() < maxConnections) {
            activeConnections.incrementAndGet();
        }
    }

    String getState() {
        return tripped ? "OPEN (tripped)" : "CLOSED (healthy)";
    }
}
