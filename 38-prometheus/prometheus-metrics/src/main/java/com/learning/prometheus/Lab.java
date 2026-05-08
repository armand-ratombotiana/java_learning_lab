package com.learning.prometheus;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class Lab {
    static abstract class Metric {
        String name;
        String help;
        Map<String, String> labels = new LinkedHashMap<>();

        Metric(String name, String help) { this.name = name; this.help = help; }

        Metric withLabels(String... kvs) {
            for (int i = 0; i < kvs.length; i += 2)
                labels.put(kvs[i], kvs[i + 1]);
            return this;
        }

        abstract String scrape();
    }

    static class Counter extends Metric {
        AtomicLong value = new AtomicLong();

        Counter(String name, String help) { super(name, help); }

        void inc() { value.incrementAndGet(); }
        void inc(double v) { value.addAndGet((long) v); }

        String scrape() {
            return "# HELP " + name + " " + help + "\n# TYPE " + name + " counter\n" + name + labelsString() + " " + value.get();
        }

        String labelsString() {
            if (labels.isEmpty()) return "";
            return "{" + labels.entrySet().stream().map(e -> e.getKey() + "=\"" + e.getValue() + "\"").collect(Collectors.joining(",")) + "}";
        }
    }

    static class Gauge extends Metric {
        AtomicDouble value = new AtomicDouble();

        Gauge(String name, String help) { super(name, help); }

        void set(double v) { value.set(v); }
        void inc() { value.addAndGet(1); }
        void dec() { value.addAndGet(-1); }

        String scrape() {
            return "# HELP " + name + " " + help + "\n# TYPE " + name + " gauge\n" + name + labelsString() + " " + value.get();
        }

        String labelsString() {
            if (labels.isEmpty()) return "";
            return "{" + labels.entrySet().stream().map(e -> e.getKey() + "=\"" + e.getValue() + "\"").collect(Collectors.joining(",")) + "}";
        }
    }

    static class Histogram extends Metric {
        final double[] buckets;
        final LongAdder[] counts;
        final LongAdder sum = new LongAdder();
        final LongAdder totalCount = new LongAdder();

        Histogram(String name, String help, double... buckets) {
            super(name, help);
            this.buckets = buckets;
            this.counts = new LongAdder[buckets.length];
            for (int i = 0; i < buckets.length; i++) counts[i] = new LongAdder();
        }

        void observe(double value) {
            sum.add((long) value);
            totalCount.increment();
            for (int i = 0; i < buckets.length; i++)
                if (value <= buckets[i]) counts[i].increment();
        }

        String scrape() {
            var sb = new StringBuilder();
            sb.append("# HELP ").append(name).append(" ").append(help).append("\n");
            sb.append("# TYPE ").append(name).append(" histogram\n");
            for (int i = 0; i < buckets.length; i++)
                sb.append(name).append("_bucket").append(labelsString())
                    .append("{le=\"").append(buckets[i] == Double.MAX_VALUE ? "+Inf" : String.valueOf((long) buckets[i])).append("\"} ")
                    .append(counts[i].sum()).append("\n");
            sb.append(name).append("_count").append(labelsString()).append(" ").append(totalCount.sum()).append("\n");
            sb.append(name).append("_sum").append(labelsString()).append(" ").append(sum.sum());
            return sb.toString();
        }

        String labelsString() {
            if (labels.isEmpty()) return "";
            return "{" + labels.entrySet().stream().map(e -> e.getKey() + "=\"" + e.getValue() + "\"").collect(Collectors.joining(",")) + "}";
        }
    }

    static class AtomicDouble {
        private final AtomicLong bits = new AtomicLong();
        void set(double v) { bits.set(Double.doubleToLongBits(v)); }
        double get() { return Double.longBitsToDouble(bits.get()); }
        void addAndGet(double delta) {
            while (true) {
                long old = bits.get();
                double newVal = Double.longBitsToDouble(old) + delta;
                if (bits.compareAndSet(old, Double.doubleToLongBits(newVal))) return;
            }
        }
    }

    static class PrometheusRegistry {
        List<Metric> metrics = new CopyOnWriteArrayList<>();

        void register(Metric m) { metrics.add(m); }

        String scrape() {
            return metrics.stream().map(Metric::scrape).collect(Collectors.joining("\n\n")) + "\n";
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Prometheus Concepts Lab ===\n");

        PrometheusRegistry registry = new PrometheusRegistry();

        System.out.println("1. Counter metric:");
        Counter httpRequests = new Counter("http_requests_total", "Total HTTP requests");
        httpRequests.withLabels("method", "GET", "path", "/api/orders");
        registry.register(httpRequests);

        for (int i = 0; i < 5; i++) httpRequests.inc();
        System.out.println("   http_requests_total: " + httpRequests.value.get());

        Counter httpErrors = new Counter("http_errors_total", "Total HTTP errors");
        httpErrors.withLabels("method", "POST", "status", "500");
        registry.register(httpErrors);
        httpErrors.inc(3);

        System.out.println("   http_errors_total: " + httpErrors.value.get());

        System.out.println("\n2. Gauge metric:");
        Gauge memoryUsage = new Gauge("memory_usage_bytes", "Current memory usage");
        memoryUsage.withLabels("region", "us-east");
        registry.register(memoryUsage);

        memoryUsage.set(4.5e9);
        System.out.println("   memory_usage_bytes: " + String.format("%.0f", memoryUsage.value.get()));

        memoryUsage.inc();
        System.out.println("   After inc: " + String.format("%.0f", memoryUsage.value.get()));
        memoryUsage.dec();
        System.out.println("   After dec: " + String.format("%.0f", memoryUsage.value.get()));

        Gauge activeConnections = new Gauge("active_connections", "Active database connections");
        registry.register(activeConnections);
        activeConnections.set(42);
        System.out.println("   active_connections: " + String.format("%.0f", activeConnections.value.get()));

        System.out.println("\n3. Histogram metric:");
        Histogram requestDuration = new Histogram("request_duration_seconds", "Request duration", 0.1, 0.5, 1.0, 2.5, 5.0);
        requestDuration.withLabels("endpoint", "/api/products");
        registry.register(requestDuration);

        double[] latencies = {0.05, 0.12, 0.45, 1.2, 3.0, 0.08, 0.22, 0.67};
        for (double lat : latencies) requestDuration.observe(lat);
        System.out.println("   Recorded " + latencies.length + " request durations");

        System.out.println("\n4. Scrape output (Prometheus exposition format):");
        System.out.println(registry.scrape());

        System.out.println("5. PromQL Query examples:");
        System.out.println("   rate(http_requests_total[5m]) - requests per second");
        System.out.println("   avg by(method) (request_duration_seconds) - avg latency per method");
        System.out.println("   histogram_quantile(0.95, rate(request_duration_seconds_bucket[5m])) - p95 latency");

        System.out.println("\n6. Service Discovery (targets):");
        System.out.println("   - static_configs: direct target lists");
        System.out.println("   - file_sd_configs: file-based target discovery");
        System.out.println("   - kubernetes_sd_configs: automatic K8s pod/service discovery");

        System.out.println("\n7. Alerting Rules:");
        System.out.println("   ALERT HighMemoryUsage");
        System.out.println("     IF memory_usage_bytes > 8e9");
        System.out.println("     FOR 5m");
        System.out.println("     LABELS { severity = 'critical' }");

        System.out.println("\n=== Lab Complete ===");
    }
}
