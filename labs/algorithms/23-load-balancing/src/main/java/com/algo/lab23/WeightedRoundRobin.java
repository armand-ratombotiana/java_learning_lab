package com.algo.lab23;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Weighted Round Robin load balancer.
 * Servers with higher weights receive proportionally more requests.
 * Time: O(n) per request, Space: O(n)
 */
public class WeightedRoundRobin implements LoadBalancer {

    private final Map<String, Integer> weights;
    private final AtomicInteger counter = new AtomicInteger(0);

    public WeightedRoundRobin(Map<String, Integer> weights) {
        this.weights = new HashMap<>(weights);
    }

    @Override
    public String selectServer(List<String> servers, String request) {
        if (servers == null || servers.isEmpty()) return null;
        int totalWeight = servers.stream().mapToInt(s -> weights.getOrDefault(s, 1)).sum();
        int idx = counter.getAndIncrement() % totalWeight;
        int cumulative = 0;
        for (String server : servers) {
            cumulative += weights.getOrDefault(server, 1);
            if (idx < cumulative) return server;
        }
        return servers.get(servers.size() - 1);
    }
}
