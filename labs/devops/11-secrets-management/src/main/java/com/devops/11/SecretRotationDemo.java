package com.devops.secrets;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretRotationDemo {
    private final Map<String, SecretEntry> store = new ConcurrentHashMap<>();

    public void store(String key, String value) {
        store.put(key, new SecretEntry(value, Instant.now()));
        System.out.println("Secret '" + key + "' stored at " + Instant.now());
    }

    public void rotate(String key, String newValue) {
        SecretEntry old = store.get(key);
        if (old != null) {
            System.out.println("Rotating secret '" + key + "' (age: "
                + java.time.Duration.between(old.createdAt, Instant.now()).toHours() + "h)");
        }
        store.put(key, new SecretEntry(newValue, Instant.now()));
    }

    public void audit() {
        System.out.println("\n=== Secret Audit ===");
        store.forEach((k, v) ->
            System.out.println("  " + k + " | created: " + v.createdAt));
    }

    private static class SecretEntry {
        final String value;
        final Instant createdAt;

        SecretEntry(String value, Instant createdAt) {
            this.value = value;
            this.createdAt = createdAt;
        }
    }

    public static void main(String[] args) {
        SecretRotationDemo demo = new SecretRotationDemo();
        demo.store("api-key", "ak-12345");
        demo.store("db-password", "db-secret");
        demo.rotate("api-key", "ak-67890");
        demo.audit();
    }
}
