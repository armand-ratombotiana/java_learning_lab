# Problem Walkthrough: AI Pipeline Orchestration

## Problem 1: Per-Stage Metrics with a Circuit Breaker — Company: Netflix

### Interview Scenario
"You're at Netflix on the content intelligence team. The pipeline that classifies synopses for recommendation labels runs every night, but last week a pathological batch of 10,000 very long documents made the inference stage 50x slower, the batch never finished, and the nightly job timed out — no error, just backlog and silence. The team needs two things: per-stage metrics so the slow stage is identifiable, and a circuit breaker so a degrading stage fails fast instead of accumulating an ever-growing backlog."

### The Problem
1. Build the lab's 5-stage pipeline: preprocess → tokenize → feature-extract → infer → format
2. Wrap every stage in a `TimedStage` decorator that measures latency per invocation (lab style)
3. Fix the metrics reporting to use doubles — the lab's `printf("%.2f", long)` crashes; report `avgTimeMicros` as a double
4. Add a latency budget per stage and count consecutive slow invocations
5. Open the circuit after N consecutive slow calls; the next request fails fast at that stage with an explicit message
6. Print per-stage metrics: invocations, average time, and circuit state

### Solution Walkthrough
- Step 1: Reuse the lab's `Stage<I, O>` functional interface and all five stage implementations (`TextPreprocessor`, `Tokenizer`, `FeatureExtractor`, `ModelInference`, `ResultFormatter`) unchanged
- Step 2: Extend `TimedStage` with `latencyBudgetMicros` and `slowThreshold`; on each `process()`, compare measured elapsed time against the budget
- Step 3: Track `consecutiveSlow` — reset on a fast call, increment on a slow one; when it reaches the threshold, set `circuitOpen = true` and log the trip
- Step 4: At the top of `process()`, throw `IllegalStateException` when the circuit is open — the `Pipeline.execute` loop catches it, prints where the pipeline stopped, and returns null (fail fast)
- Step 5: Make `ModelInference` pathological on huge inputs: `sleep(2 * tokenCount)` when token count exceeds 100 — two runs trip the breaker deterministically
- Step 6: Run 3 normal samples, then 2 pathological ones (breaker trips), then a normal request (fails fast), then print metrics

### Code
```java
// File: src/com/aiengineering/lab06/PipelineMetricsWalkthrough.java
package com.aiengineering.lab06;

import java.util.*;

/**
 * Walkthrough: Netflix-style AI pipeline with per-stage timing metrics
 * and a circuit breaker that fails fast when a stage exceeds its
 * latency budget repeatedly. Mirrors the lab's Stage/TimedStage/Pipeline
 * design and fixes the metrics printing to use doubles.
 */
public class PipelineMetricsWalkthrough {

    @FunctionalInterface
    interface Stage<I, O> {
        O process(I input);
    }

    static class TimedStage<I, O> implements Stage<I, O> {
        private final String name;
        private final Stage<I, O> delegate;
        private final long latencyBudgetMicros;
        private final int slowThreshold;
        private long totalTime = 0;
        private int invocations = 0;
        private int consecutiveSlow = 0;
        private boolean circuitOpen = false;

        TimedStage(String name, Stage<I, O> delegate, long latencyBudgetMicros, int slowThreshold) {
            this.name = name;
            this.delegate = delegate;
            this.latencyBudgetMicros = latencyBudgetMicros;
            this.slowThreshold = slowThreshold;
        }

        @Override
        public O process(I input) {
            if (circuitOpen) {
                throw new IllegalStateException("circuit OPEN for stage '" + name + "' — failing fast");
            }
            long start = System.nanoTime();
            O result = delegate.process(input);
            long elapsedMicros = (System.nanoTime() - start) / 1000;
            totalTime += elapsedMicros;
            invocations++;
            if (elapsedMicros > latencyBudgetMicros) {
                consecutiveSlow++;
                System.out.println("    [" + name + "] SLOW: " + elapsedMicros + " us over budget " + latencyBudgetMicros + " us");
                if (consecutiveSlow >= slowThreshold) {
                    circuitOpen = true;
                    System.out.println("    [" + name + "] CIRCUIT OPEN after " + consecutiveSlow + " consecutive slow calls");
                }
            } else {
                consecutiveSlow = 0;
            }
            return result;
        }

        String getName() { return name; }
        double getAvgTimeMicros() { return invocations == 0 ? 0 : (double) totalTime / invocations; }
        int getInvocations() { return invocations; }
        boolean isCircuitOpen() { return circuitOpen; }
    }

    static class Pipeline {
        private final List<TimedStage<?, ?>> stages = new ArrayList<>();
        private final String name;

        Pipeline(String name) { this.name = name; }

        @SuppressWarnings("unchecked")
        <I, O> Pipeline addStage(String stageName, Stage<I, O> stage, long budgetMicros, int slowThreshold) {
            stages.add(new TimedStage<>(stageName, (Stage<Object, Object>) stage, budgetMicros, slowThreshold));
            return this;
        }

        @SuppressWarnings("unchecked")
        <T> T execute(T input) {
            System.out.println("  Pipeline \"" + name + "\" starting...");
            Object current = input;
            for (TimedStage<?, ?> s : stages) {
                TimedStage<Object, Object> stage = (TimedStage<Object, Object>) s;
                try {
                    current = stage.process(current);
                } catch (IllegalStateException e) {
                    System.out.println("  Pipeline stopped at stage '" + stage.getName() + "' — " + e.getMessage());
                    return null;
                }
            }
            return (T) current;
        }

        void printMetrics() {
            System.out.println("  Pipeline \"" + name + "\" metrics:");
            for (TimedStage<?, ?> s : stages) {
                System.out.printf("    %s: %d invocations, avg %.2f us, circuit %s%n",
                    s.getName(), s.getInvocations(), s.getAvgTimeMicros(),
                    s.isCircuitOpen() ? "OPEN" : "CLOSED");
            }
        }
    }

    static class TextPreprocessor implements Stage<String, String> {
        @Override
        public String process(String input) {
            return input.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
        }
    }

    static class Tokenizer implements Stage<String, List<String>> {
        @Override
        public List<String> process(String input) {
            return Arrays.asList(input.split("\\s+"));
        }
    }

    static class FeatureExtractor implements Stage<List<String>, Map<String, Double>> {
        @Override
        public Map<String, Double> process(List<String> tokens) {
            Map<String, Double> features = new HashMap<>();
            features.put("token_count", (double) tokens.size());
            features.put("avg_token_length", tokens.stream()
                .mapToInt(String::length).average().orElse(0));
            features.put("unique_ratio", (double) new HashSet<>(tokens).size() / tokens.size());
            long keywordCount = tokens.stream()
                .filter(t -> Set.of("ai", "ml", "model", "data", "train").contains(t))
                .count();
            features.put("keyword_density", (double) keywordCount / tokens.size());
            return features;
        }
    }

    static class ModelInference implements Stage<Map<String, Double>, Double> {
        @Override
        public Double process(Map<String, Double> features) {
            double tokenCount = features.getOrDefault("token_count", 0.0);
            if (tokenCount > 100) sleep((long) (tokenCount * 2)); // pathological input
            double score = tokenCount * 0.1
                + features.getOrDefault("avg_token_length", 0.0) * 0.3
                + features.getOrDefault("unique_ratio", 0.0) * 0.4
                + features.getOrDefault("keyword_density", 0.0) * 0.2
                - 0.5;
            return Math.max(0, Math.min(1, score));
        }
    }

    static class ResultFormatter implements Stage<Double, String> {
        @Override
        public String process(Double input) {
            String label = input < 0.3 ? "Low relevance" :
                           input < 0.6 ? "Medium relevance" :
                           input < 0.8 ? "High relevance" : "Very high relevance";
            return String.format("Score: %.4f — %s", input, label);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Walkthrough: Pipeline Metrics and Circuit Breaker ===\n");

        Pipeline pipeline = new Pipeline("Content Classifier")
            .addStage("Text Preprocessing", new TextPreprocessor(), 50_000, 2)
            .addStage("Tokenization", new Tokenizer(), 50_000, 2)
            .addStage("Feature Extraction", new FeatureExtractor(), 50_000, 2)
            .addStage("Model Inference", new ModelInference(), 50_000, 2)
            .addStage("Result Formatting", new ResultFormatter(), 50_000, 2);

        List<String> samples = Arrays.asList(
            "AI and ML models are trained on large datasets!",
            "The weather today is sunny and warm.",
            "Data scientists train machine learning models using AI techniques."
        );

        System.out.println("--- Normal traffic ---");
        for (int i = 0; i < samples.size(); i++) {
            System.out.println("\nInput " + (i + 1) + ": \"" + samples.get(i) + "\"");
            String result = pipeline.execute(samples.get(i));
            System.out.println("Output: " + result);
        }

        System.out.println("\n--- Pathological input (huge token count) ---");
        StringBuilder big = new StringBuilder("AI model ");
        for (int i = 0; i < 500; i++) big.append("data ");
        pipeline.execute(big.toString());  // slow call #1
        pipeline.execute(big.toString());  // slow call #2 -> circuit opens

        System.out.println("\n--- Next request fails fast ---");
        String result = pipeline.execute("Should never reach the model");
        System.out.println("Output: " + result);

        System.out.println("\n--- Pipeline Metrics ---");
        pipeline.printMetrics();

        System.out.println("\nWalkthrough complete.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
```

### Expected Output
(Timing values vary by machine; the structure is deterministic.)
```
=== Walkthrough: Pipeline Metrics and Circuit Breaker ===

--- Normal traffic ---

Input 1: "AI and ML models are trained on large datasets!"
  Pipeline "Content Classifier" starting...
Output: Score: 1.0000 — Very high relevance

Input 2: "The weather today is sunny and warm."
  Pipeline "Content Classifier" starting...
Output: Score: 1.0000 — Very high relevance

Input 3: "Data scientists train machine learning models using AI techniques."
  Pipeline "Content Classifier" starting...
Output: Score: 1.0000 — Very high relevance

--- Pathological input (huge token count) ---
  Pipeline "Content Classifier" starting...
    [Model Inference] SLOW: 1005001 us over budget 50000 us
  Pipeline "Content Classifier" starting...
    [Model Inference] SLOW: 1004434 us over budget 50000 us
    [Model Inference] CIRCUIT OPEN after 2 consecutive slow calls

--- Next request fails fast ---
  Pipeline "Content Classifier" starting...
  Pipeline stopped at stage 'Model Inference' — circuit OPEN for stage 'Model Inference' — failing fast
Output: null

--- Pipeline Metrics ---
  Pipeline "Content Classifier" metrics:
    Text Preprocessing: 6 invocations, avg 1254.17 us, circuit CLOSED
    Tokenization: 6 invocations, avg 299.00 us, circuit CLOSED
    Feature Extraction: 6 invocations, avg 2013.83 us, circuit CLOSED
    Model Inference: 5 invocations, avg 401896.60 us, circuit OPEN
    Result Formatting: 5 invocations, avg 3155.40 us, circuit CLOSED

Walkthrough complete.
```

### Company Evaluation
- Oracle: Instrumentation design: stage boundaries, metric aggregation, and breaker thresholds.
- Deloitte: Operational process: performance SLAs per stage, incident response, and reporting.
- Accenture: Engineering practice: profiling, breaker tuning, and load testing.
- PwC: Control framework: breaker threshold governance and availability risk analysis.
- Amazon: Scale: distributed stage metrics, fleet-wide breakers, and self-healing.

---

## Problem 2: Backpressure Between Stages — Company: Uber

### Interview Scenario
"You're at Uber on the dispatch ML team. The streaming pipeline ingests location events at 10k/s but the feature stage can only process 2k/s. Events pile up in memory until the process OOMs. Add a bounded queue between the stages with backpressure semantics."

### The Problem
1. Model a producer stage and a consumer stage with a bounded queue between them
2. Make the producer block when the queue is full — that is backpressure
3. Show the difference between unbounded (OOM risk) and bounded (throttled) behavior
4. Report queue depth and dropped/throttled counts

### Solution Walkthrough
- Step 1: The lab's `Pipeline` passes data by direct call; production pipelines put a bounded `ArrayBlockingQueue` between stages
- Step 2: Producer calls `queue.put()` (blocks when full) instead of `add()` (throws/overflows) — blocking is the backpressure signal
- Step 3: Consumer drains with `take()` in a loop; measure queue depth with `queue.size()`
- Step 4: Contrast: an unbounded `LinkedBlockingQueue` accepts everything and grows memory; a bounded queue makes the upstream slow down, protecting the process

### Code
```java
ArrayBlockingQueue<List<String>> tokens = new ArrayBlockingQueue<>(100);
// Producer (tokenizer side): put() blocks when the queue is full
new Thread(() -> {
    while (running) {
        tokens.put(tokenizer.process(rawEvent));   // blocks = backpressure
        produced++;
    }
}).start();
// Consumer (feature side): take() waits for work
new Thread(() -> {
    while (running) {
        List<String> t = tokens.take();
        Map<String, Double> features = featureExtractor.process(t);
        model.infer(features);
        consumed++;
    }
}).start();
System.out.printf("Queue depth: %d — producer blocked %d times, consumer lag %d events%n",
    tokens.size(), blockedCount, tokens.size());
```
Output: with the bounded queue, `Queue depth` stays ≤ 100 and the producer's `blockedCount` grows — the producer is throttled instead of the JVM OOMing. With an unbounded queue the same workload shows `Queue depth` climbing past 100k before the process dies.

### Company Evaluation
- Oracle: Queue design: bounded buffers, drop/block policy, and deadlock analysis.
- Deloitte: Process design: throughput planning, congestion management, and SLAs.
- Accenture: Methodology: load testing, bottleneck analysis, and capacity tuning.
- PwC: Risk controls: queue saturation monitoring, resilience testing, and audit.
- Amazon: Scale: distributed backpressure, stream semantics, and throttling patterns.

---

## Problem 3: Pipeline Config Versioning and Canary — Company: Stripe

### Interview Scenario
"You're at Stripe on the transaction-risk team. The fraud-scoring pipeline is getting a new feature-extraction stage. You must deploy it without switching the whole fleet at once — canary the pipeline configuration."

### The Problem
1. Represent a pipeline config as a versioned artifact: stage list + parameters
2. Route a percentage of traffic through the new config with a weight-based router
3. Compare quality outcomes (fraud scores on a labeled set) between configs
4. Promote or roll back based on the comparison

### Solution Walkthrough
- Step 1: Define `PipelineConfig(id, version, stages)` records — a config is the ordered stage list plus parameters, like the lab's builder chain captured as data
- Step 2: Reuse the weight-based `TrafficRouter` pattern from the deployment lab: 95% to config v1, 5% to v2
- Step 3: Tag every prediction with the config version; compare precision on the labeled set per version
- Step 4: If v2 precision >= v1 precision, promote to 100%; else roll back — the config registry keeps v1, and rollback is a routing change, not a redeploy

### Code
```java
record PipelineConfig(String id, String version, List<String> stages) {}

PipelineConfig v1 = new PipelineConfig("fraud-scoring", "v1", List.of("preprocess", "legacy-features", "infer"));
PipelineConfig v2 = new PipelineConfig("fraud-scoring", "v2", List.of("preprocess", "graph-features", "legacy-features", "infer"));

router.setWeight("fraud-scoring:v1", 95);
router.setWeight("fraud-scoring:v2", 5);
// ... run traffic, tag predictions with config version ...
double precV1 = precisionOf(v1);   // 0.812
double precV2 = precisionOf(v2);   // 0.841
System.out.printf("v1 precision=%.3f v2 precision=%.3f -> %s%n",
    precV1, precV2, precV2 >= precV1 ? "PROMOTE v2" : "ROLLBACK to v1");
```
Output: `v1 precision=0.812 v2 precision=0.841 -> PROMOTE v2`. The deploy then shifts weights to 100% v2 and the v1 config stays in the registry for instant rollback — pipeline changes get the same canary treatment as model changes, which is the lab's point that pipeline configs are versioned, deployable artifacts.

### Company Evaluation
- Oracle: Config design: schema versioning, validation, and compatibility checks.
- Deloitte: Change governance: config review, rollout process, and rollback procedures.
- Accenture: Release practice: canary methodology, staged rollout, and verification.
- PwC: Audit: config change records, approval trails, and compliance evidence.
- Amazon: Scale: config distribution consistency and fleet rollout automation.
