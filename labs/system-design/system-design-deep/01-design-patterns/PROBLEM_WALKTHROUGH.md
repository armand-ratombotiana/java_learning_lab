# Problem Walkthrough: Transactional Outbox with Event Relay

## Problem Statement

An order service accepts order placement requests and persists order state to a
relational database. Every committed order must produce an `OrderPlaced` event that
is delivered to a message broker (Kafka) so downstream services — inventory,
shipping, analytics — can react. The service must guarantee that **no committed
order ever loses its event**, even when the broker is down, the relay crashes, or
the process restarts mid-delivery. Distributed transactions (2PC/XA) are not
available.

Design and implement the transactional outbox pattern with an event relay:
business state and outbox rows are written in a single local transaction, and a
relay process polls the outbox table, publishes events to the broker, and removes
published rows.

## Requirements

- **Atomicity:** An outbox row for an event exists if and only if the associated
  business write committed. There is no window where the order exists but its
  event is permanently gone.
- **At-least-once delivery:** Every event is eventually published. Duplicates are
  permitted because consumers are idempotent.
- **Per-aggregate ordering:** Events for the same order arrive at the broker in
  production order (insertion order in the outbox table).
- **No loss on crash:** A crash at *any* point — before publish, between publish
  and delete, or mid-batch — must not lose events.
- **Decoupled availability:** A broker outage delays delivery but never blocks the
  order service's writes.
- **Observability:** The relay must expose metrics (outbox size, relayed count,
  failure rate) so operators can alert on delivery lag.

## Constraints & Assumptions

- Single relational database; outbox table lives in the same database as the
  business tables.
- Broker is Kafka; at-least-once is an acceptable contract with idempotent
  consumers.
- Peak volume ~2,000 events/sec, bursty; a single relay is the starting topology.
- The event payload is an opaque, self-contained JSON blob at relay time.
- Ordering is required only per aggregate (per order), not globally.

## Background: The Dual-Write Problem

Any design that touches both a database and a broker faces the **dual-write
problem**: the two writes cannot be atomic. Every naive ordering leaves a hole:

1. **Publish first, commit second.** If the DB commit fails, the broker carries an
   event for state that never existed — phantom events that downstream systems
   will act on.
2. **Commit first, publish second.** If the publish fails — or the process crashes
   between the two statements — the order exists and its event is lost forever.
   There is nothing left to retry: the failure is invisible and unrecoverable.

The transactional outbox eliminates the hole by making the *pending event itself*
part of the committed transaction. The outbox table is a durable record of
"events that still need delivery," and the relay is a self-healing worker that
reduces it to empty.

## Why Not the Alternatives?

| Approach | Cost / Why Not |
|----------|----------------|
| **2PC / XA (DB + broker)** | Kafka has no XA participant API. Coordinators are bottlenecks and single points of failure; in-doubt transactions need manual intervention; a broker outage can stall DB commits. |
| **Publish-then-commit** | Phantom events on commit failure; no recovery path. |
| **Commit-then-publish (in-process)** | Silent event loss on publish failure or crash between statements. |
| **CDC (binlog → Debezium → Kafka)** | Valid and widely used, but adds a second operational system, replication lag, and schema-change coupling. Works well layered *on top of* an outbox table. |
| **Scheduled job scanning for "new orders"** | Fragile heuristics ("WHERE created_at > last_run") drift under clock skew and batch failures; no explicit delivery contract. |

The outbox pattern keeps the contract in application code, is testable, and
scales by adding relay workers.

## Solution Overview

```
+----------------------+                +-------------------+
|    Order Service     |    poll(50ms)  |    Event Relay    |
| (single transaction) | =============> | ----------------- |
| 1. INSERT order      |                | 1. claim batch    |
| 2. INSERT outbox row |                | 2. publish -> broker
+----------+-----------+                | 3. delete row     |
           |                            +---------+---------+
           v                                      v
   +---------------+                      +-----------------+
   |   Postgres    |                      |  Kafka: orders  |
   | order + outbox|                      +-----------------+
   +---------------+
```

### Invariant

> An outbox row exists until its event has been **acknowledged** by the broker.

The relay's delete is the *only* operation that removes a row, and it runs only
after a successful publish. Every crash window degrades to a duplicate, never to a
loss.

## Step-by-Step Solution

### Step 1: Design the outbox table

```
CREATE TABLE outbox (
    id            BIGSERIAL PRIMARY KEY,   -- monotonic; ordering + idempotency
    aggregate_type TEXT NOT NULL,          -- e.g. 'Order'
    aggregate_id   TEXT NOT NULL,          -- partition key; e.g. order id
    event_type     TEXT NOT NULL,          -- e.g. 'OrderPlaced'
    payload        JSONB NOT NULL,         -- opaque to the relay
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_outbox_id ON outbox (id);
```

Design choices:
- `id` is monotonic and gives total insertion order.
- No `processed` boolean — delete-on-success keeps the table small and the index hot.
- `aggregate_id` doubles as the Kafka partition key for per-aggregate ordering.

### Step 2: Write the business row and the outbox row in one transaction

The order service performs both inserts inside a single database transaction.
Because they commit together, the invariant "outbox row exists iff business write
committed" holds by construction. The demo models this with a single
synchronization point over the outbox table.

### Step 3: Build the relay loop

The relay is a long-running worker with three phases per iteration:

1. **Claim** a bounded batch of rows in `id` order (up to `batchSize`).
2. **Publish** each row to the broker under key `aggregate_id`. On failure, keep
   the row and retry on the next poll (with backoff in production).
3. **Delete** the row only after the publish succeeds.

An in-flight set prevents the same row from being processed twice concurrently if
the relay were to claim the batch again before finishing (relevant when multiple
relays shard work with `SELECT ... FOR UPDATE SKIP LOCKED`).

### Step 4: Enforce at-least-once with idempotent consumers

The relay cannot avoid duplicates: if it crashes between publish and delete, the
row is republished. Consumers must therefore deduplicate on a stable key — the
outbox `id`, or `aggregate_id + event sequence`. This converts "exactly-once" from
a transport property into a consumer contract, which is the only honest way to
achieve exactly-once *effect* across two systems.

### Step 5: Bound the polling and observe the system

Poll on an interval (here 50 ms; production: fast-poll hybrid or advisory lock),
and expose metrics: outbox table size (lag), relayed counter (throughput), publish
failure count, in-flight count. "Outbox size growing while relayed is flat" is the
primary alert for broker trouble.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab01;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 01: Transactional Outbox with Event Relay.
 * Demonstrates: atomic outbox append, relay loop with in-flight tracking,
 * at-least-once delivery, idempotency-ready keying, crash-window analysis.
 */
public class TransactionalOutboxLab {

    /** A row in the outbox table. */
    public record OutboxRecord(long id, String aggregateType, String aggregateId,
                               String eventType, String payload, Instant createdAt) {}

    /** In-memory stand-in for the Postgres outbox table (concurrent, ordered by id). */
    public static final class OutboxTable {
        private final Map<Long, OutboxRecord> rows = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);
        private final Object txLock = new Object(); // models the single DB transaction

        public long append(String aggregateType, String aggregateId,
                           String eventType, String payload) {
            long id = idGen.getAndIncrement();
            rows.put(id, new OutboxRecord(id, aggregateType, aggregateId,
                    eventType, payload, Instant.now()));
            return id;
        }

        /** Claim up to batchSize rows in strict insertion order. */
        public List<OutboxRecord> claimBatch(int batchSize) {
            synchronized (txLock) {
                return rows.values().stream()
                        .sorted(Comparator.comparingLong(OutboxRecord::id))
                        .limit(batchSize)
                        .toList();
            }
        }

        public void delete(long id) { rows.remove(id); }
        public long size() { return rows.size(); }
        public boolean isEmpty() { return rows.isEmpty(); }
    }

    /** Stand-in for Kafka: counts and logs published messages per topic. */
    public static final class EventBroker {
        private final Map<String, AtomicLong> counts = new ConcurrentHashMap<>();
        private final List<String> log = new CopyOnWriteArrayList<>();

        public void publish(String topic, String key, String payload) {
            counts.computeIfAbsent(topic, t -> new AtomicLong()).incrementAndGet();
            log.add("%s|key=%s|%s".formatted(topic, key, payload));
        }

        public long count(String topic) {
            return counts.getOrDefault(topic, new AtomicLong()).get();
        }

        public List<String> log() { return List.copyOf(log); }
    }

    /** The order service: business write + outbox append commit atomically. */
    public static final class OrderService {
        private final OutboxTable outbox;

        public OrderService(OutboxTable outbox) { this.outbox = outbox; }

        /** Single "transaction": both inserts commit together or not at all. */
        public long placeOrder(String orderId, String customerId, double total) {
            synchronized (outbox) {
                String payload = "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"total\":%.2f}"
                        .formatted(orderId, customerId, total);
                return outbox.append("Order", orderId, "OrderPlaced", payload);
            }
        }
    }

    /** The relay: poll -> claim -> publish -> delete, with retry-on-failure. */
    public static final class EventRelay implements Runnable {
        private final OutboxTable outbox;
        private final EventBroker broker;
        private final Duration pollInterval;
        private final int batchSize;
        private final Set<Long> inFlight = ConcurrentHashMap.newKeySet();
        private final AtomicLong relayed = new AtomicLong();
        private volatile boolean running = true;

        public EventRelay(OutboxTable outbox, EventBroker broker,
                          Duration pollInterval, int batchSize) {
            this.outbox = outbox;
            this.broker = broker;
            this.pollInterval = pollInterval;
            this.batchSize = batchSize;
        }

        public void stop() { running = false; }
        public long relayed() { return relayed.get(); }

        @Override
        public void run() {
            while (running) {
                try {
                    for (OutboxRecord rec : outbox.claimBatch(batchSize)) {
                        if (!inFlight.add(rec.id())) continue; // already claimed
                        try {
                            // Delete happens ONLY after acknowledged publish.
                            broker.publish("orders", rec.aggregateId(), rec.payload());
                            outbox.delete(rec.id());
                            relayed.incrementAndGet();
                        } catch (RuntimeException ex) {
                            System.out.printf("relay: publish failed for #%d: %s (will retry)%n",
                                    rec.id(), ex.getMessage());
                        } finally {
                            inFlight.remove(rec.id());
                        }
                    }
                    Thread.sleep(pollInterval.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OutboxTable outbox = new OutboxTable();
        EventBroker broker = new EventBroker();
        OrderService orders = new OrderService(outbox);

        orders.placeOrder("ORD-001", "CUST-7", 129.99);
        orders.placeOrder("ORD-002", "CUST-9", 59.50);
        orders.placeOrder("ORD-003", "CUST-2", 340.00);
        System.out.println("Outbox rows after 3 orders: " + outbox.size());

        EventRelay relay = new EventRelay(outbox, broker, Duration.ofMillis(50), 2);
        Thread relayThread = new Thread(relay, "outbox-relay");
        relayThread.start();

        Thread.sleep(300); // let the relay drain the table
        relay.stop();
        relayThread.join();

        System.out.println("Outbox rows after relay: " + outbox.size());
        System.out.println("Events published: " + broker.count("orders"));
        broker.log().forEach(System.out::println);

        boolean consistent = outbox.isEmpty() && broker.count("orders") == 3;
        System.out.println("Consistency check: " + (consistent ? "PASS (no loss, no duplicates)" : "FAIL"));
        System.out.println("Relay throughput: " + relay.relayed() + " events");
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Outbox append | O(1) | O(1) | HashMap put; `BIGSERIAL` id is O(1) amortized in practice |
| Claim batch | O(b log b) | O(b) | Sorted scan of a bounded batch `b`; with a `WHERE id > last_id` cursor it is O(b) over an index |
| Publish + delete | O(1) amortized | O(1) | Broker ack is the dominant real-world cost |
| Full drain | O(n) total | O(n) transient | `n` rows in table; batching keeps memory bounded |
| Metrics | O(1) | O(1) | Atomic counters |

**Space:** The outbox table is self-emptying (delete-on-success), so its size is
bounded by *delivery lag*, not total throughput. A healthy relay keeps it near
zero. The in-flight set is bounded by batch size.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Broker down at publish time | Row stays; relay retries next poll | Event is durable in outbox; availability of the writer is unaffected |
| Crash between publish and delete | Row republished on restart | Duplicate is possible, loss is not; idempotent consumer dedupes |
| Crash mid-batch | Unprocessed rows remain; claimed-but-unpublished rows re-claimed | Claim is not commit; in-flight set is per-process |
| Duplicate publish from double-claim | In-flight set skips the second claim | Within one relay, a row is never published concurrently |
| Payload too large / schema drift | Relay treats payload as opaque; producer and consumers own format | Relay stays dumb; registry handles evolution |
| Relay lag / poison message | One bad row blocks the batch cursor | Production fix: publish-with-dead-letter and advance `last_id` |
| Clock skew across relays | Outbox `id` ordering may not match wall clock | Ordering uses `id`, never `created_at` |

## Verification Walkthrough

1. **Happy path:** 3 orders placed, relay drains the table, broker log shows 3
   events keyed by order id, outbox empty, consistency check PASS.
2. **No-loss on failure:** inject a broker exception on the first publish — the row
   survives, subsequent polls retry and deliver it. The assertable contract is
   *no loss, duplicates allowed*.
3. **Ordering:** publish keys are `ORD-xxx`; Kafka routes per key, so per-aggregate
   order equals outbox `id` order.

## Follow-Up Questions

1. **Scale to N relays:** claim rows with `SELECT ... FOR UPDATE SKIP LOCKED`;
   each relay claims disjoint batches; per-aggregate ordering still holds because
   rows are claimed in `id` order and each order's events form a contiguous id range.
2. **Exactly-once semantics:** impossible across DB + broker; implement
   exactly-once *effect* with consumer-side idempotency keys (outbox `id`).
3. **Should the relay batch publishes to Kafka?** Yes — `send()` with flush on a
   bounded window amortizes round trips; batch size is a throughput/latency knob.
4. **Outbox table growth:** healthy relays keep it near zero; alert on size
   threshold; backfill via re-publish from the topic if rows are pruned.
5. **Combine with saga:** each saga step appends its outcome event in the same
   transaction as its local state change; the orchestrator consumes the topic and
   issues the next command — the outbox makes the saga crash-safe.
6. **CDC instead of a relay:** Debezium on the outbox table removes the relay
   process but adds binlog-lag latency and operational coupling; the outbox table
   is the same either way.

## Summary

- The outbox pattern replaces an unsolvable cross-system atomicity problem with a
  retryable local one: **transaction = correctness, relay = delivery**.
- The contract is **at-least-once + idempotent consumers**; never claim
  exactly-once across a database and a broker.
- **Delete-after-acknowledgment** is the single invariant that makes crashes
  degrade to duplicates instead of losses.
- Per-aggregate ordering comes from monotonic ids + Kafka partition keys.
- Scale by sharding claims across relays; observe via outbox size, relayed count,
  and failure rate.
