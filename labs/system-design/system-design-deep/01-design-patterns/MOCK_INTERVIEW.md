# Mock Interview Transcript: Transactional Outbox with Event Relay

| Field | Detail |
|-------|--------|
| **Level** | Senior Backend Engineer (L5) |
| **Duration** | 45 minutes |
| **Format** | System design whiteboard + implementation |
| **Problem** | "Implement the transactional outbox pattern with an event relay for a service that must publish domain events reliably." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** Good morning. Today we'll design a piece of an order-processing
system. Your order service writes order state to a relational database. Downstream
services — inventory, shipping, analytics — need to be notified of every
`OrderPlaced` event via Kafka. Your job: design and implement the mechanism that
guarantees **no event is lost**, without using distributed transactions. Walk me
through your thinking, then we'll write code.

**Candidate (C):** Understood. Before I design anything, I want to confirm a few
requirements.

---

## Part 1: Clarifying Questions (6 minutes)

**C:** Four questions. First, what delivery semantics do consumers require —
at-least-once, at-most-once, or exactly-once?

**I:** At-least-once is acceptable; consumers are idempotent. But I want you to be
explicit about what "acceptable" costs you.

**C:** Agreed — and that's the right framing, because exactly-once is a myth across
two independent systems; you get exactly-once *effect* through idempotency, never
through the transport alone. Second: is event ordering per aggregate required?

**I:** Yes — events for a given order must arrive in the order they were produced.

**C:** Third: what's the write throughput on the order table, and the event volume?

**I:** Peaks around 2,000 orders/second, roughly one event per order, bursty.

**C:** Fourth: is Kafka already provisioned with partition keys, or do I get to
define them?

**I:** You define them.

**C:** Good. Those answers shape the design. Let me restate the problem to make
sure we agree: the core difficulty is the **dual-write problem** — the DB write and
the Kafka publish can't be atomic, and any ordering between them opens a loss or
duplication window.

---

## Part 2: Framing the Dual-Write Problem (5 minutes)

**C:** There are two naive approaches, and both are broken. If you publish first
then commit the DB write, a commit failure leaves you with an event for an order
that doesn't exist. If you commit first then publish, a publish failure or a crash
between the two leaves an order with no event — silently lost. Neither has a
recovery path. The canonical fix is the **transactional outbox**: write the event
row in the *same* database transaction as the business state, then have a separate
**relay** poll the outbox table and publish to Kafka. The outbox row is the source
of truth for "events that still need delivery," so the broker failure no longer
loses data — it only delays it.

**I:** What guarantees does that give you, precisely?

**C:** Exactly two. **Durability**: an event exists in the outbox if and only if the
aggregate write committed. **At-least-once delivery**: the relay publishes every
row and deletes it only after an acknowledged publish; if the relay crashes between
publish and delete, it publishes again. That's why consumers must be idempotent —
typically via an idempotency key, in practice the outbox row id or the event's
aggregate id plus sequence number.

**I:** Why not just use a 2PC/XA transaction spanning Postgres and Kafka?

**C:** Three reasons. Kafka has no XA participant API, so 2PC isn't even available
without a compatibility layer like a transactional Kafka connector. Second, 2PC
scales poorly — the coordinator is a bottleneck and a single point of failure, and
in-doubt transactions require manual intervention. Third, it couples the
availability of the database to the broker: a broker outage can stall DB commits.
The outbox trades a small amount of extra latency for complete decoupling.

**I:** And CDC, like Debezium reading the binlog?

**C:** CDC is a valid alternative and it's popular, but it has subtle costs: binlog
replication lag adds latency, schema changes on the source table need care, and you
now operate a second system with its own ordering and backfill semantics. The
outbox pattern keeps the contract in your own code and gives you explicit control
over batching, backoff, and dead-letter handling. Many production systems use
Debezium *on the outbox table* — that's the best of both worlds, but for this
exercise I'll implement the relay directly.

---

## Part 3: High-Level Design (8 minutes)

**C:** Here's the architecture:

```
                +----------------------+      poll      +-------------------+
   HTTP POST    |    Order Service     |  ===========>  |   Event Relay      |
 -------------> |  (single transaction)|                |  (dedicated proc) |
                | 1. INSERT order      |                | 1. SELECT batch    |
                | 2. INSERT outbox row |                | 2. PUBLISH Kafka   |
                +----------+-----------+                | 3. DELETE outbox   |
                           |                            +---------+---------+
                           v                                      v
                   +---------------+                     +-----------------+
                   |  Postgres     |                     |  Kafka topic     |
                   |  (order +     |                     |  orders          |
                   |   outbox)     |                     +-----------------+
                   +---------------+
```

**I:** The relay does three operations — why is that correct?

**C:** Because the invariant is: *an outbox row exists until its event is
acknowledged by the broker.* The delete must happen after a successful publish.
If we delete first and crash, we lose the event — that's the exact failure the
pattern exists to prevent.

**I:** Ordering — you said you'd handle it. How?

**C:** Ordering is per aggregate. I key the Kafka message by the order id, which
means Kafka routes all events of one order to the same partition, preserving
relative order within that partition. The relay must also publish rows in
insertion order. My claim query orders by `id` — a monotonic sequence — which
preserves total insertion order per table. Since I batch a bounded window and
never reorder within a batch, per-aggregate ordering holds. The remaining hazard
is *concurrent relays*: with one relay this is trivial, so I'll address
multi-relay scaling in a moment.

**I:** What table schema do you want?

**C:** Minimal and useful: `id BIGSERIAL PRIMARY KEY`, `aggregate_type`, `aggregate_id`,
`event_type`, `payload JSONB`, `created_at TIMESTAMPTZ`. The `id` gives ordering and
idempotency; `aggregate_id` gives the partition key. I deliberately avoid a
`processed` flag — delete-on-success keeps the table small and the index hot.

---

## Part 4: Implementation Walkthrough (12 minutes)

**C:** Now the code. Three components: an outbox table with transactional append
semantics, the order service that writes order + outbox row atomically, and a relay
loop that claims, publishes, deletes, with in-flight tracking so a crash mid-batch
doesn't cause the same row to be published concurrently.

**I:** You said "claims." Why claim rather than just select?

**C:** With a single relay, plain select + delete is fine. Claims matter when we
scale: `SELECT ... FOR UPDATE SKIP LOCKED` lets N relays shard the work without
double-publishing. I keep the claim concept in the code so the design is honest
about where the hard part is. For the in-memory demo I claim by tracking ids in
an in-flight set and skipping rows already claimed.

**C:** (writing) The publish loop has three parts: poll, process batch, backoff.
On transient broker failure I keep the row, log, and let the next poll retry —
exponential backoff with jitter is what I'd configure in production, with a
max-retry counter before dead-lettering.

**I:** How do you keep from polling hot when the table is empty?

**C:** The loop sleeps a bounded poll interval — in production I'd use a
`WAITFOR`-style advisory lock (`pg_try_advisory_lock`) or a fast-poll/sleep hybrid.
For this demo, a `Thread.sleep` between polls with a small interval is fine and
makes the behavior deterministic in tests.

**I:** What metrics do you expose?

**C:** Four: outbox table size (lag signal), relayed counter (throughput), publish
failure rate, and in-flight count. Outbox size trending up while relayed is flat is
the first signal of broker trouble — this is the primary alert.

**I:** How would you test this?

**C:** Three layers. Unit: relay publishes and deletes each row exactly once when
the broker always succeeds. Failure-injection: broker throws on the first N
publishes — rows must survive and be delivered on retry. Crash simulation: kill the
relay between publish and delete, restart, assert exactly one duplicate is possible
but zero loss. At-least-once makes "no loss, duplicates allowed" the assertable
contract.

---

## Part 5: Scaling, Follow-Ups, and Trade-Offs (8 minutes)

**I:** You're at 2,000 events/sec now. What breaks first?

**C:** The relay becomes the bottleneck — polling and publishing serially caps out.
Three levers: (1) larger batches per poll, (2) multiple relay instances using
`FOR UPDATE SKIP LOCKED` to shard claims, and (3) higher Kafka partition count so
the broker keeps up. What does *not* change is the core invariant, which is why
this pattern scales cleanly.

**I:** Late-arriving consumers or replay?

**C:** The outbox is a delivery mechanism, not a log of record — that's what the
Kafka topic is for. If a consumer needs history, they read the topic, not the
outbox. Replaying a consumer is just `consumer.seekToBeginning()` on their group;
the outbox stays untouched. I would also keep the event payload self-contained —
no references to mutable state — so the topic is a durable event store.

**I:** Schema evolution of `payload`?

**C:** Use a schema registry with forward-compatible encoding (Avro/Protobuf, or
JSON with version fields). The relay is the wrong place to validate content — it
should treat payload as an opaque blob. Validation belongs to the producer and the
consumers.

**I:** How does this interact with a saga orchestrator?

**C:** The outbox feeds it. Each saga step that produces a side effect writes its
event to the outbox in the same transaction as its local state change; the saga
orchestrator subscribes to those events and issues the next command. The outbox is
what makes the saga's eventual consistency safe — every step's outcome is
eventually visible exactly-once-effectively, even across crashes.

**I:** Any time you would *not* use an outbox?

**C:** Yes, three cases. Low volume where a human-visible retry job suffices. A
system with no downstream consumers yet — YAGNI, add it when the first consumer
appears. And systems where the domain can tolerate losing non-critical
observability events — though I'd argue that's a policy decision, not a
technical one, and I'd default to outbox.

---

## Part 6: Closing and Feedback (4 minutes)

**I:** Wrap up with the three things you want me to remember.

**C:** One — the outbox pattern converts an impossible atomicity problem into a
retryable one: correctness comes from the transaction, delivery comes from the
relay. Two — the contract is at-least-once with idempotent consumers; never claim
exactly-once across a DB and a broker. Three — ordering is per-partition key, and
every architectural choice I made — single relay, claim semantics, delete-on-success
— exists to preserve the invariant that a row is deleted only after acknowledged
delivery.

**I:** Solid. Strong clarifying questions, correct framing of the dual-write
problem, and you correctly identified CDC and 2PC trade-offs. One suggestion: you
could have drawn the crash-window diagram earlier — the "publish then delete"
window is the heart of the pattern and worth 30 seconds of whiteboard space up
front.

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Requirements gathering | Asked about semantics, ordering, throughput before designing | 5 |
| Dual-write framing | Correctly identified both broken orderings and the recovery gap | 5 |
| Alternatives analysis | 2PC (no Kafka XA, coordinator bottleneck) and CDC (lag, ops burden) | 5 |
| Guarantees | Precise: durability + at-least-once; exactly-once-effect via idempotency | 5 |
| Ordering | Per-aggregate via Kafka key + insertion-order claim | 4 |
| Failure handling | Crash windows, retry/backoff, dead-lettering, in-flight dedup | 4 |
| Scalability | SKIP LOCKED sharding, batching, metrics — but needed prompting on levers | 4 |
| Communication | Structured, used the whiteboard well, terse and precise | 5 |

**Overall: Strong Hire** — senior-level systems reasoning with production
awareness; the weak spot was proactively enumerating scaling levers without
prompting.

## Common Pitfalls Candidates Hit

- Publishing *before* committing the DB transaction and calling it "eventual
  consistency" — that's event loss dressed up.
- Deleting the outbox row before broker acknowledgment to keep the table small.
- Claiming exactly-once delivery is achievable; the correct answer is idempotent
  consumers.
- Using a boolean `processed` flag and indexing a table that never shrinks.
- Ignoring ordering until the interviewer asks; per-aggregate keys must be explicit.
- Missing the crash window between publish and delete entirely.
