package com.aiengineering.lab08;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.time.*;

/**
 * Demonstrates AI observability: token tracking, latency monitoring,
 * cost attribution, and drift detection.
 * <p>
 * Includes a metrics collector, cost calculator, and simple
 * statistical drift detection using KL divergence approximation.
 */
public class AiObservabilityDemo {

    // ---------- Metrics Collector ----------

    static class Metric {
        final String name;
        final double value;
        final long timestamp;

        Metric(String name, double value) {
            this.name = name;
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
    }

    static class MetricsCollector {
        private final List<Metric> metrics = new CopyOnWriteArrayList<>();

        void record(String name, double value) {
            metrics.add(new Metric(name, value));
        }

        List<Metric> getByPrefix(String prefix) {
            return metrics.stream()
                .filter(m -> m.name.startsWith(prefix))
                .toList();
        }

        double average(String name) {
            return metrics.stream()
                .filter(m -> m.name.equals(name))
                .mapToDouble(Metric::value)
                .average().orElse(0);
        }

        long count() { return metrics.size(); }
    }

    // ---------- Token Tracker ----------

    static class TokenTracker {
        private final AtomicLong totalInputTokens = new AtomicLong();
        private final AtomicLong totalOutputTokens = new AtomicLong();
        private final AtomicLong requestCount = new AtomicLong();
        private final MetricsCollector collector;

        TokenTracker(MetricsCollector collector) { this.collector = collector; }

        void trackRequest(String model, int inputTokens, int outputTokens) {
            totalInputTokens.addAndGet(inputTokens);
            totalOutputTokens.addAndGet(outputTokens);
            long count = requestCount.incrementAndGet();
            collector.record("token.input." + model, inputTokens);
            collector.record("token.output." + model, outputTokens);
            collector.record("token.total." + model, inputTokens + outputTokens);
        }

        long getTotalTokens() { return totalInputTokens.get() + totalOutputTokens.get(); }

        void printReport() {
            System.out.printf("  Total input tokens:  %d%n", totalInputTokens.get());
            System.out.printf("  Total output tokens: %d%n", totalOutputTokens.get());
            System.out.printf("  Total requests:      %d%n", requestCount.get());
        }
    }

    // ---------- Latency Monitor ----------

    static class LatencyMonitor {
        private final MetricsCollector collector;

        LatencyMonitor(MetricsCollector collector) { this.collector = collector; }

        <T> T measure(String operation, SupplierWithException<T> task) throws Exception {
            long start = System.nanoTime();
            try {
                return task.run();
            } finally {
                long latencyNs = System.nanoTime() - start;
                double latencyMs = latencyNs / 1_000_000.0;
                collector.record("latency." + operation, latencyMs);
            }
        }

        void printReport() {
            List<Metric> latencies = collector.getByPrefix("latency.");
            System.out.println("  Latency metrics:");
            latencies.stream()
                .map(m -> m.name.replace("latency.", ""))
                .distinct()
                .forEach(op -> System.out.printf("    %s: avg %.2f ms%n",
                    op, collector.average("latency." + op)));
        }
    }

    @FunctionalInterface
    interface SupplierWithException<T> {
        T run() throws Exception;
    }

    // ---------- Cost Attribution ----------

    static class CostCalculator {
        private static final Map<String, Double> PRICES_PER_1K_TOKENS = Map.of(
            "gpt-4", 0.03,
            "gpt-3.5-turbo", 0.0015,
            "claude-3", 0.015
        );

        static double calculateCost(String model, int totalTokens) {
            double price = PRICES_PER_1K_TOKENS.getOrDefault(model, 0.01);
            return (totalTokens / 1000.0) * price;
        }
    }

    // ---------- Drift Detection ----------

    static class DriftDetector {
        static double klDivergence(double[] p, double[] q) {
            double kl = 0;
            for (int i = 0; i < p.length; i++) {
                if (p[i] > 0 && q[i] > 0) {
                    kl += p[i] * Math.log(p[i] / q[i]);
                }
            }
            return kl;
        }

        static boolean detectDrift(double[] referenceDistribution, double[] currentDistribution, double threshold) {
            double kl = klDivergence(referenceDistribution, currentDistribution);
            boolean drifted = kl > threshold;
            System.out.printf("  KL divergence: %.4f (threshold: %.4f) — %s%n",
                kl, threshold, drifted ? "DRIFT DETECTED" : "in distribution");
            return drifted;
        }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) throws Exception {
        System.out.println("=== AI Engineering Academy — Lab 08: AI Observability ===\n");

        MetricsCollector collector = new MetricsCollector();
        TokenTracker tokenTracker = new TokenTracker(collector);
        LatencyMonitor latencyMonitor = new LatencyMonitor(collector);

        // --- Token Tracking ---
        System.out.println("--- Token Tracking & Cost Attribution ---");
        tokenTracker.trackRequest("gpt-4", 150, 200);
        tokenTracker.trackRequest("gpt-3.5-turbo", 80, 120);
        tokenTracker.trackRequest("gpt-4", 300, 450);
        tokenTracker.trackRequest("claude-3", 200, 180);

        tokenTracker.printReport();
        System.out.printf("  Cost (gpt-4): $%.4f%n",
            CostCalculator.calculateCost("gpt-4", 1100));
        System.out.printf("  Cost (gpt-3.5-turbo): $%.4f%n",
            CostCalculator.calculateCost("gpt-3.5-turbo", 200));
        System.out.printf("  Cost (claude-3): $%.4f%n",
            CostCalculator.calculateCost("claude-3", 380));

        // --- Latency Monitoring ---
        System.out.println("\n--- Latency Monitoring ---");
        for (int i = 0; i < 5; i++) {
            int requestId = i;
            latencyMonitor.measure("inference.gpt-4", () -> {
                Thread.sleep(10 + new Random().nextInt(20));
                return "response-" + requestId;
            });
        }
        latencyMonitor.printReport();

        // --- Drift Detection ---
        System.out.println("\n--- Drift Detection ---");
        double[] referenceDistribution = {0.3, 0.2, 0.2, 0.15, 0.15};
        double[] currentDistribution = {0.28, 0.22, 0.19, 0.16, 0.15};
        double[] driftedDistribution = {0.5, 0.1, 0.1, 0.2, 0.1};

        System.out.println("  Reference: " + Arrays.toString(referenceDistribution));
        System.out.println("  Current:   " + Arrays.toString(currentDistribution));
        DriftDetector.detectDrift(referenceDistribution, currentDistribution, 0.05);

        System.out.println("  Current:   " + Arrays.toString(driftedDistribution));
        DriftDetector.detectDrift(referenceDistribution, driftedDistribution, 0.05);

        // --- Summary ---
        System.out.println("\n--- Observability Summary ---");
        System.out.printf("  Total metrics collected: %d%n", collector.count());
        System.out.println("  Token tracker, latency monitor, cost calculator, and drift detector all operational.");

        System.out.println("\nDemo complete.");
    }
}
