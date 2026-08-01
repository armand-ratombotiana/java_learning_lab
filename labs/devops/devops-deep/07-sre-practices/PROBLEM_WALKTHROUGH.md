# Lab 07: Problem Walkthrough — SRE Practices: SLIs, SLOs, Error Budgets and Toil

## Problem Statement

Implement the core SRE accounting system in pure Java 21+. Requirements:

1. **SLIs**: collect service-level indicators — availability (successful / total) and latency
   (p99) — from a stream of request outcomes.
2. **SLOs**: a target percentage over a period (e.g., 99.9% availability over 30 days).
3. **Error budget**: the SLO converted to a finite budget of allowed failures; consumption
   tracks how much of the budget is used.
4. **Burn rate**: the rate at which the budget is consumed in a window; sustained high burn is
   the signal that pages operators *before* the budget is exhausted.
5. **Toil tracking**: manual work is logged in hours and measured against a toil budget.
6. **Postmortem**: an incident record with timeline, root cause, and action items.

## Constraints

- Java 21+ only, no external frameworks.
- Deterministic demo: fixed request counts and fixed windows, so budget math is reproducible.
- All numbers are longs/doubles with explicit unit meaning — the walkthrough names them
  (`allowedErrors`, `windowFailures`) so the formulas are readable.

## Approach

SRE accounting has one equation at its core: an SLO of 99.9% over a period means the service may
be down for 0.1% of that period — or, translated to requests, it may fail 0.1% of requests. The
**error budget** is that allowance, and its job is to make reliability a *decision*: teams
spend the budget on velocity (deploys, experiments) and stop when it runs out. The **burn
rate** is the leading indicator: budget consumed per window divided by the budget available for
that window. A burn rate of 1 means the whole budget lasts exactly the period; a rate of 2
means it will be exhausted in half the period — so burning at 2x for an hour on a 30-day budget
is worth a page, because it is, in Google's terms, 'budgeting for failure fast'.

Design decisions:

- **SLI as a counter pair**: successes and failures, plus a latency ring; p99 is a sorted
  percentile over the recorded latencies (the classic implementation).
- **Budget as pure arithmetic**: `allowedErrors = (1 - target) * totalRequests`; `remaining`
  and `remainingPercent` derive from consumed failures — no external state, trivially testable.
- **Burn rate with named windows**: a window is `(failures, requests)`; the budget for that
  window is `(1 - target) * requests`, so the rate is dimensionless — comparable across
  windows of any size.
- **Alerting on rate, not on remaining**: remaining-only alerting waits until the budget is
  nearly gone; burn-rate alerting catches the bleed early — the walkthrough encodes the SRE
  book's two-tier page/warn on rate.

## Step-by-Step Solution

### Step 1: SLIs

```java
class ServiceLevelIndicator {
    private final List<Long> latencies = new ArrayList<>();
    private long successes = 0;
    private long failures = 0;

    void record(boolean success, long latencyMs) {
        latencies.add(latencyMs);
        if (success) successes++;
        else failures++;
    }

    double availability() {
        var total = successes + failures;
        return total == 0 ? 100.0 : (successes * 100.0) / total;
    }

    double p99() {
        var sorted = latencies.stream().sorted().toList();
        if (sorted.isEmpty()) return 0.0;
        var index = (int) Math.ceil(0.99 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }
}
```

### Step 2: Error Budget and Burn Rate

```java
class ErrorBudget {
    private final double allowedErrors;
    private long consumed = 0;

    ErrorBudget(ServiceLevelObjective slo, long totalRequests) {
        this.allowedErrors = (1.0 - slo.targetPercent() / 100.0) * totalRequests;
    }

    void consume(long failures) {
        consumed += failures;
    }

    double remainingPercent() {
        return Math.max(0.0, (allowedErrors - consumed) / allowedErrors * 100.0);
    }

    double burnRate(long windowFailures, long windowRequests) {
        var windowBudget = (1.0 - slo.targetPercent() / 100.0) * windowRequests;
        return windowBudget == 0 ? 0.0 : windowFailures / windowBudget;
    }
}
```

### Step 3: Alerting on Burn Rate

The SRE book's rule of thumb: page when the burn rate is 2x or more for a sustained window,
warn at 1x-2x. The demo models that decision function with explicit windows so the math is
inspectable.

## Complete Solution

The full compilable file, `SreLab.java` in package `com.devops.deep.lab07`:

```java
package com.devops.deep.lab07;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SreLab {
    public static void main(String[] args) {
        var sli = new ServiceLevelIndicator("api-gateway");
        for (int i = 0; i < 999_500; i++) {
            sli.record(true, 120);
        }
        for (int i = 0; i < 500; i++) {
            sli.record(false, 200);
        }
        sli.record(true, 950);   // one tail latency outlier

        System.out.println("SLI for " + sli.name() + ":");
        System.out.println("  availability = " + String.format("%.2f", sli.availability()) + "%");
        System.out.println("  p99 latency  = " + String.format("%.0f", sli.p99()) + "ms");

        var slo = new ServiceLevelObjective("api-gateway", 99.9, Duration.ofDays(30));
        var budget = new ErrorBudget(slo, 1_000_000);
        budget.consume(sli.failures());
        System.out.println("\nSLO " + slo.targetPercent() + "% over 30d, 1M requests:");
        System.out.println("  allowed errors   = " + String.format("%.0f", budget.allowedErrors()));
        System.out.println("  consumed         = " + budget.consumedErrors());
        System.out.println("  remaining budget = "
            + String.format("%.1f", budget.remainingPercent()) + "%");

        System.out.println("\nBurn rate in a 1h window (36,000 requests, 80 failures):");
        var rate = budget.burnRate(80, 36_000);
        System.out.println("  burn rate = " + String.format("%.2f", rate));
        System.out.println("  alert     = " + budget.alertLevel(rate));

        System.out.println("\nBurn rate in a quiet window (36,000 requests, 10 failures):");
        var quiet = budget.burnRate(10, 36_000);
        System.out.println("  burn rate = " + String.format("%.2f", quiet));
        System.out.println("  alert     = " + budget.alertLevel(quiet));

        var toil = new ToilTracker(30.0);
        toil.log("Manual DB backup", 2.5);
        toil.log("Restart crashed pods", 1.0);
        toil.log("Respond to pager", 0.5);
        System.out.println("\nToil this week: " + toil.totalHours()
            + "h of " + toil.budgetHours() + "h budget ("
            + String.format("%.1f", toil.percentage()) + "%)");
        System.out.println("Toil over budget? " + toil.overBudget());

        var postmortem = new Postmortem("INC-2026-0031", "API Gateway Latency Spike");
        postmortem.setTimeline(Instant.parse("2026-07-30T10:00:00Z"),
            Instant.parse("2026-07-30T10:42:00Z"));
        postmortem.setRootCause("Connection pool exhaustion: max_connections=10 for 40 replicas");
        postmortem.addActionItem("Raise max_connections with per-replica sizing", "SRE", "P0");
        postmortem.addActionItem("Alert on connection pool saturation", "SRE", "P1");
        System.out.println("\n" + postmortem.summary());
    }
}

class ServiceLevelIndicator {
    private final String name;
    private final List<Long> latencies = new ArrayList<>();
    private long successes = 0;
    private long failures = 0;

    ServiceLevelIndicator(String name) {
        this.name = name;
    }

    String name() {
        return name;
    }

    void record(boolean success, long latencyMs) {
        latencies.add(latencyMs);
        if (success) {
            successes++;
        } else {
            failures++;
        }
    }

    double availability() {
        var total = successes + failures;
        return total == 0 ? 100.0 : (successes * 100.0) / total;
    }

    double p99() {
        var sorted = latencies.stream().sorted().toList();
        if (sorted.isEmpty()) return 0.0;
        var index = (int) Math.ceil(0.99 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }

    long failures() {
        return failures;
    }
}

record ServiceLevelObjective(String name, double targetPercent, Duration period) {}

class ErrorBudget {
    private final ServiceLevelObjective slo;
    private final double allowedErrors;
    private long consumedErrors = 0;

    ErrorBudget(ServiceLevelObjective slo, long totalRequests) {
        this.slo = slo;
        this.allowedErrors = (1.0 - slo.targetPercent() / 100.0) * totalRequests;
    }

    void consume(long failures) {
        consumedErrors += failures;
    }

    long consumedErrors() {
        return consumedErrors;
    }

    double allowedErrors() {
        return allowedErrors;
    }

    double remainingPercent() {
        return Math.max(0.0, (allowedErrors - consumedErrors) / allowedErrors * 100.0);
    }

    double burnRate(long windowFailures, long windowRequests) {
        var windowBudget = (1.0 - slo.targetPercent() / 100.0) * windowRequests;
        return windowBudget == 0 ? 0.0 : windowFailures / windowBudget;
    }

    String alertLevel(double burnRate) {
        if (burnRate >= 2.0) return "PAGE";
        if (burnRate >= 1.0) return "WARN";
        return "OK";
    }
}

class ToilTracker {
    private final double budgetHours;
    private final List<Double> tasks = new ArrayList<>();

    ToilTracker(double budgetHours) {
        this.budgetHours = budgetHours;
    }

    void log(String task, double hours) {
        tasks.add(hours);
    }

    double totalHours() {
        return tasks.stream().mapToDouble(Double::doubleValue).sum();
    }

    double budgetHours() {
        return budgetHours;
    }

    double percentage() {
        return budgetHours == 0 ? 0.0 : totalHours() / budgetHours * 100.0;
    }

    boolean overBudget() {
        return totalHours() > budgetHours;
    }
}

class Postmortem {
    private final String incidentId;
    private final String title;
    private Instant startedAt;
    private Instant resolvedAt;
    private String rootCause;
    private final List<String> actionItems = new ArrayList<>();

    Postmortem(String incidentId, String title) {
        this.incidentId = incidentId;
        this.title = title;
    }

    void setTimeline(Instant startedAt, Instant resolvedAt) {
        this.startedAt = startedAt;
        this.resolvedAt = resolvedAt;
    }

    void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    void addActionItem(String item, String owner, String priority) {
        actionItems.add("[" + priority + "] " + item + " (" + owner + ")");
    }

    String summary() {
        var sb = new StringBuilder();
        sb.append("Postmortem ").append(incidentId).append(": ").append(title).append("\n");
        sb.append("  Duration: ")
          .append(Duration.between(startedAt, resolvedAt).toMinutes()).append("m\n");
        sb.append("  Root cause: ").append(rootCause).append("\n");
        sb.append("  Action items:\n");
        for (var item : actionItems) {
            sb.append("    - ").append(item).append("\n");
        }
        return sb.toString().stripTrailing();
    }
}
```

## Complexity Analysis

- **SLI record**: O(1); p99 requires O(N log N) once for the sorted percentile — production
  uses histogram sketches (HDRHistogram) for O(1) inserts and quantiles.
- **Budget math**: O(1) — a few multiplications and subtractions; the whole system is
  trivially cheap because it is counters, not analytics.
- **Toil / postmortem**: O(T) and O(A) over tasks and action items.
- **Space**: O(N) latencies for the p99 demo; production bounds this with histograms, which is
  also how the real Prometheus-based SLO tooling stays fast at scale.

## Test Cases

| Scenario | Expected |
|---|---|
| 999,500 ok / 500 fail of 1M | availability 99.95%, p99 = 120ms (the 950ms tail sits past the p99 index) |
| Budget for 99.9% over 1M requests | allowedErrors = 1000 |
| Consume 500 failures | remaining = 50% of budget |
| Burn rate 80/36,000 in 1h | rate = 2.22 -> PAGE |
| Burn rate 10/36,000 in 1h | rate = 0.28 -> OK |
| Toil 4h vs 30h budget | 13.3%, not over budget |
| Postmortem 42m incident | Summary shows duration, root cause, 2 action items |

Example run:

```
SLI for api-gateway:
  availability = 99.95%
  p99 latency  = 120ms

SLO 99.9% over 30d, 1M requests:
  allowed errors   = 1000
  consumed         = 500
  remaining budget = 50.0%

Burn rate in a 1h window (36,000 requests, 80 failures):
  burn rate = 2.22
  alert     = PAGE

Burn rate in a quiet window (36,000 requests, 10 failures):
  burn rate = 0.28
  alert     = OK

Toil this week: 4.0h of 30.0h budget (13.3%)
Toil over budget? false

Postmortem INC-2026-0031: API Gateway Latency Spike
  Duration: 42m
  Root cause: Connection pool exhaustion: max_connections=10 for 40 replicas
  Action items:
    - [P0] Raise max_connections with per-replica sizing (SRE)
    - [P1] Alert on connection pool saturation (SRE)
```

## Follow-Up Questions

1. **Why alert on burn rate instead of remaining budget?** Remaining-budget alerts fire when
   the budget is nearly gone — too late to act. Burn rate is a leading indicator: at 2x burn
   on a 30-day budget, you have days, not hours, to respond. The SRE book's multi-window
   approach (page on 2x sustained over 1h, or 14.4x over 5m) catches both slow bleed and fast
   breakage.
2. **What makes a good SLO target?** One that reflects what users can tolerate, not what the
   team can deliver: pick a target you'd be comfortable having paged about at 2x burn, tune it
   from observed p99/error patterns, and state the consequence of missing it. A 99.99% target
   with no definition of 'what breaks at 99.98%' is a vanity number.
3. **How do you turn an availability SLO into an error budget?** Two views: time-based — the
   allowed downtime fraction of the period — and request-based — allowed failures as
   (1 - target) * total requests. The request-based view is what teams actually consume,
   which is why the walkthrough models requests rather than minutes.
4. **What is toil, and how do you decide what counts?** Manual, repetitive, automatable work
   tied to running the service — restarting pods by hand, manual backups, hand-run migrations.
   The test: does it scale with the fleet? Does it disappear with automation? SRE's rule of
   thumb is toil below 50% of engineering time; the tracker's budget here is the tool for
   making that visible instead of argued.
5. **When do you burn the error budget on purpose?** On releases, experiments, and migrations
   — the budget exists to fund velocity. The discipline is the reverse of the alert: you
   *choose* to spend it, you know the burn rate, and you stop spending when the budget runs
   out — which is what 'error budget policing' means in practice.
6. **What does a good postmortem change?** Nothing, if it's a document. It changes behavior
   when it produces action items that are specific, owned, and scheduled, and when the
   blameless culture means the incident actually gets discussed. The walkthrough's summary is
   the artifact; the follow-up — tracking action items to completion — is the practice.
7. **How does this system compose with real monitoring?** The SLI counters become Prometheus
   metrics; the SLO and burn rate become recording rules; the alert levels become alertmanager
   routing; the postmortem becomes your incident tooling. The lab is the pure computation —
   the formulas — which is exactly the part that is identical across every stack.
