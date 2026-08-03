# Problem Walkthrough: Experiment Tracking with MLflow

## Problem 1: Hyperparameter Sweep Tracking for the Spark Engine — Company: Databricks
### Interview Scenario
"You're at Databricks building an MLOps library for customers who run MLflow in-house. Your own recommendation team runs a 12-combination hyperparameter sweep (learning rate × epochs × batch size) and keeps losing track of which run produced which accuracy — the training script prints numbers to stdout and nobody writes them down. Build a tracking layer, in the spirit of the lab's `MlflowTrackingClient`, that records parameters and final metrics per run and automatically selects the best run — but with an in-memory backend so the interview demo needs no MLflow server."

### The Problem
1. Create an experiment and 12 runs (3 learning rates × 2 epoch counts × 2 batch sizes) exactly like the lab's sweep.
2. Log `learning_rate`, `epochs`, and `batch_size` as parameters on every run before training.
3. Record the `final_accuracy` metric per run, computed by a deterministic training simulation.
4. Select and report the best run by `final_accuracy`.
5. Keep the tracking API structurally identical to the lab's `MlflowTrackingClient` (createExperiment / createRun / logParam / logMetric / setTerminated) so swapping in the real REST client is a drop-in change.

### Solution Walkthrough
- Step 1: Model the backend as `InMemoryMlflow` — an `experiments` list and a `runs` map keyed by generated `run_XXXX` ids, mirroring the lab's `MlflowTrackingClient` method-for-method but without HTTP.
- Step 2: Reuse the lab's `simulateTraining(double lr, int epochs, int batchSize)` — seeded `new Random(42)`, so final accuracy is a pure function of the hyperparameters and the walkthrough output is reproducible.
- Step 3: Iterate the same nested loops as the lab — `learningRates = {0.001, 0.01, 0.1}`, `epochsList = {10, 20}`, `batchSizes = {16, 32}` — producing the same 12 runs.
- Step 4: For each run, log the three parameters *before* training, then `final_accuracy` at step `epochs`, matching the lab's logging order and best-practice guidance.
- Step 5: Track the argmax of `final_accuracy` as runs are recorded, the way a real model-selection job would call the tracking API's search to find the best run.
- Step 6: Print one line per run and the best-run summary; note in the console that the real deployment swaps the in-memory store for `postJson` calls against `/api/2.0/mlflow/...` on `http://localhost:5000`.

### Code
```java
package com.mlops.lab02;

import java.util.*;

public class ExperimentTrackingWalkthrough {

    static class Run {
        final String runId;
        final Map<String, String> params = new LinkedHashMap<>();
        final Map<String, List<double[]>> metrics = new LinkedHashMap<>();

        Run(String runId) { this.runId = runId; }
    }

    static class InMemoryMlflow {
        final String trackingUri;
        final List<String> experiments = new ArrayList<>();
        final Map<String, Run> runs = new LinkedHashMap<>();
        int runCounter = 0;

        InMemoryMlflow(String trackingUri) { this.trackingUri = trackingUri; }

        String createExperiment(String name) {
            experiments.add(name);
            return String.valueOf(experiments.size());
        }

        String createRun(String experimentId) {
            String runId = "run_" + String.format("%04d", ++runCounter);
            runs.put(runId, new Run(runId));
            return runId;
        }

        void logParam(String runId, String key, String value) {
            runs.get(runId).params.put(key, value);
        }

        void logMetric(String runId, String key, double value, long step) {
            runs.get(runId).metrics.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(new double[]{step, value});
        }

        void setTerminated(String runId, String status) {
            // Recorded implicitly; the real client POSTs to /api/2.0/mlflow/runs/update
        }
    }

    static double simulateTraining(double lr, int epochs, int batchSize) {
        double accuracy = 0.5;
        Random rng = new Random(42);
        for (int epoch = 0; epoch < epochs; epoch++) {
            double improvement = lr * (0.8 - accuracy) * 0.1 + (rng.nextDouble() - 0.5) * 0.02;
            accuracy = Math.min(1.0, accuracy + improvement);
        }
        return accuracy;
    }

    public static void main(String[] args) {
        System.out.println("=== Experiment Tracking with MLflow (in-memory) ===\n");

        InMemoryMlflow client = new InMemoryMlflow("http://localhost:5000");
        String expId = client.createExperiment("Java_ML_Experiment");
        System.out.println("Created experiment: Java_ML_Experiment (id=" + expId + ")\n");

        double[] learningRates = {0.001, 0.01, 0.1};
        int[] epochsList = {10, 20};
        int[] batchSizes = {16, 32};

        String bestRun = null;
        double bestAccuracy = 0.0;

        for (double lr : learningRates) {
            for (int epochs : epochsList) {
                for (int batchSize : batchSizes) {
                    String runId = client.createRun(expId);
                    client.logParam(runId, "learning_rate", String.valueOf(lr));
                    client.logParam(runId, "epochs", String.valueOf(epochs));
                    client.logParam(runId, "batch_size", String.valueOf(batchSize));
                    double finalAccuracy = simulateTraining(lr, epochs, batchSize);
                    client.logMetric(runId, "final_accuracy", finalAccuracy, epochs);
                    client.setTerminated(runId, "FINISHED");
                    System.out.printf("Run %s: lr=%.3f, epochs=%d, batch=%d -> final_accuracy=%.4f%n",
                            runId, lr, epochs, batchSize, finalAccuracy);
                    if (finalAccuracy > bestAccuracy) {
                        bestAccuracy = finalAccuracy;
                        bestRun = runId;
                    }
                }
            }
        }

        System.out.printf("%nBest run: %s with final_accuracy=%.4f%n", bestRun, bestAccuracy);
        System.out.println("Best parameters: " + client.runs.get(bestRun).params);
        System.out.println("In-memory store — production swaps in the REST client against " + client.trackingUri);
    }
}
```

### Expected Output
```
=== Experiment Tracking with MLflow (in-memory) ===

Created experiment: Java_ML_Experiment (id=1)

Run run_0001: lr=0.001, epochs=10, batch=16 -> final_accuracy=0.5094
Run run_0002: lr=0.001, epochs=10, batch=32 -> final_accuracy=0.5094
Run run_0003: lr=0.001, epochs=20, batch=16 -> final_accuracy=0.5109
Run run_0004: lr=0.001, epochs=20, batch=32 -> final_accuracy=0.5109
Run run_0005: lr=0.010, epochs=10, batch=16 -> final_accuracy=0.5121
Run run_0006: lr=0.010, epochs=10, batch=32 -> final_accuracy=0.5121
Run run_0007: lr=0.010, epochs=20, batch=16 -> final_accuracy=0.5161
Run run_0008: lr=0.010, epochs=20, batch=32 -> final_accuracy=0.5161
Run run_0009: lr=0.100, epochs=10, batch=16 -> final_accuracy=0.5374
Run run_0010: lr=0.100, epochs=10, batch=32 -> final_accuracy=0.5374
Run run_0011: lr=0.100, epochs=20, batch=16 -> final_accuracy=0.5632
Run run_0012: lr=0.100, epochs=20, batch=32 -> final_accuracy=0.5632

Best run: run_0011 with final_accuracy=0.5632
Best parameters: {learning_rate=0.1, epochs=20, batch_size=16}
In-memory store — production swaps in the REST client against http://localhost:5000
```

---

## Problem 2: Reproducibility Tagging — Company: Uber
### Interview Scenario
"You're at Uber. A data scientist can't reproduce a 0.87-accuracy run from last quarter: nobody recorded the code version, the dataset version, or the seed. Add environment capture to the tracking client so every run carries its own provenance."

### The Problem
1. Capture the Java environment (JVM properties) as run metadata at `createRun` time.
2. Record a git commit SHA and dataset version as parameters.
3. Log them before any training metric, per the lab's best practice.

### Solution Walkthrough
- Step 1: Extend `InMemoryMlflow.logParam` usage: after `createRun`, immediately log `git_commit`, `dataset_version`, and `random_seed`.
- Step 2: Use `System.getProperties()` to surface `java.version` and `os.arch` — the interview notes' Java-specific reproducibility trick.
- Step 3: Print the captured environment with the run line so the demo shows provenance beside the metric.

### Code
```java
String runId = client.createRun(expId);
client.logParam(runId, "git_commit", "a3f9c21");
client.logParam(runId, "dataset_version", "events_v2026-08-01");
client.logParam(runId, "random_seed", "42");
String javaVersion = System.getProperty("java.version");
client.logParam(runId, "java_version", javaVersion);
double finalAccuracy = simulateTraining(lr, epochs, batchSize);
client.logMetric(runId, "final_accuracy", finalAccuracy, epochs);
System.out.printf("Run %s (git=%s, data=%s, java=%s): final_accuracy=%.4f%n",
        runId, "a3f9c21", "events_v2026-08-01", javaVersion, finalAccuracy);
```
### Expected Output
```
Run run_0005 (git=a3f9c21, data=events_v2026-08-01, java=25.0.2): final_accuracy=0.5121
```

---

## Problem 3: Time-Series Metric Replay — Company: Airbnb
### Interview Scenario
"You're at Airbnb. Your loss curve for a big training run was never persisted, only printed. Rebuild the per-epoch metric log so loss is queryable at any step, like MLflow's `log-metric` with `step`."

### The Problem
1. Log `loss` and `accuracy` at every epoch, keyed by step.
2. Provide a lookup returning the most recent metric at or before a step.
3. Print the loss curve tail (epochs 18-20) as proof the log works.

### Solution Walkthrough
- Step 1: Loop epochs 1..20, appending `new double[]{step, loss}` to the run's `loss` metric series — the same shape `logMetric(runId, key, value, step)` stores server-side.
- Step 2: Implement `metricAtOrBefore(run, key, step)`: scan the series for the last entry with `step <= target` — the point-in-time pattern from LeetCode 981.
- Step 3: Print the final three points; the values are deterministic because the training simulation is seeded.

### Code
```java
// Per-epoch logging with step keys
Random rng = new Random(42);
double accuracy = 0.5;
for (int epoch = 1; epoch <= 20; epoch++) {
    double improvement = 0.01 * (0.85 - accuracy) * 0.1 + (rng.nextDouble() - 0.5) * 0.01;
    accuracy = Math.min(1.0, Math.max(0.0, accuracy + improvement));
    double loss = Math.pow(1.0 - accuracy, 2) + 0.1 * rng.nextDouble();
    client.logMetric(runId, "accuracy", accuracy, epoch);
    client.logMetric(runId, "loss", loss, epoch);
}

// Point-in-time lookup: most recent loss at or before step 18
double lossAt18 = client.runs.get(runId).metrics.get("loss").stream()
        .filter(m -> m[0] <= 18)
        .max(Comparator.comparingDouble(m -> m[0]))
        .orElse(new double[]{0, 0})[1];
System.out.printf("Loss at step 18 (or before): %.4f%n", lossAt18);
```
### Expected Output
```
Loss at step 18 (or before): 0.2711
```
