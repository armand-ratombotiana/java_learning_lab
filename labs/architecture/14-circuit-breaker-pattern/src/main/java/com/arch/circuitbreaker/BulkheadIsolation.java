package com.arch.circuitbreaker;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class BulkheadIsolation {
    private final Map<String, ExecutorService> threadPools = new ConcurrentHashMap<>();
    private final Map<String, Semaphore> semaphores = new ConcurrentHashMap<>();
    private final int maxConcurrentCalls;
    private final int queueSize;

    public BulkheadIsolation(int maxConcurrentCalls, int queueSize) {
        this.maxConcurrentCalls = maxConcurrentCalls;
        this.queueSize = queueSize;
    }

    public <T> CompletableFuture<T> callWithThreadPool(String bulkheadName, Supplier<T> supplier) {
        ExecutorService executor = threadPools.computeIfAbsent(bulkheadName, k ->
            new ThreadPoolExecutor(
                maxConcurrentCalls, maxConcurrentCalls,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueSize),
                new ThreadPoolExecutor.AbortPolicy()
            )
        );
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    public <T> T callWithSemaphore(String bulkheadName, Supplier<T> supplier) {
        Semaphore semaphore = semaphores.computeIfAbsent(bulkheadName, k -> new Semaphore(maxConcurrentCalls));
        try {
            if (!semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                throw new BulkheadException("Bulkhead " + bulkheadName + " saturated");
            }
            try {
                return supplier.get();
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BulkheadException("Interrupted while acquiring bulkhead");
        }
    }

    public void shutdown() {
        threadPools.values().forEach(ExecutorService::shutdown);
    }
}

class BulkheadException extends RuntimeException {
    public BulkheadException(String message) { super(message); }
}
