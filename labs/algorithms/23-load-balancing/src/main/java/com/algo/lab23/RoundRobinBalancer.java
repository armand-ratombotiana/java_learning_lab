package com.algo.lab23;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round Robin load balancer.
 * Distributes requests sequentially across servers in a circular order.
 * Time: O(1) per request, Space: O(n) for servers
 */
public class RoundRobinBalancer implements LoadBalancer {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String selectServer(List<String> servers, String request) {
        if (servers == null || servers.isEmpty()) return null;
        int idx = counter.getAndIncrement() % servers.size();
        return servers.get(idx);
    }

    public void reset() {
        counter.set(0);
    }
}
