package com.distributed.monitoring;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

public class MetricRegistry {
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();
    private final Map<String, Histogram> histograms = new ConcurrentHashMap<>();

    public interface Metric {
        String getName();
        Map<String, String> getLabels();
    }

    public static class Counter implements Metric {
        private final String name;
        private final Map<String, String> labels;
        private final AtomicLong value = new AtomicLong(0);

        Counter(String name, Map<String, String> labels) {
            this.name = name;
            this.labels = labels;
        }

        public void increment() { value.incrementAndGet(); }
        public void increment(long n) { value.addAndGet(n); }
        public long getValue() { return value.get(); }
        public String getName() { return name; }
        public Map<String, String> getLabels() { return labels; }
    }

    public static class Gauge implements Metric {
        private final String name;
        private final Map<String, String> labels;
        private final DoubleAdder value = new DoubleAdder();

        Gauge(String name, Map<String, String> labels) {
            this.name = name;
            this.labels = labels;
        }

        public void set(double val) { value.reset(); value.add(val); }
        public double getValue() { return value.sum(); }
        public String getName() { return name; }
        public Map<String, String> getLabels() { return labels; }
    }

    public static class Histogram implements Metric {
        private final String name;
        private final Map<String, String> labels;
        private final double[] buckets;
        private final AtomicLong[] counts;
        private final AtomicLong totalCount = new AtomicLong(0);
        private final DoubleAdder sum = new DoubleAdder();

        Histogram(String name, Map<String, String> labels, double[] buckets) {
            this.name = name;
            this.labels = labels;
            this.buckets = Arrays.copyOf(buckets, buckets.length);
            this.counts = new AtomicLong[buckets.length];
            for (int i = 0; i < buckets.length; i++) counts[i] = new AtomicLong(0);
        }

        public void observe(double value) {
            totalCount.incrementAndGet();
            sum.add(value);
            for (int i = 0; i < buckets.length; i++) {
                if (value <= buckets[i]) {
                    counts[i].incrementAndGet();
                    break;
                }
            }
        }

        public long getCount() { return totalCount.get(); }
        public double getSum() { return sum.sum(); }
        public double[] getBuckets() { return buckets; }
        public long[] getBucketCounts() {
            return Arrays.stream(counts).mapToLong(AtomicLong::get).toArray();
        }
        public String getName() { return name; }
        public Map<String, String> getLabels() { return labels; }
    }

    public Counter counter(String name, Map<String, String> labels) {
        return counters.computeIfAbsent(name + labels, k -> new Counter(name, labels));
    }

    public Counter counter(String name) {
        return counter(name, Map.of());
    }

    public Gauge gauge(String name, Map<String, String> labels) {
        return gauges.computeIfAbsent(name + labels, k -> new Gauge(name, labels));
    }

    public Gauge gauge(String name) {
        return gauge(name, Map.of());
    }

    public Histogram histogram(String name, Map<String, String> labels, double[] buckets) {
        return histograms.computeIfAbsent(name + labels, k -> new Histogram(name, labels, buckets));
    }

    public Map<String, Counter> getCounters() { return Map.copyOf(counters); }
    public Map<String, Gauge> getGauges() { return Map.copyOf(gauges); }
    public Map<String, Histogram> getHistograms() { return Map.copyOf(histograms); }

    public String exportPrometheus() {
        StringBuilder sb = new StringBuilder();
        for (Counter c : counters.values()) {
            sb.append("# HELP ").append(c.getName()).append("\n");
            sb.append("# TYPE ").append(c.getName()).append(" counter\n");
            sb.append(c.getName()).append(" ").append(c.getValue()).append("\n");
        }
        for (Gauge g : gauges.values()) {
            sb.append("# HELP ").append(g.getName()).append("\n");
            sb.append("# TYPE ").append(g.getName()).append(" gauge\n");
            sb.append(g.getName()).append(" ").append(g.getValue()).append("\n");
        }
        for (Histogram h : histograms.values()) {
            sb.append("# HELP ").append(h.getName()).append("\n");
            sb.append("# TYPE ").append(h.getName()).append(" histogram\n");
            sb.append(h.getName()).append("_count ").append(h.getCount()).append("\n");
            sb.append(h.getName()).append("_sum ").append(h.getSum()).append("\n");
            double[] buckets = h.getBuckets();
            long[] counts = h.getBucketCounts();
            for (int i = 0; i < buckets.length; i++) {
                sb.append(h.getName()).append("_bucket{le=\"").append(buckets[i]).append("\"} ").append(counts[i]).append("\n");
            }
        }
        return sb.toString();
    }
}
