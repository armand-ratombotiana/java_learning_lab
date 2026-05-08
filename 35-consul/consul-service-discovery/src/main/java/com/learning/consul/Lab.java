package com.learning.consul;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {
    record ServiceInstance(String id, String name, String host, int port, String healthEndpoint, boolean healthy) {}

    static class ConsulAgent {
        private final Map<String, List<ServiceInstance>> services = new ConcurrentHashMap<>();
        private final ScheduledExecutorService healthChecker = Executors.newScheduledThreadPool(1);

        void register(ServiceInstance instance) {
            services.computeIfAbsent(instance.name(), k -> new CopyOnWriteArrayList<>()).add(instance);
            System.out.println("   Registered: " + instance.name() + " at " + instance.host() + ":" + instance.port());
        }

        void deregister(String id) {
            services.values().forEach(list -> list.removeIf(s -> s.id().equals(id)));
            System.out.println("   Deregistered: " + id);
        }

        List<ServiceInstance> discover(String serviceName) {
            return services.getOrDefault(serviceName, List.of()).stream()
                .filter(ServiceInstance::healthy)
                .collect(Collectors.toList());
        }

        List<ServiceInstance> discoverWithHealthCheck(String serviceName) {
            return services.getOrDefault(serviceName, List.of()).stream()
                .filter(ServiceInstance::healthy)
                .collect(Collectors.toList());
        }

        void markUnhealthy(String id) {
            services.values().forEach(list -> list.replaceAll(s ->
                s.id().equals(id) ? new ServiceInstance(s.id(), s.name(), s.host(), s.port(), s.healthEndpoint(), false) : s));
        }

        String getKV(String key) {
            var kv = Map.of(
                "config/database/url", "jdbc:postgresql://localhost:5432/mydb",
                "config/database/user", "app_user",
                "config/cache/ttl", "300"
            );
            return kv.get(key);
        }

        void putKV(String key, String value) {
            System.out.println("   KV stored: " + key + " = " + value);
        }

        Map<String, List<ServiceInstance>> allServices() { return services; }

        void shutdown() { healthChecker.shutdown(); }
    }

    static class LoadBalancer {
        private final Random random = new Random();
        private int roundRobin = 0;

        ServiceInstance selectRandom(List<ServiceInstance> instances) {
            return instances.get(random.nextInt(instances.size()));
        }

        ServiceInstance selectRoundRobin(List<ServiceInstance> instances) {
            return instances.get(roundRobin++ % instances.size());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Consul Service Discovery Lab ===\n");

        ConsulAgent consul = new ConsulAgent();
        LoadBalancer lb = new LoadBalancer();

        System.out.println("1. Service Registration:");
        consul.register(new ServiceInstance("srv-auth-1", "auth-service", "10.0.1.10", 8081, "/health", true));
        consul.register(new ServiceInstance("srv-auth-2", "auth-service", "10.0.1.11", 8081, "/health", true));
        consul.register(new ServiceInstance("srv-order-1", "order-service", "10.0.2.10", 8082, "/health", true));
        consul.register(new ServiceInstance("srv-order-2", "order-service", "10.0.2.11", 8082, "/health", true));
        consul.register(new ServiceInstance("srv-payment-1", "payment-service", "10.0.3.10", 8083, "/health", true));

        System.out.println("\n2. Service Discovery:");
        var authInstances = consul.discover("auth-service");
        System.out.println("   Found " + authInstances.size() + " auth-service instances:");
        authInstances.forEach(s -> System.out.println("   - " + s.host() + ":" + s.port()));

        System.out.println("\n3. Load Balancing - Round Robin:");
        for (int i = 0; i < 4; i++) {
            var selected = lb.selectRoundRobin(authInstances);
            System.out.println("   Request " + (i + 1) + " -> " + selected.host() + ":" + selected.port());
        }

        System.out.println("\n4. Health Checking:");
        consul.markUnhealthy("srv-auth-1");
        var healthy = consul.discoverWithHealthCheck("auth-service");
        System.out.println("   Healthy auth-service instances after failure: " + healthy.size());
        healthy.forEach(s -> System.out.println("   - " + s.id()));

        System.out.println("\n5. Key-Value Store:");
        var dbUrl = consul.getKV("config/database/url");
        System.out.println("   config/database/url = " + dbUrl);
        consul.putKV("config/feature/toggles", "{\"new_checkout\": true}");

        System.out.println("\n6. Service Mesh Integration:");
        System.out.println("   Sidecar proxy intercepts all traffic");
        System.out.println("   mTLS between services via Connect");
        System.out.println("   Intentions control which services can communicate");

        System.out.println("\n7. Multi-Datacenter:");
        System.out.println("   WAN gossip protocol for cross-DC federation");
        System.out.println("   Follower DCs can forward queries to leader DC");

        System.out.println("\n8. Watch / Event system:");
        System.out.println("   Watches trigger on service changes");
        System.out.println("   Custom events: consul event -name deploy app-v2");

        consul.shutdown();
        System.out.println("\n=== Lab Complete ===");
    }
}
