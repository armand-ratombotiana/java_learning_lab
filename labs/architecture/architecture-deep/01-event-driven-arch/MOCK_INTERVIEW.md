# Lab 01: Mock Interview — Event-Driven Architecture

**Role**: Senior Software Architect
**Duration**: 60 minutes
**Focus**: Event bus design, event versioning, idempotency, at-least-once vs exactly-once

---

**Interviewer**: "Welcome. We're building a payments platform that processes 500,000 events per
second. Let's design the event backbone. Start with the problem: how do you deliver domain events
reliably to multiple consumers while allowing the event schema to evolve over time?"

**Candidate**: "I'd separate three concerns: transport, schema, and delivery guarantees. For
transport, a partitioned log like Kafka gives us ordering per key and replay capability. For schema,
every event carries a schema version, and producers write to a registry so consumers can validate
compatibility. For delivery, I'd commit to at-least-once delivery and make consumers idempotent
rather than pretending the transport gives exactly-once."

**Interviewer**: "Why at-least-once instead of exactly-once?"

**Candidate**: "True exactly-once across a distributed system requires coordinated transactions
between broker, consumers, and side-effect stores — Kafka's exactly-once works within a single
KafkaStreams topology, but breaks the moment a consumer calls an external API or writes to a
database. At-least-once plus idempotent consumers is the pattern that generalizes to any side
effect, and it's what most production systems actually do."

**Interviewer**: "Walk me through the idempotency mechanism."

**Candidate**: "Each event has a globally unique event ID. When a consumer processes an event, it
stores the event ID together with its result in the same local transaction — if the consumer writes
to Postgres, it's an `INSERT INTO processed_events` alongside the business write. On redelivery, the
consumer looks up the event ID first. If present, it skips the business logic and acknowledges the
message. The key is that the dedup record and the side effect commit atomically."

**Interviewer**: "What if the consumer's side effect is an external API call that has no transaction
with the dedup store?"

**Candidate**: "Then the API should accept an idempotency key — we send the event ID as a header.
The external service dedups server-side. If the API doesn't support that, we fall back to an outbox
pattern: the consumer writes the intent to its outbox table, a relay publishes it, and the external
call becomes retryable with a stored response. The event ID stays the dedup key through the whole
chain."

**Interviewer**: "Now schema evolution. You said events carry a version. How does a consumer that
was written six months ago survive a schema change?"

**Candidate**: "Two mechanisms: upcasting and the schema registry. Old events stay in the log in
their original format — never rewrite history. When a consumer subscribes, we register it against a
schema version. The delivery pipeline upcasts events forward to the consumer's expected version, or
the consumer subscribes to the latest and applies its own migration. Either way, consumer code only
ever sees one schema, so there's no scattered `if (version == 1)` logic."

**Interviewer**: "Let's say you have `AccountCreated` v1 with `{owner, balance}` and v2 adds
`{currency}`. Show me the upcast strategy."

**Candidate**: "We register a migration chain: `v1 -> v2 -> v3`, where each migration is a pure
function. The bus applies the chain before delivery. In Java 21 terms, I'd model payloads as sealed
records and the upcaster uses pattern matching:

```java
public record AccountCreatedV1(UUID accountId, String owner, long balance) {}
public record AccountCreatedV2(UUID accountId, String owner, long balance, String currency) {}

public Object upcast(Object payload) {
    if (payload instanceof AccountCreatedV1 v1) {
        return new AccountCreatedV2(v1.accountId(), v1.owner(), v1.balance(), "USD");
    }
    throw new IllegalArgumentException("Unsupported payload: " + payload);
}
```

The compiler exhaustiveness check on sealed types means when we add v3, we must handle all old
versions — the type system enforces the migration matrix."

**Interviewer**: "What about backward compatibility — a producer that still emits v1 while consumers
expect v2?"

**Candidate**: "The schema registry validates compatibility rules at publish time. Backward
compatibility means old producers can keep writing; the bus upcasts. Forward compatibility means new
producers writing v2 won't break old consumers — those consumers get a downcast projection, which we
handle by having the registry produce a migration projection on demand. In practice, most teams
enforce backward compatibility and require consumers to keep pace within a bounded window."

**Interviewer**: "How do you route events to the right consumers?"

**Candidate**: "Subscription by event type with a typed handler. The bus keeps a map of event type
to registered consumers. Delivery order within a type follows partition order. For the in-memory
prototype we use a `ConcurrentHashMap` of type to handler lists; for production the topic name
encodes the type."

**Interviewer**: "What happens if a consumer throws during processing?"

**Candidate**: "That's the at-least-once retry path. The consumer's processing loop catches the
exception, logs the offset, and retries with exponential backoff. After N retries it parks the event
in a dead-letter queue. The event ID in the envelope tells us exactly what failed, and the DLQ can
be replayed once the root cause is fixed."

**Interviewer**: "How do you guarantee ordering with multiple consumers of the same aggregate?"

**Candidate**: "Partition by aggregate ID. All events for one aggregate go to the same partition,
and one consumer group reads that partition, so per-aggregate ordering is preserved. The in-memory
analog is a striped lock per aggregate ID, or per-aggregate version numbers that let us reject
out-of-order events."

**Interviewer**: "Now testing. How would you test an event-driven system?"

**Candidate**: "Three layers. Unit tests on the bus: publish a v1 event, assert consumers receive v2
— that's the upcasting contract. Property tests on migrations: every event in the store must upcast
to the latest version without loss of information. Integration tests on consumers: inject
duplicates, out-of-order events, and poison-pill messages, and assert the consumer stays
consistent."

**Interviewer**: "How do you trace an event through the system when something goes wrong?"

**Candidate**: "Correlation IDs. The envelope carries a trace ID that propagates to downstream
events and into logs. Every bus delivery logs envelope ID, type, version, consumer, and duration.
That gives us a full journey: produced at T1, upcast to v2 at T2, delivered to projection at T3,
acknowledged at T4."

**Interviewer**: "What's the biggest mistake teams make with event buses?"

**Candidate**: "Treating the bus as a synchronous request-response channel, which couples producers
and consumers and reintroduces the very coupling events are meant to remove. Second: sharing the
event table as a point-to-point integration contract instead of modeling domain events in the
ubiquitous language. Third: no idempotency and no versioning — the two things that make event-driven
systems safe to replay — and then discovering a year later that replay is impossible."

**Interviewer**: "Suppose you replay the event log from day one. What breaks?"

**Candidate**: "Any consumer with external side effects would double-fire them — that's why
idempotency matters for replay. Time-dependent projections would recompute with wrong 'current'
timestamps — that's why projections must be state-machine based, not wall-clock based. And old
consumers would choke on new schemas — that's why versioning is non-negotiable. Replay is the
ultimate test of all three designs."

**Interviewer**: "You're at a company that already has events but no versioning. How do you
introduce it?"

**Candidate**: "You can't retrofit versions onto historical payloads, so we introduce an envelope:
wrap all existing payloads with `{type, version: 1, eventId}` as they're written going forward, and
make the upcaster path optional. New consumers subscribe through the bus and see envelopes; old
consumers keep reading raw. Over time, the upcast chain absorbs schema changes. We'd also add event
ID generation at the producer, which is a prerequisite for idempotency."

**Interviewer**: "How do you decide between event sourcing and just publishing events?"

**Candidate**: "Event sourcing stores the log as the system of record and rebuilds state from it —
great for auditability, temporal queries, and complex domains like banking. Plain event publishing
keeps a current-state database and emits events as notifications — cheaper to operate, but you can't
rebuild state. For this payments platform, the ledger itself is the source of truth, so I'd use
event sourcing for the ledger and plain publishing for downstream notifications."

**Interviewer**: "What's your answer to the 'two generals' problem in the event bus context?"

**Candidate**: "Produce and publish atomically. The transactional outbox solves it: the business
write and the event insert happen in one DB transaction; a relay reads the outbox and publishes to
the broker. If the relay crashes, it resumes from its last offset, and the consumer-side idempotency
store absorbs the duplicates. Two generals is fundamentally unsolvable in messaging, so we shrink
the window to the DB transaction and make the rest idempotent."

**Interviewer**: "Performance: how do you avoid the bus becoming the bottleneck at 500k events/sec?"

**Candidate**: "Batch at every layer: producers batch writes, the broker groups commits, consumers
process in batches with manual offsets. Partition count scales consumers linearly. For the in-memory
design, publish delivery is per-subscriber sequential but subscribers run in parallel executor
pools. Avoid per-event locks; use `ConcurrentHashMap` and copy-on-write subscriber lists."

**Interviewer**: "Final question: what does a 'good' event name look like?"

**Candidate**: "A past-tense domain statement in the ubiquitous language: `MoneyDeposited`, not
`AccountUpdated` or `DepositHappened`... `MoneyDeposited` with an aggregate ID, amount, and
occurred-at timestamp. The name says what happened in the domain, not what the UI did, which keeps
the event log meaningful for new consumers for years."

**Interviewer**: "That's the time. Good discussion — anything you'd add?"

**Candidate**: "One thing: observability and a metrics dashboard for the bus — delivery latency
percentiles, dedup rate, DLQ depth, and upcast errors. Teams debug event pipelines blind without it,
and those four numbers would have caught most incidents I've seen in production event systems."

---

## Interviewer Feedback

**Strengths**:
- Strong narrative: at-least-once + idempotency as the core delivery contract, justified with concrete failure modes.
- Versioning answer tied to sealed types/pattern matching, showing language fluency.
- Connected replay, testing, and observability into one coherent story.

**Improvements**:
- Could have sketched the outbox table schema (columns) explicitly.
- Could have quantified batching (e.g., batch size, latency budgets) earlier.
- Could have mentioned schema registry compatibility types (backward/forward/full) by name earlier.

**Score**: Strong Hire
