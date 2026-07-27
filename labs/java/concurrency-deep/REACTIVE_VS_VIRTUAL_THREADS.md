# Reactive vs Virtual Threads — The Great Debate

> A comprehensive interview guide comparing reactive programming with Project Loom virtual threads

---

## 1. Overview

| Aspect | Reactive (Project Reactor / RxJava) | Virtual Threads |
|--------|--------------------------------------|-----------------|
| **Programming model** | Declarative, functional, chain-of-operators | Imperative, synchronous-looking |
| **Concurrency model** | Event-loop / thread pool + non-blocking I/O | Thread-per-task with blocking I/O |
| **Learning curve** | Steep | Shallow (familiar Java patterns) |
| **Stack traces** | Indecipherable chains of `Mono*`/`FlatMap*` | Normal JVM stack traces |
| **Debugging** | Hard (reactive streams hide context) | Easy (standard debugger works) |
| **Backpressure** | Built-in (request(n) signals) | Manual (blocking queues, pool limits) |
| **Error handling** | Operators (`onErrorReturn`, `retry`, `fallback`) | try-catch (as usual) |
| **I/O model** | Non-blocking (NIO, Netty, epoll/kqueue) | Blocking (standard streams, sockets) |
| **Resource utilization** | Fixed thread pool + high CPU per thread | Carrier pool + unmount on block |
| **Maturity** | Production since ~2015 (Reactor 1.0) | Production since Java 21 (2023) |

---

## 2. When Each Is Appropriate

### Choose Reactive When:

1. **High-throughput streaming** — WebSocket, Server-Sent Events, continuous data pipelines
2. **Backpressure is critical** — The declarative `request(n)` mechanism elegantly handles slow consumers
3. **You already have reactive infrastructure** — Migrating away from an established reactive stack is expensive
4. **Mixed CPU + I/O with complex flow control** — Combining, merging, splitting streams reactively is elegant
5. **Latency-sensitive at p99** — Reactive's non-blocking nature avoids carrier-switching overhead

### Choose Virtual Threads When:

1. **Simple request-response services** — Most REST APIs, CRUD applications
2. **Your team is not reactive-trained** — Virtual threads require no new mental model
3. **Existing codebase with synchronized/blocking code** — Gradual migration without rewrite
4. **Complex business logic** — Multiple I/O calls with conditions, loops, try-catch
5. **You need readable stack traces** — Production debugging is much easier

### Mixed Approach

Best practice for many teams: **Start new services with virtual threads, keep existing reactive services unless there's a clear migration benefit.**

---

## 3. Migration Paths

### From Reactive to Virtual Threads

**Step 1: Identify boundary** — Find where reactive ends and blocking begins
```java
// Current reactive:
return webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .flatMap(data -> anotherService.process(data));

// Step 1: Block at the boundary
Mono<Data> mono = webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class);

// Step 2: Use block() with virtual thread
Data data = mono.block();  // No thread wasted — VT unmounts
Data result = anotherService.process(data);  // blocking call
```

**Step 2: Refactor to imperative** — Replace operators with regular code
```java
// Before (reactive chain):
return serviceA.fetch(id)
    .zipWith(serviceB.fetch(id))
    .map(tuple -> combine(tuple.getT1(), tuple.getT2()));

// After (virtual threads):
DataA a = serviceA.fetch(id);    // blocking inside VT
DataB b = serviceB.fetch(id);    // blocking inside VT
return combine(a, b);
```

**Step 3: Benchmark** — Compare p50/p99 latency, throughput, resource usage

### From Virtual Threads to Reactive

Rare — but if you discover that carrier-switching overhead is causing issues for latency-critical paths, you can introduce reactive at hot spots:

```java
// Virtual thread wrapper around reactive core:
Mono<Result> reactiveResult = Mono.fromCallable(() -> {
        // CPU-bound work on carrier
        return computeExpensiveResult();
    })
    .subscribeOn(Schedulers.parallel());

// Block on the reactive result (VT unmounts during subscription):
Result r = reactiveResult.block();
```

---

## 4. Performance Characteristics

### Back-of-Envelope Comparison

**Scenario**: 10,000 concurrent requests, each does:
- 10ms CPU work (validate, parse, transform)
- 50ms I/O wait (database query, REST call)

| Approach | Threads | CPU Utilization | Throughput | Complexity |
|----------|---------|----------------|------------|------------|
| Platform threads (200 pool) | 200 | Low (I/O bounded by pool) | ~4,000 req/s (200×50ms) | Low |
| Reactive (Netty + Reactor) | 2-4 (event loop) | High | ~10,000 req/s | High |
| Virtual threads | 8 carriers | Very High | ~10,000 req/s | Low |

**Key insight**: Virtual threads achieve reactive-level throughput with imperative code.

### When Virtual Threads Are Slower

High-frequency short I/O operations (micro-batching):
```java
// For 1,000,000 operations with 1µs I/O each:
// Virtual threads: ~1µs mount/unmount overhead per operation → 1 second lost to switching
// Reactive: Pipelined without per-operation overhead
```

**Rule of thumb**: Virtual threads shine when I/O operations take >1ms. For sub-millisecond operations, the mount/unmount overhead can dominate.

---

## 5. Company Preferences

| Company | Preferred Approach | Rationale |
|---------|-------------------|-----------|
| **Netflix** | Reactive (RxJava) | Massive streaming workloads, decades of investment in reactive infrastructure, Hystrix bulkheading patterns |
| **Spring Team** | Virtual Threads | Spring MVC simplicity + virtual thread throughput; `spring.threads.virtual.enabled=true` |
| **Oracle** | Structured Concurrency | Strategic direction for Java; virtual threads + StructuredTaskScope + ScopedValues |
| **Google** | Both (context-dependent) | Borg/Megastore async internally; Java teams evaluating virtual threads |
| **Meta** | Mixed | Lock-free data structures for cache services; virtual threads for request handling |
| **Apple** | Preventive (low-power) | GCD-like patterns; evaluating Java virtual threads for server-side |

### Netflix's Position

Netflix has heavily invested in RxJava (they created it). Their Hystrix library pioneered bulkhead patterns and circuit breakers. Key points:

- Reactive allows fine-grained thread pool isolation per dependency
- **With virtual threads**: Can achieve bulkheading via `StructuredTaskScope` — each dependency gets its own scope
- Netflix's recommendation: **Start new projects with virtual threads, but keep existing reactive systems** — migration cost isn't worth it for stable services

### Spring Team's Position

- "Virtual threads + Spring MVC is the new default" — Josh Long
- Spring Boot 3.2+ has `spring.threads.virtual.enabled=true`
- Project Reactor is not going away, but the *recommended* path for new I/O-bound services is virtual threads
- Reactive remains recommended for:
  - WebSocket / SSE
  - High-throughput streaming
  - Embedded Netty environments

---

## 6. Sample Interview Dialogue

**Interviewer**: "Our team is building a new microservice that calls three downstream services, does some data transformation, and returns a response. Should we use reactive or virtual threads?"

**Candidate** (Senior-level response):

"I'd recommend virtual threads for this use case for several reasons:

1. **Programming model**: Your developers already know Java. With virtual threads, you write synchronous-looking code that's easy to read, debug, and maintain.

2. **Concurrent calls**: ForkJoinTask for three downstream calls:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<A> a = scope.fork(() -> downstreamA.call());
    Future<B> b = scope.fork(() -> downstreamB.call());
    Future<C> c = scope.fork(() -> downstreamC.call());
    scope.join();
    scope.throwIfFailed();
    return transform(a.resultNow(), b.resultNow(), c.resultNow());
}
```

3. **Error handling**: Standard try-catch. No `onErrorResume` chains.

4. **Performance**: Three I/O calls, each ~50ms — virtual threads easily handle 10K+ concurrent requests.

5. **Migration risk**: If a downstream service becomes slow, we can add timeouts and retries with regular Java patterns.

I'd consider reactive only if we had high-throughput streaming requirements or needed sophisticated backpressure management. For a standard request-response service, virtual threads give us the best of both worlds — reactive performance with imperative simplicity."

**Follow-up**: "What about when a downstream service becomes slow?"

**A**: "We'd use timeout + circuit breaker:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(() -> {
        try { return client.call().orTimeout(2, TimeUnit.SECONDS); }
        catch (TimeoutException e) { return fallbackData(); }
    });
    // ... rest of scope logic
}
```

With virtual threads, the timeout doesn't consume a platform thread — the carrier thread is freed for other work while waiting."

---

## 7. Decision Matrix

| Scenario | Recommendation | Reasoning |
|----------|---------------|-----------|
| REST API with DB calls | Virtual Threads | Simple, fast, easy to debug |
| WebSocket game server | Reactive | Stateful streaming, backpressure |
| Legacy monolith with synchronized code | Virtual Threads (carefully) | Gradual migration, avoid pinning |
| High-frequency trading | Reactive for data pipeline, VT for orchestration | Microsecond latency sensitivity |
| ETL pipeline | Virtual Threads | I/O-bound, simple error handling |
| Real-time analytics stream | Reactive | Windowing, grouping, backpressure |
| Simple CRUD Spring Boot | Virtual Threads (enable flag) | Spring 3.2+ built-in support |
| Chat application | Reactive (WebSocket) | Streaming bidirectional communication |

---

## 8. Key Interview Takeaways

1. **Virtual threads do not replace reactive** — they offer an alternative for I/O-bound workloads
2. **Reactive excels at streaming and backpressure** — virtual threads are about simplicity and throughput
3. **Migration is expensive** — don't rewrite working reactive systems
4. **Hybrid is normal** — reactive at the edge, virtual threads for business logic
5. **Monitor and measure** — virtual threads aren't magic; profile before and after migration
6. **Pinning awareness** — synchronized code can degrade virtual thread performance

---

*The industry is still converging. Expect reactive and virtual threads to coexist for years. The best engineers understand both and choose based on context, not trend.*

---

## 9. Virtual Threads with NIO/Netty

While virtual threads work with blocking I/O, they can also be combined with NIO/Netty for specific use cases:

```java
// Virtual thread wrapping NIO
public CompletableFuture<Response> nioWithVirtualThread(Channel channel) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            // Blocking read inside virtual thread:
            ByteBuffer buf = ByteBuffer.allocate(1024);
            channel.read(buf).get(); // virtual thread blocks here, carrier freed
            return process(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }, Executors.newVirtualThreadPerTaskExecutor());
}
```

This pattern is useful when migrating legacy NIO code: wrap NIO futures in virtual threads for backward compatibility.

---

## 10. Practical Pro/Con Summary Table

| Decision Factor | Reactive (WebFlux) | Virtual Threads | Recommendation |
|----------------|-------------------|-----------------|----------------|
| Team expertise | Requires training | Uses existing skills | Virtual threads |
| Code simplicity | Complex chains | Simple imperative | Virtual threads |
| Debugging | Poor stack traces | Normal stack traces | Virtual threads |
| Backpressure | Built-in | Manual (queue limits) | Reactive |
| Streaming/WebSocket | Excellent | Adequate | Reactive |
| Throughput (I/O bound) | Excellent | Excellent | Tie |
| Latency (p99) | Slightly better | Carrier switch adds ~µs | Reactive (marginal) |
| Memory footprint | Minimal (small heap) | More (continuations on heap) | Virtual threads* |
| Testing | Complex (StepVerifier) | Standard unit tests | Virtual threads |
| Migration from blocking | Full rewrite | Gradual | Virtual threads |

*\*Virtual threads use more heap memory, but this is rarely a bottleneck vs developer productivity.*

---

## 11. Key Interview Dialogue: The "What Would You Choose" Question

**Interviewer**: "You're the tech lead for a new greenfield service. It does I/O calls, business logic, and returns a response. Reactive or virtual threads?"

**Excellent answer**:

"I'd start with virtual threads for these reasons:

**Speed of development**: We can ship faster with synchronous code. Every developer on the team can write and review it.

**Debugging**: When a production issue happens, normal stack traces are invaluable. With reactive, the stack trace reads like a game of telephone between 20 Mono/FlatMap wrappers.

**Performance**: For a service that spends most of its time waiting on I/O, virtual threads achieve 95%+ of reactive's throughput with far simpler code.

**Future-proofing**: As the Java ecosystem optimizes for virtual threads (Spring Boot 3.2+, JDK 22+, libraries removing synchronized), performance will only improve.

**However**, I'd choose reactive if:
1. The service has heavy streaming needs (WebSocket, SSE)
2. Backpressure is a first-class concern (pipeline processing)
3. We already have a mature reactive infrastructure that we'd lose migration cost from
4. We need sub-millisecond p99 latency for short operations

In most cases, virtual threads give us the best ROI. We can always introduce reactive at specific hot spots later."

---

## 12. Migration Cost Analysis

| Migration Path | Cost | Risk | Timeline |
|---------------|------|------|----------|
| Blocking → Virtual Threads | Low (thread pool swap) | Low | Days |
| Blocking → Reactive | High (full architecture rewrite) | High | Months |
| Reactive → Virtual Threads | Medium (service-by-service) | Medium | Weeks to months |
| Reactive → Reactive (upgrade) | Low | Low | Days |

**Recommended migration order for a reactive monolith:**
1. Identify I/O-bound endpoints (most REST APIs)
2. Rewrite one endpoint with virtual threads (as a POC)
3. Benchmark: compare p50/p99 latency, throughput, resource usage
4. If successful, migrate service by service
5. Keep reactive for streaming endpoints
