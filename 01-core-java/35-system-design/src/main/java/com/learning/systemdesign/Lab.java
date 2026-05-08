package com.learning.systemdesign;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== System Design Lab ===\n");

        rateLimiter();
        loadBalancer();
        caching();
        consistentHashing();
        databaseScaling();
        messageQueues();
    }

    static void rateLimiter() {
        System.out.println("--- Rate Limiter (Token Bucket) ---");

        class TokenBucket {
            final int capacity;
            final double rate;
            double tokens;
            long lastRefill = System.nanoTime();
            TokenBucket(int cap, double r) { capacity = cap; rate = r; tokens = cap; }

            synchronized boolean tryConsume() {
                long now = System.nanoTime();
                tokens = Math.min(capacity, tokens + (now - lastRefill) / 1_000_000_000.0 * rate);
                lastRefill = now;
                if (tokens >= 1) { tokens--; return true; }
                return false;
            }
        }

        var limiter = new TokenBucket(10, 5);
        int ok = 0, deny = 0;
        for (int i = 0; i < 20; i++) {
            if (limiter.tryConsume()) ok++; else deny++;
        }
        System.out.println("  Token bucket (cap=10, rate=5/s): allowed=" + ok + " denied=" + deny);

        System.out.println("\n  Algorithms:");
        for (var a : List.of("Token Bucket", "Leaky Bucket", "Fixed Window", "Sliding Window Log", "Sliding Window Counter"))
            System.out.println("  " + a);
    }

    static void loadBalancer() {
        System.out.println("\n--- Load Balancer ---");

        record Backend(String id, int weight) {}
        var backends = List.of(new Backend("s1", 5), new Backend("s2", 3), new Backend("s3", 2));

        var counts = new HashMap<String, Integer>();
        int idx = 0;
        for (int i = 0; i < 20; i++) {
            var b = backends.get(idx++ % backends.size());
            counts.merge(b.id(), 1, Integer::sum);
        }
        System.out.println("  Round-robin: " + counts);

        System.out.println("\n  Strategies:");
        for (var s : List.of("Round Robin", "Weighted RR", "Least Connections", "IP Hash", "Geolocation"))
            System.out.println("  " + s);
    }

    static void caching() {
        System.out.println("\n--- Caching Strategies ---");

        class LRUCache<K, V> extends LinkedHashMap<K, V> {
            final int max;
            LRUCache(int m) { super(16, 0.75f, true); max = m; }
            protected boolean removeEldestEntry(Map.Entry<K, V> e) { return size() > max; }
        }

        var cache = new LRUCache<String, String>(3);
        cache.put("a", "1"); cache.put("b", "2"); cache.put("c", "3");
        cache.get("a"); cache.put("d", "4");
        System.out.println("  LRU(3): contains(a)=" + cache.containsKey("a") + " contains(b)=" + cache.containsKey("b"));

        System.out.println("\n  Strategies:");
        for (var s : List.of("Cache Aside (app checks cache first)",
                "Read Through (cache loads on miss)",
                "Write Through (sync write to cache + DB)",
                "Write Behind (async write to DB)",
                "Refresh Ahead (pre-emptive refresh)"))
            System.out.println("  " + s);
    }

    static void consistentHashing() {
        System.out.println("\n--- Consistent Hashing ---");

        class HashRing {
            final int vnodes;
            final TreeMap<Integer, String> ring = new TreeMap<>();
            HashRing(int v) { vnodes = v; }
            void addNode(String n) { for (int i = 0; i < vnodes; i++) ring.put(Math.abs((n + "#" + i).hashCode()), n); }
            void removeNode(String n) { for (int i = 0; i < vnodes; i++) ring.remove(Math.abs((n + "#" + i).hashCode())); }
            String getNode(String key) {
                if (ring.isEmpty()) return null;
                var e = ring.ceilingEntry(Math.abs(key.hashCode()));
                return (e != null ? e : ring.firstEntry()).getValue();
            }
        }

        var ring = new HashRing(3);
        ring.addNode("s1"); ring.addNode("s2"); ring.addNode("s3");
        var dist = new HashMap<String, Integer>();
        for (var k : List.of("u1","u2","u3","u4","u5","p1","p2","c1","c2","c3")) {
            var n = ring.getNode(k); dist.merge(n, 1, Integer::sum);
        }
        System.out.println("  Distribution (3 nodes, 3 vnodes each): " + dist);
        System.out.println("  Properties: monotonicity, spread, load, stability");
    }

    static void databaseScaling() {
        System.out.println("\n--- Database Scaling Patterns ---");

        for (var s : List.of("Vertical: more CPU/RAM (limited)",
                "Read Replicas: primary writes, replicas reads",
                "Sharding: partition data by key (hash/range/geo)",
                "Federation: split by domain (user DB, order DB)",
                "CQRS: separate read/write models",
                "Connection Pooling: reuse connections"))
            System.out.println("  " + s);

        System.out.println("\n  CAP Theorem:");
        System.out.println("  Consistency - every read gets latest write");
        System.out.println("  Availability - every request gets response");
        System.out.println("  Partition Tolerance - works despite network splits");
        System.out.println("  Choose CP (HBase) or AP (Cassandra)");
    }

    static void messageQueues() {
        System.out.println("\n--- Message Queues ---");

        class MQ {
            final BlockingQueue<String> q = new LinkedBlockingQueue<>(10);
            void pub(String m) throws InterruptedException { q.put(m); }
            void sub(Consumer<String> c) {
                new Thread(() -> { try { while (true) c.accept(q.take()); } catch (InterruptedException e) {} }).start();
            }
        }

        var mq = new MQ();
        mq.sub(m -> System.out.println("    Consumer1: " + m));
        mq.sub(m -> System.out.println("    Consumer2: " + m));
        try { mq.pub("Event 1"); mq.pub("Event 2"); Thread.sleep(50); } catch (InterruptedException e) {}

        System.out.println("\n  Patterns:");
        for (var p : List.of("Point-to-Point (one consumer per msg)",
                "Pub/Sub (all subscribers get msg)",
                "Request/Reply (msg + response queue)",
                "Dead Letter Queue (failed msgs)"))
            System.out.println("  " + p);

        System.out.println("\n  Brokers:");
        for (var b : List.of("Kafka: high throughput, persistent, replay",
                "RabbitMQ: flexible routing, AMQP",
                "ActiveMQ: JMS compliant",
                "SQS: fully managed, auto-scaling"))
            System.out.println("  " + b);
    }
}
