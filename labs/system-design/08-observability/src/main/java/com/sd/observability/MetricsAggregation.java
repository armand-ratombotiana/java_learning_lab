package com.sd.observability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class MetricsAggregation {

    public static class Counter {
        private final String name;
        private final AtomicLong value = new AtomicLong(0);

        public Counter(String name) { this.name = name; }

        public void increment() { value.incrementAndGet(); }
        public void increment(long delta) { value.addAndGet(delta); }
        public long getValue() { return value.get(); }
        public void reset() { value.set(0); }
    }

    public static class Gauge {
        private final String name;
        private final AtomicLong value = new AtomicLong(0);

        public Gauge(String name) { this.name = name; }

        public void set(long v) { value.set(v); }
        public long getValue() { return value.get(); }
    }

    public static class Histogram {
        private final String name;
        private final List<Long> observations = new CopyOnWriteArrayList<>();

        public Histogram(String name) { this.name = name; }

        public void record(long value) {
            observations.add(value);
            if (observations.size() > 1000) {
                observations.remove(0);
            }
        }

        public double p50() { return percentile(50); }
        public double p95() { return percentile(95); }
        public double p99() { return percentile(99); }

        private double percentile(int p) {
            List<Long> sorted = new ArrayList<>(observations);
            if (sorted.isEmpty()) return 0;
            Collections.sort(sorted);
            int idx = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
            return sorted.get(Math.max(0, Math.min(idx, sorted.size() - 1)));
        }

        public long count() { return observations.size(); }
    }

    public static class MetricsRegistry {
        private final Map<String, Counter> counters = new ConcurrentHashMap<>();
        private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
        private final Map<String, Histogram> histograms = new ConcurrentHashMap<>();

        public Counter counter(String name) {
            return counters.computeIfAbsent(name, Counter::new);
        }

        public Gauge gauge(String name) {
            return gauges.computeIfAbsent(name, Gauge::new);
        }

        public Histogram histogram(String name) {
            return histograms.computeIfAbsent(name, Histogram::new);
        }

        public void printAll() {
            System.out.println("\n=== Metrics ===");
            counters.forEach((k, v) -> System.out.println("  counter " + k + " = " + v.getValue()));
            gauges.forEach((k, v) -> System.out.println("  gauge " + k + " = " + v.getValue()));
            histograms.forEach((k, v) -> System.out.println("  histogram " + k
                + ": count=" + v.count() + " p50=" + v.p50() + " p95=" + v.p95()));
        }
    }

    public static void main(String[] args) throws Exception {
        MetricsRegistry metrics = new MetricsRegistry();
        Histogram latency = metrics.histogram("request_latency_ms");
        Counter requests = metrics.counter("http_requests_total");
        Gauge activeConn = metrics.gauge("active_connections");

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            requests.increment();
            activeConn.set(rand.nextInt(50));
            latency.record((long) (rand.nextGaussian() * 50 + 100));
        }

        metrics.printAll();
    }
}
