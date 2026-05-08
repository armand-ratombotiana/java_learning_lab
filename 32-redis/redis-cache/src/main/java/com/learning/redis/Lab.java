package com.learning.redis;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    static class RedisCache<K, V> {
        private final ConcurrentHashMap<K, CacheEntry<V>> store = new ConcurrentHashMap<>();
        private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

        static class CacheEntry<V> {
            V value;
            long expiry;
            CacheEntry(V value, long ttlMillis) {
                this.value = value; this.expiry = System.currentTimeMillis() + ttlMillis;
            }
            boolean isExpired() { return System.currentTimeMillis() > expiry; }
        }

        void set(K key, V value, long ttlMillis) {
            store.put(key, new CacheEntry<>(value, ttlMillis));
        }

        Optional<V> get(K key) {
            var entry = store.get(key);
            if (entry == null) return Optional.empty();
            if (entry.isExpired()) { store.remove(key); return Optional.empty(); }
            return Optional.of(entry.value);
        }

        void delete(K key) { store.remove(key); }

        long size() { return store.size(); }

        void clearExpired() { store.entrySet().removeIf(e -> e.getValue().isExpired()); }

        V getOrCompute(K key, long ttlMillis, Supplier<V> loader) {
            return get(key).orElseGet(() -> {
                V val = loader.get();
                set(key, val, ttlMillis);
                return val;
            });
        }

        Map<K, V> getPattern(String pattern) {
            Map<K, V> result = new HashMap<>();
            for (var e : store.entrySet()) {
                if (!e.getValue().isExpired() && e.getKey().toString().contains(pattern))
                    result.put(e.getKey(), e.getValue().value);
            }
            return result;
        }

        void shutdown() { cleaner.shutdown(); }
    }

    static class PubSub {
        private final Map<String, List<Consumer<String>>> channels = new ConcurrentHashMap<>();

        void subscribe(String channel, Consumer<String> handler) {
            channels.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(handler);
        }

        void publish(String channel, String message) {
            var handlers = channels.get(channel);
            if (handlers != null) handlers.forEach(h -> h.accept(message));
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Redis Concepts Lab ===\n");

        RedisCache<String, String> cache = new RedisCache<>();

        System.out.println("1. Cache SET with TTL:");
        cache.set("session:123", "user_data_abc", 5000);
        cache.set("product:1", "{\"name\":\"Laptop\",\"price\":1200}", 30000);
        cache.set("rate_limit:ip_1", "5", 1000);
        System.out.println("   Stored 3 keys");

        System.out.println("\n2. Cache GET:");
        cache.get("product:1").ifPresent(v -> System.out.println("   product:1 = " + v));

        System.out.println("\n3. Cache-aside pattern (getOrCompute):");
        String val = cache.getOrCompute("product:2", 30000, () -> {
            System.out.println("   (Loaded from DB)");
            return "{\"name\":\"Phone\",\"price\":800}";
        });
        System.out.println("   product:2 = " + val);

        System.out.println("\n4. Cache TTL - expired key removal:");
        cache.set("temp", "expires_fast", 100);
        Thread.sleep(150);
        cache.get("temp").ifPresentOrElse(
            v -> System.out.println("   Still alive (unexpected)"),
            () -> System.out.println("   temp key expired as expected")
        );

        System.out.println("\n5. SCAN/KEYS pattern matching:");
        cache.set("product:3", "Tablet", 30000);
        cache.set("product:4", "Monitor", 30000);
        var matched = cache.getPattern("product:");
        System.out.println("   Keys matching 'product:': " + matched.size());

        System.out.println("\n6. Cache statistics:");
        System.out.println("   Total keys: " + cache.size());

        System.out.println("\n7. Pub/Sub messaging:");
        PubSub pubsub = new PubSub();
        pubsub.subscribe("orders", msg -> System.out.println("   [Subscriber 1] Received: " + msg));
        pubsub.subscribe("orders", msg -> System.out.println("   [Subscriber 2] Received: " + msg));
        System.out.println("   Publishing 'Order created #1001'...");
        pubsub.publish("orders", "Order created #1001");

        System.out.println("\n8. Cache eviction / DELETE:");
        cache.delete("session:123");
        cache.get("session:123").ifPresentOrElse(
            v -> {},
            () -> System.out.println("   session:123 deleted successfully")
        );

        System.out.println("\n9. Rate limiting simulation (INCR pattern):");
        for (int i = 0; i < 3; i++) {
            final int requestNum = i + 1;
            cache.get("rate_limit:ip_1").ifPresent(count -> {
                int c = Integer.parseInt(count) + 1;
                cache.set("rate_limit:ip_1", String.valueOf(c), 1000);
                System.out.println("   Request " + requestNum + ": count=" + c);
            });
        }

        System.out.println("\n=== Lab Complete ===");
    }
}
