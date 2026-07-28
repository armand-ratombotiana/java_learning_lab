package com.mlops.lab08;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.*;

/**
 * Model Monitoring & Observability — Lab 08.
 * <p>
 * Demonstrates drift detection (PSI, KL divergence), performance monitoring
 * (accuracy, latency, error rates), and alerting for ML models in production.
 */
public class ModelMonitoringLab {

    /** Drift detection using statistical methods. */
    static class DriftDetector {

        /**
         * Computes Population Stability Index.
         * PSI = Σ(actual_i - expected_i) × ln(actual_i / expected_i)
         */
        static double computePSI(double[] expected, double[] actual) {
            double psi = 0.0;
            for (int i = 0; i < expected.length; i++) {
                double e = Math.max(expected[i], 1e-10);
                double a = Math.max(actual[i], 1e-10);
                psi += (a - e) * Math.log(a / e);
            }
            return psi;
        }

        /**
         * Computes KL Divergence: D_KL(P||Q) = ΣP(i) × log(P(i)/Q(i))
         */
        static double computeKLDivergence(double[] p, double[] q) {
            double kl = 0.0;
            for (int i = 0; i < p.length; i++) {
                double pi = Math.max(p[i], 1e-10);
                double qi = Math.max(q[i], 1e-10);
                kl += pi * Math.log(pi / qi);
            }
            return kl;
        }

        /**
         * Computes Jensen-Shannon Divergence (symmetrized KL).
         */
        static double computeJSDivergence(double[] p, double[] q) {
            double[] m = new double[p.length];
            for (int i = 0; i < p.length; i++) {
                m[i] = 0.5 * (p[i] + q[i]);
            }
            return 0.5 * computeKLDivergence(p, m) + 0.5 * computeKLDivergence(q, m);
        }

        /** Classifies drift severity based on PSI. */
        static String classifyDrift(double psi) {
            if (psi < 0.1) return "NONE";
            if (psi < 0.25) return "WARNING";
            return "CRITICAL";
        }
    }

    /** Sliding window performance monitor. */
    static class PerformanceMonitor {
        private final ConcurrentLinkedQueue<PredictionRecord> records = new ConcurrentLinkedQueue<>();
        private final int windowSize;

        static class PredictionRecord {
            final boolean correct;
            final long latencyMs;
            final Instant timestamp;
            PredictionRecord(boolean correct, long latencyMs) {
                this.correct = correct;
                this.latencyMs = latencyMs;
                this.timestamp = Instant.now();
            }
        }

        PerformanceMonitor(int windowSize) {
            this.windowSize = windowSize;
        }

        void record(boolean correct, long latencyMs) {
            records.add(new PredictionRecord(correct, latencyMs));
            while (records.size() > windowSize) {
                records.poll();
            }
        }

        double getAccuracy() {
            if (records.isEmpty()) return 0.0;
            long correct = records.stream().filter(r -> r.correct).count();
            return (double) correct / records.size();
        }

        double getP99Latency() {
            if (records.isEmpty()) return 0.0;
            List<Long> latencies = records.stream()
                    .map(r -> r.latencyMs)
                    .sorted()
                    .collect(Collectors.toList());
            int idx = (int) Math.ceil(0.99 * latencies.size()) - 1;
            return latencies.get(Math.max(0, idx));
        }

        long getErrorCount() {
            return records.stream().filter(r -> !r.correct).count();
        }

        void printReport() {
            System.out.printf("""
                    Monitor Report (last %d predictions):
                      Accuracy:   %.2f%%
                      P99 Latency: %.0f ms
                      Errors:     %d / %d
                    """,
                    records.size(),
                    getAccuracy() * 100,
                    getP99Latency(),
                    getErrorCount(), records.size());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Model Monitoring & Observability ===\n");

        // PART 1: Drift Detection
        System.out.println("--- Drift Detection ---");
        // Reference distribution (training data)
        double[] reference = {0.3, 0.25, 0.2, 0.15, 0.1};
        // Current distribution (production data — slight drift)
        double[] current = {0.28, 0.22, 0.21, 0.17, 0.12};
        // Current distribution — major drift
        double[] drifted = {0.15, 0.15, 0.25, 0.25, 0.2};

        double psi = DriftDetector.computePSI(reference, current);
        double psiDrifted = DriftDetector.computePSI(reference, drifted);
        double jsd = DriftDetector.computeJSDivergence(reference, drifted);

        System.out.printf("PSI (normal):       %.4f — %s%n", psi, DriftDetector.classifyDrift(psi));
        System.out.printf("PSI (drifted):      %.4f — %s%n", psiDrifted, DriftDetector.classifyDrift(psiDrifted));
        System.out.printf("JS-Div (drifted):   %.4f%n%n", jsd);

        // PART 2: Performance Monitoring
        System.out.println("--- Performance Monitoring ---");
        PerformanceMonitor monitor = new PerformanceMonitor(1000);
        Random rng = new Random(42);

        // Simulate 1000 predictions with occasional errors and latency spikes
        for (int i = 0; i < 1000; i++) {
            boolean correct = rng.nextDouble() > 0.05; // 95% accuracy
            long latency = (long) (rng.nextGaussian() * 50 + 100); // mean 100ms, std 50ms
            if (i > 800 && i < 850) {
                // Simulate drift period — more errors, higher latency
                correct = rng.nextDouble() > 0.2;
                latency = (long) (rng.nextGaussian() * 100 + 300);
            }
            monitor.record(correct, Math.max(5, latency));
        }
        monitor.printReport();

        // PART 3: Alert Rules
        System.out.println("\n--- Alert Evaluation ---");
        double currentAccuracy = monitor.getAccuracy();
        double currentLatency = monitor.getP99Latency();

        var alerts = new ArrayList<String>();
        if (currentAccuracy < 0.90) alerts.add("CRITICAL: Accuracy dropped to " +
                String.format("%.1f%%", currentAccuracy * 100));
        if (currentLatency > 500) alerts.add("CRITICAL: P99 latency " +
                String.format("%.0fms", currentLatency) + " exceeds 500ms");
        if (psiDrifted > 0.25) alerts.add("CRITICAL: Data drift detected (PSI=" +
                String.format("%.4f)", psiDrifted));
        if (psiDrifted > 0.1 && psiDrifted <= 0.25) alerts.add("WARNING: Minor drift detected (PSI=" +
                String.format("%.4f)", psiDrifted));

        if (alerts.isEmpty()) {
            System.out.println("  ✓ All metrics within thresholds");
        } else {
            alerts.forEach(a -> System.out.println("  ⚠ " + a));
        }
    }
}
