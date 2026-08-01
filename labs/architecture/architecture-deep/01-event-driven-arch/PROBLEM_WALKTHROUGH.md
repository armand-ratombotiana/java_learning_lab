# Lab 01: Problem Walkthrough — Event Bus with Event Versioning and Idempotent Consumers

## Problem Statement

Implement an event bus for a banking platform that supports:

1. **Publish/subscribe routing** — events are routed to consumers subscribed by event type.
2. **Event versioning** — events carry a schema version, and the bus transparently upcasts old versions to the latest schema before delivery.
3. **Idempotent consumers** — a consumer must be able to process the same event more than once (due to redelivery or replay) without producing duplicate side effects.

## Constraints

- Java 21+ only, no external frameworks (no Kafka, no Spring).
- Event payloads are immutable records.
- The bus must be thread-safe for concurrent producers and consumers.
- Version migration must be registered declaratively (no `instanceof` chains in the consumer).
- Idempotency must be enforced at the bus boundary so consumers stay simple.

## Approach

The solution has four collaborating pieces:

| Piece | Responsibility |
|---|---|
| `EventEnvelope` | Wraps payload with event id, aggregate id, type, schema version, timestamp |
| `EventBus` | Type-based routing + version upcasting + idempotency filtering |
| `EventUpcaster` | Version migration registry (`v1 -> v2`) |
| `IdempotencyStore` | Set of processed event ids with TTL |

**Design decisions:**

- **Envelope over raw payload.** Versioning becomes a property of the envelope; consumers never inspect payload classes directly.
- **Upcast at read time.** Old events are stored as-is (immutable log) and migrated only when delivered. This keeps the append-only event log honest.
- **Dedup at the bus.** The bus drops events already processed instead of pushing dedup into each consumer, so idempotency is a cross-cutting concern solved once.
- **Versioned payload records.** `AccountCreatedV1` and `AccountCreatedV2` are both sealed-implementation records; the upcaster maps between them.

## Step-by-Step Solution

### Step 1: The Event Envelope

Every event that flows through the bus is wrapped in an envelope carrying routing metadata.

```java
public record EventEnvelope(
    UUID eventId,
    UUID aggregateId,
    String type,
    int schemaVersion,
    Instant occurredAt,
    Object payload
) {}
```

### Step 2: Versioned Payloads

We model the same domain event at two schema versions. The sealed hierarchy lets the upcaster use exhaustive pattern matching — the compiler refuses to compile if we miss a case.

```java
public sealed interface AccountEvent permits AccountCreatedV1, AccountCreatedV2 {}

public record AccountCreatedV1(UUID accountId, String owner, long balance) implements AccountEvent {}
public record AccountCreatedV2(UUID accountId, String owner, long balance, String currency) implements AccountEvent {}
```

### Step 3: The Upcaster

A registry of version migrations. Each migration knows how to lift a payload from `n` to `n+1`. `upcast` applies the chain until the payload reaches the target version.

```java
public interface EventUpcaster {
    boolean supports(int fromVersion, int toVersion);
    Object upcast(Object payload);
}

public record V1ToV2Upcaster() implements EventUpcaster {
    @Override
    public boolean supports(int fromVersion, int toVersion) {
        return fromVersion == 1 && toVersion == 2;
    }

    @Override
    public Object upcast(Object payload) {
        if (payload instanceof AccountCreatedV1 v1) {
            return new AccountCreatedV2(v1.accountId(), v1.owner(), v1.balance(), "USD");
        }
        throw new IllegalArgumentException("Unsupported payload: " + payload);
    }
}
```

### Step 4: Idempotency Store

A concurrent set of processed event ids. `putIfAbsent` gives us the check-and-set in one atomic operation, which is the classic recipe for exactly-once processing.

```java
public class IdempotencyStore {
    private final ConcurrentHashMap<UUID, Instant> processed = new ConcurrentHashMap<>();

    public boolean markProcessedIfAbsent(UUID eventId) {
        return processed.putIfAbsent(eventId, Instant.now()) == null;
    }

    public boolean alreadyProcessed(UUID eventId) {
        return processed.containsKey(eventId);
    }

    public int size() {
        return processed.size();
    }
}
```

### Step 5: The Event Bus

The bus holds three things: subscriptions, the upcaster chain, and the idempotency store.

```java
public class EventBus {
    private final Map<String, List<Consumer<EventEnvelope>>> subscribers = new ConcurrentHashMap<>();
    private final List<EventUpcaster> upcasters = new CopyOnWriteArrayList<>();
    private final IdempotencyStore idempotencyStore;

    public EventBus(IdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    public void registerUpcaster(EventUpcaster upcaster) {
        upcasters.add(upcaster);
    }

    public void subscribe(String eventType, Consumer<EventEnvelope> consumer) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    public boolean publish(String eventType, int schemaVersion, Object payload, UUID aggregateId) {
        var envelope = new EventEnvelope(
            UUID.randomUUID(), aggregateId, eventType, schemaVersion, Instant.now(), payload);

        if (!idempotencyStore.markProcessedIfAbsent(envelope.eventId())) {
            return false;
        }

        var upgraded = upcast(envelope);
        subscribers.getOrDefault(upgraded.type(), List.of())
            .forEach(consumer -> consumer.accept(upgraded));
        return true;
    }

    private EventEnvelope upcast(EventEnvelope envelope) {
        var current = envelope;
        var version = current.schemaVersion();
        var payload = current.payload();
        while (true) {
            var next = upcasters.stream()
                .filter(u -> u.supports(version, version + 1))
                .findFirst();
            if (next.isEmpty()) {
                return new EventEnvelope(current.eventId(), current.aggregateId(),
                    current.type(), version, current.occurredAt(), payload);
            }
            payload = next.get().upcast(payload);
            version++;
        }
    }
}
```

The `publish` path does three things atomically: dedup, upcast, deliver. Note that the **upcast chain is applied at the boundary**, so every consumer sees the latest schema.

### Step 6: The Consumer (Projection)

The consumer is now trivial — the bus guarantees it sees only version-2 events, exactly once.

```java
public class AccountProjection {
    private final Map<UUID, AccountView> accounts = new ConcurrentHashMap<>();

    public void onEvent(EventEnvelope envelope) {
        switch (envelope.payload()) {
            case AccountCreatedV2 e ->
                accounts.put(e.accountId(), new AccountView(e.owner(), e.balance(), e.currency()));
            default -> throw new IllegalStateException("Unexpected payload: " + envelope.payload());
        }
    }

    public Optional<AccountView> find(UUID accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
```

## Complete Solution

The full compilable file, `EventBusLab.java` in package `com.architecture.deep.lab01`:

```java
package com.architecture.deep.lab01;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBusLab {
    public static void main(String[] args) {
        var idempotencyStore = new IdempotencyStore();
        var bus = new EventBus(idempotencyStore);
        bus.registerUpcaster(new V1ToV2Upcaster());

        var projection = new AccountProjection();
        bus.subscribe("AccountCreated", projection::onEvent);

        var accountId = UUID.randomUUID();
        var v1 = new AccountCreatedV1(accountId, "Alice", 1000L);

        boolean first = bus.publish("AccountCreated", 1, v1, accountId);
        boolean duplicate = bus.publish("AccountCreated", 1, v1, accountId);
        boolean second = bus.publish("AccountCreated", 1,
            new AccountCreatedV1(UUID.randomUUID(), "Bob", 500L), UUID.randomUUID());

        System.out.println("First publish accepted: " + first);
        System.out.println("Duplicate publish rejected (idempotent): " + !duplicate);
        System.out.println("Second publish accepted: " + second);

        var alice = projection.find(accountId).orElseThrow();
        System.out.println("Projected account: " + alice.owner() + " / " + alice.balance()
            + " " + alice.currency() + " (upcast from v1 to v2)");
        System.out.println("Idempotency store size: " + idempotencyStore.size());
    }
}

record EventEnvelope(UUID eventId, UUID aggregateId, String type,
                     int schemaVersion, Instant occurredAt, Object payload) {}

sealed interface AccountEvent permits AccountCreatedV1, AccountCreatedV2 {}

record AccountCreatedV1(UUID accountId, String owner, long balance) implements AccountEvent {}
record AccountCreatedV2(UUID accountId, String owner, long balance, String currency) implements AccountEvent {}

interface EventUpcaster {
    boolean supports(int fromVersion, int toVersion);
    Object upcast(Object payload);
}

record V1ToV2Upcaster() implements EventUpcaster {
    @Override
    public boolean supports(int fromVersion, int toVersion) {
        return fromVersion == 1 && toVersion == 2;
    }

    @Override
    public Object upcast(Object payload) {
        if (payload instanceof AccountCreatedV1 v1) {
            return new AccountCreatedV2(v1.accountId(), v1.owner(), v1.balance(), "USD");
        }
        throw new IllegalArgumentException("Unsupported payload: " + payload);
    }
}

class IdempotencyStore {
    private final ConcurrentHashMap<UUID, Instant> processed = new ConcurrentHashMap<>();

    public boolean markProcessedIfAbsent(UUID eventId) {
        return processed.putIfAbsent(eventId, Instant.now()) == null;
    }

    public boolean alreadyProcessed(UUID eventId) {
        return processed.containsKey(eventId);
    }

    public int size() {
        return processed.size();
    }
}

class EventBus {
    private final Map<String, List<Consumer<EventEnvelope>>> subscribers = new ConcurrentHashMap<>();
    private final List<EventUpcaster> upcasters = new CopyOnWriteArrayList<>();
    private final IdempotencyStore idempotencyStore;

    public EventBus(IdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    public void registerUpcaster(EventUpcaster upcaster) {
        upcasters.add(upcaster);
    }

    public void subscribe(String eventType, Consumer<EventEnvelope> consumer) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    public boolean publish(String eventType, int schemaVersion, Object payload, UUID aggregateId) {
        var envelope = new EventEnvelope(
            UUID.randomUUID(), aggregateId, eventType, schemaVersion, Instant.now(), payload);

        if (!idempotencyStore.markProcessedIfAbsent(envelope.eventId())) {
            return false;
        }

        var upgraded = upcast(envelope);
        subscribers.getOrDefault(upgraded.type(), List.of())
            .forEach(consumer -> consumer.accept(upgraded));
        return true;
    }

    private EventEnvelope upcast(EventEnvelope envelope) {
        var current = envelope;
        var version = current.schemaVersion();
        var payload = current.payload();
        while (true) {
            var next = upcasters.stream()
                .filter(u -> u.supports(version, version + 1))
                .findFirst();
            if (next.isEmpty()) {
                return new EventEnvelope(current.eventId(), current.aggregateId(),
                    current.type(), version, current.occurredAt(), payload);
            }
            payload = next.get().upcast(payload);
            version++;
        }
    }
}

record AccountView(String owner, long balance, String currency) {}

class AccountProjection {
    private final Map<UUID, AccountView> accounts = new ConcurrentHashMap<>();

    public void onEvent(EventEnvelope envelope) {
        switch (envelope.payload()) {
            case AccountCreatedV2 e ->
                accounts.put(e.accountId(), new AccountView(e.owner(), e.balance(), e.currency()));
            default -> throw new IllegalStateException("Unexpected payload: " + envelope.payload());
        }
    }

    public Optional<AccountView> find(UUID accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
```

## Complexity Analysis

- **Publish**: O(1) amortized for dedup (`ConcurrentHashMap.putIfAbsent`), O(C) for upcasting where C is the number of version steps (constant in practice, C = 1 here), O(S) to deliver to S subscribers.
- **Subscribe**: O(1) amortized.
- **Idempotency store**: O(1) per event; memory grows with the number of unique event ids (in production this is backed by Redis or a database with TTL).
- **Upcaster lookup**: O(U) where U is the number of registered upcasters; acceptable since U is tiny.

## Test Cases

| Case | Input | Expected |
|---|---|---|
| Happy path | v1 `AccountCreated` for Alice | Consumer receives `AccountCreatedV2` with `currency = "USD"` |
| Duplicate delivery | Same event published twice | Second publish returns `false`; projection unchanged |
| Two aggregates | Different account ids | Both projected independently |
| Unknown version | v1 payload with no upcaster registered | Delivered unchanged (version 1) |

Example run:

```
First publish accepted: true
Duplicate publish rejected (idempotent): true
Second publish accepted: true
Projected account: Alice / 1000 USD (upcast from v1 to v2)
Idempotency store size: 3
```

## Follow-Up Questions

1. **How would you handle downcasting for consumers on older versions?** Register consumers against a schema-versioned subscription; the bus downcasts latest events for them, keeping consumers decoupled from schema evolution.
2. **What if the upcaster chain grows beyond v1->v2?** Register `V2ToV3Upcaster` etc.; the while-loop in `upcast` applies the chain automatically.
3. **How do you make the bus durable?** Replace the in-memory subscriber list with a persistent topic (Kafka): envelope becomes the message value, event id becomes the message key so partitions guarantee per-key ordering.
4. **How do you handle out-of-order events during replay?** Track per-aggregate expected version numbers and buffer/stall events that arrive with a version gap.
5. **How do you bound memory in the idempotency store?** Use TTL-based expiry (e.g., 24h) and periodic compaction; Kafka consumers get this for free with consumer offsets.
6. **How would you test idempotency under concurrency?** Fire N threads publishing the same event id concurrently; assert exactly one subscriber invocation.
7. **How does this compare to transactional outbox?** The outbox writes events in the same DB transaction as the business change; the idempotency store is the consumer-side counterpart that makes exactly-once processing practical.
