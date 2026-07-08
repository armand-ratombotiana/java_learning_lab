package com.arch.servicemesh;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TrafficRouter {
    private final Map<String, List<RouteTarget>> routes = new ConcurrentHashMap<>();

    public void addRoute(String serviceName, RouteTarget target) {
        routes.computeIfAbsent(serviceName, k -> new CopyOnWriteArrayList<>()).add(target);
    }

    public RouteTarget selectTarget(String serviceName, Map<String, String> headers) {
        List<RouteTarget> targets = routes.get(serviceName);
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("No routes for service: " + serviceName);
        }
        double roll = ThreadLocalRandom.current().nextDouble(100.0);
        double cumulative = 0.0;
        for (RouteTarget target : targets) {
            if (matchesHeaders(target, headers)) {
                cumulative += target.weight();
                if (roll < cumulative) return target;
            }
        }
        return targets.get(targets.size() - 1);
    }

    private boolean matchesHeaders(RouteTarget target, Map<String, String> headers) {
        if (target.headers().isEmpty()) return true;
        return target.headers().entrySet().stream()
            .allMatch(e -> e.getValue().equals(headers.get(e.getKey())));
    }
}

record RouteTarget(String version, double weight, Map<String, String> headers) {
    public RouteTarget(String version, double weight) {
        this(version, weight, Map.of());
    }
}
