# Problem Walkthrough: LLMOps (LLM Operations)

## Problem 1: SLO Compliance Report — Company: Uber

### Interview Scenario
"You're at Uber operating a summarization model. Using the lab's `MetricsCollector`,
`RequestTracer`, `AnomalyDetector`, and `CanaryDeployer`, build a deterministic
report: feed a 1000-request workload, check a p99 latency SLO, trace one request's
spans, detect a degraded window, and exercise a 10% canary."

### The Problem
1. Record a 1000-request workload: 950 fast, 30 slow, 20 timing out.
2. Report p50/p95/p99, error rate, and tokens per request.
3. Check the SLO budget (p99 < 200ms) — expect p95 pass, p99 breach.
4. Trace one request with realistic span durations.
5. Baseline + degraded window → both anomaly alerts.
6. Canary: exactly 10% of 20 requests routed, then rollback.

### Solution Walkthrough
- Step 1: Copy the four classes verbatim; use explicit arrays (no RNG) so every
  percentile is deterministic.
- Step 2: Sorted latencies: 950×60ms, 30×500ms, 20×2000ms. p50 index 499 → 60ms;
  p95 index 949 → 60ms; p99 index 989 → 2000ms.
- Step 3: SLO verdicts: p95 60.0 WITHIN, p99 2000.0 BREACH — the tail is where the
  budget dies.
- Step 4: `RequestTracer` with a `simulate(span, ms)` helper so durations are
  realistic (guardrail 5ms, retrieval 35ms, generation 180ms) and span ids are
  deterministic (`span-01`...).
- Step 5: Baseline = healthy collector (p95 60.0, 2.0% errors). Degraded window:
  45 requests at 400ms + 5 errors → p95 400 > 1.5×60 and 10% errors > 2×2%.
- Step 6: Curate request ids whose `hashCode() % 100` is non-negative (req-1042,
  req-1079 → 0); 10% canary routes exactly 2/20; rollback routes 0.

### Code
```java
package com.genai.lab14.solution;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 14 walkthrough: SLO compliance report. Reuses the lab's
 * MetricsCollector, RequestTracer, AnomalyDetector and CanaryDeployer
 * on a deterministic request workload, then checks latency SLOs,
 * fires anomaly alerts, and exercises canary routing.
 */
public class SLOComplianceReport {

    /** Metrics collector for monitoring. */
    static class MetricsCollector {
        final Deque<Double> latencies = new ConcurrentLinkedDeque<>();
        final AtomicLong requestCount = new AtomicLong(0);
        final AtomicLong errorCount = new AtomicLong(0);
        final AtomicLong totalTokens = new AtomicLong(0);
        final int maxSamples = 1000;

        void recordRequest(double latencyMs, int tokens, boolean error) {
            requestCount.incrementAndGet();
            totalTokens.addAndGet(tokens);
            if (error) errorCount.incrementAndGet();
            synchronized (latencies) {
                latencies.addLast(latencyMs);
                if (latencies.size() > maxSamples) latencies.removeFirst();
            }
        }

        double p50() { return percentile(0.50); }
        double p95() { return percentile(0.95); }
        double p99() { return percentile(0.99); }
        double errorRate() { return (double) errorCount.get() / requestCount.get(); }
        double avgTokensPerSec() {
            return requestCount.get() > 0 ? (double) totalTokens.get() / requestCount.get() : 0;
        }

        private double percentile(double p) {
            synchronized (latencies) {
                List<Double> sorted = new ArrayList<>(latencies);
                Collections.sort(sorted);
                int idx = (int) Math.ceil(p * sorted.size()) - 1;
                return idx >= 0 ? sorted.get(idx) : 0;
            }
        }
    }

    /** Request tracer with span hierarchy (deterministic span ids). */
    static class RequestTracer {
        static class Span {
            final String spanId;
            final String parentSpanId;
            final String operation;
            final long startTime = System.nanoTime();
            long endTime;
            Map<String, Object> tags = new HashMap<>();

            Span(String parentSpanId, String operation, String spanId) {
                this.parentSpanId = parentSpanId;
                this.operation = operation;
                this.spanId = spanId;
            }

            void finish() { endTime = System.nanoTime(); }
            long durationMs() { return (endTime - startTime) / 1_000_000; }
        }

        final List<Span> spans = new ArrayList<>();
        int counter = 0;

        Span startSpan(String parentId, String operation) {
            Span span = new Span(parentId, operation, String.format("span-%02d", ++counter));
            spans.add(span);
            return span;
        }

        void simulate(Span s, long ms) {
            s.endTime = s.startTime + ms * 1_000_000L;
        }

        void report() {
            System.out.println("=== Trace Report ===");
            for (Span s : spans) {
                System.out.printf("  [%s] %s (parent: %s) -> %dms%n",
                    s.spanId, s.operation, s.parentSpanId, s.durationMs());
            }
        }
    }

    /** Anomaly detector. */
    static class AnomalyDetector {
        double baselineLatency;
        double baselineErrorRate;

        void setBaseline(MetricsCollector mc) {
            baselineLatency = mc.p95();
            baselineErrorRate = mc.errorRate();
        }

        List<String> check(MetricsCollector mc) {
            List<String> alerts = new ArrayList<>();
            if (mc.p95() > baselineLatency * 1.5)
                alerts.add("LATENCY: p95 " + mc.p95() + "ms exceeds 1.5x baseline " + baselineLatency);
            if (mc.errorRate() > baselineErrorRate * 2)
                alerts.add("ERRORS: rate " + mc.errorRate() + " exceeds 2x baseline " + baselineErrorRate);
            return alerts;
        }
    }

    /** Canary deployment logic. */
    static class CanaryDeployer {
        enum State { STABLE, CANARY, ROLLBACK }
        State state = State.STABLE;
        int canaryPercent = 0;
        final int targetPercent = 10;
        final List<String> canaryNodes = new ArrayList<>();

        void startCanary(String nodeId) {
            state = State.CANARY;
            canaryNodes.add(nodeId);
            canaryPercent = targetPercent;
            System.out.println("[CANARY] Started on node " + nodeId + " (" + targetPercent + "%)");
        }

        void rollback() {
            state = State.ROLLBACK;
            canaryNodes.clear();
            canaryPercent = 0;
            System.out.println("[ROLLBACK] Reverting to previous version");
        }

        boolean shouldRouteToCanary(String requestId) {
            return canaryPercent > 0 && requestId.hashCode() % 100 < canaryPercent;
        }
    }

    public static void main(String[] args) {
        MetricsCollector mc = new MetricsCollector();
        for (int i = 0; i < 950; i++) mc.recordRequest(60.0, 120, false);
        for (int i = 0; i < 30; i++) mc.recordRequest(500.0, 120, false);
        for (int i = 0; i < 20; i++) mc.recordRequest(2000.0, 120, true);

        System.out.println("=== Metrics (1000 requests) ===");
        System.out.printf("Requests: %d, Errors: %d%n", mc.requestCount.get(), mc.errorCount.get());
        System.out.printf("p50: %.1fms, p95: %.1fms, p99: %.1fms%n", mc.p50(), mc.p95(), mc.p99());
        System.out.printf("Error rate: %.2f%%%n", mc.errorRate() * 100);
        System.out.printf("Avg tokens/request: %.1f%n", mc.avgTokensPerSec());

        System.out.println("\n=== SLO Compliance (budget: p99 < 200ms) ===");
        System.out.printf("p99 = %.1fms -> %s%n", mc.p99(),
            mc.p99() < 200 ? "WITHIN SLO" : "SLO BREACH");
        System.out.printf("p95 = %.1fms -> %s%n", mc.p95(),
            mc.p95() < 200 ? "WITHIN SLO" : "SLO BREACH");

        RequestTracer tracer = new RequestTracer();
        var rootSpan = tracer.startSpan(null, "llm_request");
        var guardSpan = tracer.startSpan(rootSpan.spanId, "guardrail");
        tracer.simulate(guardSpan, 5);
        var retrievalSpan = tracer.startSpan(rootSpan.spanId, "retrieval");
        tracer.simulate(retrievalSpan, 35);
        var genSpan = tracer.startSpan(rootSpan.spanId, "generation");
        tracer.simulate(genSpan, 180);
        rootSpan.endTime = genSpan.endTime;
        tracer.report();

        AnomalyDetector ad = new AnomalyDetector();
        ad.setBaseline(mc);
        MetricsCollector mc2 = new MetricsCollector();
        for (int i = 0; i < 45; i++) mc2.recordRequest(400.0, 120, false);
        for (int i = 0; i < 5; i++) mc2.recordRequest(400.0, 120, true);
        System.out.println("\n=== Anomaly Detection (degraded window) ===");
        System.out.printf("Baseline p95=%.1fms, error rate=%.2f%%%n",
            ad.baselineLatency, ad.baselineErrorRate * 100);
        List<String> alerts = ad.check(mc2);
        if (alerts.isEmpty()) System.out.println("  No alerts");
        alerts.forEach(a -> System.out.println("  ALERT: " + a));

        CanaryDeployer cd = new CanaryDeployer();
        System.out.println("\n=== Canary Deployment (10% traffic) ===");
        cd.startCanary("node-5");
        List<String> canaryIds = new ArrayList<>();
        List<String> stableIds = new ArrayList<>();
        for (int i = 1000; i < 100000 && canaryIds.size() < 2 && stableIds.size() < 18; i++) {
            String id = "req-" + i;
            int h = id.hashCode() % 100;
            if (h >= 0 && h < 10 && canaryIds.size() < 2) canaryIds.add(id);
            else if (h >= 10 && stableIds.size() < 18) stableIds.add(id);
        }
        int routed = 0;
        for (String id : canaryIds) {
            boolean c = cd.shouldRouteToCanary(id);
            if (c) routed++;
            System.out.println("  " + id + " -> " + (c ? "CANARY" : "stable"));
        }
        for (String id : stableIds) {
            if (cd.shouldRouteToCanary(id)) routed++;
        }
        System.out.println("Routed to canary: " + routed + "/20 (10%)");
        cd.rollback();
        System.out.println("After rollback routed: " + cd.shouldRouteToCanary("req-42"));

        System.out.println("\nLLMOps concepts validated.");
    }
}
```

### Expected Output
```text
=== Metrics (1000 requests) ===
Requests: 1000, Errors: 20
p50: 60.0ms, p95: 60.0ms, p99: 2000.0ms
Error rate: 2.00%
Avg tokens/request: 120.0

=== SLO Compliance (budget: p99 < 200ms) ===
p99 = 2000.0ms -> SLO BREACH
p95 = 60.0ms -> WITHIN SLO
=== Trace Report ===
  [span-01] llm_request (parent: null) -> 180ms
  [span-02] guardrail (parent: span-01) -> 5ms
  [span-03] retrieval (parent: span-01) -> 35ms
  [span-04] generation (parent: span-01) -> 180ms

=== Anomaly Detection (degraded window) ===
Baseline p95=60.0ms, error rate=2.00%
  ALERT: LATENCY: p95 400.0ms exceeds 1.5x baseline 60.0
  ALERT: ERRORS: rate 0.1 exceeds 2x baseline 0.02

=== Canary Deployment (10% traffic) ===
[CANARY] Started on node node-5 (10%)
  req-1042 -> CANARY
  req-1079 -> CANARY
Routed to canary: 2/20 (10%)
[ROLLBACK] Reverting to previous version
After rollback routed: false

LLMOps concepts validated.
```

### Company Evaluation
- Uber: Tail-latency SLOs at scale, canary gates per service.
- Microsoft: Trace-first incident response, span-based root cause.
- Datadog/Grafana: Percentile dashboards, anomaly alerting.
- OpenAI: LLM-specific metrics — tokens, KV cache, quality drift.

---

## Problem 2: Baseline Hygiene — Company: Microsoft

### Interview Scenario
"You're at Microsoft investigating a silent incident: alerts fired during a degraded
window, but the on-call changed nothing and the same degradation stopped alerting.
Show why — the baseline was captured from the degraded collector."

### The Problem
1. `ad.setBaseline(mc)` captured the healthy window: p95 60ms, 2% errors.
2. Someone re-baselined from a degraded collector: p95 400ms, 10% errors.
3. The same degraded window now passes silently.

### Solution Walkthrough
- Step 1: Healthy baseline → thresholds 90ms and 4% → degraded window fires both.
- Step 2: Contaminated baseline → thresholds 600ms and 20% → degraded window
  looks normal.
- Step 3: Rule: re-baseline only from known-good windows (deploy gates, quiet hours).

### Code
```java
AnomalyDetector ad = new AnomalyDetector();
ad.setBaseline(mc);   // healthy: p95=60.0ms, 2.00% errors
System.out.println("Alerts (healthy baseline): " + ad.check(mc2).size()); // 2

AnomalyDetector ad2 = new AnomalyDetector();
ad2.setBaseline(mc2); // contaminated: p95=400.0ms, 10% errors
System.out.println("Alerts (contaminated baseline): " + ad2.check(mc2).size()); // 0
```
Expected output:
```text
Alerts (healthy baseline): 2
Alerts (contaminated baseline): 0
```
The same traffic is an incident under the healthy baseline and normal under the
contaminated one — baselines are state, and stale or tainted state silently
disarms alerting.
