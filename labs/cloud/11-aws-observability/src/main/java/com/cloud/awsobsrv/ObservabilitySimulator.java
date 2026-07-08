package com.cloud.awsobsrv;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class ObservabilitySimulator {

    public static class MetricsRegistry {
        private final Map<String, List<Double>> metrics = new ConcurrentHashMap<>();

        public void record(String metricName, double value, Map<String, String> dimensions) {
            String key = metricName + dimensions;
            metrics.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(value);
        }

        public double average(String metricName, Map<String, String> dimensions) {
            String key = metricName + dimensions;
            List<Double> values = metrics.get(key);
            if (values == null || values.isEmpty()) return 0;
            return values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }

        public double percentile(String metricName, Map<String, String> dimensions, double pct) {
            String key = metricName + dimensions;
            List<Double> values = metrics.get(key);
            if (values == null || values.isEmpty()) return 0;
            List<Double> sorted = values.stream().sorted().toList();
            int index = (int) Math.ceil(pct / 100.0 * sorted.size()) - 1;
            return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
        }

        public MetricQueryResult query(MetricQuery query) {
            List<Double> values = metrics.entrySet().stream()
                .filter(e -> e.getKey().startsWith(query.metricName()))
                .flatMap(e -> e.getValue().stream())
                .toList();
            return new MetricQueryResult(query.metricName(), values);
        }

        public record MetricQuery(String metricName, long startTime, long endTime, String period) {}
        public record MetricQueryResult(String metricName, List<Double> values) {}
    }

    public static class TraceSegment {
        private final String traceId;
        private final String segmentName;
        private final long startTime;
        private long endTime;
        private final Map<String, String> annotations = new HashMap<>();
        private final List<TraceSegment> subsegments = new ArrayList<>();
        private boolean fault;

        public TraceSegment(String traceId, String segmentName) {
            this.traceId = traceId;
            this.segmentName = segmentName;
            this.startTime = System.nanoTime();
        }

        public void end() { this.endTime = System.nanoTime(); }

        public void putAnnotation(String key, String value) { annotations.put(key, value); }

        public TraceSegment beginSubsegment(String name) {
            TraceSegment sub = new TraceSegment(traceId, name);
            subsegments.add(sub);
            return sub;
        }

        public void setFault() { this.fault = true; }

        public long getDurationMs() { return (endTime - startTime) / 1_000_000; }

        public String getTraceId() { return traceId; }
        public String getSegmentName() { return segmentName; }
        public Map<String, String> getAnnotations() { return annotations; }
        public boolean isFault() { return fault; }

        public void prettyPrint() {
            System.out.println("Trace: " + traceId);
            System.out.println("  Segment: " + segmentName + " (" + getDurationMs() + "ms)" + (fault ? " [FAULT]" : ""));
            annotations.forEach((k, v) -> System.out.println("    @" + k + "=" + v));
            subsegments.forEach(sub -> {
                System.out.println("    Subsegment: " + sub.segmentName + " (" + sub.getDurationMs() + "ms)");
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MetricsRegistry registry = new MetricsRegistry();
        registry.record("api_latency", 42.5, Map.of("service", "orders", "env", "prod"));
        registry.record("api_latency", 38.2, Map.of("service", "orders", "env", "prod"));
        registry.record("api_latency", 156.8, Map.of("service", "orders", "env", "prod"));
        System.out.println("P50: " + registry.percentile("api_latency", Map.of("service", "orders", "env", "prod"), 50));
        System.out.println("P99: " + registry.percentile("api_latency", Map.of("service", "orders", "env", "prod"), 99));

        TraceSegment trace = new TraceSegment("1-5f9e8a7b-6c5d4e3f2a1b9c8d", "OrderService");
        Thread.sleep(50);
        TraceSegment dbSub = trace.beginSubsegment("DynamoDB Query");
        Thread.sleep(30);
        dbSub.putAnnotation("table", "orders");
        dbSub.end();
        trace.end();
        trace.prettyPrint();
    }
}
