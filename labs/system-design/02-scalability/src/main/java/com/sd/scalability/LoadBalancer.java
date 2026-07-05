package com.sd.scalability;

import java.util.*;
import java.util.concurrent.atomic.*;

public class LoadBalancer {

    public interface Strategy {
        String select(List<String> backends);
    }

    public static class RoundRobin implements Strategy {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public String select(List<String> backends) {
            return backends.get(counter.getAndIncrement() % backends.size());
        }
    }

    public static class LeastConnections implements Strategy {
        private final Map<String, AtomicInteger> connections = new ConcurrentHashMap<>();

        @Override
        public String select(List<String> backends) {
            return backends.stream()
                .min(Comparator.comparingInt(b -> connections.getOrDefault(b, new AtomicInteger(0)).get()))
                .orElse(backends.get(0));
        }

        public void acquire(String backend) {
            connections.computeIfAbsent(backend, k -> new AtomicInteger()).incrementAndGet();
        }

        public void release(String backend) {
            AtomicInteger c = connections.get(backend);
            if (c != null && c.get() > 0) c.decrementAndGet();
        }
    }

    public static class WeightedRoundRobin implements Strategy {
        private final AtomicInteger counter = new AtomicInteger(0);
        private final Map<String, Integer> weights;

        public WeightedRoundRobin(Map<String, Integer> weights) {
            this.weights = weights;
        }

        @Override
        public String select(List<String> backends) {
            List<String> expanded = new ArrayList<>();
            for (String b : backends) {
                int w = weights.getOrDefault(b, 1);
                for (int i = 0; i < w; i++) expanded.add(b);
            }
            return expanded.get(counter.getAndIncrement() % expanded.size());
        }
    }

    public static class LoadBalancerClient {
        private final List<String> backends;
        private final Strategy strategy;

        public LoadBalancerClient(List<String> backends, Strategy strategy) {
            this.backends = backends;
            this.strategy = strategy;
        }

        public String getBackend() {
            String selected = strategy.select(backends);
            System.out.println("LB -> " + selected);
            return selected;
        }
    }

    public static void main(String[] args) {
        List<String> backends = Arrays.asList("server-1", "server-2", "server-3");

        System.out.println("=== Round Robin ===");
        LoadBalancerClient lb = new LoadBalancerClient(backends, new RoundRobin());
        for (int i = 0; i < 6; i++) lb.getBackend();

        System.out.println("\n=== Weighted ===");
        Map<String, Integer> weights = Map.of("server-1", 5, "server-2", 3, "server-3", 2);
        lb = new LoadBalancerClient(backends, new WeightedRoundRobin(weights));
        for (int i = 0; i < 10; i++) lb.getBackend();
    }
}
