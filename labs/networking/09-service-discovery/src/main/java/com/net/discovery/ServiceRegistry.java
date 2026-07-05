package com.net.discovery;

import java.util.*;
import java.util.concurrent.*;

public class ServiceRegistry {

    public static class ServiceInstance {
        public final String serviceId;
        public final String host;
        public final int port;
        public final Map<String, String> metadata;
        private volatile Status status;
        private long lastHeartbeat;

        public enum Status { UP, DOWN, UNKNOWN }

        public ServiceInstance(String serviceId, String host, int port) {
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
            this.metadata = new ConcurrentHashMap<>();
            this.status = Status.UP;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public void heartbeat() {
            this.lastHeartbeat = System.currentTimeMillis();
            this.status = Status.UP;
        }

        public boolean isExpired(long timeoutMs) {
            return System.currentTimeMillis() - lastHeartbeat > timeoutMs;
        }

        public void markDown() { this.status = Status.DOWN; }

        @Override
        public String toString() {
            return serviceId + " @ " + host + ":" + port + " [" + status + "]";
        }
    }

    public static class Registry {
        private final Map<String, List<ServiceInstance>> services = new ConcurrentHashMap<>();
        private final ScheduledExecutorService healthChecker = Executors.newSingleThreadScheduledExecutor();
        private final long healthTimeoutMs;

        public Registry(long healthTimeoutMs) {
            this.healthTimeoutMs = healthTimeoutMs;
            healthChecker.scheduleAtFixedRate(this::checkHealth, 5, 5, TimeUnit.SECONDS);
        }

        public void register(ServiceInstance instance) {
            services.computeIfAbsent(instance.serviceId, k -> new CopyOnWriteArrayList<>()).add(instance);
            System.out.println("Registered: " + instance);
        }

        public void deregister(String serviceId, String host, int port) {
            List<ServiceInstance> instances = services.get(serviceId);
            if (instances != null) {
                instances.removeIf(i -> i.host.equals(host) && i.port == port);
                System.out.println("Deregistered: " + serviceId + " @ " + host + ":" + port);
            }
        }

        public List<ServiceInstance> getInstances(String serviceId) {
            List<ServiceInstance> instances = services.get(serviceId);
            if (instances == null) return List.of();
            return instances.stream()
                .filter(i -> i.status == ServiceInstance.Status.UP)
                .toList();
        }

        public void heartbeat(String serviceId, String host, int port) {
            List<ServiceInstance> instances = services.get(serviceId);
            if (instances != null) {
                for (ServiceInstance inst : instances) {
                    if (inst.host.equals(host) && inst.port == port) {
                        inst.heartbeat();
                    }
                }
            }
        }

        private void checkHealth() {
            for (List<ServiceInstance> instances : services.values()) {
                for (ServiceInstance inst : instances) {
                    if (inst.isExpired(healthTimeoutMs)) {
                        inst.markDown();
                        System.out.println("Health check failed: " + inst);
                    }
                }
            }
        }

        public void printRegistry() {
            System.out.println("\n=== Service Registry ===");
            services.forEach((id, instances) -> {
                System.out.println("  " + id + ":");
                for (ServiceInstance inst : instances) {
                    System.out.println("    " + inst);
                }
            });
        }

        public void shutdown() { healthChecker.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        Registry registry = new Registry(10000);

        ServiceInstance s1 = new ServiceInstance("user-service", "10.0.0.1", 8081);
        ServiceInstance s2 = new ServiceInstance("user-service", "10.0.0.2", 8081);
        ServiceInstance s3 = new ServiceInstance("order-service", "10.0.0.3", 8082);

        registry.register(s1);
        registry.register(s2);
        registry.register(s3);

        s1.heartbeat();
        s2.heartbeat();
        s3.heartbeat();

        registry.printRegistry();

        System.out.println("\nInstances of user-service: " + registry.getInstances("user-service"));
        registry.shutdown();
    }
}
