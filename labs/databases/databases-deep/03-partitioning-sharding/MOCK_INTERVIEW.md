# Lab 03: Mock Interview — Partitioning & Sharding

**Role**: Senior Database / Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: FAANG (distributed database teams)

---

**Interviewer**: "Let's start with terminology. What's the difference between partitioning and sharding?"

**Candidate**: "Partitioning is a *within-database* technique: one logical table split into physical segments on the same server (PostgreSQL declarative partitioning, MySQL `PARTITION BY`). The query planner can prune partitions, and maintenance can target a segment — but all segments share the same compute. Sharding is *across-database*: the data is split across independent database instances, each owning a subset. Sharding gives horizontal scaling of both storage and compute — but you lose single-instance joins, transactions, and constraints across shards. A common architecture is both: shard the database across servers, and partition each shard's biggest table by time."

**Interviewer**: "What sharding key strategies exist? Compare them."

**Candidate**: "Three families. **Range sharding**: keys partition by value ranges — e.g., users A-M on shard 1, N-Z on shard 2. Great for range scans and time-series (partition by month), terrible for hotspotting if keys cluster (e.g., all new users land on one shard). **Hash sharding**: `shard = hash(key) % N` — even distribution, but range queries fan out to every shard and resharding requires rehashing everything. **Directory-based**: a lookup table maps key → shard — maximum flexibility (manual control, can move data deliberately) at the cost of an extra hop and a single point of failure unless replicated. Modern systems also use **consistent hashing**, which fixes the resharding problem: only ~1/N of keys move when a shard joins or leaves."

**Interviewer**: "Walk me through consistent hashing in detail."

**Candidate**: "Imagine a ring of hash values 0 to 2^32-1. Each shard (node) is hashed onto the ring — multiple times per node if you use virtual nodes. Each key is hashed and assigned to the first node clockwise from its position. When a node joins, only keys between its position and the next node's position move to it; when a node leaves, only its keys move to its successor. With V virtual nodes per physical node, the distribution smooths out — without virtual nodes, a ring of 3 nodes can split the keyspace 80/10/10 instead of 33/33/33, especially with weak hashes. The price: storage nodes must track more metadata, and key-location lookups are O(log N) with a sorted ring, or O(1) with a bucketized jump hash."

**Interviewer**: "How does rebalancing actually work in a production sharded store?"

**Candidate**: "You never move data in place — you **copy then switch**. The flow: (1) mark the shard pair 'rebalancing'; (2) copy ranges as consistent snapshots, optionally with change-data-capture streaming for the tail; (3) once caught up, flip a routing-table epoch; (4) old shard is drained and dropped. Two important details: **ordering** — move the biggest/overloaded shards first; and **throttling** — cap bandwidth so rebalancing doesn't saturate the network and cause an availability incident. This is how Cassandra's vnodes + streaming, DynamoDB's global tables, and CockroachDB's range rebalancing all work at the core."

**Interviewer**: "What about the query path when data moves? How do clients find the right shard?"

**Candidate**: "Two models. **Smart client / proxy routing**: the client (or a proxy like ProxySQL, Vitess, or Twemproxy) holds the ring and computes `shard = hash(key)` locally — zero extra hop, but the topology must be pushed consistently (versioned config, e.g., Vitess's topology service). **Metadata service**: a separate lookup (e.g., HDFS NameNode, or etcd-backed mapping) — one extra RTT per request unless cached; the cache must be invalidated on rebalance. The sweet spot: clients cache a versioned routing table, validate with a topology epoch on every request, and refresh on mismatch."

**Interviewer**: "Hot spots: say 5% of your users generate 80% of traffic and they all hash to one shard. What do you do?"

**Candidate**: "Several levers, in order of preference. (1) **Split the hot key**: append a suffix — `user_42_0..15` — spreading the celebrity across 16 sub-keys; reads fan out and merge (requires read-merge logic). (2) **Application-level caching**: the hot key's data is by definition read-heavy; a cache (Redis or in-process) absorbs most traffic before it reaches the shard. (3) **Weighted assignment**: with directory-based sharding, manually move the hot key to a dedicated shard. (4) **Cardinality-aware keys**: choose a shard key with high cardinality in the first place — the classic example is sharding an order table by `user_id` rather than `order_id` if you query by user. The general principle: pick the shard key from the *access pattern*, not from the natural key."

**Interviewer**: "Let's discuss consistency. You shard a table; a transaction touches two shards. How do you handle it?"

**Candidate**: "Options, roughly in escalating cost: (1) **Avoid cross-shard transactions** — design the shard key so related data co-locates (tenant-per-shard, user-and-their-orders). This is the #1 rule of sharding: *if you need joins/transactions across shards, you've sharded wrong*. (2) **Idempotent, outbox-based flows** — process locally, emit events, reconcile. (3) **Distributed transactions** — 2PC if you need strong atomicity (Seata, XA), or Saga if you accept eventual consistency with compensation. (4) Some engines provide distributed transactions natively — Spanner-style TrueTime, CockroachDB's transactions spanning ranges. For interviews: mention the tradeoff and that most systems default to 'shard key = transactional boundary'."

**Interviewer**: "You're designing the sharding for a time-series workload — 100B rows/year, always append, queried by time range. How do you lay it out?"

**Candidate**: "Time-series is the one workload where **range sharding beats hashing**. Hash sharding would scatter every time range across all shards, making range queries scan everything. Instead: shard by time ranges (e.g., per-month shards), and optionally *hash within* the time bucket (e.g., `shard = hash(device_id) % 32` within a month) to spread write load across servers — this is exactly what systems like Cassandra's compound keys or ClickHouse's partitioning+sharding do. Retention then becomes trivial: dropping last month's shard is a metadata delete, not a bulk DELETE. Add downsampling tiering and you have a proper TSDB layout."

**Interviewer**: "What are the failure modes of rebalancing that you'd design against?"

**Candidate**: "The big three: (1) **Rebalance-induced cascade** — moving data saturates disks/network; nodes slow down; the system interprets slowness as failure and moves *more* data; the ring thrashes. Mitigate with throttling, and with a 'rebalancing in progress' state that quiesces failure detection. (2) **Split-brain routing** — a client holds a stale ring during a flip and writes to an old node; the write is lost or duplicates. Mitigate with epoch-validated routing and fencing (the new owner rejects old-epoch writes). (3) **Partial copy** — crash mid-copy leaves the range on neither node. Mitigate with a redo log / snapshot manifest, making the copy idempotent and resumable. Every production system has hit at least one of these."

**Interviewer**: "Finally — when would you *not* shard?"

**Candidate**: "When the data fits on one node with comfortable headroom. Sharding adds: cross-shard query complexity, consistency complexity, operational complexity (rebalancing, backup/restore per shard), and hot-spot risk. If a single server with the right indexes, compression, and partitioning handles 3 years of growth — don't shard. The industry's rule of thumb: scale vertically first (bigger machines, better indexes), add read replicas, partition by time, and only then reach for sharding, typically when you exceed ~1-4TB or the write throughput of a single instance."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Fundamentals | Partitioning vs sharding clearly distinguished |
| Algorithm depth | Consistent hashing ring mechanics, virtual nodes, ~1/N moves |
| Operations | Copy-then-switch rebalancing, throttling, epochs |
| Failure analysis | Named cascade failure, split-brain routing, partial copy |
| Workload fit | Chose range-over-time + hash-within-bucket for TSDB |
| Judgment | Knew when NOT to shard |

### Candidate strengths
- Led with access-pattern-driven shard key design — the senior answer.
- Structured the consistency answer as a ladder of costs.
- Concretely named real systems (Vitess, Cassandra, CockroachDB, ClickHouse).

### Gaps to work on
- Did not quantify consistent hashing's redistribution math (1/N keys, expected 1/N node load) — memorize the expected-load formula for follow-ups.
- Could have mentioned jump consistent hashing / rendezvous hashing as ring alternatives.
- Missed mention of **read amplification** in consistent hashing for range queries (ring gives O(log N) lookup, but range scans still fan out).

## Follow-up study prompts
1. Derive: with consistent hashing and V virtual nodes per node, expected keys per node is N/V and expected redistribution on node loss is 1/N of keys.
2. Compare consistent hashing vs rendezvous hashing (HRW) — when is weighted HRW better?
3. How does CockroachDB split ranges automatically and rebalance with replica constraints?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Walk me through the exact math of consistent hashing with virtual nodes. N physical nodes, V virtual nodes each, K keys. What's the expected load per node, and how much moves when one node dies?"

**Candidate**: "With N×V virtual nodes on the ring, a key lands uniformly among them, and each physical node owns V virtual arcs, so expected load is K/N — independent of V. The variance is what V fixes: without VN (V=1), the load of the N arcs has high variance — the expected *maximum* arc is roughly (K/N)·log N with N nodes (balls-into-bins with deterministic hash arcs is worse). When one physical node dies, its V arcs are reassigned to the following virtual nodes — which belong to other physical nodes; each of the N-1 survivors picks up about V/(N-1) arcs... the total moved is the dead node's ~K/N keys, and they're spread across survivors roughly evenly *if* the ring's virtual-node placement doesn't cluster. The honest math: uniform *in expectation*; pathological rings need `jump consistent hashing` (O(1) memory, provably minimal redistribution) or rendezvous hashing for deterministic uniformity."

**Interviewer**: "You mentioned jumping hash. When is jump consistent hashing the right call, and what does it cost?"

**Candidate**: "Jump consistent hashing (Lamping & Veach): maps a key to a bucket in [0, N) using a clever 'jump' function with O(1) state — no ring, no virtual nodes, and *provably minimal movement*: when N grows to N+1, exactly K/(N+1) keys move. That's the theoretical optimum for resizing. The cost: it only supports *incrementing* bucket counts — removing a bucket (or weighted buckets) breaks its assumptions; it gives no ordering, no locality, no data-aware placement. So: great for cache tiers that only grow (and shrink by resetting); wrong for databases needing range scans, node weights, or graceful decommissioning. That's why production DBs use ring+vnode or HRW, while jump hash is popular in client-side cache sharding."

**Interviewer**: "Secondary indexes across shards — how do you support them?"

**Candidate**: "Four strategies. (1) **Document-style**: the secondary index lives *inside* each shard (a local index) — queries on it fan out to all shards (scatter-gather); works well when shard count is modest, costs one round per shard. (2) **Global index partition** (the standard): the secondary index is itself sharded by the *index key* — e.g., an email index sharded by `hash(email)` — a lookup is a single-shard point query. The price: a write updates two shards (data shard + index shard) — needing either async index maintenance (with inconsistency windows) or distributed writes. (3) **Materialized views / read models**: maintain denormalized tables by the alternate key (CQRS-flavored). (4) **Search engine**: push secondary-index needs to Elasticsearch — the database stays a primary-key store. The design rule: *every secondary index doubles the write fan-out; choose global partitioned indexes only for hot read paths, and drive them by async CDC*."

**Interviewer**: "What's the difference between *stateless* and *stateful* routing, and when does each break?"

**Candidate**: "Stateless routing computes the target shard from the key alone — `hash(key) % N` or a ring — no lookup needed, but the *mapping function must be identical everywhere*, so membership changes must be synchronized (a versioned config pushed to all clients — stale clients route wrong). Stateful routing asks a metadata service 'where is key X?' — always correct after a change (the metadata is the source of truth), but it's an extra hop and a dependency. Both break in the same place: *during a topology change* — stateless breaks when clients hold mixed config versions; stateful breaks when the metadata service is partitioned from the client. The robust pattern: stateless routing with an epoch-validated topology cache and a fallback to the metadata service on mismatch — which is roughly what Vitess does."

**Interviewer**: "Final deep-dive: you inherit a system sharded by `order_id` where the hot query is 'all orders for user U'. Walk me through your re-shard plan."

**Candidate**: "The current design is query-hostile: every 'orders by user' query scans all shards. Plan: (1) **Measure first** — confirm the fan-out cost with a query-profile analysis (latency by shard, QPS). (2) **Dual-write + backfill** — start writing new data to both the old (order_id) and new (user_id) layouts; backfill the new layout from a snapshot with CDC streaming to close the gap. (3) **Verify parity** — periodic diff queries between layouts until drift is zero. (4) **Cut over reads** — route 'orders by user' to the new layout; keep order_id lookups on the old layout if cheap, otherwise migrate them too. (5) **Retire** — drop the old layout after a full retention period. The senior points: never migrate in place (copy-then-switch), never cut over without parity verification, and keep the old path alive long enough that a rollback is a config flip, not a data restore."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Memorize the redistribution math properly — '~1/N of keys move' was stated but not derived; derive it from 'each key has equal probability of being in any node's arc'.
- Prepare one concrete re-shard runbook (the five-phase plan above) so the design answer has operational texture.
- Practice the hot-key answer with a concrete celebrity-user example including the read-merge cost.

### One-sentence takeaway
- "Sharding is a data-modeling decision first: the shard key must be chosen from the access pattern, and every cross-shard query is a design smell."

### Self-check questions (run before the real interview)
1. Can I derive expected redistribution on node loss for a ring, with and without virtual nodes?
2. Can I explain why range sharding beats hashing for time series, and hash-within-range beats both?
3. Can I lay out a copy-then-switch rebalance including the epoch flip and fencing?
4. Do I know when consistent hashing, jump hashing, and rendezvous hashing each apply?
5. Can I describe the dual-write/backfill/parity/cutover re-shard plan without notes?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Partitioning vs sharding — one sentence each.
**Hint.** Partitioning splits a table within one server; sharding splits data across servers.

**Q2.** When does range sharding beat hash sharding?
**Hint.** Time-series/ordered access — range scans stay local; retention = drop a shard.

**Q3.** How many keys move when a node joins a consistent-hash ring with V virtual nodes?
**Hint.** ~1/(N·V) of arcs — ~1/N of keys total, spread over the ring.

**Q4.** What is the #1 rule of shard-key design?
**Hint.** Choose the key from the access pattern; cross-shard joins/transactions = design smell.

**Q5.** How do you handle a hot key on one shard?
**Hint.** Sub-shard the key, cache in front, or weighted/directory-based manual placement.

**Q6.** What three failure modes does rebalancing have?
**Hint.** Cascade (thrash), split-brain routing (stale epochs), partial copy (resumable copy).

**Q7.** How do clients learn the current topology?
**Hint.** Versioned routing table + epoch validation, or a metadata service with caching.

**Q8.** When should you NOT shard?
**Hint.** Data fits one node with headroom; sharding costs queries, consistency, operations.

**Q9.** How do you support a secondary index across shards?
**Hint.** Local index + scatter-gather, or global partitioned index with write fan-out.

**Q10.** What makes the copy-then-switch rebalance safe?
**Hint.** Snapshot + CDC tail, epoch flip, fencing old-epoch writes, throttling.

### Scoring
- **8-10 correct**: ready for the sharding loop.
- **5-7**: revise consistent hashing math and the rebalance failure modes.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`ConsistentHashSharder`) and verify the ~1/N movement with the churn test.
**Day 3**: Quick-Fire rounds; derive the redistribution math on paper.
**Day 4**: Rehearse the re-shard runbook (dual-write → backfill → parity → cutover → retire).
**Day 5**: Drill the extended rounds (jump hashing, secondary indexes, stateless vs stateful routing).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
