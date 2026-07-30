# LLM Serving Infrastructure — Deep Dive Guide

## Architecture Overview

An LLM serving system consists of:
1. **API Gateway** — Accepts requests, handles auth, rate limiting
2. **Request Queue** — Buffers incoming requests for batching
3. **Scheduler/Batcher** — Groups requests into optimal batches
4. **Model Replicas** — GPU-backed inference workers
5. **Cache Layer** — KV-cache, response cache, prefix cache
6. **Load Balancer** — Distributes work across replicas

## Batching Strategies

### Static Batching
- Fixed batch size, requests wait until batch is full
- Simple but increases latency under low load

### Continuous Batching (Implemented)
- Dynamically adds requests to running batches
- Maximizes GPU utilization
- Requires careful memory management for KV-caches

## Code Walkthrough: InferenceServer

The `InferenceServer` class demonstrates:
- A `BlockingQueue` that collects incoming requests
- A background thread that polls the queue with a configurable window
- `queue.drainTo()` to collect partial batches when the window expires
- Delegation to a `LoadBalancer` for replica selection

Key parameters:
- `batchSize`: Maximum requests per batch
- `batchWindowMs`: Max time to wait before processing partial batch

## Caching Strategy

The `ResponseCache` uses a thread-safe `LinkedHashMap` with LRU eviction:
- Cache key: the prompt text (normalized)
- Cache value: the `LlmResponse` object
- Thread safety via `synchronized` methods
- Configurable capacity with automatic eviction

## Load Balancing

The round-robin `LoadBalancer` distributes requests evenly:
- Uses `AtomicInteger` for lock-free counter
- `counter.getAndIncrement() % replicas.size()` for distribution

Real-world systems use:
- **Least Connections**: Route to replica with fewest active requests
- **Power of Two Choices**: Pick two random replicas, route to least loaded
- **Adaptive**: Consider queue depth, GPU memory, and latency

## Production Considerations

- Use virtual threads (Java 21+) for request handling
- Implement request timeouts and circuit breakers
- Monitor queue depth for autoscaling signals
- Pre-allocate KV-cache memory for predictable batching
