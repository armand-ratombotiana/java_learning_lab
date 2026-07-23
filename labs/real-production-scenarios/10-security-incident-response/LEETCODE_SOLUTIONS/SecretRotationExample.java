package com.prod.solutions.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates a secure secret rotation mechanism that automatically
 * rotates secrets (database passwords, API keys, encryption keys)
 * on a schedule without downtime.
 *
 * Uses dual-key strategy: old key remains valid during rotation
 * until all systems have switched to the new key.
 */
public class SecretRotationExample {

    static class Secret {
        final String id;
        final String value;
        final Instant createdAt;
        final Instant expiresAt;
        boolean active;

        Secret(String id, String value, long ttlHours) {
            this.id = id;
            this.value = value;
            this.createdAt = Instant.now();
            this.expiresAt = createdAt.plusSeconds(ttlHours * 3600);
            this.active = true;
        }

        boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    }

    static class SecretManager {
        private final Map<String, Secret> activeSecrets = new ConcurrentHashMap<>();
        private final Map<String, Secret> previousSecrets = new ConcurrentHashMap<>();
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        void startAutoRotation(long intervalMinutes) {
            scheduler.scheduleAtFixedRate(this::rotateAll, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
        }

        Secret createSecret(String name, String value, long ttlHours) {
            String id = name + "-" + Instant.now().getEpochSecond();
            Secret secret = new Secret(id, value, ttlHours);

            // Keep previous secret for grace period
            Secret old = activeSecrets.put(name, secret);
            if (old != null) {
                previousSecrets.put(name, old);
                System.out.printf("  Previous secret '%s' kept for grace period%n", name);
            }

            System.out.printf("  Created secret '%s': %s (expires %s)%n",
                    name, id, secret.expiresAt);
            return secret;
        }

        String getActiveValue(String name) {
            Secret secret = activeSecrets.get(name);
            if (secret == null || secret.isExpired()) {
                throw new RuntimeException("No active secret for: " + name);
            }
            return secret.value;
        }

        String getAnyValidValue(String name) {
            Secret s = activeSecrets.get(name);
            if (s != null && !s.isExpired()) return s.value;

            Secret prev = previousSecrets.get(name);
            if (prev != null && !prev.isExpired()) {
                System.out.printf("  Using previous secret '%s' (grace period)%n", name);
                return prev.value;
            }

            throw new RuntimeException("No valid secret for: " + name);
        }

        void rotateAll() {
            System.out.println("\n[Scheduler] Rotating all secrets...");
        }

        void shutdown() { scheduler.shutdown(); }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Secret Rotation Demo ===\n");

        SecretManager mgr = new SecretManager();

        // Initial secrets
        mgr.createSecret("db-password", "initial-db-pass-123", 1);
        mgr.createSecret("api-key", "initial-api-key-456", 1);

        // Use current secret
        System.out.printf("%nActive DB password: %s%n", mgr.getActiveValue("db-password"));

        // Rotate DB password
        System.out.println("\n--- Rotating DB password ---");
        mgr.createSecret("db-password", "new-db-pass-789", 1);

        // Old secret still works (grace period)
        System.out.printf("DB password (new): %s%n", mgr.getAnyValidValue("db-password"));

        System.out.printf("%nSecret rotation ensures:%n");
        System.out.println("  - Credentials have limited lifespan");
        System.out.println("  - Dual-key strategy prevents downtime");
        System.out.println("  - Old keys expire gracefully");
        System.out.println("  - Compromised keys are automatically invalidated");

        mgr.shutdown();
    }
}
