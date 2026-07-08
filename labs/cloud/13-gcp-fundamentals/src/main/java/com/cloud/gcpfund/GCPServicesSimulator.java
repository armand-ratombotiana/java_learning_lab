package com.cloud.gcpfund;

import java.util.*;
import java.util.concurrent.*;

public class GCPServicesSimulator {

    public static class ComputeEngineInstance {
        private final String name;
        private final String machineType;
        private final String zone;
        private Status status;
        private final String publicIp;

        public enum Status { PROVISIONING, RUNNING, STOPPING, STOPPED, TERMINATED }

        public ComputeEngineInstance(String name, String machineType, String zone) {
            this.name = name; this.machineType = machineType; this.zone = zone;
            this.status = Status.PROVISIONING;
            this.publicIp = generateIp();
        }

        private String generateIp() { Random r = new Random(); return "34." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256); }

        public void start() { if (status == Status.STOPPED || status == Status.PROVISIONING) status = Status.RUNNING; }
        public void stop() { if (status == Status.RUNNING) status = Status.STOPPED; }
        public void terminate() { status = Status.TERMINATED; }
        public Status getStatus() { return status; }
        public String getName() { return name; }
        public String getPublicIp() { return publicIp; }

        @Override public String toString() { return name + " (" + machineType + ") [" + zone + "] " + status + " " + publicIp; }
    }

    public static class CloudStorageBucket {
        private final String name;
        private final String location;
        private final Map<String, byte[]> objects = new ConcurrentHashMap<>();
        private final Map<String, Map<String, String>> metadata = new ConcurrentHashMap<>();

        public CloudStorageBucket(String name, String location) { this.name = name; this.location = location; }

        public void uploadObject(String objectName, byte[] data, Map<String, String> objectMetadata) {
            objects.put(objectName, data);
            metadata.put(objectName, objectMetadata);
        }

        public byte[] downloadObject(String objectName) { return objects.get(objectName); }

        public List<String> listObjects() { return List.copyOf(objects.keySet()); }

        public void deleteObject(String objectName) { objects.remove(objectName); metadata.remove(objectName); }

        public String getName() { return name; }
    }

    public static class GkeCluster {
        private final String name;
        private final String location;
        private final int nodeCount;
        private Status status;
        private final List<String> namespaces = new CopyOnWriteArrayList<>();

        public enum Status { PROVISIONING, RUNNING, RECONCILING, STOPPED }

        public GkeCluster(String name, String location, int nodeCount) {
            this.name = name; this.location = location; this.nodeCount = nodeCount;
            this.status = Status.PROVISIONING;
        }

        public void activate() { status = Status.RUNNING; namespaces.add("default"); }

        public void createNamespace(String ns) { if (!namespaces.contains(ns)) namespaces.add(ns); }

        public void deployWorkload(String name, String image, int replicas) {
            System.out.println("Deploying " + name + " (" + image + ") x" + replicas + " to " + this.name);
        }

        public Status getStatus() { return status; }
        public String getName() { return name; }
    }

    public static void main(String[] args) {
        ComputeEngineInstance vm = new ComputeEngineInstance("web-server", "e2-standard-2", "us-central1-a");
        vm.start();
        System.out.println("VM: " + vm);

        CloudStorageBucket bucket = new CloudStorageBucket("my-java-bucket", "US-CENTRAL1");
        bucket.uploadObject("data.txt", "Hello GCP Cloud Storage!".getBytes(), Map.of("content-type", "text/plain"));
        byte[] content = bucket.downloadObject("data.txt");
        System.out.println("Bucket object: " + new String(content));

        GkeCluster cluster = new GkeCluster("prod-cluster", "us-central1", 3);
        cluster.activate();
        cluster.deployWorkload("java-app", "gcr.io/my-project/java-app:latest", 3);
    }
}
