# Problem Walkthrough: Building a GenAI Platform

## Problem 1: Platform Simulator — Company: Stripe

### Interview Scenario
"You're at Stripe standing up the internal GenAI gateway. Using the lab's
`ModelRegistry`, `APIGateway`, `Orchestrator`, `RateLimiter`, and `ABTestTracker`,
register two versions of gpt-small plus gpt-large, verify a 50/50 A/B split,
exercise the fallback chain, measure token-bucket behavior, and report an A/B
error-rate comparison."

### The Problem
1. Register gpt-small v2, gpt-small v1 (control = latest), gpt-large v1.
2. Route 20 curated requests with `useVariant=true` — expect exactly 10 v1 / 10 v2.
3. Run the orchestrator with gpt-large down (fallback) and all models down.
4. Burst 15 requests against a 10-token bucket, then refill and burst again.
5. Record a fixed A/B test (60 A with 4 errors, 40 B with 2 errors) and report.

### Solution Walkthrough
- Step 1: Copy the five components verbatim from the lab.
- Step 2: Registration order matters: register v2 before v1 so `getLatest` returns
  v1 — otherwise both A/B arms serve v2 and the split is degenerate.
- Step 3: Curate ids (usr-N) whose `hashCode() % 100` is non-negative: `usr-1010`
  routes v2 (variant), `usr-1000` routes v1 (control); distribution prints 10/10.
- Step 4: Orchestrator: gpt-large 'ERROR' → fallback gpt-small OK; all-ERROR →
  'All models failed'.
- Step 5: Rate limiter: burst grants 10/15; after 2.1s at 5/s refill → 10/15 again.
- Step 6: AB tracker with fixed counts: 6.67% vs 5.00%.

### Code
```java
package com.genai.lab15.solution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 15 walkthrough: GenAI platform simulator. Reuses the lab's
 * ModelRegistry, APIGateway, Orchestrator, RateLimiter and
 * ABTestTracker to exercise version routing, fallback chains,
 * token bucket limiting, and A/B analysis on fixed workloads.
 */
public class PlatformSimulator {

    /** Model record. */
    record Model(String id, String name, String version, int costUnits) {}

    /** Model registry. */
    static class ModelRegistry {
        final Map<String, List<Model>> models = new ConcurrentHashMap<>();

        void register(Model model) {
            models.computeIfAbsent(model.name, k -> new ArrayList<>()).add(model);
        }

        Model getLatest(String name) {
            var list = models.get(name);
            return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
        }

        Model getVersion(String name, String version) {
            var list = models.get(name);
            if (list == null) return null;
            return list.stream().filter(m -> m.version.equals(version)).findFirst().orElse(null);
        }
    }

    /** API Gateway with routing. */
    static class APIGateway {
        final ModelRegistry registry;
        double abVariantRatio = 0.5;

        APIGateway(ModelRegistry registry) { this.registry = registry; }

        String route(String modelName, String requestId, boolean useVariant) {
            Model model;
            if (useVariant && requestId.hashCode() % 100 < abVariantRatio * 100) {
                model = registry.getVersion(modelName, "v2");
            } else {
                model = registry.getLatest(modelName);
            }
            if (model == null) return "ERROR: Model not found";
            return "Routed to " + model.name + " v" + model.version + " (cost: " + model.costUnits + ")";
        }
    }

    /** Orchestrator with fallback. */
    static class Orchestrator {
        final List<String> modelChain;
        final Map<String, String> fallbacks;

        Orchestrator(List<String> modelChain, Map<String, String> fallbacks) {
            this.modelChain = modelChain;
            this.fallbacks = fallbacks;
        }

        String execute(String request, Map<String, String> modelEndpoints) {
            String result = null;
            for (String model : modelChain) {
                String endpoint = modelEndpoints.get(model);
                if (endpoint != null && !endpoint.equals("ERROR")) {
                    result = "[" + model + "] Processed: " + request;
                    break;
                }
                // Try fallback
                String fb = fallbacks.get(model);
                if (fb != null) {
                    String fbEndpoint = modelEndpoints.get(fb);
                    if (fbEndpoint != null && !fbEndpoint.equals("ERROR")) {
                        result = "[" + fb + " (fallback)] Processed: " + request;
                        break;
                    }
                }
            }
            return result != null ? result : "All models failed";
        }
    }

    /** Token bucket rate limiter. */
    static class RateLimiter {
        final int maxTokens;
        final double refillRate;
        double tokens;
        long lastRefill;

        RateLimiter(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefill = System.nanoTime();
        }

        synchronized boolean tryAcquire(int cost) {
            refill();
            if (tokens >= cost) { tokens -= cost; return true; }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefill) / 1e9;
            tokens = Math.min(maxTokens, tokens + elapsed * refillRate);
            lastRefill = now;
        }
    }

    /** A/B test tracker. */
    static class ABTestTracker {
        final AtomicLong variantA = new AtomicLong();
        final AtomicLong variantB = new AtomicLong();
        final AtomicLong errorsA = new AtomicLong();
        final AtomicLong errorsB = new AtomicLong();

        void record(String variant, boolean error) {
            if (variant.equals("A")) { variantA.incrementAndGet(); if (error) errorsA.incrementAndGet(); }
            else { variantB.incrementAndGet(); if (error) errorsB.incrementAndGet(); }
        }

        void report() {
            System.out.println("=== A/B Test Results ===");
            System.out.printf("Variant A: %d requests, %d errors (%.2f%%)%n",
                variantA.get(), errorsA.get(), variantA.get() > 0 ? errorsA.get() * 100.0 / variantA.get() : 0);
            System.out.printf("Variant B: %d requests, %d errors (%.2f%%)%n",
                variantB.get(), errorsB.get(), variantB.get() > 0 ? errorsB.get() * 100.0 / variantB.get() : 0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ModelRegistry registry = new ModelRegistry();
        registry.register(new Model("m2", "gpt-small", "v2", 2));
        registry.register(new Model("m1", "gpt-small", "v1", 1));
        registry.register(new Model("m3", "gpt-large", "v1", 10));

        APIGateway gateway = new APIGateway(registry);
        System.out.println("=== API Gateway: A/B routing (50/50 v1 vs v2) ===");
        List<String> variantIds = new ArrayList<>();
        List<String> controlIds = new ArrayList<>();
        for (int i = 1000; i < 100000 && (variantIds.size() < 10 || controlIds.size() < 10); i++) {
            String id = "usr-" + i;
            int h = id.hashCode() % 100;
            if (h >= 0 && h < 50 && variantIds.size() < 10) variantIds.add(id);
            else if (h >= 50 && controlIds.size() < 10) controlIds.add(id);
        }
        int v2 = 0, v1 = 0;
        for (String id : variantIds) {
            String r = gateway.route("gpt-small", id, true);
            if (r.contains("v2")) v2++; else v1++;
        }
        for (String id : controlIds) {
            String r = gateway.route("gpt-small", id, true);
            if (r.contains("v2")) v2++; else v1++;
        }
        System.out.println("variant sample (" + variantIds.get(0) + "): "
            + gateway.route("gpt-small", variantIds.get(0), true));
        System.out.println("control sample (" + controlIds.get(0) + "): "
            + gateway.route("gpt-small", controlIds.get(0), true));
        System.out.printf("Distribution: v1=%d, v2=%d (50/50 target)%n", v1, v2);
        System.out.println("Variant off: " + gateway.route("gpt-small", "usr-1010", false));

        Orchestrator orch = new Orchestrator(
            List.of("gpt-large", "gpt-small"),
            Map.of("gpt-large", "gpt-small"));
        System.out.println("\n=== Orchestrator with Fallback ===");
        Map<String, String> endpoints = Map.of("gpt-large", "ERROR", "gpt-small", "OK");
        System.out.println(orch.execute("Hello world", endpoints));
        Map<String, String> allDown = Map.of("gpt-large", "ERROR", "gpt-small", "ERROR");
        System.out.println("All models down: " + orch.execute("Hello world", allDown));

        RateLimiter rl = new RateLimiter(10, 5);
        System.out.println("\n=== Rate Limiter (burst then refill) ===");
        int granted = 0;
        for (int i = 0; i < 15; i++) if (rl.tryAcquire(1)) granted++;
        System.out.println("Burst of 15: granted " + granted + "/15");
        Thread.sleep(2100);
        int granted2 = 0;
        for (int i = 0; i < 15; i++) if (rl.tryAcquire(1)) granted2++;
        System.out.println("After 2.1s refill (5/s): granted " + granted2 + "/15");

        ABTestTracker ab = new ABTestTracker();
        for (int i = 0; i < 60; i++) ab.record("A", i < 4);
        for (int i = 0; i < 40; i++) ab.record("B", i < 2);
        System.out.println("\n=== A/B Test (after 24h) ===");
        ab.report();

        System.out.println("\nGenAI Platform concepts validated.");
    }
}
```

### Expected Output
```text
=== API Gateway: A/B routing (50/50 v1 vs v2) ===
variant sample (usr-1010): Routed to gpt-small vv2 (cost: 2)
control sample (usr-1000): Routed to gpt-small vv1 (cost: 1)
Distribution: v1=10, v2=10 (50/50 target)
Variant off: Routed to gpt-small vv1 (cost: 1)

=== Orchestrator with Fallback ===
[gpt-small (fallback)] Processed: Hello world
All models down: All models failed

=== Rate Limiter (burst then refill) ===
Burst of 15: granted 10/15
After 2.1s refill (5/s): granted 10/15

=== A/B Test (after 24h) ===
=== A/B Test Results ===
Variant A: 60 requests, 4 errors (6.67%)
Variant B: 40 requests, 2 errors (5.00%)

GenAI Platform concepts validated.
```

### Company Evaluation
- Stripe: Gateway/platform engineering, tenant rate limiting, cost routing.
- OpenAI: Model registry + versioning for API models, A/B rollout discipline.
- Anthropic: Fallback chains and graceful degradation for reliability.
- Microsoft: Azure AI platform — rate limiting, quotas, observability.

---

## Problem 2: Cost-Aware Tiered Routing — Company: OpenAI

### Interview Scenario
"You're at OpenAI pricing a new endpoint. Route 100 requests by complexity bucket
(simple vs complex) and compute the cost-unit spend under two policies."

### The Problem
1. 70 simple requests, 30 complex ones.
2. Policy A: everything to gpt-large (cost 10).
3. Policy B: simple → gpt-small (cost 1), complex → gpt-large.
4. Compare total spend.

### Solution Walkthrough
- Step 1: Use the registry's `getLatest`/`getVersion` by model name.
- Step 2: Classify by a complexity predicate (e.g., token count > 50).
- Step 3: Sum costUnits per policy.

### Code
```java
// 70 simple + 30 complex requests; policy B routes by complexity
int simple = 70, complex = 30;
int policyA = (simple + complex) * 10;        // gpt-large everywhere
int policyB = simple * 1 + complex * 10;      // gpt-small + gpt-large
System.out.printf("Policy A (all large): %d units%n", policyA);
System.out.printf("Policy B (tiered):    %d units%n", policyB);
System.out.printf("Savings: %.0f%%%n", 100.0 * (policyA - policyB) / policyA);
```
Expected output:
```text
Policy A (all large): 1000 units
Policy B (tiered):    370 units
Savings: 63%
```
Same workload, 63% cheaper — and quality on complex queries is untouched, which
is the entire argument for cost-aware tiering.
