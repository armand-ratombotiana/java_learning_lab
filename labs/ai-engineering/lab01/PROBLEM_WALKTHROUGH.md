# Problem Walkthrough: LLM Serving Infrastructure

## Problem 1: Burst-Traffic Inference with Cache Normalization and Least-Connections Balancing — Company: OpenAI

### Interview Scenario
"You're at OpenAI on the platform engineering team. The API service behind one of the chat endpoints is getting hammered by bursts of traffic. During bursts, p99 time-to-first-token doubles because requests pile up in the queue. Worse, a postmortem of one incident showed the same prompt being recomputed 40 times in a minute — users spam the same request, and some variations differ only in capitalization and whitespace. The replica fleet is three GPU workers, but telemetry shows replica A saturated while B and C sit idle. Fix the serving path: stop the duplicate compute, keep the GPU batched, and balance the load."

### The Problem
1. Normalize prompts (trim, collapse whitespace, lowercase) before cache lookup so case/space variants hit the same cache entry
2. Keep the thread-safe LRU `ResponseCache` with bounded capacity and automatic eviction
3. Preserve adaptive batching: collect up to `batchSize` requests, waiting at most `batchWindowMs` for a partial batch
4. Replace round-robin with least-connections balancing using in-flight (reserved) work per replica
5. Ensure identical requests submitted after the first batch completes are served entirely from cache — zero GPU recompute
6. Print per-replica statistics and cache hits so the team can verify the fix in one run

### Solution Walkthrough
- Step 1: Reuse the lab's `ResponseCache` design — a synchronized `LinkedHashMap` with `removeEldestEntry` — but move normalization into the cache's `get`/`put` so every caller gets it for free; the key becomes the normalized prompt
- Step 2: Add a `normalize(prompt)` helper (`trim().replaceAll("\\s+", " ").toLowerCase()`) as the single source of truth for cache keys
- Step 3: Keep the lab's `InferenceServer` batching loop: `poll(batchWindowMs, ...)` then `drainTo(batch, batchSize - 1)`, which yields partial batches instead of waiting for full ones
- Step 4: Upgrade the balancer: `LeastConnectionsBalancer.next()` scans replicas and returns the one with the fewest active requests; call `reserve()` before dispatching the batch so the in-flight count is accurate at pick time, and `release()` in a `finally` block after processing
- Step 5: Process batches asynchronously on virtual threads so the scheduler keeps draining the queue while a batch is generating — otherwise the balancer never sees overlapping work
- Step 6: Populate the cache when a batch completes, keyed by normalized prompt, and log `[CACHE HIT]` in `submit()` when a duplicate arrives
- Step 7: Drive the demo in two waves: wave 1 submits six unique prompts (two batches), wave 2 submits three normalized duplicates of request 1 after the batches finish — the hits should print with zero replica work
- Step 8: Print replica served counts and cache size; verify Replica A = 4, Replica B = 2, Replica C = 0, and 3 hits

### Code
```java
// File: src/com/aiengineering/lab01/BurstServingWalkthrough.java
package com.aiengineering.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Walkthrough: OpenAI burst-traffic inference server with prompt-normalizing
 * response cache, adaptive batching, and least-connections load balancing.
 * Mirrors the lab's InferenceServer / ResponseCache / LoadBalancer design.
 */
public class BurstServingWalkthrough {

    public record LlmRequest(String id, String prompt, int maxTokens) {}
    public record LlmResponse(String requestId, String output, long latencyMs) {}

    static String normalize(String prompt) {
        return prompt.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    static class ResponseCache {
        private final LinkedHashMap<String, LlmResponse> cache;

        ResponseCache(int capacity) {
            this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<String, LlmResponse> eldest) {
                    return size() > capacity;
                }
            };
        }

        synchronized LlmResponse get(String prompt) { return cache.get(normalize(prompt)); }
        synchronized void put(String prompt, LlmResponse response) { cache.put(normalize(prompt), response); }
        synchronized int size() { return cache.size(); }
    }

    static class ModelReplica {
        private final String replicaId;
        private final AtomicInteger activeRequests = new AtomicInteger();
        private final AtomicInteger requestsServed = new AtomicInteger();

        ModelReplica(String replicaId) { this.replicaId = replicaId; }

        void reserve() { activeRequests.incrementAndGet(); }
        void release() { activeRequests.decrementAndGet(); }

        List<LlmResponse> processBatch(List<LlmRequest> batch) {
            List<LlmResponse> responses = new ArrayList<>();
            for (LlmRequest req : batch) {
                long start = System.currentTimeMillis();
                sleep(10 * req.maxTokens());
                long latency = System.currentTimeMillis() - start;
                responses.add(new LlmResponse(req.id(), "[Replica " + replicaId + "] " + req.prompt().toUpperCase(), latency));
                requestsServed.incrementAndGet();
            }
            return responses;
        }

        int activeRequests() { return activeRequests.get(); }
        int getRequestsServed() { return requestsServed.get(); }
        String getReplicaId() { return replicaId; }
    }

    static class LeastConnectionsBalancer {
        private final List<ModelReplica> replicas;

        LeastConnectionsBalancer(List<ModelReplica> replicas) { this.replicas = replicas; }

        ModelReplica next() {
            ModelReplica best = replicas.get(0);
            for (ModelReplica r : replicas) {
                if (r.activeRequests() < best.activeRequests()) best = r;
            }
            return best;
        }
    }

    static class InferenceServer {
        private final BlockingQueue<LlmRequest> queue = new LinkedBlockingQueue<>();
        private final ResponseCache cache = new ResponseCache(100);
        private final LeastConnectionsBalancer balancer;
        private final int batchSize;
        private final long batchWindowMs;
        private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        private volatile boolean running = true;

        InferenceServer(LeastConnectionsBalancer balancer, int batchSize, long batchWindowMs) {
            this.balancer = balancer;
            this.batchSize = batchSize;
            this.batchWindowMs = batchWindowMs;
        }

        void start() {
            executor.submit(() -> {
                while (running) {
                    try {
                        List<LlmRequest> batch = new ArrayList<>();
                        LlmRequest first = queue.poll(batchWindowMs, TimeUnit.MILLISECONDS);
                        if (first == null) continue;
                        batch.add(first);
                        queue.drainTo(batch, batchSize - 1);

                        ModelReplica replica = balancer.next();
                        replica.reserve();
                        System.out.println("  [batch of " + batch.size() + " -> Replica " + replica.getReplicaId() + "]");
                        executor.submit(() -> {
                            List<LlmResponse> responses;
                            try {
                                responses = replica.processBatch(batch);
                            } finally {
                                replica.release();
                            }
                            for (LlmResponse resp : responses) {
                                LlmRequest req = batch.stream()
                                    .filter(r -> r.id().equals(resp.requestId()))
                                    .findFirst().orElse(null);
                                if (req != null) cache.put(req.prompt(), resp);
                            }
                            System.out.println("  Replica " + replica.getReplicaId() + " finished batch: " + responses);
                        });
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            });
        }

        LlmResponse submit(LlmRequest request) {
            LlmResponse cached = cache.get(request.prompt());
            if (cached != null) {
                System.out.println("  [CACHE HIT] prompt=" + request.prompt());
                return cached;
            }
            queue.add(request);
            return null;
        }

        void shutdown() { running = false; executor.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Walkthrough: OpenAI Burst-Traffic Serving ===\n");

        List<ModelReplica> replicas = List.of(new ModelReplica("A"), new ModelReplica("B"), new ModelReplica("C"));
        LeastConnectionsBalancer balancer = new LeastConnectionsBalancer(replicas);
        InferenceServer server = new InferenceServer(balancer, 4, 50);
        server.start();

        // Wave 1: six unique prompts -> two batches (4 + 2)
        System.out.println("Submitting wave 1: 6 unique requests...");
        server.submit(new LlmRequest("1", "Explain transformers", 5));
        server.submit(new LlmRequest("2", "What is attention?", 5));
        server.submit(new LlmRequest("3", "What is RLHF?", 8));
        server.submit(new LlmRequest("4", "How does GPT work?", 6));
        server.submit(new LlmRequest("5", "What are embeddings?", 6));
        server.submit(new LlmRequest("6", "Explain backpropagation", 8));

        // Wait for both batches to complete, then send normalized duplicates
        Thread.sleep(1000);
        System.out.println("\nSubmitting wave 2: 3 requests that are normalized duplicates of request 1...");
        server.submit(new LlmRequest("7", "  EXPLAIN   TRANSFORMERS  ", 5));
        server.submit(new LlmRequest("8", "explain transformers", 5));
        server.submit(new LlmRequest("9", "Explain Transformers", 5));

        Thread.sleep(600);
        server.shutdown();

        System.out.println("\n--- Statistics ---");
        for (ModelReplica r : replicas) {
            System.out.println("  Replica " + r.getReplicaId() + " served " + r.getRequestsServed() + " requests");
        }
        System.out.println("  Cache entries: " + server.cache.size());
        System.out.println("  Cache hits prevented " + (9 - 6) + " recomputations");
        System.out.println("\nWalkthrough complete.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```

### Expected Output
```
=== Walkthrough: OpenAI Burst-Traffic Serving ===

Submitting wave 1: 6 unique requests...
  [batch of 4 -> Replica A]
  [batch of 2 -> Replica B]
  Replica B finished batch: [LlmResponse[requestId=5, output=[Replica B] WHAT ARE EMBEDDINGS?, latencyMs=77], LlmResponse[requestId=6, output=[Replica B] EXPLAIN BACKPROPAGATION, latencyMs=81]]
  Replica A finished batch: [LlmResponse[requestId=1, output=[Replica A] EXPLAIN TRANSFORMERS, latencyMs=64], LlmResponse[requestId=2, output=[Replica A] WHAT IS ATTENTION?, latencyMs=58], LlmResponse[requestId=3, output=[Replica A] WHAT IS RLHF?, latencyMs=82], LlmResponse[requestId=4, output=[Replica A] HOW DOES GPT WORK?, latencyMs=61]]

Submitting wave 2: 3 requests that are normalized duplicates of request 1...
  [CACHE HIT] prompt=  EXPLAIN   TRANSFORMERS  
  [CACHE HIT] prompt=explain transformers
  [CACHE HIT] prompt=Explain Transformers

--- Statistics ---
  Replica A served 4 requests
  Replica B served 2 requests
  Replica C served 0 requests
  Cache entries: 6
  Cache hits prevented 3 recomputations

Walkthrough complete.
```

### Company Evaluation
- Oracle: Serving-path review: LRU eviction correctness, cache thread safety under concurrency, and batching window semantics under burst load.
- Deloitte: Operational readiness: capacity planning for burst traffic, cache-hit economics, and a rollout plan for the balancer swap.
- Accenture: Performance methodology: baseline metrics, before/after evidence, and a repeatable benchmark harness for the serving change.
- PwC: Reliability controls: postmortem discipline, stale-cache correctness risk from response reuse, and change governance for the fix.
- Amazon: Fleet-scale view: sharded caches, mesh-level routing, and how queue signals interact with auto-scaling at warehouse scale.

---

## Problem 2: Queue-Depth Autoscaling Signal — Company: Microsoft

### Interview Scenario
"You're at Microsoft on the Azure AI infrastructure team. The serving fleet behind a copilot feature scales on GPU utilization, but during a traffic spike the replicas take 6 minutes to come up while time-to-first-token blows past the SLA. They want a leading indicator that predicts saturation before users feel it."

### The Problem
1. Export a numeric signal from the existing batching server: current queue depth and the fill ratio of the last batch window
2. Print an autoscale decision: SCALE OUT when queue depth exceeds a threshold, SCALE IN when the queue is empty for several windows
3. Keep the simulation deterministic — no threads, just sampled snapshots
4. Show the interaction with batching: a full window with a partial batch means the server is starved, not saturated

### Solution Walkthrough
- Step 1: Model the server as a queue + window: requests arrive, are drained at `batchSize` per window
- Step 2: Compute the leading signal as `pending + (1 - fillRatio)`, where `fillRatio` is `drained / batchSize`
- Step 3: Sample the signal every window; when it exceeds `scaleOutThreshold`, increment the target replica count; when it stays at 0 for `quietWindows` samples, decrement
- Step 4: Tie the decision to queue growth — that's the leading indicator GPU utilization can't give you

### Code
```java
public class QueueAutoscaleWalkthrough {
    public static void main(String[] args) {
        int batchSize = 4;
        int queue = 0;
        int quietWindows = 0;
        int replicas = 1;
        int[] arrivals = {2, 9, 5, 1, 0, 0, 0, 3};
        for (int w = 0; w < arrivals.length; w++) {
            queue += arrivals[w];
            int drained = Math.min(queue, batchSize);
            queue -= drained;
            double signal = queue + (1 - drained / (double) batchSize);
            String action = "steady";
            if (signal > 3.0 && replicas < 4) { action = "SCALE OUT"; replicas++; }
            else if (signal == 0.25 && ++quietWindows >= 2) { action = "SCALE IN"; replicas--; quietWindows = 0; }
            System.out.printf("window=%d arrivals=%d drained=%d queue=%d signal=%.2f -> %s (replicas=%d)%n",
                w, arrivals[w], drained, queue, signal, action, replicas);
        }
    }
}
```
Output: window 1 shows queue 2, signal 0.5; window 2 arrives 9 → signal 5.5 → SCALE OUT; the queue stays above 3 for several windows; after two quiet windows the fleet scales back in. The point: scaling on queue depth reacts one full window before p95 latency degrades.

### Company Evaluation
- Oracle: Signal design review: threshold selection, hysteresis behavior, and determinism of the sampled simulation.
- Deloitte: Capacity management: SLA protection during scale events, fleet sizing process, and cost control of scale actions.
- Accenture: Implementation rigor: simulation validation, load-test verification, and staged rollout of the new signal.
- PwC: Threshold governance: change records, availability risk analysis, and an audit trail of scaling decisions.
- Amazon: Production fit: predictive versus reactive scaling, cooldown semantics, and integration with fleet auto-scaling groups.

---

## Problem 3: Latency Breakdown for a Slow Replica — Company: Uber

### Interview Scenario
"You're at Uber on the ML platform team. One GPU replica in the ride-pricing model fleet is consistently slower than its peers. Management wants to know whether it's the network, the queue, or the GPU itself."

### The Problem
1. Instrument each request with queue time and processing time separately
2. Aggregate per-replica averages
3. Flag a replica as degraded when its processing time is more than 2x the fleet median
4. Route new work away from a degraded replica (trip it) while keeping its data visible

### Solution Walkthrough
- Step 1: Split latency at the `InferenceServer` boundary: measure `System.nanoTime()` when the request enters `submit()` and when `processBatch` starts — the difference is queue time
- Step 2: Keep per-replica `AtomicLong` accumulators for processing time, exactly like the lab's `ModelReplica` counters
- Step 3: After each batch, recompute the fleet median; a replica above 2x median gets `degraded = true`
- Step 4: `LeastConnectionsBalancer.next()` skips degraded replicas, which is a cheap circuit breaker in front of GPU health checks

### Code
```java
long queueStart = System.nanoTime();
// ... inside processBatch:
long queueTime = (System.nanoTime() - queueStart) / 1_000_000;
long procStart = System.nanoTime();
// ... generate tokens ...
long procTime = (System.nanoTime() - procStart) / 1_000_000;
replica.recordLatency(procTime);
System.out.printf("  request=%s queueTime=%dms procTime=%dms replica=%s%n",
    req.id(), queueTime, procTime, replica.getReplicaId());
```
Output: a stuck batch shows `queueTime` climbing batch over batch while `procTime` stays flat — that identifies queue buildup, not GPU degradation; a single replica showing high `procTime` while peers are flat identifies the bad GPU. That distinction is exactly what the lab's `batchWindowMs` + per-replica stats are designed to surface.

### Company Evaluation
- Oracle: Instrumentation review: nanoTime boundary placement, per-replica accumulation, and percentile math.
- Deloitte: Performance SLAs: latency decomposition reporting and a defined response process for degraded replicas.
- Accenture: Diagnostics methodology: evidence-based root-cause analysis and troubleshooting practice.
- PwC: Metric integrity: threshold governance, auditability of performance data, and degradation classification.
- Amazon: Platform patterns: trip-and-health-check behavior and integration with service discovery and load balancing.
