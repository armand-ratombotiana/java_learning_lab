package com.capstone.spark;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class TaskExecutor {
    private final String executorId;
    private final ExecutorService threadPool;
    private final AtomicLong taskIdGen = new AtomicLong(0);
    private final Map<Long, TaskResult> results = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public record Task<T>(long taskId, String taskType, Callable<T> callable, Instant submittedAt) {}
    public record TaskResult<T>(long taskId, T result, TaskStatus status, long durationMs, Instant completedAt) {}
    public enum TaskStatus { SUCCESS, FAILED }

    public TaskExecutor(String executorId, int numThreads) {
        this.executorId = executorId;
        this.threadPool = Executors.newFixedThreadPool(numThreads);
    }

    public <T> CompletableFuture<TaskResult<T>> submitTask(Callable<T> callable, String taskType) {
        long taskId = taskIdGen.incrementAndGet();
        Instant submitted = Instant.now();
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            try {
                T result = callable.call();
                long duration = System.currentTimeMillis() - start;
                TaskResult<T> tr = new TaskResult<>(taskId, result, TaskStatus.SUCCESS, duration, Instant.now());
                results.put(taskId, tr);
                return tr;
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - start;
                TaskResult<T> tr = new TaskResult<>(taskId, null, TaskStatus.FAILED, duration, Instant.now());
                results.put(taskId, tr);
                return tr;
            }
        }, threadPool);
    }

    public <T> List<TaskResult<T>> submitBatch(List<Callable<T>> tasks, String taskType) {
        List<CompletableFuture<TaskResult<T>>> futures = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futures.add(submitTask(task, taskType));
        }
        return futures.stream().map(CompletableFuture::join).toList();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<TaskResult<T>> getResult(long taskId) {
        return Optional.ofNullable((TaskResult<T>) results.get(taskId));
    }

    public List<TaskResult<?>> getAllResults() { return List.copyOf(results.values()); }

    public long completedTaskCount() {
        return results.values().stream().filter(r -> r.status() == TaskStatus.SUCCESS).count();
    }

    public long failedTaskCount() {
        return results.values().stream().filter(r -> r.status() == TaskStatus.FAILED).count();
    }

    public String getExecutorId() { return executorId; }

    public void shutdown() { running = false; threadPool.shutdown(); }

    public boolean isRunning() { return running; }
}
