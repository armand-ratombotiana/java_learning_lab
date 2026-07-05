package com.net.discovery;

import java.util.*;
import java.util.concurrent.atomic.*;

public class LoadBalancer {

    public interface Strategy {
        ServiceRegistry.ServiceInstance select(List<ServiceRegistry.ServiceInstance> instances);
    }

    public static class RoundRobinStrategy implements Strategy {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public ServiceRegistry.ServiceInstance select(List<ServiceRegistry.ServiceInstance> instances) {
            int idx = counter.getAndIncrement() % instances.size();
            return instances.get(idx);
        }
    }

    public static class RandomStrategy implements Strategy {
        private final Random random = new Random();

        @Override
        public ServiceRegistry.ServiceInstance select(List<ServiceRegistry.ServiceInstance> instances) {
            return instances.get(random.nextInt(instances.size()));
        }
    }

    public static class LeastConnectionsStrategy implements Strategy {
        private final Map<String, AtomicInteger> connections = new ConcurrentHashMap<>();

        @Override
        public ServiceRegistry.ServiceInstance select(List<ServiceRegistry.ServiceInstance> instances) {
            return instances.stream()
                .min(Comparator.comparingInt(i ->
                    connections.getOrDefault(i.serviceId + ":" + i.host, new AtomicInteger(0)).get()))
                .orElse(instances.get(0));
        }

        public void acquire(String key) {
            connections.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();
        }

        public void release(String key) {
            AtomicInteger c = connections.get(key);
            if (c != null) c.decrementAndGet();
        }
    }

    public static class LoadBalancerClient {
        private final Map<String, List<ServiceRegistry.ServiceInstance>> services;
        private final Strategy strategy;

        public LoadBalancerClient(Map<String, List<ServiceRegistry.ServiceInstance>> services, Strategy strategy) {
            this.services = services;
            this.strategy = strategy;
        }

        public ServiceRegistry.ServiceInstance getInstance(String serviceId) {
            List<ServiceRegistry.ServiceInstance> instances = services.get(serviceId);
            if (instances == null || instances.isEmpty()) {
                throw new NoSuchElementException("No instances for " + serviceId);
            }
            ServiceRegistry.ServiceInstance selected = strategy.select(instances);
            System.out.println("LB [" + strategy.getClass().getSimpleName() + "] " + serviceId
                + " -> " + selected.host + ":" + selected.port);
            return selected;
        }
    }

    public static void main(String[] args) {
        ServiceRegistry.Registry registry = new ServiceRegistry.Registry(30000);
        ServiceRegistry.ServiceInstance s1 = new ServiceRegistry.ServiceInstance("api", "10.0.0.1", 8080);
        ServiceRegistry.ServiceInstance s2 = new ServiceRegistry.ServiceInstance("api", "10.0.0.2", 8080);
        ServiceRegistry.ServiceInstance s3 = new ServiceRegistry.ServiceInstance("api", "10.0.0.3", 8080);

        Map<String, List<ServiceRegistry.ServiceInstance>> services = new HashMap<>();
        services.put("api", List.of(s1, s2, s3));

        LoadBalancerClient lb = new LoadBalancerClient(services, new RoundRobinStrategy());

        System.out.println("=== Load Balancer ===");
        for (int i = 0; i < 6; i++) {
            lb.getInstance("api");
        }
    }
}
