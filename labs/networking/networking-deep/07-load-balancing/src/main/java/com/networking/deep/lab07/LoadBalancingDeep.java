package com.networking.deep.lab07;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class LoadBalancingDeep {

    public record Backend(String id, String host, int port, int weight, boolean healthy, int activeConnections) {}
    public record HttpRequest(String method, String path, String clientIp, String cookie) {}

    public interface LbAlgorithm {
        Backend select(List<Backend> backends, String clientKey);
    }

    public static class RoundRobin implements LbAlgorithm {
        private final AtomicInteger counter = new AtomicInteger(0);

        public Backend select(List<Backend> backends, String clientKey) {
            var healthy = backends.stream().filter(Backend::healthy).toList();
            if (healthy.isEmpty()) return null;
            int idx = counter.getAndIncrement() % healthy.size();
            return healthy.get(idx);
        }
    }

    public static class WeightedRoundRobin implements LbAlgorithm {
        private final AtomicInteger counter = new AtomicInteger(0);

        public Backend select(List<Backend> backends, String clientKey) {
            var healthy = backends.stream().filter(Backend::healthy).toList();
            if (healthy.isEmpty()) return null;
            int totalWeight = healthy.stream().mapToInt(Backend::weight).sum();
            int idx = counter.getAndIncrement() % totalWeight;
            int cumulative = 0;
            for (var b : healthy) {
                cumulative += b.weight();
                if (idx < cumulative) return b;
            }
            return healthy.get(0);
        }
    }

    public static class LeastConnections implements LbAlgorithm {
        public Backend select(List<Backend> backends, String clientKey) {
            return backends.stream()
                .filter(Backend::healthy)
                .min(Comparator.comparingInt(Backend::activeConnections))
                .orElse(null);
        }
    }

    public static class ConsistentHash implements LbAlgorithm {
        private static final int VIRTUAL_NODES = 150;

        public Backend select(List<Backend> backends, String clientKey) {
            var healthy = backends.stream().filter(Backend::healthy).toList();
            if (healthy.isEmpty()) return null;
            if (healthy.size() == 1) return healthy.get(0);

            var ring = new TreeMap<Integer, Backend>();
            for (var b : healthy) {
                for (int i = 0; i < VIRTUAL_NODES; i++) {
                    ring.put((b.id() + ":" + i).hashCode() & 0x7FFFFFFF, b);
                }
            }
            int keyHash = clientKey.hashCode() & 0x7FFFFFFF;
            var entry = ring.ceilingEntry(keyHash);
            if (entry == null) entry = ring.firstEntry();
            return entry.getValue();
        }
    }

    public static class LoadBalancer {
        private final List<Backend> backends = new CopyOnWriteArrayList<>();
        private final LbAlgorithm algorithm;
        private final Map<String, String> stickySessions = new ConcurrentHashMap<>();

        public LoadBalancer(LbAlgorithm algorithm) { this.algorithm = algorithm; }

        public void addBackend(Backend backend) { backends.add(backend); }

        public Backend route(HttpRequest request) {
            if (request.cookie() != null && stickySessions.containsKey(request.cookie())) {
                var stickyId = stickySessions.get(request.cookie());
                return backends.stream()
                    .filter(b -> b.id().equals(stickyId) && b.healthy())
                    .findFirst()
                    .orElse(null);
            }
            var selected = algorithm.select(backends, request.clientIp());
            if (selected != null) {
                var cookie = "LB=" + selected.id();
                stickySessions.put(cookie, selected.id());
            }
            return selected;
        }
    }

    public static class HealthChecker {
        private final List<Backend> backends;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public HealthChecker(List<Backend> backends) { this.backends = backends; }

        public void start() {
            scheduler.scheduleAtFixedRate(() -> {
                for (int i = 0; i < backends.size(); i++) {
                    var b = backends.get(i);
                    boolean healthy = simulateCheck(b);
                    backends.set(i, new Backend(b.id(), b.host(), b.port(), b.weight(), healthy, b.activeConnections()));
                }
            }, 0, 5, TimeUnit.SECONDS);
        }

        private boolean simulateCheck(Backend b) { return new Random().nextDouble() > 0.1; }
        public void stop() { scheduler.shutdown(); }
    }

    public static void main(String[] args) {
        var backends = List.of(
            new Backend("app-1", "10.0.0.1", 8080, 5, true, 3),
            new Backend("app-2", "10.0.0.2", 8080, 3, true, 7),
            new Backend("app-3", "10.0.0.3", 8080, 2, true, 1),
            new Backend("app-4", "10.0.0.4", 8080, 5, false, 0)
        );

        System.out.println("=== Round Robin ===");
        var rr = new RoundRobin();
        var lb = new LoadBalancer(rr);
        backends.forEach(lb::addBackend);
        for (int i = 0; i < 5; i++) {
            var backend = lb.route(new HttpRequest("GET", "/api/data", "10.0.0." + i, null));
            System.out.println("  Request " + i + " -> " + (backend != null ? backend.id() : "none"));
        }

        System.out.println("\n=== Weighted Round Robin ===");
        var wrr = new WeightedRoundRobin();
        var lb2 = new LoadBalancer(wrr);
        backends.stream().filter(Backend::healthy).forEach(lb2::addBackend);
        for (int i = 0; i < 6; i++) {
            var backend = lb2.route(new HttpRequest("GET", "/api/data", "10.0.0." + i, null));
            System.out.println("  Request " + i + " -> " + (backend != null ? backend.id() : "none"));
        }

        System.out.println("\n=== Least Connections ===");
        var lc = new LeastConnections();
        var backend = lc.select(backends, "test");
        System.out.println("Selected: " + backend.id() + " (" + backend.activeConnections() + " connections)");

        System.out.println("\n=== Consistent Hash ===");
        var ch = new ConsistentHash();
        var healthy = backends.stream().filter(Backend::healthy).toList();
        for (var key : List.of("user-1", "user-2", "user-3", "user-1")) {
            var b = ch.select(healthy, key);
            System.out.println("  " + key + " -> " + (b != null ? b.id() : "none"));
        }
    }
}
