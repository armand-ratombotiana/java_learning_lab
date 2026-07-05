package com.net.dns;

import java.util.*;
import java.util.concurrent.*;

public class DnsResolverSim {

    public static class DnsRecord {
        public final String name;
        public final String type;
        public final String value;
        public final long ttl;

        public DnsRecord(String name, String type, String value, long ttl) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.ttl = ttl;
        }

        @Override
        public String toString() {
            return name + " " + ttl + " " + type + " " + value;
        }
    }

    public static class DnsResolver {
        private final Map<String, List<DnsRecord>> zone = new ConcurrentHashMap<>();
        private final Map<String, CachedResult> cache = new ConcurrentHashMap<>();

        private static class CachedResult {
            final List<DnsRecord> records;
            final long expiresAt;

            CachedResult(List<DnsRecord> records, long expiresAt) {
                this.records = records;
                this.expiresAt = expiresAt;
            }

            boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
        }

        public void addRecord(DnsRecord record) {
            zone.computeIfAbsent(record.name, k -> new CopyOnWriteArrayList<>()).add(record);
        }

        public List<DnsRecord> resolve(String name) {
            CachedResult cached = cache.get(name);
            if (cached != null && !cached.isExpired()) {
                System.out.println("Cache HIT for " + name);
                return cached.records;
            }

            List<DnsRecord> records = zone.get(name);
            if (records != null) {
                long maxTtl = records.stream().mapToLong(r -> r.ttl).max().orElse(300);
                cache.put(name, new CachedResult(records, System.currentTimeMillis() + maxTtl * 1000));
                System.out.println("Resolved " + name + " -> " + records);
            } else {
                System.out.println("NXDOMAIN: " + name);
            }
            return records;
        }

        public void clearCache() { cache.clear(); }
    }

    public static void main(String[] args) {
        DnsResolver resolver = new DnsResolver();

        resolver.addRecord(new DnsRecord("example.com", "A", "93.184.216.34", 300));
        resolver.addRecord(new DnsRecord("example.com", "AAAA", "2606:2800:220:1:248:1893:25c8:1946", 300));
        resolver.addRecord(new DnsRecord("api.example.com", "A", "93.184.216.35", 60));
        resolver.addRecord(new DnsRecord("www.example.com", "CNAME", "example.com", 600));

        System.out.println("=== DNS Resolution ===");
        resolver.resolve("example.com");
        resolver.resolve("api.example.com");
        resolver.resolve("api.example.com");
        resolver.resolve("unknown.test.local");
    }
}
