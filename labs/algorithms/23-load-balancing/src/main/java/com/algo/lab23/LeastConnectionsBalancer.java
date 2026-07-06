package com.algo.lab23;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Least Connections load balancer.
 * Routes requests to the server with the fewest active connections.
 * Time: O(n) per request, Space: O(n)
 */
public class LeastConnectionsBalancer implements LoadBalancer {

    private final Map<String, AtomicInteger> connections = new ConcurrentHashMap<>();

    @Override
    public String selectServer(List<String> servers, String request) {
        if (servers == null || servers.isEmpty()) return null;
        String selected = servers.get(0);
        int minConnections = Integer.MAX_VALUE;
        for (String server : servers) {
            int count = connections.computeIfAbsent(server, k -> new AtomicInteger(0)).get();
            if (count < minConnections) {
                minConnections = count;
                selected = server;
            }
        }
        connections.computeIfAbsent(selected, k -> new AtomicInteger(0)).incrementAndGet();
        return selected;
    }

    public void releaseConnection(String server) {
        AtomicInteger count = connections.get(server);
        if (count != null && count.get() > 0) {
            count.decrementAndGet();
        }
    }

    public int getConnectionCount(String server) {
        return connections.getOrDefault(server, new AtomicInteger(0)).get();
    }
}
