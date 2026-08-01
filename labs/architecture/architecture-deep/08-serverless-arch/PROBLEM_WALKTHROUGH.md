# Lab 08: Problem Walkthrough — Serverless Function Orchestrator with Event Routing

## Problem Statement

Implement a serverless function runtime for an order-processing pipeline. Requirements:

1. **Function model**: functions receive a context (invocation id, payload, cold-start flag) and return a result.
2. **Event routing**: incoming events are routed to the right function by an event-type header.
3. **Orchestration patterns**: function chaining (pipeline) and fan-out/fan-in (parallel then aggregate).
4. **Cold-start simulation**: functions that haven't run within a warm-up window are flagged as cold-started; the runtime tracks warm vs cold invocations.
5. **Externalized state**: orchestrations persist intermediate state in a key-value store (the FaaS rule: never trust local state).

## Constraints

- Java 21+ only.
- The runtime is single-JVM but must be thread-safe (fan-out runs in parallel).
- Cold-start state (last invocation time) must be shared, not per-thread.
- Functions are stateless except for the external state store.

## Approach

Serverless platforms (AWS Lambda, Cloud Functions) execute single-purpose functions on ephemeral runtimes. This lab builds the *orchestration plane*:

- **Event router**: maps `eventType` -> function, like S3 -> Lambda or SQS -> Lambda triggers.
- **Invoker**: simulates the FaaS lifecycle — a function invocation container stays warm for a window (`warmTimeout`); if the last invocation is older than the window, the next one is a cold start (new container, environment setup).
- **Orchestrator**: composes functions using `CompletableFuture` — chain for `fn1 -> fn2 -> fn3`, fan-out for parallel branches joined by an aggregator.
- **StateStore**: the only place state lives — survives across invocations, exactly like DynamoDB/S3 in real serverless.

## Step-by-Step Solution

### Step 1: The Function Contract

```java
record FunctionContext(String invocationId, String functionName, String eventType,
                       Object payload, boolean coldStart, Instant startedAt) {}

interface ServerlessFunction {
    String name();
    Object handle(FunctionContext context);
}
```

### Step 2: Example Functions

Two simple functions for the order pipeline: enrichment and payment validation.

```java
class EnrichOrderFunction implements ServerlessFunction {
    @Override
    public String name() { return "enrich-order"; }

    @Override
    public Object handle(FunctionContext context) {
        Map<String, Object> order = (Map<String, Object>) context.payload();
        order.put("region", "eu-west-1");
        order.put("enrichedAt", Instant.now().toString());
        return order;
    }
}

class ValidatePaymentFunction implements ServerlessFunction {
    @Override
    public String name() { return "validate-payment"; }

    @Override
    public Object handle(FunctionContext context) {
        Map<String, Object> order = (Map<String, Object>) context.payload();
        long amount = (long) order.get("amount");
        if (amount > 100_000) {
            throw new IllegalStateException("Payment requires manual review: " + amount);
        }
        order.put("paymentStatus", "APPROVED");
        return order;
    }
}
```

### Step 3: Runtime with Cold-Start Simulation

The invoker tracks the last invocation per function; an invocation after `warmTimeout` is cold. This mirrors Lambda's container reuse: a warm container handles consecutive invocations within the timeout.

```java
class FunctionRuntime {
    private final Map<String, ServerlessFunction> functions = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastInvocation = new ConcurrentHashMap<>();
    private final Duration warmTimeout;
    private final AtomicInteger coldStarts = new AtomicInteger();
    private final AtomicInteger totalInvocations = new AtomicInteger();

    FunctionRuntime(Duration warmTimeout) {
        this.warmTimeout = warmTimeout;
    }

    void register(ServerlessFunction function) {
        functions.put(function.name(), function);
    }

    Object invoke(String functionName, String eventType, Object payload) {
        var function = functions.get(functionName);
        if (function == null) throw new IllegalArgumentException("Unknown function: " + functionName);

        boolean coldStart = isColdStart(functionName);
        var context = new FunctionContext(UUID.randomUUID().toString(), functionName,
            eventType, payload, coldStart, Instant.now());
        totalInvocations.incrementAndGet();
        if (coldStart) coldStarts.incrementAndGet();

        try {
            return function.handle(context);
        } catch (RuntimeException e) {
            System.out.println("[runtime] " + functionName + " failed: " + e.getMessage());
            throw e;
        } finally {
            lastInvocation.put(functionName, Instant.now());
        }
    }

    private boolean isColdStart(String functionName) {
        var last = lastInvocation.get(functionName);
        return last == null || Duration.between(last, Instant.now()).compareTo(warmTimeout) > 0;
    }

    int coldStartCount() { return coldStarts.get(); }
    int invocationCount() { return totalInvocations.get(); }
    boolean isWarm(String functionName) { return !isColdStart(functionName); }
}
```

### Step 4: Event Router

The router binds event types to functions — the "trigger" layer. Unrouted events are dead-lettered.

```java
class EventRouter {
    private final FunctionRuntime runtime;
    private final Map<String, String> routes = new ConcurrentHashMap<>();
    private final List<Object> deadLetterQueue = new CopyOnWriteArrayList<>();

    EventRouter(FunctionRuntime runtime) {
        this.runtime = runtime;
    }

    void route(String eventType, String functionName) {
        routes.put(eventType, functionName);
    }

    Optional<Object> dispatch(String eventType, Object payload) {
        var functionName = routes.get(eventType);
        if (functionName == null) {
            deadLetterQueue.add(payload);
            System.out.println("[router] No route for " + eventType + "; dead-lettered");
            return Optional.empty();
        }
        return Optional.ofNullable(runtime.invoke(functionName, eventType, payload));
    }

    int deadLetterCount() { return deadLetterQueue.size(); }
}
```

### Step 5: Orchestrator — Chaining and Fan-Out/Fan-In

Composition over the runtime. Chaining sequences calls; fan-out runs branches in parallel and aggregates their results.

```java
class Orchestrator {
    private final FunctionRuntime runtime;

    Orchestrator(FunctionRuntime runtime) {
        this.runtime = runtime;
    }

    Object chain(List<String> functions, Object initialPayload) {
        Object payload = initialPayload;
        for (var name : functions) {
            payload = runtime.invoke(name, "chain", payload);
        }
        return payload;
    }

    Object fanOut(List<String> functions, Object payload) {
        var futures = functions.stream()
            .map(name -> CompletableFuture.supplyAsync(() -> runtime.invoke(name, "fanout", payload)))
            .toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }
}
```

### Step 6: External State Store

Serverless functions must not hold state between invocations; the store is the substitute for a database/cache.

```java
class StateStore {
    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    void put(String key, Object value) {
        store.put(key, value);
    }

    Optional<Object> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    long size() {
        return store.size();
    }
}
```

### Step 7: Main

```java
public class ServerlessLab {
    public static void main(String[] args) {
        var runtime = new FunctionRuntime(Duration.ofSeconds(5));
        runtime.register(new EnrichOrderFunction());
        runtime.register(new ValidatePaymentFunction());

        var router = new EventRouter(runtime);
        router.route("order.created", "enrich-order");
        router.route("order.enriched", "validate-payment");

        var orchestrator = new Orchestrator(runtime);
        var state = new StateStore();

        var order = new ConcurrentHashMap<String, Object>();
        order.put("orderId", "ORD-1001");
        order.put("amount", 42_000L);

        var enriched = router.dispatch("order.created", order).orElseThrow();
        var validated = router.dispatch("order.enriched", enriched).orElseThrow();
        System.out.println("[demo] routed pipeline result: " + validated);

        var chained = orchestrator.chain(List.of("enrich-order", "validate-payment"), order);
        System.out.println("[demo] chained result paymentStatus: "
            + ((Map<String, Object>) chained).get("paymentStatus"));

        var fanned = orchestrator.fanOut(List.of("enrich-order", "enrich-order"), order);
        System.out.println("[demo] fan-out aggregated " + fanned.size() + " results");

        state.put("order:" + order.get("orderId"), "processing");
        System.out.println("[demo] state size: " + state.size());

        System.out.println("[demo] invocations=" + runtime.invocationCount()
            + " coldStarts=" + runtime.coldStartCount());
    }
}
```

## Complete Solution

The full compilable file, `ServerlessLab.java` in package `com.architecture.deep.lab08`:

```java
package com.architecture.deep.lab08;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerlessLab {
    public static void main(String[] args) {
        var runtime = new FunctionRuntime(Duration.ofSeconds(5));
        runtime.register(new EnrichOrderFunction());
        runtime.register(new ValidatePaymentFunction());

        var router = new EventRouter(runtime);
        router.route("order.created", "enrich-order");
        router.route("order.enriched", "validate-payment");

        var orchestrator = new Orchestrator(runtime);
        var state = new StateStore();

        var order = new ConcurrentHashMap<String, Object>();
        order.put("orderId", "ORD-1001");
        order.put("amount", 42_000L);

        var enriched = router.dispatch("order.created", order).orElseThrow();
        var validated = router.dispatch("order.enriched", enriched).orElseThrow();
        System.out.println("[demo] routed pipeline result: " + validated);

        var chained = orchestrator.chain(List.of("enrich-order", "validate-payment"), order);
        System.out.println("[demo] chained result paymentStatus: "
            + ((Map<String, Object>) chained).get("paymentStatus"));

        var fanned = orchestrator.fanOut(List.of("enrich-order", "enrich-order"), order);
        System.out.println("[demo] fan-out aggregated " + fanned.size() + " results");

        state.put("order:" + order.get("orderId"), "processing");
        System.out.println("[demo] state size: " + state.size());

        System.out.println("[demo] invocations=" + runtime.invocationCount()
            + " coldStarts=" + runtime.coldStartCount());
    }
}

record FunctionContext(String invocationId, String functionName, String eventType,
                       Object payload, boolean coldStart, Instant startedAt) {}

interface ServerlessFunction {
    String name();
    Object handle(FunctionContext context);
}

class EnrichOrderFunction implements ServerlessFunction {
    @Override
    public String name() { return "enrich-order"; }

    @Override
    public Object handle(FunctionContext context) {
        Map<String, Object> order = (Map<String, Object>) context.payload();
        order.put("region", "eu-west-1");
        order.put("enrichedAt", Instant.now().toString());
        return order;
    }
}

class ValidatePaymentFunction implements ServerlessFunction {
    @Override
    public String name() { return "validate-payment"; }

    @Override
    public Object handle(FunctionContext context) {
        Map<String, Object> order = (Map<String, Object>) context.payload();
        long amount = (long) order.get("amount");
        if (amount > 100_000) {
            throw new IllegalStateException("Payment requires manual review: " + amount);
        }
        order.put("paymentStatus", "APPROVED");
        return order;
    }
}

class FunctionRuntime {
    private final Map<String, ServerlessFunction> functions = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastInvocation = new ConcurrentHashMap<>();
    private final Duration warmTimeout;
    private final AtomicInteger coldStarts = new AtomicInteger();
    private final AtomicInteger totalInvocations = new AtomicInteger();

    FunctionRuntime(Duration warmTimeout) {
        this.warmTimeout = warmTimeout;
    }

    void register(ServerlessFunction function) {
        functions.put(function.name(), function);
    }

    Object invoke(String functionName, String eventType, Object payload) {
        var function = functions.get(functionName);
        if (function == null) throw new IllegalArgumentException("Unknown function: " + functionName);

        boolean coldStart = isColdStart(functionName);
        var context = new FunctionContext(UUID.randomUUID().toString(), functionName,
            eventType, payload, coldStart, Instant.now());
        totalInvocations.incrementAndGet();
        if (coldStart) coldStarts.incrementAndGet();

        try {
            return function.handle(context);
        } catch (RuntimeException e) {
            System.out.println("[runtime] " + functionName + " failed: " + e.getMessage());
            throw e;
        } finally {
            lastInvocation.put(functionName, Instant.now());
        }
    }

    private boolean isColdStart(String functionName) {
        var last = lastInvocation.get(functionName);
        return last == null || Duration.between(last, Instant.now()).compareTo(warmTimeout) > 0;
    }

    int coldStartCount() { return coldStarts.get(); }
    int invocationCount() { return totalInvocations.get(); }
    boolean isWarm(String functionName) { return !isColdStart(functionName); }
}

class EventRouter {
    private final FunctionRuntime runtime;
    private final Map<String, String> routes = new ConcurrentHashMap<>();
    private final List<Object> deadLetterQueue = new CopyOnWriteArrayList<>();

    EventRouter(FunctionRuntime runtime) {
        this.runtime = runtime;
    }

    void route(String eventType, String functionName) {
        routes.put(eventType, functionName);
    }

    Optional<Object> dispatch(String eventType, Object payload) {
        var functionName = routes.get(eventType);
        if (functionName == null) {
            deadLetterQueue.add(payload);
            System.out.println("[router] No route for " + eventType + "; dead-lettered");
            return Optional.empty();
        }
        return Optional.ofNullable(runtime.invoke(functionName, eventType, payload));
    }

    int deadLetterCount() { return deadLetterQueue.size(); }
}

class Orchestrator {
    private final FunctionRuntime runtime;

    Orchestrator(FunctionRuntime runtime) {
        this.runtime = runtime;
    }

    Object chain(List<String> functions, Object initialPayload) {
        Object payload = initialPayload;
        for (var name : functions) {
            payload = runtime.invoke(name, "chain", payload);
        }
        return payload;
    }

    Object fanOut(List<String> functions, Object payload) {
        var futures = functions.stream()
            .map(name -> CompletableFuture.supplyAsync(() -> runtime.invoke(name, "fanout", payload)))
            .toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }
}

class StateStore {
    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    void put(String key, Object value) {
        store.put(key, value);
    }

    Optional<Object> get(String key) {
        return Optional.ofNullable(store.get(key));
    }

    long size() {
        return store.size();
    }
}
```

## Complexity Analysis

- **Invoke**: O(1) map lookup; O(1) cold-start check.
- **Chain**: O(N) sequential invocations — latency is additive (the reason serverless pipelines are often step functions rather than nested calls).
- **Fan-out**: O(N) parallel invocations — latency is the slowest branch; time complexity of `join` is O(N) aggregate.
- **Space**: O(F) for function registry + O(N) for fan-out results.

## Test Cases

| Scenario | Expected |
|---|---|
| Route `order.created` | Dispatched to `enrich-order`; payload gains `region` |
| Route unknown event type | Dead-lettered; `deadLetterCount` increments |
| Chain enrich -> validate | Result has `paymentStatus=APPROVED` |
| Chain with amount > 100,000 | `IllegalStateException` propagated |
| Two invocations within warm timeout | Only the first is a cold start |
| Invocation after warm timeout | Cold start counted again |
| Fan-out of 2 branches | Aggregated list of size 2 |

Example run:

```
[runtime] validate-payment failed: Payment requires manual review: 100000
[demo] routed pipeline result: {orderId=ORD-1001, amount=42000, enrichedAt=..., region=eu-west-1, paymentStatus=APPROVED}
[demo] chained result paymentStatus: APPROVED
[demo] fan-out aggregated 2 results
[demo] state size: 1
[demo] invocations=7 coldStarts=3
```

## Follow-Up Questions

1. **Why does cold start matter in production?** Cold starts add 100ms-1s latency (JVM/Spring worst case). Mitigations: keep-warm pings, SnapStart/snapshotting, smaller runtimes, provisioned concurrency for hot paths.
2. **How do you make chaining reliable?** Route each function's output to a queue (SQS) with a retry policy, rather than synchronous nesting — that gives durability and backpressure for free.
3. **Where should the dead-letter queue live?** A real DLQ (SQS DLQ) with the original event and error metadata, replayed after a fix; here the in-memory list is the placeholder.
4. **How do you scale fan-out beyond the JVM?** Each branch is an event to a queue with its own function instance — the broker (SQS/SNS) is the fan-out mechanism.
5. **How do you enforce the statelessness rule?** State goes only through `StateStore` (DynamoDB in production); the runtime could even reject functions that write instance fields — a rule worth documenting and linting.
6. **How do you test cold-start logic?** Clock injection: pass a `Clock` to the runtime so tests can jump the warm window deterministically.
7. **How does this map to AWS Step Functions?** `chain` and `fanOut` are `States` — `Pass`/`Task` sequences and `Parallel` states with `ResultPath` aggregation; the orchestrator is a mini Step Functions engine.
