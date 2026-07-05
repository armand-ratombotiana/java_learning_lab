package com.net.dns;

import java.util.*;
import java.util.concurrent.atomic.*;

public class RoundRobinDns {

    public static class RoundRobinResolver {
        private final Map<String, List<String>> records = new HashMap<>();
        private final Map<String, AtomicInteger> counters = new HashMap<>();

        public void addRecord(String domain, List<String> ips) {
            records.put(domain, new ArrayList<>(ips));
            counters.put(domain, new AtomicInteger(0));
            System.out.println("Added " + ips.size() + " IPs for " + domain);
        }

        public String resolve(String domain) {
            List<String> ips = records.get(domain);
            if (ips == null || ips.isEmpty()) return null;

            AtomicInteger counter = counters.get(domain);
            int idx = counter.getAndUpdate(i -> (i + 1) % ips.size());
            String ip = ips.get(idx);

            System.out.println("RR resolve " + domain + " -> " + ip + " (index=" + idx + ")");
            return ip;
        }

        public void removeIp(String domain, String ip) {
            List<String> ips = records.get(domain);
            if (ips != null) {
                ips.remove(ip);
                System.out.println("Removed " + ip + " from " + domain);
            }
        }
    }

    public static class WeightedRoundRobin {
        private final Map<String, List<WeightedIp>> pools = new HashMap<>();
        private final Map<String, AtomicInteger> counters = new HashMap<>();

        public static class WeightedIp {
            final String ip;
            final int weight;

            public WeightedIp(String ip, int weight) {
                this.ip = ip;
                this.weight = weight;
            }
        }

        public void addPool(String domain, List<WeightedIp> ips) {
            List<WeightedIp> expanded = new ArrayList<>();
            for (WeightedIp wip : ips) {
                for (int i = 0; i < wip.weight; i++) {
                    expanded.add(wip);
                }
            }
            pools.put(domain, expanded);
            counters.put(domain, new AtomicInteger(0));
            System.out.println("Added weighted pool for " + domain + " with " + expanded.size() + " entries");
        }

        public String resolve(String domain) {
            List<WeightedIp> pool = pools.get(domain);
            if (pool == null || pool.isEmpty()) return null;
            AtomicInteger counter = counters.get(domain);
            int idx = counter.getAndUpdate(i -> (i + 1) % pool.size());
            return pool.get(idx).ip;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Round-Robin DNS ===");
        RoundRobinResolver rr = new RoundRobinResolver();
        rr.addRecord("api.example.com", Arrays.asList("10.0.0.1", "10.0.0.2", "10.0.0.3"));

        for (int i = 0; i < 5; i++) {
            rr.resolve("api.example.com");
        }

        System.out.println("\n=== Weighted Round-Robin ===");
        WeightedRoundRobin wrr = new WeightedRoundRobin();
        wrr.addPool("api.example.com", Arrays.asList(
            new WeightedRoundRobin.WeightedIp("10.0.0.1", 5),
            new WeightedRoundRobin.WeightedIp("10.0.0.2", 3),
            new WeightedRoundRobin.WeightedIp("10.0.0.3", 2)
        ));

        for (int i = 0; i < 10; i++) {
            System.out.println("  " + wrr.resolve("api.example.com"));
        }
    }
}
