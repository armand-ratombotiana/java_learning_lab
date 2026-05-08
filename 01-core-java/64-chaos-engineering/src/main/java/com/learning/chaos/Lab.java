package com.learning.chaos;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class Lab {

    static class Experiment {
        private final String name;
        private final Runnable inject;
        private final Supplier<Boolean> steadyState;
        private volatile boolean running = false;

        Experiment(String name, Runnable inject, Supplier<Boolean> steadyState) {
            this.name = name;
            this.inject = inject;
            this.steadyState = steadyState;
        }

        void run() {
            System.out.println("  [Hypothesis] " + name);
            running = true;
            inject.run();
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            boolean ok = steadyState.get();
            System.out.println("  [Steady state] " + (ok ? "OK" : "VIOLATED"));
            System.out.println("  [Result] " + (ok ? "Hypothesis holds" : "System degraded"));
            running = false;
        }
    }

    static class SystemUnderTest {
        private final AtomicBoolean upstreamAvailable = new AtomicBoolean(true);
        private final AtomicLong latency = new AtomicLong(20);
        private final AtomicInteger errorRate = new AtomicInteger(0);

        String callService() {
            if (!upstreamAvailable.get()) throw new RuntimeException("Upstream unavailable");
            if (ThreadLocalRandom.current().nextInt(100) < errorRate.get()) {
                throw new RuntimeException("Random failure");
            }
            try { Thread.sleep(latency.get()); } catch (InterruptedException e) {}
            return "OK";
        }

        void killUpstream() { upstreamAvailable.set(false); }
        void addLatency(long ms) { latency.addAndGet(ms); }
        void setErrorRate(int pct) { errorRate.set(pct); }
    }

    public static void main(String[] args) {
        System.out.println("=== Chaos Engineering Lab ===\n");

        principles();
        faultInjection();
        latencyExperiment();
        dependencyFailure();
        blastRadius();
        gameDay();
    }

    static void principles() {
        System.out.println("--- Chaos Engineering Principles ---");
        System.out.println("""
  Principles (from Principles of Chaos Engineering):

  1. Define steady state - measurable output of normal behavior
  2. Hypothesize that steady state continues in experiments
  3. Introduce realistic failure conditions
  4. Minimize blast radius (start small, expand)
  5. Automate experiments to run continuously

  NOT random breaking - controlled hypothesis testing

  Maturity model:
  Level 1: Manual experiments (game days)
  Level 2: Automated experiments in staging
  Level 3: Continuous experiments in production
  Level 4: Self-healing (auto-remediation)
    """);
    }

    static void faultInjection() {
        System.out.println("\n--- Fault Injection ---");
        var system = new SystemUnderTest();
        var results = new CopyOnWriteArrayList<String>();

        var killExp = new Experiment(
            "Calling service when upstream is down returns fallback",
            system::killUpstream,
            () -> {
                try {
                    system.callService();
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
        );
        killExp.run();

        var killExp2 = new Experiment(
            "Fallback returns cached data",
            () -> {},
            () -> {
                try { var r = system.callService(); return false; }
                catch (Exception e) { return true; }
            }
        );
        killExp2.run();

        System.out.println("""
  Fault types:
  - Service failure (kill process, return 500)
  - Latency injection (delay 1-10s)
  - Resource exhaustion (CPU, memory, disk)
  - Network issues (packet loss, partition)
  - DNS failures
  - Certificate expiry
    """);
    }

    static void latencyExperiment() {
        System.out.println("\n--- Latency Experiment ---");
        var system = new SystemUnderTest();

        var latencyExp = new Experiment(
            "Adding 500ms latency to database does not timeout the API",
            () -> system.addLatency(500),
            () -> {
                try {
                    var r = system.callService();
                    return r.equals("OK");
                } catch (Exception e) {
                    return false;
                }
            }
        );
        latencyExp.run();

        System.out.println("""
  Common latency injection tools:
  - Toxiproxy: proxy with latency/error injection
  - Chaos Mesh: HTTPChaos (delay), NetworkChaos
  - Envoy: fault injection filter
  - AWS FIS: Fault Injection Simulator

  Metrics to monitor during experiment:
  p50/p95/p99 latency, error rate, throughput
    """);
    }

    static void dependencyFailure() {
        System.out.println("\n--- Dependency Failure ---");
        System.out.println("""
  Single point of failure analysis:

  Dependencies:        | Failure impact:
  Database             | App unavailable (no data)
  Cache (Redis)        | Degraded (slower, stale data)
  Service A -> B       | App unavailable if B fails
  Message queue        | Async processing delayed
  External API         | Feature X unavailable
  DNS                  | Everything breaks

  Mitigation strategies:
  - Circuit breaker: fail fast, don't hang
  - Bulkhead: isolate resources per dependency
  - Timeout: per-call timeout (connect + read)
  - Retry with backoff: transient failure recovery
  - Fallback: default values, cached data
  - Degrade gracefully: disable feature x, not whole site
    """);
    }

    static void blastRadius() {
        System.out.println("\n--- Blast Radius Control ---");
        System.out.println("""
  Start with minimal blast radius:

  Environment:
    1. Local dev (single container)
    2. Staging (isolated traffic)
    3. Canary (1% production traffic)
    4. Regional shard (5% users)
    5. Full production (all users)

  User impact minimization:
  - Target internal-only requests (not customer-facing)
  - Target non-critical services
  - Run during low-traffic hours
  - Add automatic rollback conditions

  "Chaos" != random destruction
  Controlled experiments with clear boundaries

  Tools:
  - Chaos Monkey (Netflix): random instance termination
  - Chaos Kong (Netflix): AWS region failure simulation
  - Chaos Mesh (CNCF): Kubernetes-native fault injection
  - LitmusChaos: cloud-native chaos engineering
  - Gremlin: SaaS chaos engineering platform
    """);
    }

    static void gameDay() {
        System.out.println("\n--- Game Day Exercise ---");
        System.out.println("""
  Scenario: "Primary database fails, failover to replica"

  Timeline:
  08:00 - Announce game day to team
  08:15 - Inject fault: block traffic to primary DB
  08:16 - Monitoring alerts fire
  08:17 - On-call engineer investigates
  08:20 - Auto-failover triggers
  08:22 - Service restored (reads from replica)
  08:25 - Verify data consistency
  08:30 - Inject rollback: restore primary
  08:35 - Debrief & document findings

  Success criteria:
  - Failover completed within RTO (<5min)
  - Data loss within RPO (<1min)
  - Alert notifications received
  - Dashboard shows degradation

  After action review:
  - What broke unexpectedly?
  - Did the runbook work?
  - What needs improvement?
    """);
    }
}
