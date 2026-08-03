# Problem Walkthrough: ML Pipeline Orchestration

## Problem 1: Stabilize the Nightly Recommendation Training DAG — Company: Netflix
### Interview Scenario
"You're at Netflix. The nightly recommendation model training pipeline is a six-task DAG — ingest, validate, feature engineering, train, evaluate, deploy — and it fails two or three nights a week. S3 throttles the ingest step transiently, and GPU preemption kills the training step once or twice per run. Worse: when `train_model` fails, downstream tasks still execute against stale artifacts, and the on-call engineer restarts the whole pipeline from scratch every time. They need a pipeline that retries transient failures automatically, never runs a task whose dependency failed, and reports exactly what happened."

### The Problem
1. Model the six tasks as a DAG with explicit `dependsOn` edges: ingest → validate → featurize → train → evaluate → deploy.
2. Resolve a correct execution order with topological sort, and fail loudly if the graph contains a cycle.
3. Give ingest and train automatic retries (2 each) so transient S3 throttling and GPU preemption self-heal.
4. Skip every downstream task when a dependency fails — no task may run against a failed dependency's outputs.
5. Print a per-task summary showing status and retry counts so the on-call engineer can see what happened without grepping logs.

### Solution Walkthrough
- Step 1: Reuse the lab's two core abstractions — `PipelineTask` (name, dependencies, action, `maxRetries`, status machine `PENDING → RUNNING → SUCCESS/FAILED`) and `PipelineDAG` (a `LinkedHashMap<String, PipelineTask>` plus `addTask`).
- Step 2: Build the graph with the fluent `dependsOn(String taskName)` API exactly as the lab does for `ML Training Pipeline`, so the edge set is readable at a glance.
- Step 3: Execute via `topologicalSort()` — the lab's Kahn's algorithm with in-degree map, adjacency list, and queue, which throws `IllegalStateException("Cycle detected in pipeline DAG")` if `sorted.size() != tasks.size()`.
- Step 4: Before each task, check `task.dependencies.stream().allMatch(d -> tasks.get(d).status == TaskStatus.SUCCESS)`; if false, mark the task `FAILED` with `errorMessage = "Dependency failed"` and print `Skipped — dependency failed` instead of running it.
- Step 5: Wrap the retry loop around `execute()`: re-invoke while status is neither `SUCCESS` nor `FAILED`, since `execute()` increments `retryCount`, prints `Failed (attempt X/Y): ... — retrying...` when budget remains, and flips to `FAILED` only after `maxRetries` is exhausted.
- Step 6: Make the two flaky steps deterministic for the walkthrough: `data_ingest` throws once ("S3 throttled") before succeeding on attempt 2; `train_model` throws twice ("GPU preemption") before succeeding on attempt 3 — this exercises retry semantics without random failure.
- Step 7: Give `deploy_model` `maxRetries = 0`, mirroring the lab's reasoning that side-effecting steps must not be blindly retried.
- Step 8: Close with `printSummary()`, which renders each task as `OK`/`FAIL`/`RUN`/`PEND` and annotates `(retried N time(s))` for tasks that recovered — the lab's production-readiness reporting.
- Step 9: Note the production upgrade path from the lab's INTERVIEW notes: exponential backoff between retries, PagerDuty/Slack alerting on the retry-exhaustion path, and checkpoint resume by persisting per-task terminal status.

### Code
```java
package com.mlops.lab01;

import java.util.*;

public class RecommendationPipelineWalkthrough {

    enum TaskStatus { PENDING, RUNNING, SUCCESS, FAILED }

    static class PipelineTask {
        final String name;
        final List<String> dependencies = new ArrayList<>();
        final Runnable action;
        final int maxRetries;
        TaskStatus status = TaskStatus.PENDING;
        int retryCount = 0;
        String errorMessage;

        PipelineTask(String name, Runnable action, int maxRetries) {
            this.name = name;
            this.action = action;
            this.maxRetries = maxRetries;
        }

        PipelineTask dependsOn(String taskName) {
            dependencies.add(taskName);
            return this;
        }

        void execute() {
            status = TaskStatus.RUNNING;
            System.out.printf("[%s] Starting (attempt %d/%d)%n", name, retryCount + 1, maxRetries + 1);
            try {
                action.run();
                status = TaskStatus.SUCCESS;
                System.out.printf("[%s] Completed successfully%n", name);
            } catch (Exception e) {
                retryCount++;
                errorMessage = e.getMessage();
                if (retryCount <= maxRetries) {
                    System.out.printf("[%s] Failed (attempt %d/%d): %s — retrying...%n",
                            name, retryCount, maxRetries + 1, e.getMessage());
                } else {
                    status = TaskStatus.FAILED;
                    System.out.printf("[%s] Failed after %d retries: %s%n",
                            name, maxRetries, e.getMessage());
                }
            }
        }
    }

    static class PipelineDAG {
        final String name;
        final Map<String, PipelineTask> tasks = new LinkedHashMap<>();

        PipelineDAG(String name) { this.name = name; }

        void addTask(PipelineTask task) { tasks.put(task.name, task); }

        List<PipelineTask> topologicalSort() {
            Map<String, Integer> inDegree = new HashMap<>();
            Map<String, List<String>> adjacency = new HashMap<>();
            for (String t : tasks.keySet()) {
                inDegree.putIfAbsent(t, 0);
                adjacency.putIfAbsent(t, new ArrayList<>());
            }
            for (PipelineTask task : tasks.values()) {
                for (String dep : task.dependencies) {
                    adjacency.computeIfAbsent(dep, k -> new ArrayList<>()).add(task.name);
                    inDegree.merge(task.name, 1, Integer::sum);
                }
            }
            Queue<String> queue = new LinkedList<>();
            for (String t : inDegree.keySet()) {
                if (inDegree.get(t) == 0) queue.add(t);
            }
            List<PipelineTask> sorted = new ArrayList<>();
            while (!queue.isEmpty()) {
                String node = queue.poll();
                sorted.add(tasks.get(node));
                for (String neighbor : adjacency.getOrDefault(node, List.of())) {
                    inDegree.merge(neighbor, -1, Integer::sum);
                    if (inDegree.get(neighbor) == 0) queue.add(neighbor);
                }
            }
            if (sorted.size() != tasks.size()) {
                throw new IllegalStateException("Cycle detected in pipeline DAG");
            }
            return sorted;
        }

        void execute() {
            System.out.printf("=== Executing pipeline: %s ===%n", name);
            for (PipelineTask task : topologicalSort()) {
                boolean depsOk = task.dependencies.stream()
                        .allMatch(d -> tasks.get(d).status == TaskStatus.SUCCESS);
                if (!depsOk) {
                    task.status = TaskStatus.FAILED;
                    task.errorMessage = "Dependency failed";
                    System.out.printf("[%s] Skipped — dependency failed%n", task.name);
                    continue;
                }
                while (task.status != TaskStatus.SUCCESS && task.status != TaskStatus.FAILED) {
                    task.execute();
                }
            }
            printSummary();
        }

        void printSummary() {
            System.out.println("\n--- Pipeline Summary ---");
            for (PipelineTask task : tasks.values()) {
                String statusStr = switch (task.status) {
                    case SUCCESS -> "OK";
                    case FAILED -> "FAIL";
                    case RUNNING -> "RUN";
                    case PENDING -> "PEND";
                };
                System.out.printf("  %s: %s", task.name, statusStr);
                if (task.status == TaskStatus.FAILED) {
                    System.out.printf(" — %s", task.errorMessage);
                }
                if (task.retryCount > 0 && task.status == TaskStatus.SUCCESS) {
                    System.out.printf(" (retried %d time(s))", task.retryCount);
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        PipelineDAG dag = new PipelineDAG("Netflix Rec Training DAG");

        final int[] ingestAttempts = {0};
        PipelineTask ingest = new PipelineTask("data_ingest", () -> {
            ingestAttempts[0]++;
            if (ingestAttempts[0] == 1) throw new RuntimeException("S3 throttled");
            System.out.println("  [data_ingest] 1.2M rows ingested from s3://events/2026-08-03/");
        }, 2);

        PipelineTask validate = new PipelineTask("data_validate", () -> {
            System.out.println("  [data_validate] schema valid, null rate 0.4%");
        }, 1);

        PipelineTask featurize = new PipelineTask("feature_eng", () -> {
            System.out.println("  [feature_eng] 42 features written to feature_store");
        }, 2);

        final int[] trainAttempts = {0};
        PipelineTask train = new PipelineTask("train_model", () -> {
            trainAttempts[0]++;
            if (trainAttempts[0] <= 2) throw new RuntimeException("GPU preemption");
            System.out.println("  [train_model] model.bin accuracy=0.912");
        }, 2);

        PipelineTask evaluate = new PipelineTask("evaluate_model", () -> {
            System.out.println("  [evaluate_model] win rate vs champion +0.014");
        }, 1);

        PipelineTask deploy = new PipelineTask("deploy_model", () -> {
            System.out.println("  [deploy_model] canary 5% deployed");
        }, 0);

        validate.dependsOn("data_ingest");
        featurize.dependsOn("data_validate");
        train.dependsOn("feature_eng");
        evaluate.dependsOn("train_model");
        deploy.dependsOn("evaluate_model");

        dag.addTask(ingest);
        dag.addTask(validate);
        dag.addTask(featurize);
        dag.addTask(train);
        dag.addTask(evaluate);
        dag.addTask(deploy);

        dag.execute();
    }
}
```

### Expected Output
```
=== Executing pipeline: Netflix Rec Training DAG ===
[data_ingest] Starting (attempt 1/3)
[data_ingest] Failed (attempt 1/3): S3 throttled — retrying...
[data_ingest] Starting (attempt 2/3)
  [data_ingest] 1.2M rows ingested from s3://events/2026-08-03/
[data_ingest] Completed successfully
[data_validate] Starting (attempt 1/2)
  [data_validate] schema valid, null rate 0.4%
[data_validate] Completed successfully
[feature_eng] Starting (attempt 1/3)
  [feature_eng] 42 features written to feature_store
[feature_eng] Completed successfully
[train_model] Starting (attempt 1/3)
[train_model] Failed (attempt 1/3): GPU preemption — retrying...
[train_model] Starting (attempt 2/3)
[train_model] Failed (attempt 2/3): GPU preemption — retrying...
[train_model] Starting (attempt 3/3)
  [train_model] model.bin accuracy=0.912
[train_model] Completed successfully
[evaluate_model] Starting (attempt 1/2)
  [evaluate_model] win rate vs champion +0.014
[evaluate_model] Completed successfully
[deploy_model] Starting (attempt 1/1)
  [deploy_model] canary 5% deployed
[deploy_model] Completed successfully

--- Pipeline Summary ---
  data_ingest: OK (retried 1 time(s))
  data_validate: OK
  feature_eng: OK
  train_model: OK (retried 2 time(s))
  evaluate_model: OK
  deploy_model: OK
```

---

## Problem 2: Cycle Detection in a Forecast Pipeline — Company: Amazon
### Interview Scenario
"You're at Amazon. A new engineer on the demand-forecast team wired `model_eval → retrain` to stop a stale-model alert, but `retrain` already depended on `model_eval`, and the orchestrator silently never ran the pipeline again. Prove the bug before it ships."

### The Problem
1. Build a three-task DAG with an accidental cycle (retrain ↔ model_eval).
2. Detect the cycle at submission time using the lab's topological sort.
3. Verify the scheduler refuses to execute and surfaces a clear error.

### Solution Walkthrough
- Step 1: Reuse `PipelineDAG.topologicalSort()` — Kahn's algorithm returns fewer nodes than tasks when a cycle exists.
- Step 2: Wire the cycle: `model_eval.dependsOn("retrain")` and `retrain.dependsOn("model_eval")` — both in-degrees stay ≥ 1, the queue empties early.
- Step 3: Throw the lab's exact guard: `throw new IllegalStateException("Cycle detected in pipeline DAG")`.
- Step 4: Run this check in CI on every DAG PR — the same ~30-line algorithm becomes a build gate, the way Airflow validates DAGs on parse.

### Code
```java
// Cycle detection using the lab's topological sort (Kahn's algorithm)
PipelineDAG dag = new PipelineDAG("Forecast DAG");
PipelineTask ingest = new PipelineTask("data_ingest", () -> {}, 1);
PipelineTask retrain = new PipelineTask("retrain", () -> {}, 1);
PipelineTask modelEval = new PipelineTask("model_eval", () -> {}, 1);

ingest.dependsOn("data_ingest");            // source node, in-degree 0
retrain.dependsOn("data_ingest");
modelEval.dependsOn("retrain");             // cycle edge 1
retrain.dependsOn("model_eval");            // cycle edge 2 — back-edge

dag.addTask(ingest); dag.addTask(retrain); dag.addTask(modelEval);

try {
    dag.topologicalSort();                  // throws: sorted.size() == 0 != 3
} catch (IllegalStateException e) {
    System.out.println("Blocked: " + e.getMessage());
}
```
### Expected Output
```
Blocked: Cycle detected in pipeline DAG
```

---

## Problem 3: Exponential Backoff Before Retry — Company: Stripe
### Interview Scenario
"You're at Stripe. The fraud-model ingest step retries instantly and still fails whenever the data lake briefly throttles — immediate retries just hammer the recovery. Add backoff between attempts."

### The Problem
1. Add exponential backoff (`2^retry × 50ms`, capped) between retry attempts.
2. Keep the lab's attempt printouts and budget semantics.
3. Preserve determinism for testing — no random jitter in the demo.

### Solution Walkthrough
- Step 1: Replace the tight retry loop with one that sleeps before re-invoking `execute()`.
- Step 2: Compute `delay = Math.min(50L << retryCount, 1000)` — attempt 2 waits 100ms, attempt 3 waits 200ms.
- Step 3: Log the wait so operators can correlate retry pacing in the summary logs, then loop back into the same status-gated loop.

### Code
```java
while (task.status != TaskStatus.SUCCESS && task.status != TaskStatus.FAILED) {
    task.execute();
    if (task.status == TaskStatus.RUNNING && task.retryCount > 0) {
        long delay = Math.min(50L << task.retryCount, 1000);   // 100ms, 200ms, ...
        System.out.printf("  [%s] backing off %dms before attempt %d%n",
                task.name, delay, task.retryCount + 1);
        try { Thread.sleep(delay); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```
### Expected Output
```
[data_ingest] Failed (attempt 1/3): S3 throttled — retrying...
  [data_ingest] backing off 100ms before attempt 2
[data_ingest] Starting (attempt 2/3)
  [data_ingest] 1.2M rows ingested from s3://events/2026-08-03/
[data_ingest] Completed successfully
```
