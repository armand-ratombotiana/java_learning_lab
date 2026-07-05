package com.cloud.awsfund;

import java.util.*;
import java.util.concurrent.*;

public class S3OperationsSim {

    public static class S3Object {
        public final String key;
        public final String data;
        public final Map<String, String> metadata;
        public final long createdAt;

        public S3Object(String key, String data, Map<String, String> metadata) {
            this.key = key;
            this.data = data;
            this.metadata = metadata;
            this.createdAt = System.currentTimeMillis();
        }
    }

    public static class S3Bucket {
        private final String name;
        private final String region;
        private final Map<String, S3Object> objects = new ConcurrentHashMap<>();
        private final Map<String, String> policy = new HashMap<>();

        public S3Bucket(String name, String region) {
            this.name = name;
            this.region = region;
        }

        public void putObject(String key, String data, Map<String, String> metadata) {
            S3Object obj = new S3Object(key, data, metadata);
            objects.put(key, obj);
            System.out.println("[" + name + "] PUT " + key + " (" + data.length() + " bytes)");
        }

        public Optional<S3Object> getObject(String key) {
            S3Object obj = objects.get(key);
            if (obj == null) {
                System.out.println("[" + name + "] GET " + key + " -> NOT FOUND");
                return Optional.empty();
            }
            System.out.println("[" + name + "] GET " + key + " -> found");
            return Optional.of(obj);
        }

        public boolean deleteObject(String key) {
            S3Object removed = objects.remove(key);
            System.out.println("[" + name + "] DELETE " + key + " -> " + (removed != null));
            return removed != null;
        }

        public List<String> listObjects() {
            return new ArrayList<>(objects.keySet());
        }

        public int size() { return objects.size(); }
        public String getName() { return name; }
    }

    public static class S3Client {
        private final Map<String, S3Bucket> buckets = new ConcurrentHashMap<>();

        public S3Bucket createBucket(String name, String region) {
            S3Bucket bucket = new S3Bucket(name, region);
            buckets.put(name, bucket);
            System.out.println("Created bucket: " + name + " in " + region);
            return bucket;
        }

        public S3Bucket getBucket(String name) {
            return buckets.get(name);
        }

        public void deleteBucket(String name) {
            S3Bucket removed = buckets.remove(name);
            System.out.println("Deleted bucket: " + name + " -> " + (removed != null));
        }
    }

    public static void main(String[] args) {
        S3Client s3 = new S3Client();
        S3Bucket bucket = s3.createBucket("my-app-data", "us-east-1");

        bucket.putObject("users/1.json", "{\"name\":\"Alice\"}", Map.of("content-type", "application/json"));
        bucket.putObject("config/app.properties", "key=value", Map.of("version", "1.0"));
        bucket.putObject("images/logo.png", "fake-png-data", Map.of("content-type", "image/png"));

        System.out.println("\nBucket objects (" + bucket.size() + "):");
        bucket.listObjects().forEach(k -> System.out.println("  " + k));

        bucket.getObject("users/1.json");
        bucket.getObject("nonexistent");
    }
}
