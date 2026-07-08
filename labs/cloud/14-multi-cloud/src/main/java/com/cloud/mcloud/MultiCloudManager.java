package com.cloud.mcloud;

import java.util.*;
import java.util.concurrent.*;

public class MultiCloudManager {

    public interface CloudProvider {
        ComputeInstance createInstance(String name, String type);
        void deleteInstance(String id);
        StorageBucket createBucket(String name);
        void uploadObject(String bucket, String key, byte[] data);
        byte[] downloadObject(String bucket, String key);
        List<ComputeInstance> listInstances();
    }

    public record ComputeInstance(String id, String name, String type, String provider, String status, String publicIp) {}
    public record StorageBucket(String name, String provider, String endpoint) {}

    public static class AwsProvider implements CloudProvider {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();
        private final Map<String, StorageBucket> buckets = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);

        public ComputeInstance createInstance(String name, String type) {
            String id = "i-" + UUID.randomUUID().toString().substring(0, 8);
            ComputeInstance inst = new ComputeInstance(id, name, type, "AWS", "RUNNING",
                "52." + ThreadLocalRandom.current().nextInt(1, 256) + "." +
                ThreadLocalRandom.current().nextInt(0, 256) + "." +
                ThreadLocalRandom.current().nextInt(1, 256));
            instances.put(id, inst);
            return inst;
        }

        public void deleteInstance(String id) { instances.remove(id); }
        public List<ComputeInstance> listInstances() { return List.copyOf(instances.values()); }

        public StorageBucket createBucket(String name) {
            StorageBucket b = new StorageBucket(name, "AWS", "https://" + name + ".s3.amazonaws.com");
            buckets.put(name, b); return b;
        }
        public void uploadObject(String bucket, String key, byte[] data) {}
        public byte[] downloadObject(String bucket, String key) { return new byte[0]; }
    }

    public static class GcpProvider implements CloudProvider {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);

        public ComputeInstance createInstance(String name, String type) {
            String id = "gce-" + UUID.randomUUID().toString().substring(0, 8);
            ComputeInstance inst = new ComputeInstance(id, name, type, "GCP", "RUNNING",
                "35." + ThreadLocalRandom.current().nextInt(1, 256) + "." +
                ThreadLocalRandom.current().nextInt(0, 256) + "." +
                ThreadLocalRandom.current().nextInt(1, 256));
            instances.put(id, inst); return inst;
        }

        public void deleteInstance(String id) { instances.remove(id); }
        public List<ComputeInstance> listInstances() { return List.copyOf(instances.values()); }
        public StorageBucket createBucket(String name) { return new StorageBucket(name, "GCP", "https://storage.googleapis.com/" + name); }
        public void uploadObject(String bucket, String key, byte[] data) {}
        public byte[] downloadObject(String bucket, String key) { return new byte[0]; }
    }

    public static class AzureProvider implements CloudProvider {
        private final Map<String, ComputeInstance> instances = new ConcurrentHashMap<>();

        public ComputeInstance createInstance(String name, String type) {
            String id = "az-" + UUID.randomUUID().toString().substring(0, 8);
            ComputeInstance inst = new ComputeInstance(id, name, type, "Azure", "RUNNING",
                "20." + ThreadLocalRandom.current().nextInt(1, 256) + "." +
                ThreadLocalRandom.current().nextInt(0, 256) + "." +
                ThreadLocalRandom.current().nextInt(1, 256));
            instances.put(id, inst); return inst;
        }
        public void deleteInstance(String id) { instances.remove(id); }
        public List<ComputeInstance> listInstances() { return List.copyOf(instances.values()); }
        public StorageBucket createBucket(String name) { return new StorageBucket(name, "Azure", "https://" + name + ".blob.core.windows.net"); }
        public void uploadObject(String bucket, String key, byte[] data) {}
        public byte[] downloadObject(String bucket, String key) { return new byte[0]; }
    }

    public static class CloudflareService {
        private final Map<String, String> dnsRecords = new ConcurrentHashMap<>();
        private final Map<String, String> loadBalancerPools = new ConcurrentHashMap<>();

        public void addDnsRecord(String name, String value, String type) { dnsRecords.put(name, value); }
        public void addLoadBalancerPool(String poolName, String endpoint) { loadBalancerPools.put(poolName, endpoint); }
        public String resolve(String name) { return dnsRecords.getOrDefault(name, "NXDOMAIN"); }
        public Map<String, String> getDnsRecords() { return Map.copyOf(dnsRecords); }
    }

    public static void main(String[] args) {
        AwsProvider aws = new AwsProvider();
        GcpProvider gcp = new GcpProvider();
        AzureProvider azure = new AzureProvider();

        aws.createInstance("web-01", "t3.medium");
        gcp.createInstance("web-02", "e2-standard-2");
        azure.createInstance("web-03", "Standard_D2s_v3");

        System.out.println("AWS: " + aws.listInstances().size() + " instances");
        System.out.println("GCP: " + gcp.listInstances().size() + " instances");
        System.out.println("Azure: " + azure.listInstances().size() + " instances");

        CloudflareService cf = new CloudflareService();
        cf.addDnsRecord("app.example.com", "52.1.2.3", "A");
        cf.addDnsRecord("api.example.com", "35.4.5.6", "A");
        System.out.println("DNS: " + cf.resolve("app.example.com"));
    }
}
