package com.genai.lab14;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LLMOps (LLM Operations)
 * 
 * Demonstrates metrics collection, request tracing, anomaly detection,
 * alerting, and deployment strategies in Java.
 */
public class Main {

    /** Metrics collector for monitoring. */
    static class MetricsCollector {
        final Deque<Double> latencies = new ConcurrentLinkedDeque<>();
        final AtomicLong requestCount = new AtomicLong(0);
        final AtomicLong errorCount = new AtomicLong(0);
        final AtomicLong totalTokens = new AtomicLong(0);
        final int maxSamples = 1000;

        void recordRequest(double latencyMs, int tokens, boolean error) {
            requestCount.incrementAndGet();
            totalTokens.addAndGet(tokens);
            if (error) errorCount.incrementAndGet();
            synchronized (latencies) {
                latencies.addLast(latencyMs);
                if (latencies.size() > maxSamples) latencies.removeFirst();
            }
        }

        double p50() { return percentile(0.50); }
        double p95() { return percentile(0.95); }
        double p99() { return percentile(0.99); }
        double errorRate() { return (double) errorCount.get() / requestCount.get(); }
        double avgTokensPerSec() {
            return requestCount.get() > 0 ? (double) totalTokens.get() / requestCount.get() : 0;
        }

        private double percentile(double p) {
            synchronized (latencies) {
                List<Double> sorted = new ArrayList<>(latencies);
                Collections.sort(sorted);
                int idx = (int) Math.ceil(p * sorted.size()) - 1;
                return idx >= 0 ? sorted.get(idx) : 0;
            }
        }
    }

    /** Request tracer with span hierarchy. */
    static class RequestTracer {
        static class Span {
            final String spanId = UUID.randomUUID().toString().substring(0, 8);
            final String parentSpanId;
            final String operation;
            final long startTime = System.nanoTime();
            long endTime;
            Map<String, Object> tags = new HashMap<>();

            Span(String parentSpanId, String operation) {
                this.parentSpanId = parentSpanId;
                this.operation = operation;
            }

            void finish() { endTime = System.nanoTime(); }
            long durationMs() { return (endTime - startTime) / 1_000_000; }
        }

        final List<Span> spans = new ArrayList<>();

        Span startSpan(String parentId, String operation) {
            Span span = new Span(parentId, operation);
            spans.add(span);
            return span;
        }

        void report() {
            System.out.println("=== Trace Report ===");
            for (Span s : spans) {
                System.out.printf("  [%s] %s (parent: %s) -> %dms%n",
                    s.spanId, s.operation, s.parentSpanId, s.durationMs());
            }
        }
    }

    /** Anomaly detector. */
    static class AnomalyDetector {
        double baselineLatency;
        double baselineErrorRate;

        void setBaseline(MetricsCollector mc) {
            baselineLatency = mc.p95();
            baselineErrorRate = mc.errorRate();
        }

        List<String> check(MetricsCollector mc) {
            List<String> alerts = new ArrayList<>();
            if (mc.p95() > baselineLatency * 1.5)
                alerts.add("LATENCY: p95 " + mc.p95() + "ms exceeds 1.5x baseline " + baselineLatency);
            if (mc.errorRate() > baselineErrorRate * 2)
                alerts.add("ERRORS: rate " + mc.errorRate() + " exceeds 2x baseline " + baselineErrorRate);
            return alerts;
        }
    }

    /** Canary deployment logic. */
    static class CanaryDeployer {
        enum State { STABLE, CANARY, ROLLBACK }
        State state = State.STABLE;
        int canaryPercent = 0;
        final int targetPercent = 10;
        final List<String> canaryNodes = new ArrayList<>();

        void startCanary(String nodeId) {
            state = State.CANARY;
            canaryNodes.add(nodeId);
            canaryPercent = targetPercent;
            System.out.println("[CANARY] Started on node " + nodeId + " (" + targetPercent + "%)");
        }

        void rollback() {
            state = State.ROLLBACK;
            canaryNodes.clear();
            canaryPercent = 0;
            System.out.println("[ROLLBACK] Reverting to previous version");
        }

        boolean shouldRouteToCanary(String requestId) {
            return canaryPercent > 0 && requestId.hashCode() % 100 < canaryPercent;
        }
    }

    public static void main(String[] args) {
        MetricsCollector mc = new MetricsCollector();
        Random rng = new Random(42);
        for (int i = 0; i < 500; i++) {
            mc.recordRequest(50 + rng.nextGaussian() * 20, 100 + rng.nextInt(200), rng.nextDouble() < 0.03);
        }

        System.out.println("=== Metrics ===");
        System.out.printf("Requests: %d, Errors: %d%n", mc.requestCount.get(), mc.errorCount.get());
        System.out.printf("p50: %.1fms, p95: %.1fms, p99: %.1fms%n", mc.p50(), mc.p95(), mc.p99());
        System.out.printf("Error rate: %.2f%%%n", mc.errorRate() * 100);

        RequestTracer tracer = new RequestTracer();
        var rootSpan = tracer.startSpan(null, "llm_request");
        var embedSpan = tracer.startSpan(rootSpan.spanId, "embedding");
        embedSpan.finish();
        var genSpan = tracer.startSpan(rootSpan.spanId, "generation");
        genSpan.finish();
        rootSpan.finish();
        tracer.report();

        AnomalyDetector ad = new AnomalyDetector();
        ad.setBaseline(mc);
        MetricsCollector mc2 = new MetricsCollector();
        for (int i = 0; i < 50; i++) mc2.recordRequest(200 + rng.nextGaussian() * 50, 100, i % 5 == 0);
        System.out.println("\n=== Anomaly Detection ===");
        ad.check(mc2).forEach(a -> System.out.println("  ALERT: " + a));

        CanaryDeployer cd = new CanaryDeployer();
        System.out.println("\n=== Canary Deployment ===");
        cd.startCanary("node-5");
        System.out.println("Request req-42 -> canary: " + cd.shouldRouteToCanary("req-42"));
        cd.rollback();

        System.out.println("\nLLMOps concepts validated.");
    }
}
