package com.cloud.deep.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class MultiCloudOrchestrator {

    public record ComputeInstance(String id, String name, String type, String provider, String zone, boolean healthy) {}
    public record StorageBucket(String name, String provider, String endpoint) {}
    public record MessageQueue(String name, String provider, String endpoint) {}

    public interface CloudCompute {
        ComputeInstance provision(String name, String type, Map<String,String> tags);
        boolean terminate(String instanceId);
        List<ComputeInstance> list();
        boolean healthCheck();
    }

    public interface CloudStorage {
        StorageBucket createBucket(String name, String region);
        boolean upload(String bucket, String key, byte[] data);
        Optional<byte[]> download(String bucket, String key);
        boolean exists(String bucket);
    }

    public interface CloudMessaging {
        MessageQueue createQueue(String name);
        boolean send(String queue, String message);
        Optional<String> receive(String queue, long timeoutMs);
    }

    public static class AwsCompute implements CloudCompute {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);
        private final String region;

        public AwsCompute(String region) { this.region = region; }

        public ComputeInstance provision(String name, String type, Map<String,String> tags) {
            String id = "i-" + UUID.randomUUID().toString().substring(0, 8);
            var inst = new ComputeInstance(id, name, type, "AWS", region, true);
            instances.put(id, inst);
            return inst;
        }

        public boolean terminate(String instanceId) { return instances.remove(instanceId) != null; }

        public List<ComputeInstance> list() { return List.copyOf(instances.values()); }

        public boolean healthCheck() { return true; }
    }

    public static class GcpCompute implements CloudCompute {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();
        private final String region;

        public GcpCompute(String region) { this.region = region; }

        public ComputeInstance provision(String name, String type, Map<String,String> tags) {
            String id = "gce-" + UUID.randomUUID().toString().substring(0, 8);
            var inst = new ComputeInstance(id, name, type, "GCP", region, true);
            instances.put(id, inst);
            return inst;
        }

        public boolean terminate(String instanceId) { return instances.remove(instanceId) != null; }

        public List<ComputeInstance> list() { return List.copyOf(instances.values()); }

        public boolean healthCheck() { return true; }
    }

    public static class AzureCompute implements CloudCompute {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();
        private final String region;

        public AzureCompute(String region) { this.region = region; }

        public ComputeInstance provision(String name, String type, Map<String,String> tags) {
            String id = "az-" + UUID.randomUUID().toString().substring(0, 8);
            var inst = new ComputeInstance(id, name, type, "Azure", region, true);
            instances.put(id, inst);
            return inst;
        }

        public boolean terminate(String instanceId) { return instances.remove(instanceId) != null; }

        public List<ComputeInstance> list() { return List.copyOf(instances.values()); }

        public boolean healthCheck() { return true; }
    }

    public static class FailoverOrchestrator {
        private final List<CloudCompute> providers = new ArrayList<>();
        private final Map<CloudCompute, Boolean> healthStatus = new ConcurrentHashMap<>();

        public FailoverOrchestrator(CloudCompute... providers) {
            Collections.addAll(this.providers, providers);
            for (var p : providers) healthStatus.put(p, true);
        }

        public ComputeInstance provisionWithFailover(String name, String type) {
            for (var provider : providers) {
                if (Boolean.TRUE.equals(healthStatus.get(provider)) && provider.healthCheck()) {
                    try {
                        return provider.provision(name, type, Map.of());
                    } catch (Exception e) {
                        healthStatus.put(provider, false);
                    }
                }
            }
            throw new RuntimeException("All providers unhealthy, cannot provision " + name);
        }

        public void healthCheckLoop(long intervalMs) {
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                for (var p : providers) {
                    boolean ok = p.healthCheck();
                    healthStatus.put(p, ok);
                }
            }, 0, intervalMs, TimeUnit.MILLISECONDS);
        }
    }

    public static void main(String[] args) {
        var aws = new AwsCompute("us-east-1");
        var gcp = new GcpCompute("us-central1");
        var azure = new AzureCompute("eastus");

        var orchestrator = new FailoverOrchestrator(aws, gcp, azure);

        var instance = orchestrator.provisionWithFailover("web-server", "t3.medium");
        System.out.println("Provisioned: " + instance);

        var second = orchestrator.provisionWithFailover("api-server", "e2-standard-2");
        System.out.println("Provisioned: " + second);
    }
}
