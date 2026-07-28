package com.mlops.lab14;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * AutoML Pipelines — Lab 14.
 * <p>
 * Demonstrates hyperparameter tuning and neural architecture search concepts.
 * Implements grid search, random search, and a simplified Bayesian optimization
 * for hyperparameter optimization. Includes simulated model training evaluation.
 */
public class AutoMLLab {

    /** Defines a hyperparameter space. */
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

        /** Generates all grid combinations (for small spaces). */
        List<Map<String, Object>> gridCombinations() {
            List<Map<String, Object>> result = new ArrayList<>();
            // Collect all parameter options
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
                    // Round to reasonable precision
                    double rounded = Math.round(val * 10000.0) / 10000.0;
                    values.add(rounded);
                }
                allParams.put(k, values);
            });
            // Cartesian product
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

        /** Generates random samples from the space. */
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

    /** Trial result for hyperparameter evaluation. */
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

    /** Simulates model training and returns a metric (higher is better). */
    static double simulateTraining(Map<String, Object> hp, Random rng) {
        double lr = (double) hp.getOrDefault("learning_rate", 0.01);
        int batchSize = (int) hp.getOrDefault("batch_size", 32);
        int hiddenUnits = (int) hp.getOrDefault("hidden_units", 128);
        double dropout = (double) hp.getOrDefault("dropout", 0.2);
        String optimizer = (String) hp.getOrDefault("optimizer", "adam");

        // Simulate accuracy as a function of hyperparameters
        double baseAccuracy = 0.85;
        double lrEffect = -Math.pow(Math.log10(lr) + 2.5, 2) * 0.02; // sweet spot around 0.003
        double batchEffect = -Math.pow(Math.log(batchSize) - 4.5, 2) * 0.01; // sweet spot around 90
        double hiddenEffect = Math.log(hiddenUnits) * 0.01; // more is better, diminishing returns
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

    /** Grid search tuner. */
    static List<TrialResult> gridSearch(HyperparameterSpace space) {
        List<Map<String, Object>> combinations = space.gridCombinations();
        System.out.printf("Grid search: %d combinations%n", combinations.size());
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);
        for (Map<String, Object> hp : combinations) {
            Instant start = Instant.now();
            double metric = simulateTraining(hp, rng);
            long duration = Duration.between(start, Instant.now()).toMillis();
            results.add(new TrialResult(hp, metric, duration));
        }
        return results;
    }

    /** Random search tuner. */
    static List<TrialResult> randomSearch(HyperparameterSpace space, int nTrials) {
        System.out.printf("Random search: %d trials%n", nTrials);
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<TrialResult>> futures = new ArrayList<>();

        for (int i = 0; i < nTrials; i++) {
            Map<String, Object> hp = space.randomSample(rng);
            futures.add(executor.submit(() -> {
                Instant start = Instant.now();
                double metric = simulateTraining(hp, new Random(42));
                long duration = Duration.between(start, Instant.now()).toMillis();
                return new TrialResult(hp, metric, duration);
            }));
        }
        for (Future<TrialResult> f : futures) {
            try { results.add(f.get()); } catch (Exception e) { e.printStackTrace(); }
        }
        executor.shutdown();
        return results;
    }

    /** Simplified Bayesian optimization using random sampling with local refinement. */
    static List<TrialResult> bayesianOptimization(HyperparameterSpace space, int nTrials) {
        System.out.printf("Bayesian optimization: %d trials%n", nTrials);
        List<TrialResult> results = new ArrayList<>();
        Random rng = new Random(42);

        // Phase 1: Random exploration (initial design)
        for (int i = 0; i < nTrials / 3; i++) {
            Map<String, Object> hp = space.randomSample(rng);
            double metric = simulateTraining(hp, rng);
            results.add(new TrialResult(hp, metric, 100));
        }

        // Phase 2: Exploit best region with local perturbations
        for (int i = 0; i < nTrials - nTrials / 3; i++) {
            // Find best so far
            TrialResult best = results.stream()
                    .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
            Map<String, Object> hp = new LinkedHashMap<>(best.hyperparameters);
            // Local perturbation
            for (Map.Entry<String, double[]> entry : space.numerical.entrySet()) {
                double range = entry.getValue()[1] - entry.getValue()[0];
                double perturbation = rng.nextGaussian() * range * 0.1;
                double val = ((Number) hp.get(entry.getKey())).doubleValue() + perturbation;
                val = Math.max(entry.getValue()[0], Math.min(entry.getValue()[1], val));
                hp.put(entry.getKey(), Math.round(val * 10000.0) / 10000.0);
            }
            for (Map.Entry<String, List<Object>> entry : space.categorical.entrySet()) {
                if (rng.nextDouble() < 0.3) { // 30% chance to change category
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

        // Define hyperparameter space
        HyperparameterSpace space = new HyperparameterSpace()
                .addNumerical("learning_rate", 0.0001, 0.1, "log")
                .addCategorical("optimizer", "adam", "sgd", "rmsprop")
                .addNumerical("batch_size", 16, 256, "linear")
                .addNumerical("dropout", 0.0, 0.5, "linear");

        // Grid Search
        System.out.println("--- Grid Search ---");
        List<TrialResult> gridResults = gridSearch(space);
        TrialResult bestGrid = gridResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestGrid.metric, bestGrid.hyperparameters);

        // Random Search
        System.out.println("--- Random Search ---");
        List<TrialResult> randomResults = randomSearch(space, 30);
        TrialResult bestRandom = randomResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestRandom.metric, bestRandom.hyperparameters);

        // Bayesian Optimization
        System.out.println("--- Bayesian Optimization ---");
        List<TrialResult> bayesResults = bayesianOptimization(space, 30);
        TrialResult bestBayes = bayesResults.stream()
                .max(Comparator.comparingDouble(r -> r.metric)).orElseThrow();
        System.out.printf("Best: accuracy=%.4f, params=%s%n%n",
                bestBayes.metric, bestBayes.hyperparameters);

        // Summary
        System.out.println("=== Summary ===");
        System.out.printf("Grid search:    best=%.4f (%d trials)%n",
                bestGrid.metric, gridResults.size());
        System.out.printf("Random search: best=%.4f (30 trials)%n", bestRandom.metric);
        System.out.printf("Bayesian opt:  best=%.4f (30 trials)%n", bestBayes.metric);
    }
}
