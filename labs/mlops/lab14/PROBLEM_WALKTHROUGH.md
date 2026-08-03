# Problem Walkthrough: AutoML Pipelines

## Problem 1: Hyperparameter Search on a Simulated Model — Company: Netflix

### Interview Scenario

> **Interviewer**: "Our content-recommendation retraining needs to pick hyperparameters without burning GPU-hours. We have a Java skeleton with a `HyperparameterSpace`, three search strategies — grid, random, and a simplified Bayesian optimizer — and a simulated accuracy function that scores any hyperparameter combination. The demo defines a space with `learning_rate` (log scale), `optimizer`, `batch_size`, and `dropout`, runs 375 grid combinations plus 30-trial random and Bayesian searches, and prints the best of each. One catch: the lab crashes on the first grid trial — your version must run clean."
>
> **Candidate**: "The crash is a type bug I can fix in one line, and the walkthrough must prove every printed accuracy comes from a real run."

### The Problem

1. Define a `HyperparameterSpace`: `learning_rate` in `[0.0001, 0.1]` on log scale, `optimizer` in `{adam, sgd, rmsprop}`, `batch_size` in `[16, 256]` linear, `dropout` in `[0.0, 0.5]` linear.
2. Implement `gridCombinations` — five values per numeric parameter (log-spaced or linear-spaced) crossed into a cartesian product of 5 × 3 × 5 × 5 = 375 combinations.
3. Score every combination with `simulateTraining` — a deterministic accuracy simulator with a learning-rate sweet spot near 0.003, a batch-size sweet spot near 90, and optimizer effects — then report the grid best.
4. Run random search (30 trials, 4-thread pool) and the simplified Bayesian optimizer (10 exploration trials, 20 local-refinement trials) on the same space.
5. Print a summary comparing the three strategies — and the whole transcript must be reproducible.

### Solution Walkthrough

1. **Design the space with scales, not ranges.** `addNumerical(name, min, max, scale)` records bounds plus a scale; `gridCombinations` computes five values: on `"log"` scale the points are `exp(logMin + t*(logMax - logMin))` for `t ∈ {0, 0.25, 0.5, 0.75, 1}` — so learning rate samples 0.0001, 0.0006, 0.0032, 0.0178, 0.1 — while linear parameters are evenly spaced (batch_size: 16, 76, 136, 196, 256). All values are rounded to 4 decimals, which keeps the printed `params` maps clean and the transcript stable.
2. **Build the cartesian product recursively.** The depth-first `cartesianProduct` walks the parameter keys in insertion order and appends a copy of the completed map at the leaves — 375 combinations for this space. The `LinkedHashMap` copies preserve key order, so every printed `params={...}` has the same shape.
3. **Fix the type bug before the first trial.** The lab's `gridCombinations` always emits `batch_size` as a rounded `Double`, but `simulateTraining` reads it with `(int) hp.get("batch_size")` — a `ClassCastException` on the very first trial. The walkthrough normalizes with `((Number) hp.getOrDefault("batch_size", 32)).intValue()`, which accepts either the Double from the grid or the Integer default. The lesson: a space whose values are `Object`s must be consumed with `Number` conversions at the boundary, never raw casts.
4. **Score with a meaningful simulator.** `simulateTraining` models accuracy as a smooth function: `-pow(log10(lr) + 2.5, 2) * 0.02` peaks at lr ≈ 0.003, `-pow(log(batch) - 4.5, 2) * 0.01` peaks near 90, hidden units add `log`-diminishing returns, dropout is best around 0.3, and adam beats rmsprop beats sgd. A small Gaussian noise term (deterministic under the seeded RNG) keeps the search honest — the surface has structure the optimizers can exploit.
5. **Grid search exhaustively, cheaply.** `gridSearch` evaluates all 375 combinations sequentially with `new Random(42)`, so the noise sequence is fixed; the best is the max-accuracy `TrialResult`. The point of the demo: exhaustive coverage over a small, coarse grid is the correctness baseline — 375 calls to a simulator that would be GPU-minutes in reality.
6. **Random search adds parallelism and scale.** `randomSearch` samples 30 points from the space (log/linear-aware `randomSample`) and evaluates them on a 4-thread pool — the same wall-clock as the grid with ~12× fewer trials. The walkthrough keeps the lab's per-trial `new Random(42)` (same noise draw for every trial) precisely because it makes the result deterministic regardless of thread scheduling.
7. **Bayesian optimization exploits the best region.** Phase 1 runs 10 uniform random trials; phase 2 takes the current best and perturbs numeric params with `nextGaussian() * 0.1 * range` (clamped to bounds) and flips categoricals with 30% probability — a simplified, honest stand-in for acquisition-function selection. The `rng` is shared and sequential, so the trajectory (and the final best) is reproducible.
8. **Verify against the compiled run.** The Expected Output below is the exact stdout of the walkthrough class — the grid best, random best, Bayesian best, and the summary lines all come from the actual run.

### Code

```java
package com.mlops.lab14;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class AutoMLWalkthrough {

    static class HyperparameterSpace {
        final Map<String, List<Object>> categorical = new LinkedHashMap<>();
        final Map<String, double[]> numerical = new LinkedHashMap<>();
        final Map<String, String> scales = new LinkedHashMap<>();

        HyperparameterSpace addCategorical(String name, Object... values) {
            categorical.put(name, Arrays.asList(values));
            return this;
        }

        HyperparameterSpace addNumerical(String name, double min, double max, String scale) {
            numerical.put(name, new double[]{min, max});
            scales.put(name, scale);
            return this;
        }

        List<Map<String, Object>> gridCombinations() {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, List<Object>> allParams = new LinkedHashMap<>();
            categorical.forEach((k, v) -> allParams.put(k, v));
            numerical.forEach((k, v) -> {
                List<Object> values = new ArrayList<>();
                double logMin = Math.log(v[0]);
                double logMax = Math.log(v[1]);
                for (int i = 0; i <= 4; i++) {
                    double t = i / 4.0;
                    double val = "log".equals(scales.get(k))
                            ? Math.exp(logMin + t * (logMax - logMin))
                            : v[0] + t * (v[1] - v[0]);
                    double rounded = Math.round(val * 10000.0) / 10000.0;
                    values.add(rounded);
                }
                allParams.put(k, values);
            });
            cartesianProduct(result, allParams, new ArrayList<>(allParams.keySet()), 0, new LinkedHashMap<>());
            return result;
        }

        private void cartesianProduct(List<Map<String, Object>> result, Map<String, List<Object>> params,
                                       List<String> keys, int idx, Map<String, Object> current) {
            if (idx == keys.size()) {
                result.add(new LinkedHashMap<>(current));
                return;
            }
            String key = keys.get(idx);
            for (Object val : params.get(key)) {
                current.put(key, val);
                cartesianProduct(result, params, keys, idx + 1, current);
            }
        }

        Map<String, Object> randomSample(Random rng) {
            Map<String, Object> sample = new LinkedHashMap<>();
            categorical.forEach((k, v) -> sample.put(k, v.get(rng.nextInt(v.size()))));
            numerical.forEach((k, v) -> {
                double val;
                if ("log".equals(scales.get(k))) {
                    val = Math.exp(Math.log(v[0]) + rng.nextDouble() * (Math.log(v[1]) - Math.log(v[0])));
                } else {
                    val = v[0] + rng.nextDouble() * (v[1] - v[0]);
                }
                sample.put(k, Math.round(val * 10000.0) / 10000.0);
            });
            return sample;
        }
    }

    static class TrialResult {
        final Map<String, Object> hyperparameters;
        final double metric;
        final long durationMs;

        TrialResult(Map<String, Object> hyperparameters, double metric, long durationMs) {
            this.hyperparameters = hyperparameters;
            this.metric = metric;
            this.durationMs = durationMs;
        }
    }

    static double simulateTraining(Map<String, Object> hp, Random rng) {
        double lr = (double) hp.getOrDefault("learning_rate", 0.01);
        int batchSize = ((Number) hp.getOrDefault("batch_size", 32)).intValue();
        int hiddenUnits = (int) hp.getOrDefault("hidden_units", 128);
        double dropout = (double) hp.getOrDefault("dropout", 0.2);
        String optimizer = (String) hp.getOrDefault("optimizer", "adam");

        double baseAccuracy = 0.85;
        double lrEffect = -Math.pow(Math.log10(lr) + 2.5, 2) * 0.02;
        double batchEffect = -Math.pow(Math.log(batchSize) - 4.5, 2) * 0.01;
        double hiddenEffect = Math.log(hiddenUnits) * 0.01;
        double dropoutEffect = dropout < 0.3 ? dropout * 0.05 : 0.015 - dropout * 0.05;
        double optEffect = switch (optimizer) {
            case "adam" -> 0.02;
            case "rmsprop" -> 0.01;
            case "sgd" -> -0.01;
            default -> 0.0;
        };
        double noise = rng.nextGaussian() * 0.005;
        double accuracy = baseAccuracy + lrEffect + batchEffect + hiddenEffect + dropoutEffect + optEffect + noise;
        return Math.min(1.0, Math.max(0.5, accuracy));
    }

    static List<TrialResult> gridSearch(HyperparameterSpace space) {
        List<Map<String, Object>> combinations = space.gridCombinations();
        System.out.printf("Grid search: %d combinations%n", combinations.size());
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);
        for (Map<String, Object> hp : combinations) {
            double metric = simulateTraining(hp, rng);
            results.add(new TrialResult(hp, metric, 0));
        }
        return results;
    }

    static List<TrialResult> randomSearch(HyperparameterSpace space, int nTrials) {
        System.out.printf("Random search: %d trials%n", nTrials);
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<TrialResult>> futures = new ArrayList<>();

        for (int i = 0; i < nTrials; i++) {
            Map<String, Object> hp = space.randomSample(rng);
            futures.add(executor.submit(() -> {
                double metric = simulateTraining(hp, new Random(42));
                return new TrialResult(hp, metric, 0);
            }));
        }
        for (Future<TrialResult> f : futures) {
            try { results.add(f.get()); } catch (Exception e) { e.printStackTrace(); }
        }
        executor.shutdown();
        return results;
    }

    static List<TrialResult> bayesianOptimization(HyperparameterSpace space, int nTrials) {
        System.out.printf("Bayesian optimization: %d trials%n", nTrials);
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);

        for (int i = 0; i < nTrials / 3; i++) {
            Map<String, Object> hp = space.randomSample(rng);
            double metric = simulateTraining(hp, rng);
            results.add(new TrialResult(hp, metric, 100));
        }

        for (int i = 0; i < nTrials - nTrials / 3; i++) {
            TrialResult best = results.stream()
                    .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
            Map<String, Object> hp = new LinkedHashMap<>(best.hyperparameters);
            for (Map.Entry<String, double[]> entry : space.numerical.entrySet()) {
                double range = entry.getValue()[1] - entry.getValue()[0];
                double perturbation = rng.nextGaussian() * range * 0.1;
                double val = ((Number) hp.get(entry.getKey())).doubleValue() + perturbation;
                val = Math.max(entry.getValue()[0], Math.min(entry.getValue()[1], val));
                hp.put(entry.getKey(), Math.round(val * 10000.0) / 10000.0);
            }
            for (Map.Entry<String, List<Object>> entry : space.categorical.entrySet()) {
                if (rng.nextDouble() < 0.3) {
                    hp.put(entry.getKey(), entry.getValue().get(rng.nextInt(entry.getValue().size())));
                }
            }
            double metric = simulateTraining(hp, rng);
            results.add(new TrialResult(hp, metric, 100));
        }
        return results;
    }

    public static void main(String[] args) {
        System.out.println("=== AutoML Pipelines ===\n");

        HyperparameterSpace space = new HyperparameterSpace()
                .addNumerical("learning_rate", 0.0001, 0.1, "log")
                .addCategorical("optimizer", "adam", "sgd", "rmsprop")
                .addNumerical("batch_size", 16, 256, "linear")
                .addNumerical("dropout", 0.0, 0.5, "linear");

        System.out.println("--- Grid Search ---");
        List<TrialResult> gridResults = gridSearch(space);
        TrialResult bestGrid = gridResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestGrid.metric, bestGrid.hyperparameters);

        System.out.println("--- Random Search ---");
        List<TrialResult> randomResults = randomSearch(space, 30);
        TrialResult bestRandom = randomResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestRandom.metric, bestRandom.hyperparameters);

        System.out.println("--- Bayesian Optimization ---");
        List<TrialResult> bayesResults = bayesianOptimization(space, 30);
        TrialResult bestBayes = bayesResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestBayes.metric, bestBayes.hyperparameters);

        System.out.println("=== Summary ===");
        System.out.printf("Grid search:    best=%.4f (%d trials)%n",
                bestGrid.metric, gridResults.size());
        System.out.printf("Random search: best=%.4f (30 trials)%n", bestRandom.metric);
        System.out.printf("Bayesian opt:  best=%.4f (30 trials)%n", bestBayes.metric);
    }
}
```

### Expected Output

```
=== AutoML Pipelines ===

--- Grid Search ---
Grid search: 375 combinations
Best: accuracy=0.9335, params={optimizer=adam, learning_rate=0.0032, batch_size=76.0, dropout=0.25}

--- Random Search ---
Random search: 30 trials
Best: accuracy=0.9313, params={optimizer=adam, learning_rate=0.002, batch_size=95.7578, dropout=0.1576}

--- Bayesian Optimization ---
Bayesian optimization: 30 trials
Best: accuracy=0.9353, params={optimizer=adam, learning_rate=0.0026, batch_size=91.3899, dropout=0.291}

=== Summary ===
Grid search:    best=0.9335 (375 trials)
Random search: best=0.9313 (30 trials)
Bayesian opt:  best=0.9353 (30 trials)
```

*(Bug fix note: the lab's `simulateTraining` casts `batch_size` with `(int) hp.get(...)`, but the grid emits rounded `Double`s — the lab crashes with `ClassCastException` on the very first grid trial. The walkthrough reads it as `((Number) ...).intValue()`; every number above is captured from the fixed class's compiled run.)*

## Problem 2: Search Strategy Comparison — Company: Datadog

### The Problem

Grid search found 0.9335 in 375 trials, random search found 0.9313 in 30 trials, and Bayesian optimization found 0.9353 in 30 trials. Explain to a stakeholder how 30 trials can match or beat 375 — and when each strategy is the right call.

### Solution Walkthrough

1. **Grid search samples a coarse lattice, not the whole space.** The grid's five values per numeric parameter form a 5 × 3 × 5 × 5 lattice — 375 points, but only 5 distinct values per axis, so the surface is probed along a sparse grid. It lands near the learning-rate sweet spot (the t=0.5 log sample is 0.0032, close to the optimum near 0.003) and finds a strong 0.9335 — but it needed 12× the trials of the other methods to get there.
2. **Random search spreads points in the true volume.** Because `randomSample` draws continuous values from the space (log-aware for `learning_rate`), 30 random points cover the promising region with unique values on every axis — 0.9313, within 0.2 points of the 375-trial grid. This is the classic Bergstra & Bengio result: per trial, random search dominates grid search in medium-dimension spaces, because grid points re-combine the same axis values while random points explore genuinely new regions.
3. **Bayesian optimization concentrates near the best.** The two-phase structure — 10 uniform trials, then 20 perturbations of the running best — refines toward the optimum and finds 0.9353, the best of all three, in 30 trials. The refinement mechanism (Gaussian perturbation of 10% of each numeric range, clamped) is a simplified stand-in for acquisition-function sampling, but it demonstrates the principle: spend trials where the surface is promising, not where it is known-bad.
4. **Map strategies to budgets.** Grid: the correctness baseline for tiny spaces (2-3 params, few values) where the full lattice is affordable and the surface should be catalogued. Random: the default for medium budgets — robust, embarrassingly parallel, no assumptions about the surface. Bayesian: the right choice when trials are expensive (GPU-hours) and the surface is smooth enough to exploit — with the caveat that true Bayesian optimization's sequential surrogate fitting is harder to parallelize, which the lab's simplified version trades away for determinism and simplicity.

## Problem 3: Reproducible AutoML — Company: Strava

### The Problem

The training pipeline's nightly hyperparameter search reports a different best configuration every night, and model cards (Lab 11) can't cite a stable recipe. The search code is the lab's — `new Random(42)` in three places, plus a 4-thread pool. Find the nondeterminism and fix the workflow.

### Solution Walkthrough

1. **Find the seeded-but-misused RNGs.** Grid and Bayesian share one sequential `Random(42)` — deterministic. Random search *samples* with the seeded `rng`, but each trial's noise comes from a fresh `new Random(42)` inside the lambda — deterministic in isolation, but the *order* in which the four pool threads draw is irrelevant here only because each trial has its own RNG. The real hazards: any `new Random()` without a seed, and any trial that shares one RNG across threads — both poison reproducibility even when the model is fixed.
2. **Carry seeds through the pipeline.** The walkthrough's discipline — every RNG seeded, thread-local per trial, noise attached to the trial's index — makes the search a pure function of the seed; the nightly run becomes reproducible by construction, and the model card can cite `search_seed=42` alongside the winning params.
3. **Persist every trial, not just the best.** The gap in the lab: `gridSearch` returns only the winner; a reproducible workflow records all 375/30/30 trials (params, metric, seed, timestamp) to the experiment store — Lab 02's tracking — so the 'best' is a query over an immutable record, and the card's claim is auditable.
4. **Make the summary the contract.** The nightly report should fail loudly if the best trial changes for a fixed seed (a code or data change), and the `=== Summary ===` block is the natural golden-output for that check — the same golden-file discipline as the walkthrough's Expected Output, applied to the production search.
