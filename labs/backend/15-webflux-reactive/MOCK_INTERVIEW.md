# Mock Interview: WebFlux (Lab 15)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is reactive programming and how does Spring WebFlux implement it?

**Candidate:** Reactive programming is a declarative programming paradigm concerned with data streams and propagation of change. Spring WebFlux implements it using:
- **Project Reactor** as the reactive library (Flux<T> for N elements, Mono<T> for 0-1 elements)
- **Netty** as the embedded server (non-blocking I/O event loop)
- **Reactive Streams** specification for backpressure
- **`@RestController`** with reactive return types (Mono/Flux)

Unlike Spring MVC which uses a thread-per-request model, WebFlux uses a small number of event loop threads to handle many concurrent connections, making it more efficient for I/O-bound workloads.

**Interviewer:** Compare Spring MVC and Spring WebFlux. When would you choose each?

**Candidate:**

| Aspect | Spring MVC | Spring WebFlux |
|--------|-----------|----------------|
| Thread model | 1 thread per request | Event loop (few threads) |
| Server | Tomcat, Jetty, Undertow | Netty (default), Tomcat/Jetty |
| Concurrency | Thread pool bounded | Thousands of connections |
| Memory/request | 1-2 MB (thread stack) | 10-50 KB |
| Throughput | ~200 req/s/core (I/O bound) | ~5000 req/s/core (I/O bound) |
| Blocking | Expected (JDBC, JPA) | Avoid at all costs |
| Programming | Imperative, familiar | Reactive (learning curve) |

Choose WebFlux for: I/O-bound services, streaming (SSE, WebSockets), API gateways, high-concurrency backends. Choose MVC for: CPU-bound services, simple CRUD, teams new to reactive programming.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you handle database access in WebFlux? What about JPA?

**Candidate:** WebFlux requires non-blocking database drivers:
- **R2DBC** (Reactive Relational Database Connectivity) for SQL databases — PostgreSQL, MySQL, H2, SQL Server
- **Spring Data R2DBC** as the reactive alternative to Spring Data JPA
- **MongoDB Reactive Streams** for document databases
- **Redis Reactive** (Lettuce) for caching

JPA/Hibernate are blocking (JDBC) and cannot be used directly in reactive chains. If you must use JPA, wrap the blocking call in `Mono.fromCallable()` with a separate thread pool (`Schedulers.boundedElastic()`).

```java
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return Mono.fromCallable(() -> userService.findById(id))
        .subscribeOn(Schedulers.boundedElastic());
}
```

For new reactive projects, prefer R2DBC + Spring Data R2DBC.

**Interviewer:** How does backpressure work in Project Reactor?

**Candidate:** Backpressure is the ability of a consumer to signal to a producer how much data it can handle. Implemented via `Subscription.request(n)`:
- `buffer()` — buffers elements when downstream is slow
- `drop()` — drops elements that can't be processed
- `latest()` — keeps only the latest element, drops older
- `error()` — throws error if downstream can't keep up
- `limitRate()` — splits upstream request into smaller batches

Example:
```java
Flux.interval(Duration.ofMillis(10))
    .onBackpressureDrop(dropped -> log.warn("Dropped: {}", dropped))
    .limitRate(100) // request 100 at a time
    .concatMap(this::processItem) // async processing
    .subscribe();
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a real-time stock price streaming service using WebFlux that handles 1M updates/second.

**Candidate:** 

**Architecture:**
```
Market Data Feed → WebFlux Service → SSE Stream → Client
       │                │
       │           [Transform & Aggregate]
       │                │
       └──→ Kafka ─────┘
           (persist)
```

**Reactive pipeline:**
```java
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    @GetMapping(value = "/{symbol}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockPrice> streamStock(@PathVariable String symbol) {
        return kafkaReceiver.receive()
            .map(record -> record.value())
            .filter(price -> price.symbol().equals(symbol))
            .map(this::enrichWithMovingAverage)
            .sample(Duration.ofMillis(100)) // throttle to 10 updates/sec per client
            .share();
    }
}
```

**Backpressure handling for 1M updates/sec:**
1. Use `sample(Duration.ofMillis(100))` to throttle output to 10 msg/sec per client
2. Use `onBackpressureDrop()` to drop intermediate price ticks
3. Use `window(Duration.ofSeconds(1))` to batch-process OHLC (Open-High-Low-Close) candles
4. Use `flatMapSequential()` instead of `flatMap()` for ordered processing

**Scaling considerations:**
- Netty event loop groups: `reactor.netty.ioWorkerCount` and `reactor.netty.ioSelectCount`
- Connection limit: Netty default 1000 concurrent connections
- Each connection is just a few KB of memory
- For 100K concurrent clients: ~2GB RAM for connections alone

**Interviewer:** How does WebFlux's event loop model work? What happens if a blocking operation is called?

**Candidate:** WebFlux uses Netty's event loop model (similar to Node.js):
- A small number of event loop threads (typically `availableProcessors * 2`)
- Each event loop runs a continuous loop: accept connections → read data → execute handlers → write responses
- Handlers must be non-blocking — they return immediately with Mono/Flux

If a blocking operation (like JDBC `executeQuery()`) is called on an event loop thread:
1. The event loop thread is blocked — it can't process other connections
2. All connections assigned to that event loop are stalled
3. If all event loops block simultaneously, the server freezes

**Detection:** Reactor's `BlockHound` library detects blocking calls on non-blocking threads during testing. Use `Schedulers.boundedElastic()` for blocking operations or `@Async` with a separate thread pool.

---

## Interviewer Feedback

**Strengths:** Strong reactive fundamentals, good backpressure understanding, practical streaming design  
**Areas to Improve:** Could discuss RSocket as an alternative to HTTP streaming  
**Verdict:** Strong Hire

---

*Lab 15 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
