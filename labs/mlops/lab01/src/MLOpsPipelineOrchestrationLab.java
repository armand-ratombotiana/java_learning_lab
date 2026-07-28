package com.mlops.lab01;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * MLOps Pipeline Orchestration Lab.
 * <p>
 * Demonstrates DAG-based ML pipeline orchestration with task dependencies,
 * retry logic, topological execution, and concurrency control.
 * Models concepts found in Airflow, Prefect, and Dagster.
 */
public class MLOpsPipelineOrchestrationLab {

    /** Represents the status of a pipeline task. */
    enum TaskStatus { PENDING, RUNNING, SUCCESS, FAILED }

    /** A single task node in the pipeline DAG. */
    static class PipelineTask {
        final String name;
        final List<String> dependencies = new ArrayList<>();
        final Consumer<PipelineTask> action;
        final int maxRetries;
        TaskStatus status = TaskStatus.PENDING;
        int retryCount = 0;
        String errorMessage;

        PipelineTask(String name, Consumer<PipelineTask> action, int maxRetries) {
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
                action.accept(this);
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

    /** DAG that manages tasks and orchestrates execution order. */
    static class PipelineDAG {
        final String name;
        final Map<String, PipelineTask> tasks = new LinkedHashMap<>();

        PipelineDAG(String name) {
            this.name = name;
        }

        void addTask(PipelineTask task) {
            tasks.put(task.name, task);
        }

        /** Topological sort using Kahn's algorithm. */
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
            Instant start = Instant.now();
            List<PipelineTask> sorted = topologicalSort();
            // Use a thread pool for concurrent execution at each level
            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (PipelineTask task : sorted) {
                // Wait for dependencies to complete
                boolean depsOk = task.dependencies.stream()
                        .allMatch(d -> tasks.get(d).status == TaskStatus.SUCCESS);
                if (!depsOk) {
                    task.status = TaskStatus.FAILED;
                    task.errorMessage = "Dependency failed";
                    System.out.printf("[%s] Skipped — dependency failed%n", task.name);
                    continue;
                }
                executor.submit(() -> {
                    while (task.status != TaskStatus.SUCCESS && task.status != TaskStatus.FAILED) {
                        task.execute();
                        if (task.status == TaskStatus.FAILED && task.retryCount > task.maxRetries) {
                            break;
                        }
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Instant end = Instant.now();
            long seconds = Duration.between(start, end).toSeconds();
            System.out.printf("=== Pipeline finished in %d seconds ===%n", seconds);
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

    /** Simulates a task that may fail transiently. */
    static Consumer<PipelineTask> simulatedTask(String desc, long workMs, double failProbability) {
        return task -> {
            try { Thread.sleep(workMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            if (Math.random() < failProbability) {
                throw new RuntimeException(desc + " transient error");
            }
            System.out.printf("  [%s] %s done%n", task.name, desc);
        };
    }

    public static void main(String[] args) {
        // Build a simple ML pipeline DAG
        PipelineDAG dag = new PipelineDAG("ML Training Pipeline");

        PipelineTask ingest = new PipelineTask("data_ingest",
                simulatedTask("Ingesting raw data", 1000, 0.2), 2);
        PipelineTask validate = new PipelineTask("data_validate",
                simulatedTask("Validating schema", 800, 0.1), 1);
        PipelineTask featurize = new PipelineTask("feature_eng",
                simulatedTask("Engineering features", 1200, 0.1), 2);
        PipelineTask train = new PipelineTask("train_model",
                simulatedTask("Training model", 1500, 0.3), 2);
        PipelineTask evaluate = new PipelineTask("evaluate_model",
                simulatedTask("Evaluating model", 600, 0.0), 1);
        PipelineTask deploy = new PipelineTask("deploy_model",
                simulatedTask("Deploying to staging", 500, 0.0), 0);

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
