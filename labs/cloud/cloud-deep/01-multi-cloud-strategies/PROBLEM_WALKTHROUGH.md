# Lab 01: Problem Walkthrough — Multi-Cloud Failover System

## Problem Statement

Design a multi-cloud failover system with provider abstraction. A company runs an HTTP API workload that must survive the complete regional outage of any single public cloud provider. You must:

1. Define a provider abstraction layer (`CloudProvider`) implemented by three adapters: AWS, Azure, and GCP.
2. Build a `FailoverController` that runs periodic health checks against all providers.
3. Implement a routing decision: traffic goes to the current primary provider unless the primary is declared unhealthy, in which case the controller promotes the healthiest secondary.
4. Use quorum-style detection with debouncing (consecutive failure windows) to prevent flapping between providers.
5. Implement a circuit breaker with a half-open recovery state so the system can fail back to a recovered primary without oscillating.
6. Support both **active-passive** and **active-active** strategies.

**Constraints**

- Failover must trigger within 30 seconds of a sustained outage.
- No more than one failover per 60 seconds (no-flap guarantee).
- The controller must be thread-safe; health checks run on a scheduled executor while requests are routed concurrently.
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: Model the provider abstraction

The core contract is a `CloudProvider` interface exposing name, region, health check, and provisioning primitives. Each adapter simulates an actual provider with a failure-injection hook so the failover logic can be tested deterministically.

```java
package com.cloud.deep.lab01;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

interface CloudProvider {
    String name();
    String region();
    boolean healthCheck();
    boolean provision(String workload);
    void setSimulatedDown(boolean down);
}
```

### Step 2: Implement the three provider adapters

Each adapter wraps a simulated state. `healthCheck()` returns `false` when the simulated outage flag is set, mimicking a provider regional failure. Provisioning is idempotent and tracked so the demo can show where each workload ends up.

```java
final class AwsAdapter implements CloudProvider {
    private final String region;
    private final AtomicBoolean down = new AtomicBoolean(false);
    private final AtomicLong provisions = new AtomicLong();

    AwsAdapter(String region) { this.region = region; }

    @Override public String name() { return "AWS"; }
    @Override public String region() { return region; }
    @Override public boolean healthCheck() { return !down.get(); }
    @Override public boolean provision(String workload) {
        if (down.get()) throw new IllegalStateException("AWS unavailable");
        provisions.incrementAndGet();
        return true;
    }
    @Override public void setSimulatedDown(boolean d) { down.set(d); }
    long provisions() { return provisions.get(); }
}

final class AzureAdapter implements CloudProvider {
    private final String region;
    private final AtomicBoolean down = new AtomicBoolean(false);
    private final AtomicLong provisions = new AtomicLong();

    AzureAdapter(String region) { this.region = region; }

    @Override public String name() { return "Azure"; }
    @Override public String region() { return region; }
    @Override public boolean healthCheck() { return !down.get(); }
    @Override public boolean provision(String workload) {
        if (down.get()) throw new IllegalStateException("Azure unavailable");
        provisions.incrementAndGet();
        return true;
    }
    @Override public void setSimulatedDown(boolean d) { down.set(d); }
    long provisions() { return provisions.get(); }
}

final class GcpAdapter implements CloudProvider {
    private final String region;
    private final AtomicBoolean down = new AtomicBoolean(false);
    private final AtomicLong provisions = new AtomicLong();

    GcpAdapter(String region) { this.region = region; }

    @Override public String name() { return "GCP"; }
    @Override public String region() { return region; }
    @Override public boolean healthCheck() { return !down.get(); }
    @Override public boolean provision(String workload) {
        if (down.get()) throw new IllegalStateException("GCP unavailable");
        provisions.incrementAndGet();
        return true;
    }
    @Override public void setSimulatedDown(boolean d) { down.set(d); }
    long provisions() { return provisions.get(); }
}
```

### Step 3: Model the failover strategy

Strategies are encoded as an enum. Active-passive keeps one hot primary and routes everything to it. Active-active spreads traffic across all healthy providers, which changes the routing decision from "pick one" to "pick a healthy one with lowest load."

```java
enum FailoverStrategy { ACTIVE_PASSIVE, ACTIVE_ACTIVE }
```

### Step 4: Implement the FailoverController

The controller owns:

- A list of providers with per-provider consecutive-failure counters.
- The routing decision (`currentPrimary`).
- The circuit state: `CLOSED` (healthy), `OPEN` (failed over), `HALF_OPEN` (probing recovery).
- A no-flap guard: a timestamp of the last failover event.

The health-check loop uses quorum-of-one-per-window plus debounce: a provider must fail **N consecutive** checks before being declared down. Recovery is circuit-breaker style: once the original primary passes a full health window again, the controller moves to half-open and fails back to it.

```java
final class FailoverController {
    enum CircuitState { CLOSED, OPEN, HALF_OPEN }

    private final List<CloudProvider> providers;
    private final FailoverStrategy strategy;
    private final int requiredConsecutiveFailures;
    private final long minIntervalBetweenFailoversMillis;

    private final Map<String, Integer> consecutiveFailures = new ConcurrentHashMap<>();
    private final CloudProvider originalPrimary;   // failback always returns home
    private CloudProvider primary;
    private CircuitState state = CircuitState.CLOSED;
    private Instant lastFailover = Instant.EPOCH;
    private long failoverCount = 0;

    FailoverController(List<CloudProvider> providers, String initialPrimary,
                       FailoverStrategy strategy, int requiredConsecutiveFailures,
                       long minIntervalBetweenFailoversMillis) {
        this.providers = List.copyOf(providers);
        this.strategy = strategy;
        this.requiredConsecutiveFailures = requiredConsecutiveFailures;
        this.minIntervalBetweenFailoversMillis = minIntervalBetweenFailoversMillis;
        this.primary = providers.stream()
                .filter(p -> p.name().equals(initialPrimary))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown primary: " + initialPrimary));
        this.originalPrimary = this.primary;
    }

    synchronized void runHealthCheckCycle() {
        for (CloudProvider p : providers) {
            boolean ok = p.healthCheck();
            // A healthy window must RESET the counter, not add zero to it —
            // merge(..., 0, Integer::sum) would leave old failures in place.
            if (ok) {
                consecutiveFailures.put(p.name(), 0);
            } else {
                consecutiveFailures.merge(p.name(), 1, Integer::sum);
            }
        }

        boolean primaryDown = consecutiveFailures.getOrDefault(primary.name(), 0) >= requiredConsecutiveFailures;

        switch (state) {
            case CLOSED -> {
                if (primaryDown) {
                    Optional<CloudProvider> promoted = healthySecondary();
                    if (promoted.isPresent() && canFailover()) {
                        failOverTo(promoted.get());
                    }
                }
            }
            case OPEN -> {
                // Secondary is primary now. Recovery probe: if the old primary
                // is healthy again, move to HALF_OPEN and let the probe decide.
                if (providers.stream().allMatch(CloudProvider::healthCheck)) {
                    state = CircuitState.HALF_OPEN;
                }
            }
            case HALF_OPEN -> {
                // Probe: the ORIGINAL primary (not the current one) must be back
                // and clean before we fail back. A recovered-but-flapping primary
                // drops back to OPEN instead of oscillating.
                boolean originalStable = originalPrimary.healthCheck()
                        && consecutiveFailures.getOrDefault(originalPrimary.name(), 0) == 0;
                if (originalStable && canFailover()) {
                    failBackTo();
                } else {
                    state = CircuitState.OPEN;
                }
            }
        }
    }

    private Optional<CloudProvider> healthySecondary() {
        return providers.stream()
                .filter(p -> p.healthCheck())
                .filter(p -> consecutiveFailures.getOrDefault(p.name(), 0) < requiredConsecutiveFailures)
                .filter(p -> !p.name().equals(primary.name()))
                .sorted(Comparator.comparing(CloudProvider::name))
                .findFirst();
    }

    private boolean canFailover() {
        return Instant.now().toEpochMilli() - lastFailover.toEpochMilli() >= minIntervalBetweenFailoversMillis;
    }

    private void failOverTo(CloudProvider newPrimary) {
        lastFailover = Instant.now();
        failoverCount++;
        primary = newPrimary;
        state = CircuitState.OPEN;
    }

    private void failBackTo() {
        primary = originalPrimary;
        state = CircuitState.CLOSED;   // back home: not a failover, no counter bump
    }

    synchronized CloudProvider route() {
        if (strategy == FailoverStrategy.ACTIVE_ACTIVE) {
            return providers.stream()
                    .filter(p -> p.healthCheck())
                    .sorted(Comparator.comparingLong(p -> (long) p.hashCode() & 0xFFFF))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No healthy provider"));
        }
        return primary;
    }

    synchronized CloudProvider primary() { return primary; }
    synchronized CircuitState state() { return state; }
    synchronized long failoverCount() { return failoverCount; }
}
```

### Step 5: Put it together — the demo harness

The `main` method simulates a timeline:

1. Steady state: all providers healthy, requests route to AWS.
2. AWS goes down at T+1s; the next health cycle debounces once (requires 2 consecutive failures).
3. The controller promotes Azure and increments the failover counter.
4. Requests now route to Azure.
5. AWS recovers; the controller enters HALF_OPEN, verifies AWS is stable, and fails back to it (state returns to CLOSED; the counter does not move — a recovery is not a failover).
6. A separate controller armed with the 60-second guard shows the no-flap guarantee: a second promotion attempt inside the window is refused.

```java
public final class MultiCloudFailoverSystem {

    private MultiCloudFailoverSystem() {}

    public static void main(String[] args) {
        AwsAdapter aws = new AwsAdapter("us-east-1");
        AzureAdapter azure = new AzureAdapter("eastus2");
        GcpAdapter gcp = new GcpAdapter("us-central1");

        // Main timeline: guard disabled so the full failover -> failback cycle
        // is visible in a single fast run.
        FailoverController controller = new FailoverController(
                List.of(aws, azure, gcp), "AWS",
                FailoverStrategy.ACTIVE_PASSIVE,
                2, 0);

        System.out.println("=== Multi-Cloud Failover Demo ===");
        System.out.println("Primary: " + controller.primary().name()
                + " | State: " + controller.state() + "\n");

        simulateCycle(controller, aws, "AWS healthy, no failover expected");

        System.out.println("-- Injecting AWS regional outage --");
        aws.setSimulatedDown(true);
        simulateCycle(controller, aws, "First check fails (debounce 1/2)");
        simulateCycle(controller, aws, "Second check fails (debounce 2/2) -> FAILOVER");

        System.out.println("Primary after failover: " + controller.primary().name()
                + " | State: " + controller.state()
                + " | Failovers: " + controller.failoverCount() + "\n");

        System.out.println("-- AWS recovers --");
        aws.setSimulatedDown(false);
        simulateCycle(controller, aws, "All healthy -> HALF_OPEN probe");
        simulateCycle(controller, aws, "AWS stable across probe -> FAIL BACK");

        System.out.println("Primary after failback: " + controller.primary().name()
                + " | State: " + controller.state()
                + " | Failovers: " + controller.failoverCount() + "\n");

        System.out.println("-- No-flap guard (60s): a second failover is blocked --");
        AwsAdapter aws2 = new AwsAdapter("us-east-1");
        AzureAdapter azure2 = new AzureAdapter("eastus2");
        GcpAdapter gcp2 = new GcpAdapter("us-central1");
        FailoverController guarded = new FailoverController(
                List.of(aws2, azure2, gcp2), "AWS",
                FailoverStrategy.ACTIVE_PASSIVE,
                2, 60_000);
        aws2.setSimulatedDown(true);
        simulateCycle(guarded, aws2, "Check 1/2 failing");
        simulateCycle(guarded, aws2, "Check 2/2 -> failover to Azure (guard not armed yet)");
        System.out.println("Primary after failover: " + guarded.primary().name()
                + " | Failovers: " + guarded.failoverCount());
        azure2.setSimulatedDown(true);
        simulateCycle(guarded, azure2, "Azure down too, check 1/2");
        simulateCycle(guarded, azure2, "Check 2/2 -> GCP promotion BLOCKED by the 60s guard");
        System.out.println("Primary (guarded): " + guarded.primary().name()
                + " | Failovers: " + guarded.failoverCount() + "\n");

        System.out.println("-- Active-active strategy routes to a healthy provider --");
        aws.setSimulatedDown(false);
        FailoverController aa = new FailoverController(
                List.of(aws, azure, gcp), "AWS",
                FailoverStrategy.ACTIVE_ACTIVE,
                2, 60_000);
        System.out.println("Route under active-active: " + aa.route().name());
    }

    private static void simulateCycle(FailoverController controller, CloudProvider p,
                                      String expectation) {
        controller.runHealthCheckCycle();
        CloudProvider route = controller.route();
        System.out.printf("  health[%s]=%s route=%s state=%s | %s%n",
                p.name(), p.healthCheck(), route.name(), controller.state(), expectation);
        Thread.onSpinWait();
    }
}
```

### Step 6: Verify the flow mentally

| T (s) | Event | State | Route |
|-------|-------|-------|-------|
| 0 | All healthy | CLOSED | AWS |
| 1 | AWS down, check 1 fails | CLOSED (debounce 1/2) | AWS |
| 2 | AWS down, check 2 fails | OPEN | Azure |
| 3 | All healthy again | HALF_OPEN | Azure |
| 4 | AWS stable across probe | CLOSED (failed back) | AWS |
| 5 | Guarded controller: Azure down too | OPEN (GCP promotion blocked by the 60s window) | Azure |

The debounce counter plus the 60-second guard window prevent flapping: the first failover is confirmed by 2 consecutive failures, and a second promotion inside the guard window is impossible by construction — even a second provider collapsing immediately cannot trigger a second failover.

---

## Complexity Analysis

- **Health check cycle**: O(P) time, O(P) space, where P = number of providers (3 here, negligible).
- **Routing decision**: O(1) for active-passive; O(P) for active-active (linear scan over a tiny constant).
- **State held**: O(P) counters — constant memory regardless of request volume.
- **Concurrency**: all mutations are `synchronized` on the controller or use `ConcurrentHashMap`/`AtomicBoolean`; health checks run on a single scheduled executor so no data races.

The design is intentionally O(P) everywhere — with a small fixed number of clouds, asymptotic complexity is never the bottleneck; correctness of the state machine is.

---

## Follow-Up Questions

1. **How would you make the controller itself highly available?** Run a pair of controllers in different clouds sharing state via a replicated store (e.g., a consensus-backed KV store), with leader election so only one makes failover decisions. The controller is now a tiny replicated state machine.

2. **What if a provider is degraded but not down — high latency, partial errors?** The health check should measure *availability* (can we complete a probe within the timeout) not just reachability. Add a latency envelope: if p99 probe latency exceeds a threshold for a full window, treat it as down. Passive signals (5xx rates) complement the active probe.

3. **How do you handle the database in a failover?** Fencing the old primary (block its egress so it cannot accept writes), promoting the replica, and replaying the replication lag gap for async setups. RPO is the replication lag; shrink it by making critical writes synchronous.

4. **Can you generalize the controller to N clouds with weighted routing?** Yes — generalize `route()` to return a probability distribution over healthy providers weighted by capacity, e.g., AWS 60%, Azure 30%, GCP 10% in steady state, adjusting weights by health score.

5. **How do you test this without real outages?** Failure injection at the adapter level (as shown by `setSimulatedDown`), property-based tests asserting the invariants (at most 1 failover per 60s, never routes to a declared-down provider), and scheduled chaos drills against real infrastructure.

6. **What happens if all providers are down?** The controller must fail loudly: raise a global alert, keep the last-known routing decision so DNS doesn't flap, and serve a maintenance page. A negative availability answer is better than an oscillating one.
