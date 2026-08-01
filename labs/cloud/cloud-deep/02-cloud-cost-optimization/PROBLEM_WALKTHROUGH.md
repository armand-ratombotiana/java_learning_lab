# Lab 02: Problem Walkthrough — Cloud Cost Optimization Engine

## Problem Statement

Design a cloud cost optimization engine that produces rightsizing recommendations for a fleet of VMs across multiple clouds. Given per-minute utilization samples (CPU %, memory %) for each instance over an observation window, the engine must:

1. Compute a **usage profile** per instance: p50, p95, p99 percentiles for CPU and memory.
2. Classify each instance: `RIGHT_SIZE` (resize down), `SHUTDOWN` (idle), `KEEP` (correctly sized), or `SUPPRESS` (would save less than the minimum threshold).
3. For rightsizing candidates, find the smallest instance in the family ladder whose **simulated** p99 utilization fits under the headroom target (75% CPU, 85% memory).
4. Apply **anti-thrashing** guards: suppress recommendations when the utilization trend is rising, and honor a per-instance cooldown after a resize.
5. Compute projected monthly savings for every recommendation, using an effective hourly rate.

**Constraints**

- The engine must reason in normalized units (vCPU count), not cloud-specific SKUs.
- Recommendations must be deterministic: same input, same output.
- The headroom simulation must account for the fact that utilization doubles when instance size halves.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model the inputs

An `InstanceProfile` records the observation window and a per-minute sample series. A sample holds CPU and memory percentages at a point in time.

```java
package com.cloud.deep.lab02;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class CostOptimizationEngine {

    public record UtilizationSample(Instant ts, double cpuPct, double memPct) {}

    public record InstanceProfile(String instanceId, String account, String environment,
                                  int currentVCpu, double currentMemoryGb,
                                  double pricePerHour,
                                  List<UtilizationSample> samples,
                                  Instant lastResize) {}

    public enum RecommendationKind { RIGHT_SIZE, SHUTDOWN, KEEP, SUPPRESS }

    public record Recommendation(String instanceId, RecommendationKind kind,
                                 int currentVCpu, int recommendedVCpu,
                                 String reason, double projectedMonthlySavings,
                                 double confidence) {}
```

### Step 2: Compute percentiles

Percentile computation uses the nearest-rank method over the sample series — deterministic and simple. Percentiles are computed per metric.

```java
    private static double percentile(List<Double> values, double p) {
        if (values.isEmpty()) return 0.0;
        List<Double> sorted = values.stream().sorted().toList();
        int rank = (int) Math.ceil(p / 100.0 * sorted.size());
        return sorted.get(Math.min(rank, sorted.size()) - 1);
    }

    private record UsageProfile(double cpuP50, double cpuP95, double cpuP99,
                                double memP99, double trendSlope, double avgCpu) {}

    private static UsageProfile profile(InstanceProfile inst) {
        List<Double> cpu = new ArrayList<>();
        List<Double> mem = new ArrayList<>();
        for (UtilizationSample s : inst.samples()) {
            cpu.add(s.cpuPct());
            mem.add(s.memPct());
        }
        return new UsageProfile(
                percentile(cpu, 50), percentile(cpu, 95), percentile(cpu, 99),
                percentile(mem, 99), linearTrendSlope(cpu), cpu.stream()
                        .mapToDouble(Double::doubleValue).average().orElse(0.0));
    }

    private static double linearTrendSlope(List<Double> cpu) {
        int n = cpu.size();
        if (n < 8) return 0.0;
        double meanX = (n - 1) / 2.0;
        double meanY = cpu.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            num += (i - meanX) * (cpu.get(i) - meanY);
            den += (i - meanX) * (i - meanX);
        }
        return den == 0 ? 0.0 : num / den;
    }
```

The trend slope is expressed in CPU-percent per sample; a consistently positive slope signals a growing workload.

### Step 3: Define the instance family ladder

The engine knows the size ladder of a generic instance family — number of vCPUs, in increasing order. A simulated resize maps utilization onto the candidate size: `simulatedPct = observedPct * currentVCpu / candidateVCpu`.

```java
    private static final int[] FAMILY_LADDER = {2, 4, 8, 16, 32};

    private static final double CPU_HEADROOM_P99 = 75.0;
    private static final double MEM_HEADROOM_P99 = 85.0;
    private static final double IDLE_THRESHOLD_P95 = 2.0;
    private static final double MIN_MONTHLY_SAVINGS = 20.0;
    private static final double MIN_SLOPE_TO_SUPPRESS = 0.05;
    private static final long COOLDOWN_DAYS = 30;

    private static double simulatedPct(double observedPct, int currentVCpu, int candidateVCpu) {
        return observedPct * (double) currentVCpu / candidateVCpu;
    }
```

### Step 4: Classification logic

The classifier decides the recommendation kind:

- **SHUTDOWN**: p95 CPU and memory below 2% → idle.
- **RIGHT_SIZE**: finds the smallest ladder size whose simulated p99 CPU ≤ 75% and simulated p99 memory ≤ 85%, that is strictly smaller than the current size.
- **KEEP**: no smaller size fits.
- **SUPPRESS**: a fit exists but projected savings fall below the minimum threshold.

Anti-thrashing guards are applied here as well: a positive trend slope above the threshold suppresses the downsize, and a resource resized within the cooldown window is suppressed.

```java
    public List<Recommendation> evaluate(List<InstanceProfile> instances) {
        List<Recommendation> out = new ArrayList<>();
        for (InstanceProfile inst : instances) {
            out.add(evaluateOne(inst));
        }
        return out;
    }

    Recommendation evaluateOne(InstanceProfile inst) {
        UsageProfile p = profile(inst);
        if (p.cpuP95() < IDLE_THRESHOLD_P95 && p.memP99() < IDLE_THRESHOLD_P95) {
            double savings = 730.0 * inst.pricePerHour(); // ~730 hours/month
            return new Recommendation(inst.instanceId(), RecommendationKind.SHUTDOWN,
                    inst.currentVCpu(), 0,
                    "Idle: p95 CPU " + fmt(p.cpuP95()) + "%",
                    savings, 0.95);
        }

        if (p.trendSlope() > MIN_SLOPE_TO_SUPPRESS) {
            return new Recommendation(inst.instanceId(), RecommendationKind.SUPPRESS,
                    inst.currentVCpu(), inst.currentVCpu(),
                    "Rising utilization trend (" + fmt(p.trendSlope()) + " %/sample)",
                    0.0, 0.0);
        }

        if (cooldownActive(inst)) {
            return new Recommendation(inst.instanceId(), RecommendationKind.SUPPRESS,
                    inst.currentVCpu(), inst.currentVCpu(),
                    "Within 30-day cooldown since last resize",
                    0.0, 0.0);
        }

        int best = -1;
        for (int size : FAMILY_LADDER) {
            if (size >= inst.currentVCpu()) break;
            if (simulatedPct(p.cpuP99(), inst.currentVCpu(), size) <= CPU_HEADROOM_P99
                    && simulatedPct(p.memP99(), inst.currentVCpu(), size) <= MEM_HEADROOM_P99) {
                best = size;
                break;
            }
        }

        if (best == -1) {
            return new Recommendation(inst.instanceId(), RecommendationKind.KEEP,
                    inst.currentVCpu(), inst.currentVCpu(),
                    "No smaller size fits p99 CPU " + fmt(p.cpuP99()) + "%",
                    0.0, 0.9);
        }

        double savings = 730.0 * inst.pricePerHour()
                * (1.0 - (double) best / inst.currentVCpu());
        if (savings < MIN_MONTHLY_SAVINGS) {
            return new Recommendation(inst.instanceId(), RecommendationKind.SUPPRESS,
                    inst.currentVCpu(), best,
                    "Savings $" + fmt(savings) + "/mo below $20 threshold",
                    0.0, 0.6);
        }

        double confidence = Math.min(0.99, 0.7 + p.cpuP99() / 400.0
                + (double) inst.currentVCpu() / best / 100.0);
        return new Recommendation(inst.instanceId(), RecommendationKind.RIGHT_SIZE,
                inst.currentVCpu(), best,
                "Simulated p99 CPU " + fmt(simulatedPct(p.cpuP99(), inst.currentVCpu(), best))
                        + "% fits " + best + " vCPU",
                savings, confidence);
    }

    private boolean cooldownActive(InstanceProfile inst) {
        return inst.lastResize() != null
                && inst.lastResize().isAfter(Instant.now().minusSeconds(COOLDOWN_DAYS * 24 * 3600));
    }

    private static String fmt(double d) { return String.format("%.1f", d); }
```

### Step 5: Exercise the engine with a demo

The demo builds four instances with synthetic profiles: an idle box, a flat 30%-CPU box (should right-size 8→4), a bursty box (p99 90% — must NOT downsize), and a ramp-up box (rising trend — suppressed).

```java
    public static void main(String[] args) {
        Instant now = Instant.now();
        List<InstanceProfile> fleet = List.of(
                idle("i-0001", now),
                flatCpu("i-0002", now),
                bursty("i-0003", now),
                ramping("i-0004", now));

        var engine = new CostOptimizationEngine();
        System.out.println("=== Cost Optimization Engine Demo ===\n");
        for (Recommendation r : engine.evaluate(fleet)) {
            System.out.printf("%-8s %-11s %4d -> %-4d | %s | $%.2f/mo | conf %.2f%n",
                    r.instanceId(), r.kind(), r.currentVCpu(),
                    r.recommendedVCpu() == 0 ? 0 : r.recommendedVCpu(),
                    r.reason(), r.projectedMonthlySavings(), r.confidence());
        }
    }

    private static InstanceProfile idle(String id, Instant now) {
        List<UtilizationSample> s = new ArrayList<>();
        for (int i = 0; i < 1440; i++) s.add(new UtilizationSample(now.minusSeconds(i * 60L), 0.4, 1.1));
        return new InstanceProfile(id, "acct-dev", "dev", 8, 16.0, 0.35, s, now.minusSeconds(90L * 24 * 3600));
    }

    private static InstanceProfile flatCpu(String id, Instant now) {
        List<UtilizationSample> s = new ArrayList<>();
        for (int i = 0; i < 1440; i++) s.add(new UtilizationSample(now.minusSeconds(i * 60L), 30.0, 20.0));
        return new InstanceProfile(id, "acct-prod", "prod", 8, 16.0, 0.80, s, now.minusSeconds(90L * 24 * 3600));
    }

    private static InstanceProfile bursty(String id, Instant now) {
        List<UtilizationSample> s = new ArrayList<>();
        for (int i = 0; i < 1440; i++) {
            double v = i % 240 < 20 ? 92.0 : 12.0;
            s.add(new UtilizationSample(now.minusSeconds(i * 60L), v, 40.0));
        }
        return new InstanceProfile(id, "acct-prod", "prod", 8, 16.0, 0.80, s, now.minusSeconds(90L * 24 * 3600));
    }

    private static InstanceProfile ramping(String id, Instant now) {
        List<UtilizationSample> s = new ArrayList<>();
        for (int i = 0; i < 1440; i++) {
            double v = 20.0 + i * 0.06;   // slope 0.06 > 0.05 suppression threshold
            s.add(new UtilizationSample(now.minusSeconds(i * 60L), v, 30.0));
        }
        return new InstanceProfile(id, "acct-prod", "prod", 8, 16.0, 0.80, s, now.minusSeconds(90L * 24 * 3600));
    }
}
```

### Step 6: Verify the expected outputs

| Instance | Profile | Expected decision |
|----------|---------|-------------------|
| i-0001 | Idle (CPU 0.4%, mem 1.1%) | `SHUTDOWN`, savings = 730 × $0.35 ≈ $255/mo |
| i-0002 | Flat 30% CPU | `RIGHT_SIZE` 8→4 vCPU: simulated p99 = 30% × 8/4 = 60% ≤ 75% ✓ |
| i-0003 | Bursty (p99 = 92%) | `KEEP`: simulated on 4 vCPU = 92% × 2 = 184% > 75% ✗ |
| i-0004 | Ramping (+0.06%/sample) | `SUPPRESS`: slope 0.06 > 0.05 → wait for trend to flatten |

The bursty box is the critical case: an average-based engine would downsize it and cause CPU throttling on every burst. Percentile-based simulation prevents that.

---

## Complexity Analysis

- **Profile computation**: O(S log S) per instance due to the sort for percentile, where S = number of samples (14 days × 1440 min = 20,160 samples).
- **Classification**: O(L) per instance, where L = ladder size (5 entries) — effectively O(1).
- **Space**: O(S) transiently per instance for the sorted sample list; O(R) for the recommendation output, R = number of instances.
- **Total fleet run**: O(R · S log S). For 10,000 instances with 20k samples each, this is roughly 10k × 20k × 15 ≈ 3 billion compare operations — runs nightly in a batch job; columnar storage and pre-aggregated percentiles (t-digest) would cut this to O(R) per night at the cost of exactness.

---

## Follow-Up Questions

1. **How would you make percentiles streaming-friendly?** Replace exact sorts with a t-digest or HDR histogram — near-exact p99 with O(1) memory per instance, enabling continuous evaluation instead of nightly batch.

2. **How do you decide which recommendation class has the biggest financial impact first?** Rank by projected savings across the fleet and run a Pareto report: typically the top 10% of recommendations (mostly shutdown/stop-start) produce 60-70% of savings.

3. **What if a resize breaks the workload?** The engine emits the recommendation; application happens via IaC with post-apply verification for 24-48h and automatic revert if p99 latency or throttling metrics degrade beyond a baseline.

4. **How do you handle spot/preemptible instances in the model?** They need a different headroom model — utilization targets drop (they can be reclaimed at any moment) and savings math uses the spot rate; the engine adds a `RELIABILITY_RISK` flag when a stateful workload is on spot.

5. **How do you price cross-family recommendations (memory-optimized vs general-purpose)?** Add a per-family ladder table with per-vCPU prices, then search all families: pick the cheapest family-size pair that fits the simulated p99 for both CPU and memory.

6. **How do you feed recommendations back into the system?** Publish to a recommendations topic consumed by the approval workflow (Jira/ServiceNow integration); approved items generate Terraform PRs, keeping everything auditable.
