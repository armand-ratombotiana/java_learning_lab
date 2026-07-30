package com.aiengineering.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.time.*;

/**
 * Demonstrates core LLM serving infrastructure concepts in Java:
 * model serving, request batching, response caching, and load balancing.
 * <p>
 * This lab simulates an inference server that queues incoming requests,
 * batches them for efficient GPU utilization, caches identical prompts,
 * and distributes load across multiple model replicas.
 */
public class LlmServingInfrastructureDemo {

    // ---------- Data Classes ----------

    /** A single LLM inference request. */
    public record LlmRequest(String id, String prompt, int maxTokens) {}

    /** The result of an LLM inference. */
    public record LlmResponse(String requestId, String output, long latencyMs) {}

    // ---------- Cache ----------

    /** Simple thread-safe LRU cache for model responses. */
    static class ResponseCache {
        private final LinkedHashMap<String, LlmResponse> cache;

        ResponseCache(int capacity) {
            this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<String, LlmResponse> eldest) {
                    return size() > capacity;
                }
            };
        }

        synchronized LlmResponse get(String prompt) { return cache.get(prompt); }
        synchronized void put(String prompt, LlmResponse response) { cache.put(prompt, response); }
        synchronized int size() { return cache.size(); }
    }

    // ---------- Model Replica ----------

    /** Simulates a model replica that processes batches. */
    static class ModelReplica {
        private final String replicaId;
        private final AtomicInteger requestsServed = new AtomicInteger();

        ModelReplica(String replicaId) { this.replicaId = replicaId; }

        List<LlmResponse> processBatch(List<LlmRequest> batch) {
            List<LlmResponse> responses = new ArrayList<>();
            for (LlmRequest req : batch) {
                long start = System.currentTimeMillis();
                // Simulate inference latency (~10ms per token)
                sleep(10 * req.maxTokens());
                long latency = System.currentTimeMillis() - start;
                responses.add(new LlmResponse(req.id(), "[Replica " + replicaId + "] " + req.prompt().toUpperCase(), latency));
                requestsServed.incrementAndGet();
            }
            return responses;
        }

        int getRequestsServed() { return requestsServed.get(); }
        String getReplicaId() { return replicaId; }
    }

    // ---------- Load Balancer ----------

    /** Round-robin load balancer across model replicas. */
    static class LoadBalancer {
        private final List<ModelReplica> replicas;
        private final AtomicInteger counter = new AtomicInteger(0);

        LoadBalancer(List<ModelReplica> replicas) { this.replicas = replicas; }

        ModelReplica next() {
            return replicas.get(counter.getAndIncrement() % replicas.size());
        }
    }

    // ---------- Inference Server with Batching ----------

    /** Batched inference server that collects requests and processes them in batches. */
    static class InferenceServer {
        private final BlockingQueue<LlmRequest> queue = new LinkedBlockingQueue<>();
        private final ResponseCache cache = new ResponseCache(100);
        private final LoadBalancer loadBalancer;
        private final int batchSize;
        private final long batchWindowMs;
        private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        private volatile boolean running = true;

        InferenceServer(LoadBalancer loadBalancer, int batchSize, long batchWindowMs) {
            this.loadBalancer = loadBalancer;
            this.batchSize = batchSize;
            this.batchWindowMs = batchWindowMs;
        }

        /** Starts the background batching loop. */
        void start() {
            executor.submit(() -> {
                while (running) {
                    try {
                        List<LlmRequest> batch = new ArrayList<>();
                        LlmRequest first = queue.poll(batchWindowMs, TimeUnit.MILLISECONDS);
                        if (first == null) continue;
                        batch.add(first);
                        queue.drainTo(batch, batchSize - 1);

                        ModelReplica replica = loadBalancer.next();
                        List<LlmResponse> responses = replica.processBatch(batch);
                        for (LlmResponse resp : responses) {
                            LlmRequest req = batch.stream()
                                .filter(r -> r.id().equals(resp.requestId()))
                                .findFirst().orElse(null);
                            if (req != null) cache.put(req.prompt(), resp);
                            System.out.println("  -> " + resp);
                        }
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            });
        }

        /** Submits a request (checks cache first). */
        LlmResponse submit(LlmRequest request) {
            LlmResponse cached = cache.get(request.prompt());
            if (cached != null) {
                System.out.println("  [CACHE HIT] prompt=" + request.prompt().substring(0, Math.min(30, request.prompt().length())) + "...");
                return cached;
            }
            queue.add(request);
            return null; // processed asynchronously
        }

        void shutdown() { running = false; executor.shutdown(); }
    }

    // ---------- Main Demo ----------

    public static void main(String[] args) throws Exception {
        System.out.println("=== AI Engineering Academy — Lab 01: LLM Serving Infrastructure ===\n");

        // Create model replicas
        List<ModelReplica> replicas = List.of(new ModelReplica("A"), new ModelReplica("B"), new ModelReplica("C"));
        LoadBalancer lb = new LoadBalancer(replicas);
        InferenceServer server = new InferenceServer(lb, 4, 50);
        server.start();

        List<LlmRequest> requests = Arrays.asList(
            new LlmRequest("1", "Explain transformers", 5),
            new LlmRequest("2", "What is attention?", 5),
            new LlmRequest("3", "Explain transformers", 5), // duplicate
            new LlmRequest("4", "What is RLHF?", 8),
            new LlmRequest("5", "How does GPT work?", 6),
            new LlmRequest("6", "Explain transformers", 5)  // another duplicate
        );

        System.out.println("Submitting " + requests.size() + " requests...\n");
        for (LlmRequest req : requests) {
            LlmResponse resp = server.submit(req);
            if (resp != null) {
                System.out.println("  [CACHED] " + resp);
            }
        }

        // Wait for async processing to complete
        Thread.sleep(2000);
        server.shutdown();

        System.out.println("\n--- Statistics ---");
        for (ModelReplica r : replicas) {
            System.out.println("  Replica " + r.getReplicaId() + " served " + r.getRequestsServed() + " requests");
        }
        System.out.println("  Cache entries: " + server.cache.size());
        System.out.println("\nDemo complete.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
