# Reactive Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Compare Reactive Streams with Java 8 Streams. What's the fundamental difference?"
- "How does Project Reactor implement backpressure? Explain the request(n) protocol."
- "Explain the difference between hot and cold publishers. Give examples of each in Reactor."

### Amazon
- "Design a reactive microservice that calls three downstream services in parallel and combines results."
- "How does backpressure work in a distributed system? Can Project Reactor's backpressure cross network boundaries?"
- "You have a reactive pipeline with latency spikes. How do you debug reactive chains?"

### Meta
- "Compare Project Reactor with RxJava. Which would you choose for a new project?"
- "How does WebFlux compare to traditional Spring MVC? When would you choose each?"
- "Explain Schedulers.parallel() vs Schedulers.boundedElastic() vs Schedulers.immediate()."

### Apple
- "How does reactive programming reduce memory footprint compared to thread-per-request?"
- "How does Project Reactor's Hooks.onOperatorDebug() help with debugging reactive chains?"

### Oracle
- "Explain the Reactive Streams specification (org.reactivestreams:Publisher, Subscriber, Subscription, Processor)."
- "How does RSocket compare with HTTP/2 for reactive streaming?"
- "How does the Reactor Netty integration work? Where does ChannelPipeline interact with Flux/Mono?"

## LeetCode Problems

| Problem | Reactive Concept |
|---------|-----------------|
| 1188 Design Bounded Blocking Queue | Producer-consumer with backpressure |
| 1242 Web Crawler Multithreaded | Reactive crawling with Flux.merge() |
| 1279 Traffic Light Controller | State machine as reactive stream |
| Design News Feed (System Design) | Reactive fan-out with backpressure |
| Design Rate Limiter | Reactive request n-count tracking |

## FAANG Interview Stories

**Story 1: Google — Debugging Reactive Chains**
> *"A reactive service had 'empty response' errors. The chain was: A → flatMap → B → filter → C. FlatMap on Mono.empty() caused the whole chain to complete empty. The fix: switchIfEmpty() with a fallback. The debugging challenge: stack traces in reactive chains are notoriously unhelpful."* — SWE, Google

**Story 2: Amazon — WebFlux Backpressure**
> *"A DynamoDB stream processor using WebFlux would OOM under high load. The problem: the reactive chain wasn't propagating backpressure correctly. The SQS consumer would push messages faster than DynamoDB writes. Fix: limitRate() on the source and ensure the database client (AWS SDK v2 reactive) properly signals demand."* — SDE III, Amazon

**Story 3: Netflix — Hystrix to Reactor Migration**
> *"Migrating from Hystrix to Reactor's resilience4j involved rewriting all circuit breaker patterns as Reactor operators. The key: .transformDeferred(CircuitBreakerOperator.of(circuitBreaker)). The challenge: testing reactive circuits required StepVerifier with virtual time."* — Senior Engineer, Netflix

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain the difference between flatMap, concatMap, and switchMap. Give use cases for each."
- "How does Reactor's subscribeOn vs publishOn differ? How do they affect thread hopping?"
- "What is a Sink in Reactor? When would you use a Sinks.Many vs Sinks.One?"

### Staff-Level
- "Design a reactive transaction manager. How do you handle rollback across reactive microservices?"
- "How does Reactor's assembly vs subscription model work? Where does operator fusion happen?"
- "Design a backpressure-aware protocol that works across network boundaries (like RSocket's request-n)."
- "How would you implement a reactive connection pool? What happens when all connections are busy?"

## System Design Connections

| System | Reactive Technology |
|--------|-------------------|
| API gateway | WebFlux, Reactor Netty |
| Event processor | Reactor Kafka, reactive SQS |
| Stream processor | Project Reactor, RSocket |
| Notification service | WebSocket with reactive backpressure |
| Data pipeline | Reactive Sources (MongoDB, Postgres R2DBC) |

## Code Review Scenarios

**Scenario 1**: Blocking call in reactive pipeline.
```java
// Bad: blocking call inside reactive operator
flux.flatMap(item -> {
    String result = jdbcTemplate.query(item);  // Blocks event loop!
    return Mono.just(result);
});
// Fix: use .publishOn(Schedulers.boundedElastic()) or reactive driver
```

**Scenario 2**: Missing error handling.
```java
// Bad: unhandled exception terminates stream
flux.map(item -> riskyOperation(item));
// Fix: .onErrorResume(), .onErrorReturn(), .doOnError()
```

**Scenario 3**: Subscribing without demand control.
```java
// Bad: unbounded demand
flux.subscribe(System.out::println);
// Fix: use .limitRate(100) or .take(1000) for controlled demand
```

## Debugging Scenarios

**Scenario 1**: Reactive chain not executing.
- Cause: No subscriber (lazy evaluation)
- Fix: Add .subscribe() or return Mono/Flux to Spring WebFlux controller
- Detection: Hooks.onOperatorDebug() to see assembly trace

**Scenario 2**: Memory leak in reactive pipeline.
- Cause: Subscriber not cancelling after completion
- Fix: Use .take(), .timeout(), or ensure downstream cancels
- Detection: Heap dump shows Disposable objects accumulating

**Scenario 3**: Infinite retry loop.
```java
// Bad: unconditional retry
flux.retry();  // Infinite
// Fix: .retry(3) or .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
```
