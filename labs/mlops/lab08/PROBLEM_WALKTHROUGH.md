# Problem Walkthrough: Model Monitoring & Observability

## Problem 1: Production Drift and Performance Watch for the Ads Model — Company: Google
### Interview Scenario
"You're at Google on the ads-ranking MLOps team. The click-through model's input distribution drifted after a product launch, and the first sign was a PSI spike in one hourly window — but accuracy was still fine, so nobody noticed until CTR dropped. Build the lab's monitoring stack — `DriftDetector` with PSI/JS divergence and `PerformanceMonitor` with a sliding window — and run the demo so the alert fires on the leading indicator (drift) while the lagging indicators (accuracy, latency) are still green."

### The Problem
1. Compute PSI between the training reference and two production windows — one nearly normal, one heavily drifted — and classify each with the lab's severity bands.
2. Compute JS divergence on the drifted pair as a second, bounded signal.
3. Feed a 1,000-prediction sliding window with a degraded mid-run stretch (records 801-849) and print the accuracy, P99 latency, and error count.
4. Evaluate the alert rules: accuracy < 90%, P99 > 500ms, drift > 0.25 — and print exactly what fires.
5. Mirror the lab's seeded simulation so the report is reproducible to the decimal.

### Solution Walkthrough
- Step 1: Reuse `DriftDetector.computePSI` — `Σ (a−e)·ln(a/e)` with `Math.max(x, 1e-10)` guards — and `computeJSDivergence` (symmetrized KL over the midpoint).
- Step 2: Use the lab's reference `{0.3, 0.25, 0.2, 0.15, 0.1}`, normal window `{0.28, 0.22, 0.21, 0.17, 0.12}`, and drifted window `{0.15, 0.15, 0.25, 0.25, 0.2}` — the demo distributions.
- Step 3: Classify via `classifyDrift`: `< 0.1` NONE, `< 0.25` WARNING, `≥ 0.25` CRITICAL — expecting `0.0119 NONE` and `0.2866 CRITICAL`.
- Step 4: Build `PerformanceMonitor(1000)` and replay the lab's seeded stream: 95% baseline accuracy, ~100ms latency, and the degraded stretch (801-849) at 20% errors with ~300ms latency.
- Step 5: Print `printReport()` — the expected `96.10%` accuracy, `369 ms` P99, `39/1000` errors — then evaluate the alert rules in the lab's order: accuracy, latency, critical drift, warning drift.
- Step 6: End with the alert verdict: only the CRITICAL drift alert fires — the leading indicator moving while the lagging indicators hold, which is the retrain trigger.

### Code
```java
package com.mlops.lab08;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ModelMonitoringWalkthrough {

    static class DriftDetector {
        static double computePSI(double[] expected, double[] actual) {
            double psi = 0.0;
            for (int i = 0; i < expected.length; i++) {
                double e = Math.max(expected[i], 1e-10);
                double a = Math.max(actual[i], 1e-10);
                psi += (a - e) * Math.log(a / e);
            }
            return psi;
        }

        static double computeKLDivergence(double[] p, double[] q) {
            double kl = 0.0;
            for (int i = 0; i < p.length; i++) {
                double pi = Math.max(p[i], 1e-10);
                double qi = Math.max(q[i], 1e-10);
                kl += pi * Math.log(pi / qi);
            }
            return kl;
        }

        static double computeJSDivergence(double[] p, double[] q) {
            double[] m = new double[p.length];
            for (int i = 0; i < p.length; i++) {
                m[i] = 0.5 * (p[i] + q[i]);
            }
            return 0.5 * computeKLDivergence(p, m) + 0.5 * computeKLDivergence(q, m);
        }

        static String classifyDrift(double psi) {
            if (psi < 0.1) return "NONE";
            if (psi < 0.25) return "WARNING";
            return "CRITICAL";
        }
    }

    static class PerformanceMonitor {
        private final ConcurrentLinkedQueue<PredictionRecord> records = new ConcurrentLinkedQueue<>();
        private final int windowSize;

        static class PredictionRecord {
            final boolean correct;
            final long latencyMs;
            PredictionRecord(boolean correct, long latencyMs) {
                this.correct = correct;
                this.latencyMs = latencyMs;
            }
        }

        PerformanceMonitor(int windowSize) { this.windowSize = windowSize; }

        void record(boolean correct, long latencyMs) {
            records.add(new PredictionRecord(correct, latencyMs));
            while (records.size() > windowSize) records.poll();
        }

        double getAccuracy() {
            if (records.isEmpty()) return 0.0;
            long correct = records.stream().filter(r -> r.correct).count();
            return (double) correct / records.size();
        }

        double getP99Latency() {
            if (records.isEmpty()) return 0.0;
            List<Long> latencies = records.stream()
                    .map(r -> r.latencyMs).sorted().collect(Collectors.toList());
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
        System.out.println("=== Model Monitoring & Observability (ads CTR model) ===\n");

        System.out.println("--- Drift Detection ---");
        double[] reference = {0.3, 0.25, 0.2, 0.15, 0.1};
        double[] current = {0.28, 0.22, 0.21, 0.17, 0.12};
        double[] drifted = {0.15, 0.15, 0.25, 0.25, 0.2};

        double psi = DriftDetector.computePSI(reference, current);
        double psiDrifted = DriftDetector.computePSI(reference, drifted);
        double jsd = DriftDetector.computeJSDivergence(reference, drifted);

        System.out.printf("PSI (normal):       %.4f — %s%n", psi, DriftDetector.classifyDrift(psi));
        System.out.printf("PSI (drifted):      %.4f — %s%n", psiDrifted, DriftDetector.classifyDrift(psiDrifted));
        System.out.printf("JS-Div (drifted):   %.4f%n%n", jsd);

        System.out.println("--- Performance Monitoring ---");
        PerformanceMonitor monitor = new PerformanceMonitor(1000);
        Random rng = new Random(42);
        for (int i = 0; i < 1000; i++) {
            boolean correct = rng.nextDouble() > 0.05;
            long latency = (long) (rng.nextGaussian() * 50 + 100);
            if (i > 800 && i < 850) {
                correct = rng.nextDouble() > 0.2;
                latency = (long) (rng.nextGaussian() * 100 + 300);
            }
            monitor.record(correct, Math.max(5, latency));
        }
        monitor.printReport();

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
            System.out.println("  All metrics within thresholds");
        } else {
            alerts.forEach(a -> System.out.println("  " + a));
        }
    }
}
```

### Expected Output
```
=== Model Monitoring & Observability (ads CTR model) ===

--- Drift Detection ---
PSI (normal):       0.0119 — NONE
PSI (drifted):      0.2866 — CRITICAL
JS-Div (drifted):   0.0353

--- Performance Monitoring ---
Monitor Report (last 1000 predictions):
  Accuracy:   96.10%
  P99 Latency: 369 ms
  Errors:     39 / 1000

--- Alert Evaluation ---
  CRITICAL: Data drift detected (PSI=0.2866)
```
*(Only the drift alert fires: accuracy 96.10% ≥ 90%, P99 369ms ≤ 500ms, and the warning band `0.1 < PSI ≤ 0.25` is skipped because the drifted PSI is already critical.)*

---

## Problem 2: KL Attribution Across Features — Company: Netflix
### Interview Scenario
"You're at Netflix. PSI says the recommendation features drifted, but you need to know *which* feature moved most. Use KL divergence per feature to attribute the drift."

### The Problem
1. Compute per-feature KL divergence between reference and current distributions.
2. Sort features by contribution and report the top mover.
3. Show the total matches a full-distribution KL computation.

### Solution Walkthrough
- Step 1: Apply `computeKLDivergence` per feature — treating each feature's distribution vector as a one-bin pair per class.
- Step 2: Sum the per-feature contributions; the sum equals the multivariate KL when features are treated independently.
- Step 3: Print the ranked attribution; the top feature is the retraining priority.

### Code
```java
String[] features = {"watch_time_7d", "genre_mix", "device_type", "hour_of_day"};
double[][] ref = {{0.5, 0.3, 0.2}, {0.4, 0.4, 0.2}, {0.6, 0.3, 0.1}, {0.2, 0.5, 0.3}};
double[][] cur = {{0.45, 0.35, 0.2}, {0.4, 0.4, 0.2}, {0.5, 0.35, 0.15}, {0.3, 0.5, 0.2}};

double total = 0.0;
Map<String, Double> perFeature = new LinkedHashMap<>();
for (int f = 0; f < features.length; f++) {
    double kl = DriftDetector.computeKLDivergence(ref[f], cur[f]);
    perFeature.put(features[f], kl);
    total += kl;
}
perFeature.entrySet().stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
        .forEach(e -> System.out.printf("  %s: KL=%.4f%n", e.getKey(), e.getValue()));
System.out.printf("  Total KL: %.4f%n", total);
```
### Expected Output
```
  hour_of_day: KL=0.0405
  device_type: KL=0.0226
  watch_time_7d: KL=0.0064
  genre_mix: KL=0.0000
  Total KL: 0.0696
```

---

## Problem 3: Alert Threshold Engine — Company: Amazon
### Interview Scenario
"You're at Amazon. Alert thresholds live in the monitoring code and the SRE team can't tune them without a deploy. Extract the lab's thresholds into a small rules engine driven by configuration."

### The Problem
1. Define alert rules (metric, operator, threshold, severity) as data.
2. Evaluate a metrics snapshot against the rules.
3. Print the fired alerts, sorted by severity.

### Solution Walkthrough
- Step 1: Model a rule as `(metric, operator, threshold, severity)` — turning the lab's `if` cascade into a table.
- Step 2: Evaluate each rule against the monitor's outputs (96.10% accuracy, 369ms P99, PSI 0.2866).
- Step 3: Print only the rules that fire, in severity order — the same verdict as the lab's demo, now config-driven.

### Code
```java
record AlertRule(String metric, String operator, double threshold, String severity) {}

List<AlertRule> rules = List.of(
        new AlertRule("accuracy", "<", 0.90, "CRITICAL"),
        new AlertRule("p99_latency_ms", ">", 500, "CRITICAL"),
        new AlertRule("drift_psi", ">", 0.25, "CRITICAL"),
        new AlertRule("drift_psi", ">", 0.10, "WARNING"),
        new AlertRule("error_rate", ">", 0.01, "WARNING"));

Map<String, Double> metrics = Map.of(
        "accuracy", 0.9610, "p99_latency_ms", 369.0,
        "drift_psi", 0.2866, "error_rate", 0.039);

List<String> fired = new ArrayList<>();
for (AlertRule r : rules) {
    double v = metrics.get(r.metric);
    boolean hit = switch (r.operator) {
        case "<" -> v < r.threshold;
        case ">" -> v > r.threshold;
        default -> false;
    };
    if (hit) fired.add(String.format("%s: %s=%.4f vs threshold %.2f",
            r.severity, r.metric, v, r.threshold));
}
fired.forEach(System.out::println);
```
### Expected Output
```
CRITICAL: drift_psi=0.2866 vs threshold 0.25
WARNING: drift_psi=0.2866 vs threshold 0.10
WARNING: error_rate=0.0390 vs threshold 0.01
```
*(Evaluation order follows the rules list: the `> 0.25` rule fires CRITICAL, the `> 0.10` rule also fires WARNING because this rules engine applies no upper bound — deliberately simpler than the lab's exclusive `0.1 < psi <= 0.25` branch — and the error rate of 3.9% trips its WARNING rule.)*
