# Lab 04: Mock Interview — Replication & Log Shipping

**Role**: Senior Database Engineer
**Duration**: 45 minutes
**Company style**: FAANG / database vendor (PostgreSQL, MySQL, MongoDB, Cassandra)

---

**Interviewer**: "Start with the basics: what is replication, and what problems does it solve?"

**Candidate**: "Replication is maintaining copies of data on multiple servers. It solves three problems: **availability** (a copy survives a server failure), **read scaling** (reads spread across replicas), and **durability** (a lost disk doesn't mean lost data). But it's not free — it introduces consistency questions: is a read on a replica guaranteed to see the latest write? Do replicas converge if two masters both accept writes? Those questions define the replication topology and consistency model, and picking the wrong one is how outages become data-loss incidents."

**Interviewer**: "What are the main replication topologies?"

**Candidate**: "Four families. **Single-leader (master-slave)**: one node accepts writes, others replay them; simplest, strongest consistency options, but writes are single-point and read scaling only. **Multi-leader**: several nodes accept writes and propagate to each other — good for multi-datacenter setups and offline-first apps, but writes can conflict and you must pick a conflict-resolution strategy (LWW, per-field merge, CRDTs). **Leaderless (quorum)**: any node accepts a write, clients read from several nodes and compare versions — Dynamo/Cassandra style; best availability, but you need versioning and read repair to converge. And **synchronous vs asynchronous** is the orthogonal axis: whether the primary waits for the replica to acknowledge before reporting success to the client."

**Interviewer**: "Let's drill into your lab: log shipping. What exactly is the 'log' and how does a replica apply it?"

**Candidate**: "The log is the database's write-ahead log (WAL) — every change is appended as a durable record *before* the data page is modified: transaction begin, inserted row, updated row (old + new values), commit. A replica subscribes to the stream of WAL records and replays them in order. Two flavors: **physical log shipping** (PostgreSQL `pg_wal` records, MySQL binlog statement/row formats) — byte-level or record-level replay, high fidelity, and **logical replication** (PostgreSQL logical decoding, Debezium) — a higher-level 'row changed' stream that can be applied by a different engine or transformed. The key property of the log: it is *ordered and immutable*, so replay is deterministic and idempotent if records carry sequence numbers."

**Interviewer**: "How does the replica track its position in the log?"

**Candidate**: "Every record gets a monotonic position: LSN (log sequence number) in PostgreSQL, binlog file+offset in MySQL, or a plain offset in Kafka. The replica stores 'last applied LSN' — checkpoints persist it so the replica can resume after a crash from exactly where it left off. The master keeps a per-replica view: which LSNs each replica has acknowledged, and it *retains* WAL segments that a lagging replica hasn't consumed — which is why a long-stalled replica causes master-side WAL growth and needs monitoring. There's a subtle correctness point: the replica must apply in order — applying record N+1 before N can break page-level invariants, so apply is single-threaded or carefully pipelined per-transaction."

**Interviewer**: "What's the difference between synchronous and asynchronous replication, and how do you choose?"

**Candidate**: "Asynchronous: the master commits and replies to the client without waiting; the replica catches up in the background. Latency is minimal, but on master failure you can lose the last few committed transactions. Synchronous: the master waits for at least one replica to flush the commit record — then a committed transaction survives master loss. The cost: commit latency rises to replica RTT (with group commit to amortize), and if the required replicas are down, the master stalls — production systems use `synchronous_standby_names` with priorities and degrade gracefully. The choice is a durability/latency trade: financial systems pick sync; high-QPS systems pick async with replication-lag alerting. PostgreSQL's model is a nice middle ground: async by default, `synchronous_commit = remote_apply` for the strictest mode."

**Interviewer**: "Walk me through a master failover — what has to happen, in order?"

**Candidate**: "Five phases. (1) **Detection** — the health-check/consensus layer decides the master is gone (must be authoritative to avoid split-brain: ZooKeeper/etcd or a quorum; a replica promoting itself on a flaky network is how you get two masters). (2) **Promotion** — pick the replica with the highest LSN that's confirmed current; PostgreSQL does this manually or via Patroni + etcd; the promoted node re-enables writes. (3) **Recovery** — the promoted replica applies the tail of the WAL it received, rolls back in-flight transactions, then serves. (4) **Repoint clients** — connections move to the new master; a load balancer or discovery service hides this. (5) **Rebuild / rejoin old master** — the old master comes back as a *replica*, re-syncs from the new master (full base backup if it fell too far behind), and if it was 'split-brain', its divergent writes are discarded or quarantined. The acid test: after failover, exactly the set of transactions that were durably committed remain."

**Interviewer**: "What is read-your-writes, and how do you fix it with replication lag?"

**Candidate**: "If a client writes to the master and immediately reads from a lagging replica, it can see its own write missing — that's the read-your-writes consistency violation. Fixes, in order of elegance: (1) **session pinning** — route a session's reads to the master (or to a replica known to be caught up) for a grace period after a write; (2) **LSN-based routing** — the write returns its LSN; reads go to any replica whose `last_applied_lsn >= write_lsn` (PostgreSQL has `pg_current_wal_lsn()`-based wait-for-LSN APIs); (3) **monotonic reads with min-progress tracking** — remember the high-water mark you've seen and refuse to read from anything behind it. The general principle: *bind the replica to a promise, not a guess* — an explicit watermark beats a random replica selection."

**Interviewer**: "Multi-master conflict: two datacenters accept writes to the same row. How do you resolve the conflict?"

**Candidate**: "Ladder of approaches. **Last-writer-wins** — timestamp comparison; simplest, but loses updates silently. **Version vectors / Lamport clocks** — each write carries causal history; conflicts are *detected*, stored as sibling values, and merged by application logic or read repair (this is what DynamoDB/Cassandra do: both values survive, and the next read repairs). **CRDTs** — the merge function is commutative, so replicas converge deterministically — best when the data type fits (counters, sets, registers). **Per-field resolution** (CouchDB-style) or **application-level merge**. The senior answer: don't hope the system resolves conflicts — *design the data model so conflicts are impossible* (partition ownership by tenant, or use CRDT-friendly types), and if conflicts are unavoidable, make resolution explicit and auditable, never implicit."

**Interviewer**: "Your lab simulates master-slave with log shipping. What's the most important invariant you'd test?"

**Candidate**: "The golden test: *any prefix of the replicated log, applied to any replica, must produce the same state as the master at that LSN*. Concretely: insert N rows, capture replica state at each LSN checkpoint, then kill the master and promote a replica — the promoted state must contain exactly the committed rows up to the last acknowledged LSN, and the unacknowledged tail must be rolled back. I'd also test the resume path: disconnect a replica, let the master advance 100 records, reconnect — the replica must resume from its persisted last-applied LSN, not re-apply from scratch, and not skip. And idempotency: if the crash happens mid-apply, re-applying the record must not duplicate the row."

**Interviewer**: "Where does replication fail in production? Give me the classic incidents."

**Candidate**: "The big four. (1) **Split-brain**: network partition, two nodes think they're master, both accept writes → divergent datasets that must be merged manually. (2) **Replication lag cascade**: a bulk job on the master generates a huge WAL burst; replicas fall behind, read-your-writes violations trigger app errors, and the org 'fixes' it by throttling the job — but the lag alert was the real bug. (3) **WAL retention**: a replica offline for 3 days, master's `wal_keep_size` exhausted → the replica must be rebuilt from backup; if backups are stale, data loss. (4) **Schema drift**: someone runs `ALTER TABLE ... DROP COLUMN` manually on the master; logical replicas applying by column name break or silently misapply. Every one of these is survivable if you have monitoring (lag, WAL retention, replica count) and a runbook."

**Interviewer**: "Final question: replication vs. partitioning — they both copy data? How do you decide?"

**Candidate**: "They're orthogonal. **Partitioning/sharding splits data *across* nodes — no node has it all; you gain capacity and write throughput, you lose cross-shard queries. **Replication copies the same data to *multiple* nodes; you gain durability and read throughput, you lose write simplicity (or gain consistency costs). Production systems combine them: shard by key for capacity, replicate each shard 3x (Cassandra RF=3, CockroachDB replication factor 3, Mongo replica sets). The decision framework: shard when one node can't hold or ingest the data; replicate when you need reads to scale, failure tolerance, or geographic locality. Both are expensive in their own way — every replica is a full copy of the shard's data, so replication factor multiplies storage cost."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Terminology | Single/multi-leader/leaderless + sync/async axes |
| Mechanics | LSN tracking, ordered apply, WAL retention |
| Failover | Detection → promotion → recovery → repoint → rebuild |
| Consistency | Read-your-writes fixes (pinning, LSN routing) |
| Conflict handling | Ladder: LWW → version vectors → CRDTs → app merge |
| Operations | Named the classic production failure modes |

### Candidate strengths
- Gave ordered, phase-by-phase failover — exactly what interviewers want.
- Knew the "replica lags → master WAL grows" feedback loop.
- Closed with a clear replication-vs-partitioning decision framework.

### Gaps to work on
- Did not mention **cascading replication** (replica-of-replica) or its lag implications.
- Could have quantified sync-replication commit latency (RTT + fsync on both nodes).
- Missed the **gossip/membership** aspect of failure detection — cite SWIM or etcd leases.

## Follow-up study prompts
1. PostgreSQL `synchronous_commit` modes: off / local / remote_write / remote_apply — what does each promise?
2. How does MySQL Group Replication / semi-sync differ from PostgreSQL's sync replication?
3. What is "logical decoding" and what data-format guarantees make Debezium-style CDC safe?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on synchronous replication. What does `synchronous_commit = remote_apply` actually guarantee, and what's the latency cost compared to `on`?"

**Candidate**: "`on` (the default when a standby is configured) means the master waits for the standby's *WAL flush* — the commit record is durable on the standby's disk, but the standby may not have *applied* it yet. `remote_apply` waits until the standby has actually applied the record — so a failover to that standby serves the transaction *without any replay gap*. The cost: the commit waits for apply, which includes the standby's page writes — noticeably slower than just the flush. The correct mental model: `remote_write` (standby wrote to OS, not fsync) → `on` (fsync on standby) → `remote_apply` (visible on standby). And there's a hidden cost: if the synchronous standby is down, the master's commits stall — the standard mitigation is `synchronous_standby_names` with multiple candidates and the `ANY`/`FIRST` selection logic, and monitoring `sync_state`."

**Interviewer**: "Group commit — how does it make synchronous replication affordable?"

**Candidate**: "Group commit batches commit fsyncs: transactions that commit in the same small window share *one* fsync (and one WAL flush round-trip to the standby). Instead of latency = RTT + fsync per transaction, it's amortized: at 10K TPS with a 10ms standby RTT, naive sync replication caps at 100 TPS — group commit turns that into 'the batch's RTT once per window', so throughput stays high while each commit still waits for the group's flush. The tradeoff: individual latency is the *batch's* latency (a transaction may wait for its group), and the group size is bounded by `commit_delay`/`commit_siblings`. Interview-wise: never discuss synchronous replication without mentioning group commit — it's the reason sync replication is viable at all."

**Interviewer**: "What about replication of DDL? Why is it harder than DML?"

**Candidate**: "DML is row-level: records are unambiguous. DDL is *schema-level*: a `ALTER TABLE ADD COLUMN` changes the layout both sides must agree on. Problems: (1) the schema change applies to *future* records on both sides — replicas replaying older WAL after the DDL may see records in the old layout; (2) logical replication applies by column names — a column dropped on the master but not the replica (or vice versa) silently misaligns; (3) locks: DDL takes a schema lock, and replication applies it on the replica while transactions are replaying — deadlocks and stalls. Production answers: PostgreSQL logical replication can replicate DDL only via extensions (`pglogical`, `pgl_ddl_deploy`); physical replication gets DDL for free (WAL is physical), which is a strong argument for physical streaming when possible. The interview point: *DDL replication is a correctness minefield — version schemas, use additive migrations, and test replica replay*."

**Interviewer**: "How do you *measure* replication lag properly, and what's the difference between lag and data loss exposure?"

**Candidate**: "Measurement must distinguish: (1) **WAL-received lag** — the standby's last-received LSN vs the master's current LSN (`pg_stat_replication`: `sent_lsn`, `write_lsn`, `flush_lsn`, `apply_lsn` — four different numbers, each a different promise); (2) **apply lag** — how far the standby is *behind in replay* (the number that matters for read-your-writes); (3) **time-based lag** — '10 seconds behind' computed from WAL timestamps, which is what dashboards usually show. Data-loss exposure: with async replication, the exposure is *sent vs applied* — transactions acked to the client but not yet on the standby. The sharp answer: lag alerts should track apply lag (via `pg_stat_wal_receiver` or a heartbeat table the replica updates), and the data-loss budget must be derived from the *flush* position, not the dashboard's time-based lag."

**Interviewer**: "Final deep-dive: design a failover for a system that cannot lose a committed transaction. Give me the architecture."

**Candidate**: "Constraints: zero committed-transaction loss, sub-minute failover. Architecture: (1) **Synchronous quorum replication**: three nodes, commit waits for fsync on at least one standby (`synchronous_standby_names = ANY 1 (s1, s2)`) — a committed transaction exists on ≥2 machines by construction; (2) **Consensus-based promotion**: a Patroni-style manager backed by etcd — the etcd lease is the single authority; only the lease holder may promote, and the old master is fenced (its etcd lease is revoked; it rejects writes with a stored 'no longer primary' flag) — *fencing is mandatory*, a split-brain master that continues accepting writes violates the invariant even if the new master is perfect; (3) **Application-level idempotency**: clients carry a write-id per transaction, so replays after failover (client retried against the new master) are deduplicated; (4) **Verification**: after promotion, the new master's state is verified against the acked LSN before serving reads; (5) **Tests**: kill the master, assert zero lost committed transactions (compare before/after state via the write-id log), assert failover < 60s, and assert the fenced master rejected every write. The invariant to state out loud: *committed = acknowledged to client = fsynced on the primary and ≥1 standby*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Quantify sync-replication latency properly (RTT + fsync + group-commit window) — the interviewer had to drag the group-commit mention out.
- Practice the four-LSN-state answer (`sent/write/flush/apply`) — it's the perfect 'measure lag precisely' response and takes 30 seconds to give.
- Rehearse a fencing-first failover narrative — split-brain prevention deserves the first sentence, not the third.

### One-sentence takeaway
- "Replication is a durability-latency continuum: the same WAL, the same commit, but *where you wait* (nothing, write, flush, apply) defines the guarantee."

### Self-check questions (run before the real interview)
1. Can I enumerate `synchronous_commit` modes and the exact guarantee of each?
2. Can I explain group commit's throughput-vs-latency tradeoff with numbers?
3. Can I design a zero-loss failover with fencing in five steps?
4. Do I know the four LSN states in `pg_stat_replication` and what to alert on?
5. Can I explain why physical replication handles DDL but logical replication needs help?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** What is the WAL, and why must it be durable before the page change?
**Hint.** Replay + crash recovery need the record on disk; write-ahead ordering is the ACID foundation.

**Q2.** Sync vs async replication — the one-line tradeoff.
**Hint.** Async: low latency, loss window on master failure. Sync: durability, commit waits on replica RTT.

**Q3.** What LSN does a replica track, and why persist it?
**Hint.** Last-applied position — resume after crash without replaying or skipping.

**Q4.** What happens to master WAL when a replica lags for hours?
**Hint.** WAL retention grows; `wal_keep_size` exhausted → replica must rebuild from backup.

**Q5.** Order the failover phases.
**Hint.** Detection (authoritative!) → promotion (highest LSN) → recovery → repoint clients → rebuild/rejoin old master.

**Q6.** How do you fix read-your-writes with lag?
**Hint.** Session pinning, LSN-based routing, or min-progress watermark.

**Q7.** What is split-brain and how is it prevented?
**Hint.** Two masters from flaky detection; quorum/consensus or fencing — never self-promotion on network timeout alone.

**Q8.** LWW vs vector clocks — when is each dangerous?
**Hint.** LWW loses concurrent updates silently; vector clocks surface siblings — pick by merge semantics.

**Q9.** What does `synchronous_commit = remote_apply` guarantee?
**Hint.** The standby has APPLIED the record — failover serves the transaction with zero replay gap.

**Q10.** Replication vs partitioning — which solves read scaling?
**Hint.** Replication copies data (read scaling, durability); partitioning splits it (capacity, write throughput).

### Scoring
- **8-10 correct**: ready for the replication loop.
- **5-7**: revise failover ordering and LSN semantics.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`LogShippingReplication`) and run the prefix-of-log golden test.
**Day 3**: Quick-Fire rounds; write the failover runbook from memory.
**Day 4**: Rehearse the four-LSN-states answer (`sent/write/flush/apply`) and the sync-replication latency math.
**Day 5**: Drill the extended rounds (group commit, DDL replication, lag measurement, zero-loss design).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
