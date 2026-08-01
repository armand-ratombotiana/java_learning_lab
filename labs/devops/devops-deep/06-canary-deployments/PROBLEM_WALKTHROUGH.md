# Lab 06: Problem Walkthrough — Canary Deployments with Metrics-Driven Promotion

## Problem Statement

Implement an Argo Rollouts-style canary deployment controller in pure Java 21+. Requirements:

1. **Weighted steps**: a rollout is a list of steps, each shifting a weight of traffic to the
   canary version (10% -> 25% -> 50% -> ... -> 100%).
2. **Metrics-driven analysis**: between steps, the controller evaluates the canary's error rate
   and p99 latency against thresholds.
3. **Automatic promotion**: healthy canary -> shift to the next step, until 100% and `Promoted`.
4. **Automatic rollback**: any step violating a threshold aborts the rollout and reverts traffic
   to the stable version (`RolledBack`).
5. **Auditable history**: the rollout keeps a log of every step decision, so operators can see
   exactly where and why a rollout stopped.
6. **Deterministic demo**: the same metrics always produce the same promotion or rollback.

## Constraints

- Java 21+ only, no external frameworks.
- The controller is synchronous and single-threaded — the loop, not the concurrency, is the
  lesson.
- Thresholds are configurable per rollout (error rate %, p99 ms).

## Approach

A canary controller is a loop: *shift traffic -> measure -> decide*. Argo Rollouts implements
this as a state machine — Pending -> Progressing -> Promoted or RolledBack — with an analysis
step between every traffic shift. The analysis is what makes it safe: promotion is not a
schedule, it's a *decision* driven by metrics from the new version. If the canary violates the
error-rate or latency budget at any step, the controller aborts and stable traffic is
restored.

Design decisions:

- **Steps carry weight + pause**: the pause is a human/configurable dwell time; in the demo the
  pause is recorded in the log and execution is synchronous.
- **Analysis after each step**: the canary's metrics are checked against the rollout's
  thresholds; the *stable* version's metrics are logged as the baseline for comparison — a
  canary that is 'fine' but worse than stable is still suspicious.
- **Fail-fast on first violation**: the controller aborts at the first step that violates a
  threshold, leaving a log trail showing the exact weight where it failed.
- **Version labeling in metrics**: `MetricsService` keys by version string, which is how the
  controller compares canary vs stable without knowing what the versions are.

## Step-by-Step Solution

### Step 1: Steps and Metrics

```java
record CanaryStep(int weight, String pause) {}

record VersionMetrics(double errorRate, double p99Ms) {}

class MetricsService {
    private final Map<String, VersionMetrics> byVersion = new ConcurrentHashMap<>();

    void set(String version, double errorRate, double p99Ms) {
        byVersion.put(version, new VersionMetrics(errorRate, p99Ms));
    }

    VersionMetrics of(String version) {
        return byVersion.getOrDefault(version, new VersionMetrics(0.0, 0.0));
    }
}
```

### Step 2: The Decision Loop

For each step: shift the weight, read the canary metrics, compare against thresholds, decide.

```java
for (var step : steps) {
    currentWeight = step.weight();
    log.add("  weight " + step.weight() + "% (pause: " + step.pause() + ")");
    var canary = metrics.of(canaryVersion);
    var stable = metrics.of(stableVersion);
    log.add("    canary  err=" + canary.errorRate() + "%  p99=" + canary.p99Ms() + "ms");
    log.add("    stable  err=" + stable.errorRate() + "%  p99=" + stable.p99Ms() + "ms");
    if (canary.errorRate() > errorRateThreshold) {
        state = "RolledBack";
        log.add("    ABORT: canary error rate " + canary.errorRate()
            + "% exceeds threshold " + errorRateThreshold + "%");
        return;
    }
    if (canary.p99Ms() > latencyThresholdMs) {
        state = "RolledBack";
        log.add("    ABORT: canary p99 " + canary.p99Ms()
            + "ms exceeds threshold " + latencyThresholdMs + "ms");
        return;
    }
}
state = "Promoted";
```

The final step (100%) means the stable version is scaled down; the walkthrough records it as
`Promoted` — production Argo also scales down stable replicas here.

### Step 3: Composition Root

The demo runs three rollouts: a healthy one that promotes through all five steps, one that
violates the error-rate budget at 10%, and one that violates the latency budget at 25%.

## Complete Solution

The full compilable file, `CanaryLab.java` in package `com.devops.deep.lab06`:

```java
package com.devops.deep.lab06;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CanaryLab {
    public static void main(String[] args) {
        var metrics = new MetricsService();
        metrics.set("nginx:1.25", 0.2, 120);
        metrics.set("nginx:1.26", 0.3, 130);

        var healthy = new ArgoRollout("web-app", "nginx:1.25", "nginx:1.26",
            10.0, 500);
        healthy.addStep(new CanaryStep(10, "pause 2m"));
        healthy.addStep(new CanaryStep(25, "pause 2m"));
        healthy.addStep(new CanaryStep(50, "pause 1m"));
        healthy.addStep(new CanaryStep(75, "pause 1m"));
        healthy.addStep(new CanaryStep(100, "done"));
        healthy.execute(metrics);
        healthy.log().forEach(System.out::println);
        System.out.println("Outcome: " + healthy.status() + "\n");

        var badErrors = new ArgoRollout("web-app", "nginx:1.25", "nginx:1.26-bad",
            10.0, 500);
        badErrors.addStep(new CanaryStep(10, "pause 1m"));
        metrics.set("nginx:1.26-bad", 15.0, 200);
        badErrors.execute(metrics);
        badErrors.log().forEach(System.out::println);
        System.out.println("Outcome: " + badErrors.status() + "\n");

        var badLatency = new ArgoRollout("web-app", "nginx:1.25", "nginx:1.26-slow",
            10.0, 500);
        badLatency.addStep(new CanaryStep(25, "pause 1m"));
        metrics.set("nginx:1.26-slow", 0.5, 850);
        badLatency.execute(metrics);
        badLatency.log().forEach(System.out::println);
        System.out.println("Outcome: " + badLatency.status());
    }
}

record CanaryStep(int weight, String pause) {}

record VersionMetrics(double errorRate, double p99Ms) {}

class MetricsService {
    private final Map<String, VersionMetrics> byVersion = new ConcurrentHashMap<>();

    void set(String version, double errorRate, double p99Ms) {
        byVersion.put(version, new VersionMetrics(errorRate, p99Ms));
    }

    VersionMetrics of(String version) {
        return byVersion.getOrDefault(version, new VersionMetrics(0.0, 0.0));
    }
}

class ArgoRollout {
    private final String name;
    private final String stableVersion;
    private final String canaryVersion;
    private final double errorRateThreshold;
    private final double latencyThresholdMs;
    private final List<CanaryStep> steps = new ArrayList<>();
    private final List<String> log = new ArrayList<>();
    private int currentWeight = 0;
    private String state = "Pending";

    ArgoRollout(String name, String stableVersion, String canaryVersion,
                double errorRateThreshold, double latencyThresholdMs) {
        this.name = name;
        this.stableVersion = stableVersion;
        this.canaryVersion = canaryVersion;
        this.errorRateThreshold = errorRateThreshold;
        this.latencyThresholdMs = latencyThresholdMs;
    }

    void addStep(CanaryStep step) {
        steps.add(step);
    }

    void execute(MetricsService metrics) {
        state = "Progressing";
        log.add("Canary " + name + ": " + stableVersion + " -> " + canaryVersion);
        log.add("Thresholds: error rate <= " + errorRateThreshold
            + "%, p99 <= " + latencyThresholdMs + "ms");
        for (var step : steps) {
            currentWeight = step.weight();
            log.add("  weight " + step.weight() + "% (pause: " + step.pause() + ")");
            var canary = metrics.of(canaryVersion);
            var stable = metrics.of(stableVersion);
            log.add("    canary  err=" + canary.errorRate() + "%  p99=" + canary.p99Ms() + "ms");
            log.add("    stable  err=" + stable.errorRate() + "%  p99=" + stable.p99Ms() + "ms");
            if (canary.errorRate() > errorRateThreshold) {
                state = "RolledBack";
                log.add("    ABORT: canary error rate " + canary.errorRate()
                    + "% exceeds threshold " + errorRateThreshold + "%");
                return;
            }
            if (canary.p99Ms() > latencyThresholdMs) {
                state = "RolledBack";
                log.add("    ABORT: canary p99 " + canary.p99Ms()
                    + "ms exceeds threshold " + latencyThresholdMs + "ms");
                return;
            }
        }
        state = "Promoted";
        log.add("Canary promoted to 100%; stable scaled down to 0");
    }

    String status() {
        return state + (state.equals("RolledBack")
            ? " (reverted to " + stableVersion + " at weight " + currentWeight + "%)"
            : "");
    }

    List<String> log() {
        return List.copyOf(log);
    }
}
```

## Complexity Analysis

- **execute**: O(S) over steps; each step is O(1) — two metric lookups and two comparisons.
- **Metrics storage**: O(V) over versions; O(1) per lookup.
- **Space**: O(S + L) for steps and the decision log — a real controller persists the log
  (Argo stores rollout status in the CRD) for operator auditability.
- **The cost that matters**: time, not CPU — each pause is real dwell time so metrics
  accumulate statistically significant samples before the next shift.

## Test Cases

| Scenario | Expected |
|---|---|
| Healthy canary through 10/25/50/75/100 | `Promoted`, no aborts, 5 weight shifts logged |
| Error rate 15% at 10% weight | `RolledBack` at weight 10, error-rate abort reason logged |
| p99 850ms at 25% weight | `RolledBack` at weight 25, latency abort reason logged |
| Error rate exactly at threshold | No abort (strictly greater than) |
| Metrics missing for canary | Treated as healthy baseline (0.0/0.0) — production should alert instead |
| Stable version metrics | Logged each step as the comparison baseline |

Example run:

```
Canary web-app: nginx:1.25 -> nginx:1.26
Thresholds: error rate <= 10.0%, p99 <= 500.0ms
  weight 10% (pause: pause 2m)
    canary  err=0.3%  p99=130.0ms
    stable  err=0.2%  p99=120.0ms
  weight 25% (pause: pause 2m)
    canary  err=0.3%  p99=130.0ms
    stable  err=0.2%  p99=120.0ms
  weight 50% (pause: pause 1m)
    canary  err=0.3%  p99=130.0ms
    stable  err=0.2%  p99=120.0ms
  weight 75% (pause: pause 1m)
    canary  err=0.3%  p99=130.0ms
    stable  err=0.2%  p99=120.0ms
  weight 100% (pause: done)
    canary  err=0.3%  p99=130.0ms
    stable  err=0.2%  p99=120.0ms
Canary promoted to 100%; stable scaled down to 0
Outcome: Promoted

Canary web-app: nginx:1.25 -> nginx:1.26-bad
Thresholds: error rate <= 10.0%, p99 <= 500.0ms
  weight 10% (pause: pause 1m)
    canary  err=15.0%  p99=200.0ms
    stable  err=0.2%  p99=120.0ms
    ABORT: canary error rate 15.0% exceeds threshold 10.0%
Outcome: RolledBack (reverted to nginx:1.25 at weight 10%)

Canary web-app: nginx:1.25 -> nginx:1.26-slow
Thresholds: error rate <= 10.0%, p99 <= 500.0ms
  weight 25% (pause: pause 1m)
    canary  err=0.5%  p99=850.0ms
    stable  err=0.2%  p99=120.0ms
    ABORT: canary p99 850.0ms exceeds threshold 500.0ms
Outcome: RolledBack (reverted to nginx:1.25 at weight 25%)
```

## Follow-Up Questions

1. **Why not just switch traffic all at once?** Because blast radius scales with weight: at 10%,
   a bad release affects one tenth of users and the metrics still have signal; at 100%, it
   affects everyone instantly. Weighted steps trade a longer rollout for a bounded, observable
   failure — and the rollback is a traffic shift, not a redeploy.
2. **What metrics should drive promotion?** The canary's error rate and p99 latency are the
   minimum; production systems add success rate per endpoint, saturation (CPU/GC), and
   business metrics (checkout conversions) when available. The rule: promote on the metrics
   that would page you — if a regression wouldn't page you in production, it shouldn't block a
   canary either.
3. **How long should each pause be?** Long enough for statistically significant samples at that
   traffic level — at 1% traffic, high-signal metrics need much longer than at 25%. The answer
   is a function of traffic volume: fewer requests per minute means longer pauses; a 1% canary
   on a low-traffic service can take hours or days to be meaningful.
4. **What does rollback actually do?** Shift weight back to the stable version immediately and
   scale down the canary; it does not roll back the code deploy. That's the elegance of
   canaries: the fix for a bad release is a routing decision, not a redeploy — the broken
   version stays deployed but unserved.
5. **How does this differ from blue/green?** Blue/green keeps two full environments and flips
   the router as one big switch — instant, but all-or-nothing and expensive. Canary shifts
   weight gradually and is metrics-driven; blue/green is simpler to reason about but has no
   graduated exposure. Teams often use blue/green for infra-level cuts and canary for app
   releases.
6. **How do you make this deterministic in production?** The controller doesn't need
   randomness — Argo's controller watches metrics via an AnalysisRun (Prometheus queries),
   and a failed analysis aborts the rollout; 'deterministic' here means the decision function
   is pure: same metrics, same decision, which is exactly what makes it testable in the lab.
7. **What could make a healthy canary falsely roll back?** Metric flakiness (a 15% error-rate
   spike for 30 seconds at 10% traffic is often noise, not signal); missing metrics treated as
   zero (hiding a broken canary — the walkthrough's baseline is a deliberate simplification);
   and thresholds tuned too tight, causing repeated aborts and deploy fatigue, which
   eventually makes operators bypass the gate.
