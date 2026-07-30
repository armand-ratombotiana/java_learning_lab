package com.cloud.deep.lab06;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CloudMonitoringDeep {

    public enum MetricType { COUNTER, GAUGE, HISTOGRAM }
    public enum Severity { INFO, WARNING, CRITICAL }
    public record MetricPoint(String name, double value, long timestamp, Map<String,String> labels, MetricType type) {}
    public record LogEvent(Instant timestamp, Severity severity, String message, Map<String,String> context) {}
    public record Span(String traceId, String spanId, String parentSpanId, String name, long startTime, long endTime, Map<String,String> attributes) {}
    public record Alert(String name, String description, Severity severity, double threshold, double currentValue) {}

    public static class MetricsStore {
        private final List<MetricPoint> metrics = new CopyOnWriteArrayList<>();

        public void record(MetricPoint point) { metrics.add(point); }

        public double average(String name, long windowMs) {
            var now = System.currentTimeMillis();
            return metrics.stream()
                .filter(m -> m.name().equals(name) && now - m.timestamp() <= windowMs)
                .mapToDouble(MetricPoint::value)
                .average()
                .orElse(0);
        }

        public double percentile(String name, double p, long windowMs) {
            var now = System.currentTimeMillis();
            var vals = metrics.stream()
                .filter(m -> m.name().equals(name) && now - m.timestamp() <= windowMs)
                .mapToDouble(MetricPoint::value)
                .sorted()
                .toArray();
            if (vals.length == 0) return 0;
            int idx = (int) Math.ceil(p / 100.0 * vals.length) - 1;
            return vals[Math.max(0, idx)];
        }
    }

    public static class TraceCollector {
        private final List<Span> spans = new CopyOnWriteArrayList<>();
        private final ThreadLocalRandom rand = ThreadLocalRandom.current();

        public Span createRootSpan(String name) {
            var traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            var spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            var span = new Span(traceId, spanId, "", name, System.currentTimeMillis(), 0, Map.of());
            spans.add(span);
            return span;
        }

        public Span createChildSpan(Span parent, String name) {
            var spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            var span = new Span(parent.traceId(), spanId, parent.spanId(), name, System.currentTimeMillis(), 0, Map.of());
            spans.add(span);
            return span;
        }

        public void endSpan(Span span) {
            var idx = spans.indexOf(span);
            if (idx >= 0) {
                spans.set(idx, new Span(span.traceId(), span.spanId(), span.parentSpanId(), span.name(), span.startTime(),
                    System.currentTimeMillis(), span.attributes()));
            }
        }

        public List<Span> getTrace(String traceId) {
            return spans.stream().filter(s -> s.traceId().equals(traceId)).toList();
        }

        public String exportOtlp() {
            var sb = new StringBuilder();
            sb.append("{\"resourceSpans\":[{\"scopeSpans\":[{\"spans\":[");
            var first = true;
            for (var span : spans) {
                if (!first) sb.append(",");
                first = false;
                sb.append(String.format(
                    "{\"traceId\":\"%s\",\"spanId\":\"%s\",\"parentSpanId\":\"%s\",\"name\":\"%s\",\"startTime\":%d,\"endTime\":%d}",
                    span.traceId(), span.spanId(), span.parentSpanId(), span.name(), span.startTime(), span.endTime()));
            }
            sb.append("]}]}]}");
            return sb.toString();
        }
    }

    public static class AnomalyDetector {
        private final double[] window;
        private int index = 0;
        private int count = 0;
        private final double threshold;

        public AnomalyDetector(int windowSize, double threshold) {
            this.window = new double[windowSize];
            this.threshold = threshold;
        }

        public boolean record(double value) {
            window[index] = value;
            index = (index + 1) % window.length;
            count = Math.min(count + 1, window.length);
            if (count < 2) return false;

            double mean = Arrays.stream(window).limit(count).average().orElse(0);
            double variance = Arrays.stream(window).limit(count).map(v -> Math.pow(v - mean, 2)).average().orElse(0);
            double stddev = Math.sqrt(variance);
            double zScore = stddev > 0 ? Math.abs(value - mean) / stddev : 0;
            return zScore > threshold;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Metrics ===");
        var store = new MetricsStore();
        for (int i = 0; i < 100; i++) {
            store.record(new MetricPoint("request.latency", 50 + ThreadLocalRandom.current().nextDouble(100),
                System.currentTimeMillis() - (i * 1000), Map.of("service", "web"), MetricType.HISTOGRAM));
        }
        System.out.printf("Average latency: %.2fms%n", store.average("request.latency", 120000));
        System.out.printf("P95 latency: %.2fms%n", store.percentile("request.latency", 95, 120000));

        System.out.println("\n=== Distributed Tracing ===");
        var traces = new TraceCollector();
        var root = traces.createRootSpan("handle.order");
        var db = traces.createChildSpan(root, "db.query");
        var cache = traces.createChildSpan(root, "cache.get");
        traces.endSpan(db);
        traces.endSpan(cache);
        traces.endSpan(root);
        System.out.println("Trace spans: " + traces.getTrace(root.traceId()).size());
        System.out.println("OTLP export excerpt: " + traces.exportOtlp().substring(0, 200) + "...");

        System.out.println("\n=== Anomaly Detection ===");
        var detector = new AnomalyDetector(10, 2.5);
        for (int i = 0; i < 15; i++) {
            double val = i < 10 ? 100 : 500;
            boolean anomaly = detector.record(val);
            if (anomaly) System.out.printf("Anomaly detected at index %d: %.0f%n", i, val);
        }
    }
}
