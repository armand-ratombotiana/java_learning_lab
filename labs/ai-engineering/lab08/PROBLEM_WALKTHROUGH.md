# Problem Walkthrough: AI Observability

## Problem 1: Per-User Cost Attribution, Budget Alerts, and Drift Detection — Company: OpenAI

### Interview Scenario
"You're on OpenAI's platform observability team. The platform serves GPT-4 and GPT-3.5-Turbo in production, plus an experimental Claude-3 evaluation pilot. Finance wants per-user cost attribution; SRE wants latency visibility; the ML team wants drift detection on the intent distribution. The lab's `MetricsCollector`, `TokenTracker`, `CostCalculator`, and `DriftDetector` are your building blocks. Build the monitoring walkthrough that: tracks tokens per user and per model, computes exact costs from the lab's price table, alerts when a user exceeds a budget, reports average latency per model, and detects drift with both KL divergence and PSI against the 0.05/0.1 thresholds."

### The Problem
1. Track requests for three users (alice, bob, carol) across three models using the lab's `TokenTracker.trackRequest(model, input, output)`
2. Compute per-user total cost with the lab's `CostCalculator` price table (gpt-4 = $0.03/1K, gpt-3.5-turbo = $0.0015/1K, claude-3 = $0.015/1K)
3. Alert when a user's spend exceeds a per-user budget — carol must trigger ALERT
4. Record per-model latency metrics and report the average for each model
5. Detect drift with KL divergence (threshold 0.05) and PSI (threshold 0.1) — one distribution must stay in bounds, one must trigger DRIFT DETECTED
6. Print an observability summary with total metric counts

### Solution Walkthrough
- Step 1: Copy the lab's `Metric`/`MetricsCollector` verbatim — the `CopyOnWriteArrayList` store with `record`, `getByPrefix`, `average`, and `count`
- Step 2: Extend the lab's `TokenTracker` with a per-model breakdown map so cost can be attributed per model per user — the lab's AtomicLong totals alone can't split costs by model; the walkthrough tracks `[input, output]` per model and sums `calculateCost` per model
- Step 3: Keep the lab's `CostCalculator` and price table untouched, and compute costs via `(total_tokens / 1000.0) * price`
- Step 4: Model latency with a fixed table — gpt-4 250 ms, gpt-3.5-turbo 80 ms, claude-3 150 ms — recorded under the lab's `latency.<model>` naming; production uses the lab's `LatencyMonitor.measure` with `System.nanoTime()`, but fixed values keep the expected output deterministic
- Step 5: Copy the lab's `klDivergence` and add the symmetric `psi` from the guide's formula; both compare reference to current and return a drift boolean vs. the threshold
- Step 6: Wire it together — three `TokenTracker`s per user, budget checks with `>`, latency averages via the collector, and a final ALERT when either drift check fires
- Step 7: Verify the numbers by hand: alice = 1100 gpt-4 tokens = $0.0330, carol = 5000 gpt-4 tokens = $0.1500 vs. a $0.10 budget = ALERT at 150%

### Code
```java
// File: src/com/aiengineering/lab08/ObservabilityWalkthrough.java
package com.aiengineering.lab08;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Walkthrough: observability for a multi-model LLM platform.
 * Reuses the lab's MetricsCollector, TokenTracker, CostCalculator,
 * and DriftDetector. Latency values are recorded from a fixed
 * model-latency table so the expected output is deterministic
 * (production measures wall-clock with System.nanoTime() as the
 * lab's LatencyMonitor does).
 */
public class ObservabilityWalkthrough {

    // ---------- Metrics Collector (lab) ----------

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
                .mapToDouble(m -> m.value)
                .average().orElse(0);
        }

        long count() { return metrics.size(); }
    }

    // ---------- Token Tracker (lab shape, extended with per-model breakdown) ----------

    static class TokenTracker {
        private final String user;
        private final Map<String, long[]> perModel = new ConcurrentHashMap<>(); // [input, output]
        private final AtomicLong requestCount = new AtomicLong();
        private final MetricsCollector collector;

        TokenTracker(String user, MetricsCollector collector) {
            this.user = user;
            this.collector = collector;
        }

        void trackRequest(String model, int inputTokens, int outputTokens) {
            perModel.computeIfAbsent(model, k -> new long[2]);
            long[] totals = perModel.get(model);
            totals[0] += inputTokens;
            totals[1] += outputTokens;
            requestCount.incrementAndGet();
            collector.record("token.input." + model, inputTokens);
            collector.record("token.output." + model, outputTokens);
            collector.record("token.total." + model, inputTokens + outputTokens);
        }

        long getTotalTokens() {
            return perModel.values().stream().mapToLong(v -> v[0] + v[1]).sum();
        }

        double getCost() {
            return perModel.entrySet().stream()
                .mapToDouble(e -> CostCalculator.calculateCost(e.getKey(),
                    Math.toIntExact(e.getValue()[0] + e.getValue()[1])))
                .sum();
        }

        void printReport() {
            System.out.printf("  %s:%n", user);
            System.out.printf("    Total input tokens:  %d%n", perModel.values().stream().mapToLong(v -> v[0]).sum());
            System.out.printf("    Total output tokens: %d%n", perModel.values().stream().mapToLong(v -> v[1]).sum());
            System.out.printf("    Total requests:      %d%n", requestCount.get());
            System.out.printf("    Total cost: $%.4f%n", getCost());
        }
    }

    // ---------- Cost Attribution (lab) ----------

    static class CostCalculator {
        static final Map<String, Double> PRICES_PER_1K_TOKENS = Map.of(
            "gpt-4", 0.03,
            "gpt-3.5-turbo", 0.0015,
            "claude-3", 0.015
        );

        static double calculateCost(String model, int totalTokens) {
            double price = PRICES_PER_1K_TOKENS.getOrDefault(model, 0.01);
            return (totalTokens / 1000.0) * price;
        }
    }

    // ---------- Mock model with fixed latency table ----------

    static class MockModel {
        private final MetricsCollector collector;
        private static final Map<String, Double> LATENCY_MS = Map.of(
            "gpt-4", 250.0,
            "gpt-3.5-turbo", 80.0,
            "claude-3", 150.0
        );

        MockModel(MetricsCollector collector) { this.collector = collector; }

        String generate(String model) {
            collector.record("latency." + model, LATENCY_MS.get(model));
            return "response-" + model;
        }
    }

    // ---------- Drift Detection (lab: KL divergence + PSI) ----------

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

        static double psi(double[] p, double[] q) {
            double psi = 0;
            for (int i = 0; i < p.length; i++) {
                if (p[i] > 0 && q[i] > 0) {
                    psi += (p[i] - q[i]) * Math.log(p[i] / q[i]);
                }
            }
            return psi;
        }

        static boolean detectDrift(double[] reference, double[] current, double threshold) {
            double kl = klDivergence(reference, current);
            boolean drifted = kl > threshold;
            System.out.printf("    KL divergence: %.4f (threshold: %.4f) — %s%n",
                kl, threshold, drifted ? "DRIFT DETECTED" : "in distribution");
            return drifted;
        }

        static boolean detectPsi(double[] reference, double[] current, double threshold) {
            double psi = psi(reference, current);
            boolean drifted = psi > threshold;
            System.out.printf("    PSI: %.4f (threshold: %.4f) — %s%n",
                psi, threshold, drifted ? "DRIFT DETECTED" : "in distribution");
            return drifted;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Observability for a Multi-Model LLM Platform ===\n");

        MetricsCollector collector = new MetricsCollector();

        // --- Token tracking and per-user cost attribution ---
        System.out.println("--- Per-User Token Tracking & Cost Attribution ---");
        TokenTracker alice = new TokenTracker("alice", collector);
        TokenTracker bob = new TokenTracker("bob", collector);
        TokenTracker carol = new TokenTracker("carol", collector);

        alice.trackRequest("gpt-4", 150, 200);
        alice.trackRequest("gpt-4", 300, 450);
        bob.trackRequest("gpt-3.5-turbo", 80, 120);
        bob.trackRequest("claude-3", 200, 180);
        for (int i = 0; i < 5; i++) {
            carol.trackRequest("gpt-4", 400, 600);
        }

        alice.printReport();
        bob.printReport();
        carol.printReport();

        long platformTokens = alice.getTotalTokens() + bob.getTotalTokens() + carol.getTotalTokens();
        double platformCost = alice.getCost() + bob.getCost() + carol.getCost();
        System.out.printf("  platform: total tokens=%d, total cost=$%.4f%n", platformTokens, platformCost);

        // --- Budget alerts ---
        System.out.println("\n--- Budget & Cost-Spike Alerts ---");
        double aliceBudget = 0.04, bobBudget = 0.02, carolBudget = 0.10;
        double aliceCost = alice.getCost();
        double bobCost = bob.getCost();
        double carolCost = carol.getCost();
        System.out.printf("  alice: $%.4f of $%.4f budget (%.0f%% used) — %s%n",
            aliceCost, aliceBudget, aliceCost / aliceBudget * 100, aliceCost > aliceBudget ? "ALERT" : "OK");
        System.out.printf("  bob:   $%.4f of $%.4f budget (%.0f%% used) — %s%n",
            bobCost, bobBudget, bobCost / bobBudget * 100, bobCost > bobBudget ? "ALERT" : "OK");
        System.out.printf("  carol: $%.4f of $%.4f budget (%.0f%% used) — %s%n",
            carolCost, carolBudget, carolCost / carolBudget * 100, carolCost > carolBudget ? "ALERT" : "OK");

        // --- Latency monitoring ---
        System.out.println("\n--- Latency Monitoring (mock inference calls) ---");
        MockModel model = new MockModel(collector);
        String[] calls = {"gpt-4", "gpt-4", "gpt-4", "gpt-3.5-turbo", "gpt-3.5-turbo",
                          "gpt-3.5-turbo", "claude-3", "claude-3", "claude-3"};
        for (String m : calls) {
            model.generate(m);
        }
        System.out.println("  Latency metrics:");
        System.out.printf("    gpt-4: avg %.2f ms%n", collector.average("latency.gpt-4"));
        System.out.printf("    gpt-3.5-turbo: avg %.2f ms%n", collector.average("latency.gpt-3.5-turbo"));
        System.out.printf("    claude-3: avg %.2f ms%n", collector.average("latency.claude-3"));

        // --- Drift detection ---
        System.out.println("\n--- Drift Detection ---");
        double[] reference = {0.3, 0.2, 0.2, 0.15, 0.15};
        double[] current = {0.28, 0.22, 0.19, 0.16, 0.15};
        double[] drifted = {0.5, 0.1, 0.1, 0.2, 0.1};

        System.out.println("  Reference: " + Arrays.toString(reference));
        System.out.println("  Current:   " + Arrays.toString(current));
        boolean drift1 = DriftDetector.detectDrift(reference, current, 0.05);
        DriftDetector.detectPsi(reference, current, 0.1);

        System.out.println("  Current:   " + Arrays.toString(drifted));
        boolean drift2 = DriftDetector.detectDrift(reference, drifted, 0.05);
        DriftDetector.detectPsi(reference, drifted, 0.1);

        if (drift1 || drift2) {
            System.out.println("  ALERT: intent distribution shifted — investigate before next deploy.");
        } else {
            System.out.println("  All distributions in bounds — no alert.");
        }

        // --- Summary ---
        System.out.println("\n--- Observability Summary ---");
        System.out.printf("  Total metrics collected: %d%n", collector.count());
        System.out.printf("  Token metrics: %d | Latency metrics: %d%n",
            collector.getByPrefix("token.").size(), collector.getByPrefix("latency.").size());

        System.out.println("\nWalkthrough complete.");
    }
}
```

### Expected Output
```
=== Walkthrough: Observability for a Multi-Model LLM Platform ===

--- Per-User Token Tracking & Cost Attribution ---
  alice:
    Total input tokens:  450
    Total output tokens: 650
    Total requests:      2
    Total cost: $0.0330
  bob:
    Total input tokens:  280
    Total output tokens: 300
    Total requests:      2
    Total cost: $0.0060
  carol:
    Total input tokens:  2000
    Total output tokens: 3000
    Total requests:      5
    Total cost: $0.1500
  platform: total tokens=6680, total cost=$0.1890

--- Budget & Cost-Spike Alerts ---
  alice: $0.0330 of $0.0400 budget (83% used) — OK
  bob:   $0.0060 of $0.0200 budget (30% used) — OK
  carol: $0.1500 of $0.1000 budget (150% used) — ALERT

--- Latency Monitoring (mock inference calls) ---
  Latency metrics:
    gpt-4: avg 250.00 ms
    gpt-3.5-turbo: avg 80.00 ms
    claude-3: avg 150.00 ms

--- Drift Detection ---
  Reference: [0.3, 0.2, 0.2, 0.15, 0.15]
  Current:   [0.28, 0.22, 0.19, 0.16, 0.15]
    KL divergence: 0.0022 (threshold: 0.0500) — in distribution
    PSI: 0.0044 (threshold: 0.1000) — in distribution
  Current:   [0.5, 0.1, 0.1, 0.2, 0.1]
    KL divergence: 0.1417 (threshold: 0.0500) — DRIFT DETECTED
    PSI: 0.2755 (threshold: 0.1000) — DRIFT DETECTED
  ALERT: intent distribution shifted — investigate before next deploy.

--- Observability Summary ---
  Total metrics collected: 36
  Token metrics: 27 | Latency metrics: 9

Walkthrough complete.
```

### Company Evaluation
- Oracle: Attribution design: per-user tracking, cost calculation, and budget semantics.
- Deloitte: Financial controls: cost governance, budget ownership, and reporting cycles.
- Accenture: Practice: observability build-out, alert calibration, and dashboards.
- PwC: Cost integrity: spend auditability, forecast accuracy, and control design.
- Amazon: Scale: fleet cost analytics, anomaly detection, and cost optimization patterns.

---

## Problem 2: p99 Latency SLA Monitoring — Company: Apple

### Interview Scenario
"You're on Apple's Siri platform team. The latency SLA for the intent classifier is p99 under 200 ms. The nightly run produced 100 latency samples. Build the percentile check that decides PASS vs. BREACH."

### The Problem
1. Sort 100 latency samples
2. Compute p50, p95, p99 by index into the sorted array
3. Compare p99 against the 200 ms SLA
4. Print the verdict and the failing percentile

### Solution Walkthrough
- Step 1: Use the lab's `MetricsCollector.average` philosophy but extend it — an average alone cannot enforce a percentile SLA, so the walkthrough keeps every sample
- Step 2: Sort ascending; percentile = sample at `index = (int)(p / 100 * (n - 1))` (nearest-rank)
- Step 3: Compare p99 to the SLA; the mean can be under 200 ms while p99 breaches — the point of the exercise
- Step 4: Print the verdict

### Code
```java
double[] samples = {112.3, 98.1, 134.7, 221.4, 87.2, 145.9, 178.3, 199.8, 96.5, 110.2,
                    105.4, 143.2, 168.9, 92.7, 122.8, 187.1, 154.3, 99.6, 131.0, 146.2,
                    118.9, 137.4, 208.5, 91.3, 124.6, 156.7, 142.0, 109.8, 173.4, 128.5,
                    121.7, 133.9, 147.6, 100.3, 162.1, 138.8, 115.2, 126.4, 153.9, 119.5,
                    161.3, 135.0, 149.8, 117.6, 128.9, 166.4, 141.2, 125.8, 155.6, 136.1,
                    129.4, 157.8, 132.5, 123.1, 152.0, 139.7, 118.4, 144.5, 165.2, 127.0,
                    158.9, 148.3, 120.6, 130.2, 164.7, 150.5, 116.8, 135.6, 151.4, 146.9,
                    122.3, 159.4, 143.8, 119.2, 132.0, 160.7, 147.1, 126.9, 140.5, 168.2,
                    111.5, 145.3, 163.6, 124.1, 149.2, 137.9, 131.8, 154.7, 142.8, 118.7,
                    130.6, 153.1, 141.5, 121.9, 144.8, 163.0, 139.2, 128.1, 156.0, 133.4};
java.util.Arrays.sort(samples);
double p50 = samples[(int) (0.50 * (samples.length - 1))];
double p95 = samples[(int) (0.95 * (samples.length - 1))];
double p99 = samples[(int) (0.99 * (samples.length - 1))];
double sla = 200.0;
System.out.printf("p50=%.1f ms p95=%.1f ms p99=%.1f ms vs SLA %.0f ms -> %s%n",
    p50, p95, p99, sla, p99 <= sla ? "PASS" : "BREACH");
```
Output: `p50=137.9 ms p95=173.4 ms p99=208.5 ms vs SLA 200 ms -> BREACH` — the average is ~139 ms and looks healthy, but the 99th percentile exceeds the SLA; production alerting must run on percentiles, which is exactly why the lab's demo output of a single average is a simplification.

### Company Evaluation
- Oracle: Percentile math: p99 computation, bucket design, and threshold selection.
- Deloitte: SLA governance: SLA definition, breach response, and reporting.
- Accenture: Monitoring practice: latency dashboards, alert tuning, and runbooks.
- PwC: Control: SLA evidence, breach auditability, and performance compliance.
- Amazon: Scale: distributed latency monitoring and tail-latency engineering.

---

## Problem 3: Per-User Token Budget Enforcement — Company: Uber

### Interview Scenario
"You're on Uber's LLM platform team. The nightly batch job lets each team call the models, but a runaway prompt loop blew through a month of tokens in one night. Enforce a daily token budget per user."

### The Problem
1. Track cumulative tokens per user
2. Check each incoming request against the user's daily cap
3. Allow the request only if under the cap; otherwise block and log
4. Print the final ledger

### Solution Walkthrough
- Step 1: Reuse the lab's `TokenTracker` counters as the running total, then add the budget gate — the tracker tracks, the gate decides
- Step 2: For each request: if `used + newTokens > cap`, block; else add and record with `collector.record("token.total." + user, ...)`
- Step 3: The ledger shows exactly where the cap stopped the batch — observability plus enforcement

### Code
```java
Map<String, Long> used = new java.util.HashMap<>();
long cap = 15000;
int[][] requests = {{2400, 500}, {3100, 800}, {2600, 400}, {4200, 900}, {2500, 300}, {5100, 1200}};
for (int i = 0; i < requests.length; i++) {
    long tokens = requests[i][0] + requests[i][1];
    long cumulative = used.getOrDefault("supply-team", 0L) + tokens;
    if (cumulative > cap) {
        System.out.printf("  req-%02d BLOCKED (%d tokens, would exceed %d cap)%n", i + 1, tokens, cap);
    } else {
        used.put("supply-team", cumulative);
        System.out.printf("  req-%02d allowed (cumulative %d)%n", i + 1, cumulative);
    }
}
```
Output:
```
  req-01 allowed (cumulative 2900)
  req-02 allowed (cumulative 6800)
  req-03 allowed (cumulative 9800)
  req-04 allowed (cumulative 14900)
  req-05 BLOCKED (2800 tokens, would exceed 15000 cap)
  req-06 BLOCKED (6300 tokens, would exceed 15000 cap)
```
The cap stops the runaway loop at 99% of budget instead of letting it run 30% over — the lab's production note on token budgets per user, enforced in code rather than discovered in a bill.

### Company Evaluation
- Oracle: Enforcement design: budget checks, denial semantics, and per-user accounting.
- Deloitte: Cost control: budget allocation, usage review, and user communication.
- Accenture: Implementation: enforcement policy, test coverage, and migration.
- PwC: Financial control: usage auditability, chargeback integrity, and governance.
- Amazon: Scale: per-tenant metering and budget enforcement at platform scale.
