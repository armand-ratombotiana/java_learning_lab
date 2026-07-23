# Lab 06 — Production Deployment Rollback: Code Examples

## Automated Rollback Decision Engine

```java
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RollbackDecisionEngine {

    private final Map<String, ServiceMetrics> serviceMetrics = new ConcurrentHashMap<>();
    private final RollbackAction rollbackAction;

    public RollbackDecisionEngine(RollbackAction rollbackAction) {
        this.rollbackAction = rollbackAction;
    }

    public void evaluateDeployment(String serviceName, String version, MetricsSnapshot current) {
        ServiceMetrics history = serviceMetrics.computeIfAbsent(serviceName,
                k -> new ServiceMetrics());

        RollbackDecision decision = shouldRollback(history, current);

        if (decision.shouldRollback()) {
            System.err.println("ROLLBACK TRIGGERED for " + serviceName +
                    " version " + version + ": " + decision.reason());
            rollbackAction.execute(serviceName, version, decision.reason());
        }

        history.updateBaseline(current);
    }

    RollbackDecision shouldRollback(ServiceMetrics history, MetricsSnapshot current) {
        if (history.baseline == null) {
            return RollbackDecision.noRollback("First measurement — establishing baseline");
        }

        List<String> reasons = new ArrayList<>();

        // Error rate check
        if (current.errorRate > history.baseline.errorRate * 2 && current.errorRate > 0.01) {
            reasons.add("Error rate " + current.errorRate + " vs baseline " + history.baseline.errorRate);
        }

        // Latency check
        if (current.p99Latency > history.baseline.p99Latency * 1.5) {
            reasons.add("P99 latency " + current.p99Latency + "ms vs baseline " + history.baseline.p99Latency + "ms");
        }

        // CPU check
        if (current.cpuUtilization > 90) {
            reasons.add("CPU " + current.cpuUtilization + "% > 90%");
        }

        // Health check failures
        if (current.healthCheckFailures > 10) {
            reasons.add("Health check failures: " + current.healthCheckFailures);
        }

        if (!reasons.isEmpty()) {
            return RollbackDecision.rollback(String.join("; ", reasons));
        }

        return RollbackDecision.noRollback("All metrics within thresholds");
    }

    record MetricsSnapshot(double errorRate, double p99Latency, double cpuUtilization,
                           int healthCheckFailures, Instant timestamp) {}

    record RollbackDecision(boolean shouldRollback, String reason) {
        static RollbackDecision rollback(String reason) {
            return new RollbackDecision(true, reason);
        }
        static RollbackDecision noRollback(String reason) {
            return new RollbackDecision(false, reason);
        }
    }

    static class ServiceMetrics {
        MetricsSnapshot baseline;

        void updateBaseline(MetricsSnapshot current) {
            if (baseline == null) {
                this.baseline = current;
            } else {
                // Exponential moving average for baseline
                this.baseline = new MetricsSnapshot(
                    baseline.errorRate * 0.9 + current.errorRate * 0.1,
                    baseline.p99Latency * 0.9 + current.p99Latency * 0.1,
                    baseline.cpuUtilization * 0.9 + current.cpuUtilization * 0.1,
                    baseline.healthCheckFailures,
                    current.timestamp
                );
            }
        }
    }

    interface RollbackAction {
        void execute(String serviceName, String version, String reason);
    }
}
```

## Canary Analyzer

```java
import java.time.*;

public class CanaryAnalyzer {

    public CanaryResult analyze(CanaryMetrics baseline, CanaryMetrics canary, Duration window) {
        double errorRateRatio = canary.errorRate / Math.max(baseline.errorRate, 0.0001);
        double p99LatencyRatio = canary.p99Latency / baseline.p99Latency;
        double p50LatencyRatio = canary.p50Latency / baseline.p50Latency;

        boolean errorRateFailed = errorRateRatio > 2.0;
        boolean latencyFailed = p99LatencyRatio > 1.5 || p50LatencyRatio > 1.3;
        boolean cpuFailed = canary.cpuUtilization > 80;
        boolean memoryFailed = canary.memoryUtilization > 85;

        List<String> failures = new java.util.ArrayList<>();
        if (errorRateFailed) failures.add("Error rate " + String.format("%.2f", errorRateRatio) + "x baseline");
        if (latencyFailed) failures.add("Latency P99 " + String.format("%.2f", p99LatencyRatio) + "x baseline");
        if (cpuFailed) failures.add("CPU " + canary.cpuUtilization + "%");
        if (memoryFailed) failures.add("Memory " + canary.memoryUtilization + "%");

        boolean passed = failures.isEmpty();
        return new CanaryResult(passed, failures, window);
    }

    record CanaryMetrics(double errorRate, double p99Latency, double p50Latency,
                         double cpuUtilization, double memoryUtilization, Instant timestamp) {}

    record CanaryResult(boolean passed, java.util.List<String> failures, Duration window) {}
}
```

## Unit Tests

```java
import org.junit.jupiter.api.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class RollbackDecisionEngineTest {
    private RollbackDecisionEngine engine;
    private String lastRollbackReason;

    @BeforeEach
    void setup() {
        engine = new RollbackDecisionEngine((svc, ver, reason) -> {
            lastRollbackReason = reason;
        });
    }

    @Test
    void testNoRollbackWhenMetricsNormal() {
        var baseline = new RollbackDecisionEngine.MetricsSnapshot(
            0.01, 50.0, 40.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v1", baseline);

        var current = new RollbackDecisionEngine.MetricsSnapshot(
            0.02, 60.0, 45.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v2", current);

        assertNull(lastRollbackReason, "Should not rollback for normal metrics");
    }

    @Test
    void testRollbackOnErrorRateSpike() {
        var baseline = new RollbackDecisionEngine.MetricsSnapshot(
            0.01, 50.0, 40.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v1", baseline);

        var current = new RollbackDecisionEngine.MetricsSnapshot(
            0.05, 60.0, 45.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v2", current);

        assertNotNull(lastRollbackReason, "Should rollback on error rate spike");
        assertTrue(lastRollbackReason.contains("Error rate"));
    }

    @Test
    void testRollbackOnHighCPU() {
        var baseline = new RollbackDecisionEngine.MetricsSnapshot(
            0.01, 50.0, 40.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v1", baseline);

        var current = new RollbackDecisionEngine.MetricsSnapshot(
            0.01, 50.0, 95.0, 0, Instant.now());
        engine.evaluateDeployment("test-svc", "v2", current);

        assertNotNull(lastRollbackReason, "Should rollback on high CPU");
    }
}
```
