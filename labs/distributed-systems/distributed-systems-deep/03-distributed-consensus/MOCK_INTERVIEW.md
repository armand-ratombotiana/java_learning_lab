# Lab 03: Mock Interview — Distributed Consensus (Quorum-Based Systems)

**Role**: Senior Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: FAANG / NoSQL vendors (Cassandra, DynamoDB, ScyllaDB)

---

**Interviewer**: "Let's define terms. What is quorum-based read/write, and what consistency does it give you?"

**Candidate**: "A quorum-based system replicates each key to N nodes. Writes must be acknowledged by at least W nodes; reads must hear from at least R nodes. The classic result: if R + W > N, the read quorum and write quorum overlap by at least one node, so a read always sees the latest write it intersects with — giving **strong consistency for reads** (assuming latest-writer-wins and no lost updates). For example, N=3, R=2, W=2: a write to 2 of 3 and a read from 2 of 3 must share at least one node, so the read sees the fresh value. If R + W ≤ N, you get *eventual consistency* — reads may miss the latest write. The general rule is `R + W > N` for strong read-your-writes and `R + W = N + 1` for strictest guarantees; with `W > N/2` and `R > N/2` you also get serializable-ish behavior on single keys under one-dimensional versioning."

**Interviewer**: "Walk me through what happens on a read in a Dynamo-style store — the full path."

**Candidate**: "The client (or coordinator) sends the read to R nodes — usually the first R healthy nodes in preference order. Each returns (value, version). The coordinator **merges**: it keeps the highest version (or all conflicting siblings in Dynamo's vector-clock model). If all R responses agree, return. If versions conflict — e.g., the key was updated concurrently in different partitions — the coordinator returns both siblings *and* performs **read repair**: it writes the merged result back to the stale replicas (with the latest version), so subsequent reads converge. Two crucial details: (1) the coordinator must wait for R responses even if the fastest ones are consistent (that's what R means), and (2) read repair is triggered on *any* divergence, not just explicit failures — that's how eventual consistency heals."

**Interviewer**: "What about *sloppy* quorums? When and why do you relax W + R > N?"

**Candidate**: "A sloppy quorum lets the coordinator pick *any* W healthy nodes when the preferred nodes are down — so writes continue during a partition even if the key's primary replicas are unreachable. The data lands on nodes that aren't the key's 'home' — Dynamo's **hinted handoff**: the surrogate node stores the write with a hint ('this belongs to node X'); when X returns, the write is handed back. What does this do to consistency? R + W > N still holds *among the nodes actually contacted*, but since the write may live on a non-home node, a subsequent read routed to home nodes can miss it — so sloppy quorums are an *availability* mechanism for write-mostly paths, and they explicitly weaken the consistency contract. The design rule: sloppy quorums for the write path during 'mostly healthy but degraded' periods; if you need correctness during partitions, use strict quorums and accept availability loss."

**Interviewer**: "Versioning — how do you know which value is newer? Walk me through vector clocks vs LWW."

**Candidate**: "**Last-writer-wins (LWW)**: each write carries a timestamp (or a monotonic counter); the highest wins. Simple, space-cheap, but loses updates silently when clocks skew or two clients write 'concurrently' — the loser's update is gone. **Vector clocks**: each replica keeps a per-node counter; a write from node i bumps entry i. Ordering: version A precedes B if every entry of A ≤ B's and at least one is < — then B supersedes A. If A and B are incomparable (each has entries the other lacks — concurrent writes), they're **conflicting siblings**: both are stored, and the application merges them on the next read (with read repair). Vector clocks give you *detection* of concurrency without a global clock; the cost is metadata that grows with node count (Dynamo truncates old entries to bound it). The interview point: LWW assumes a total order that may not exist; vector clocks make the absence of order visible instead of silently destructive."

**Interviewer**: "So with vector clocks, concurrent writes produce siblings that never resolve automatically. Isn't that a deal-breaker?"

**Candidate**: "It's a *feature* with a cost. For data where merges are well-defined — shopping carts (union of items), counters (sum), registries (last-write-wins per field) — siblings merge deterministically, often in application code or with CRDTs. For other data, you add business logic: 'keep both, ask the user,' or a deterministic merge rule (e.g., for a profile: per-field LWW). The cost: reads can return multiple values, and the API and app must handle it. Cassandra's default is LWW per column (a timestamp per cell) — simple, no siblings; DynamoDB uses LWW with `ConsistentRead` for strong single-item reads; Riak exposed siblings as a first-class API. The senior answer: *choose the versioning model to match the merge semantics of your data* — don't bolt LWW onto data that needs conflict visibility."

**Interviewer**: "What does quorum-based replication NOT give you, compared to Raft?"

**Candidate**: "Three gaps. (1) **Total order / atomic broadcast**: quorum reads/writes are per-key operations — there's no global log, so cross-key transactions need separate machinery (Cassandra lightweight transactions use Paxos; DynamoDB transactions use a different protocol). (2) **Consensus on membership and metadata**: the quorum protocol assumes you know the replica set; agreeing on *who is in the set* is itself a consensus problem — which is why production systems embed Raft/Zab for membership (etcd, CockroachDB ranges) or use gossip (Cassandra) with tunable convergence. (3) **Serializability**: R+W>N gives strong consistency per key with a single version chain; it does not give serializable transactions across keys. The mental model: quorums = per-key linearizability-ish semantics at the cost of availability tuning; Raft = a totally ordered replicated log you can build anything on — including quorum reads on top of it."

**Interviewer**: "Design question: you're building a global user-profile store — read-heavy, multi-region, writes from any region. Walk me through your consistency design."

**Candidate**: "Constraints first: reads must see the user's own latest write (read-your-writes), concurrent edits should not silently lose data, and availability must stay high across regions. My design: **per-key versioning with vector clocks + read repair**, N=3 per region *plus* cross-region async replication, R=2, W=2 within the region of the write, and **session pinning** — a client that writes to region X reads from region X (with the highest version it has seen as a floor, so it never goes backwards). Cross-region: async replication with vector-clock merge — the region that wrote last wins ties by its own clock, siblings surface for app merge. For 'last edit wins' fields (nickname), per-field LWW; for lists, CRDT or sibling merge. The consistency sweet spot: local R+W>N for strong regional behavior, vector clocks for cross-region concurrency, read repair to converge, and per-field semantics chosen by merge-ability."

**Interviewer**: "How do you test that R+W>N actually gives you strong consistency?"

**Candidate**: "A linearizability checker. The standard: record every read/write with its start and end timestamps; after the run, check whether there's a total order (linearization) consistent with real-time ordering in which every read returns the value of the last write before it. That's what Jepsen's `knossos` does against real databases. For a lab: (1) *quorum math tests* — kill nodes so that exactly R+W-1 are up and verify reads fail or are stale, then bring the overlap back; (2) *overlap test* — with N=3, R=2, W=2, write v1, 'kill' the node holding v1, write v2 to the other two, read from the two that have v2 → must return v2; (3) *partition test* — split the cluster so write quorum and read quorum can't overlap → the read must either block or return an explicit error (never a silent stale value if you've promised strong consistency)."

**Interviewer**: "Final question: N=3, R=2, W=2 — what happens to availability if one node dies? What about two?"

**Candidate**: "One node dies: writes need 2 of the remaining 2 → still succeed; reads need 2 of 2 → succeed. Full availability with a quorum of 2. Two nodes die: writes need 2 of 1 remaining → fail (unless sloppy quorum to a healthy surrogate — then they succeed but with weakened guarantees); reads need 2 of 1 → fail too. So the availability cliff is at N - (R or W) + 1 failures: with N=3, R=2, W=2, you tolerate exactly 1 failure and then go read-only/write-only at best. The tradeoff with N=5, R=3, W=3: tolerate 2 failures, but every operation needs 3 round trips and the write quorum is bigger. That's the fundamental quorum equation: `failures tolerated = min(R, W) - 1` (for the read and write paths respectively), and the availability/consistency/latency knob is where you place R and W."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Quorum math | R + W > N overlap argument stated precisely |
| Read path | Merge versions, read repair, sibling handling |
| Failure tolerance | Sloppy quorum + hinted handoff mechanics |
| Versioning | Vector clocks, concurrency detection vs LWW silent loss |
| Gap analysis | Honest about what quorums don't give (order, transactions) |
| Testing | Linearizability checkers (Jepsen/knossos) |

### Candidate strengths
- Stated the overlap argument as *set intersection*, not folklore — the precise version.
- The "LWW assumes a total order that may not exist" framing is sharp and senior.
- Availability math at the end (tolerated failures = min(R,W)-1) is exactly right.

### Gaps to work on
- Didn't mention **read-your-writes floor** mechanics in detail (client-side version watermark) — it appeared only briefly.
- Could have discussed **durable vs in-memory quorum** (W=2 including a replica that hasn't fsynced).
- Missed **tombstones in quorum systems** (deletes must propagate as versions too).

## Follow-up study prompts
1. Prove: with R+W>N and a single version chain per key, every read returns the latest acknowledged write.
2. How does Cassandra's `QUORUM` vs `LOCAL_QUORUM` vs `EACH_QUORUM` map to the R/W equation in multi-DC?
3. Why does DynamoDB's `ConsistentRead` double the read cost, and what does it guarantee exactly?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on the write path. When a write 'succeeds' with W=2 in a 3-node cluster, what exactly has happened — and what hasn't?"

**Candidate**: "What has happened: the value is on ≥ 2 nodes with a fresh version, so any read quorum (R=2) will *overlap* at least one of them — the read-your-writes and strong-consistency contract holds *assuming those acks are durable*. What hasn't happened: (1) **durability** — if W includes a node that acked before fsync (or that's in-memory only), a crash of both acking nodes loses the write; 'durable quorum' means the ack implies fsync. (2) **Propagation** — the third node may be stale; reads from a quorum that includes it still return the fresh value (version wins), but the stale node needs read repair or background anti-entropy to converge. (3) **Membership certainty** — the 'N' must be agreed; if the node set changed between the write and the read, the overlap argument can fail. So the precise contract: with durable acks and a stable membership, R+W>N gives *strong consistency and bounded durability* — and production systems pick W=N-1 (or fsync-on-ack) when they need the durability half, too."

**Interviewer**: "Tombstones in a quorum system — deletes are the hardest operation. Walk me through the failure you're preventing."

**Candidate**: "The failure: a delete is a write, and a write can be *lost to read repair*. Scenario: replicas r1, r2 hold value v1 (version {A:1}); r3 holds an older v0. A delete with version {D:1} lands on r1, r2. r3 (stale, still has v0 with version {A:0} or an empty version) later merges into a read quorum: a read that hears from r2 (delete tombstone) and r3 (v0) — the versions are incomparable ({D:1} vs {A:0} — concurrent!) → the read returns a *sibling*: the deleted value resurrects as a sibling, and read repair may even write it back out. The fix: deletes are tombstone writes with a version; a tombstone must be *dominating* — implemented as 'value absent + version present', so merge logic treats 'tombstone with version V' as the value for V, and only GCs the tombstone once *all replicas* are known to have it (Dynamo's tombstone GC requires all-replica confirmation, which is why deletes linger). The interview point: *in a versioned store, delete is just a write whose value is 'gone' — the version must survive the value or resurrection is guaranteed*."

**Interviewer**: "Anti-entropy vs read repair — what's the difference, and when is each enough?"

**Candidate**: "**Read repair** is opportunistic: divergence is fixed only when a read happens to touch a stale node — it heals the paths users actually exercise, but never touches cold keys; a stale value can sit forever on a rarely-read key. **Anti-entropy** (background gossip or merkle-tree reconciliation) continuously compares replica state and repairs divergence without reads — it heals everything, at the cost of background traffic and the merkle-tree machinery (per-node, per-range hashes; walk down on mismatch, repair leaves). The design answer: *read repair alone is sufficient for user-visible convergence on hot paths; anti-entropy is required for correctness of cold data, delete propagation, and replica healing after node loss* — production stores run both, and you can prove it: 'every key converges even with zero reads' is the anti-entropy property, and 'every key read with a quorum returns the latest' is the read-repair + quorum property."

**Interviewer**: "Quorum systems and network partitions — the classic critique: R+W>N fails under asymmetric partitions. Walk me through it."

**Candidate**: "The critique: with N=3, R=2, W=2, consider a partition where the coordinator can reach only r1 — but *other* nodes can reach each other. The write path: a client in partition P1 reaches only r1 → can't write (W=2 needs 2 acks). Reads also fail. That's the *availability* loss — correct behavior. The dangerous case is **sloppy quorum**: the coordinator accepts the write on r1 *plus a surrogate* outside the key's home set — the write quorum is {r1, surrogate} which is disjoint from a strict read quorum {r2, r3} → a strict read returns stale data *while believing it met R=2*. That's why sloppy quorums must be paired with *hinted handoff*: the surrogate is required to return the hint to the home node, and reads that find hints must treat them as authoritative. And the deeper point: with *strict* quorums, asymmetry is safe — the overlap argument holds for any two quorums that actually met, and the system fails closed (no quorum = no answer) rather than wrong."

**Interviewer**: "Final: DynamoDB's `ConsistentRead=true` — what does it *actually* guarantee, and why is it expensive?"

**Candidate**: "`ConsistentRead` performs a quorum read (read from both replicas and compare) instead of reading the leader's local copy — it guarantees the read returns the most recent *committed* write for that item (strongly consistent, but not necessarily across items — it's per-item linearizability, not multi-item transactions). Why it's expensive: (1) it reads *both* replicas instead of one — ~2x read cost and latency; (2) it can't use the cached leader-only path; (3) it must wait for the slower of the two responses — tail latency. Why it exists as an opt-in: most reads can tolerate eventual consistency (the SLO is single-digit-millisecond staleness), and DynamoDB prices the guarantee — strong reads cost roughly double. The design answer: *use eventually-consistent reads for the 99% path, ConsistentRead for the money path (or when read-after-write matters), and single-item strong consistency is the boundary — cross-item invariants still need transactions (DynamoDB transactions use a different, more expensive protocol)*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Open the write-path answer with the durability caveat (ack ≠ fsync) — it's the first thing a senior interviewer probes after the happy-path explanation.
- Prepare the tombstone-resurrection scenario as a concrete version-vector walkthrough — the strongest answer in the extended rounds.
- Rehearse 'what exactly is expensive about ConsistentRead' with the two-cost story (2x reads + tail latency).

### One-sentence takeaway
- "Quorum systems buy per-key strong consistency with an availability budget — R+W>N is the contract, versioning is the machinery, and repair (read or background) is the price of convergence."

### Self-check questions (run before the real interview)
1. Can I prove R+W>N with the set-intersection argument in one minute?
2. Can I explain why tombstones must carry versions, with a resurrection scenario?
3. Can I distinguish read repair from anti-entropy and state when each is sufficient?
4. Can I explain what sloppy quorums + hinted handoff do to the consistency contract?
5. Do I know exactly what DynamoDB `ConsistentRead` guarantees and why it costs double?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** State the quorum theorem.
**Hint.** R + W > N ⇒ every read quorum overlaps every write quorum ⇒ reads see latest writes.

**Q2.** N=3, R=2, W=2: how many failures tolerated?
**Hint.** One — the availability cliff is at N − min(R,W) + 1 failures.

**Q3.** What is read repair, and when is it triggered?
**Hint.** A read that sees divergence writes the merged version to stale replicas; heals on-access paths only.

**Q4.** What does a sloppy quorum trade away?
**Hint.** Consistency of routing (writes land on surrogates) for availability — hinted handoff returns them later.

**Q5.** When are two versions 'concurrent'?
**Hint.** Incomparable vector clocks — neither supersedes; both are siblings for the app to merge.

**Q6.** Why must tombstones carry versions?
**Hint.** Otherwise a stale replica's value resurrects as a sibling after the delete merges.

**Q7.** What does quorum consistency NOT give you?
**Hint.** Total order, cross-key transactions, membership consensus — per-key strong consistency only.

**Q8.** Anti-entropy vs read repair — which heals cold keys?
**Hint.** Anti-entropy (background reconciliation); read repair only heals what users read.

**Q9.** Why is `ConsistentRead` ~2x the cost?
**Hint.** Reads both replicas and waits for the slower — ~2x reads and tail latency.

**Q10.** Under an asymmetric partition, what does a strict quorum do?
**Hint.** Fails closed — no quorum, no answer; never a silent stale value.

### Scoring
- **8-10 correct**: ready for the quorum loop.
- **5-7**: revise the overlap proof and versioning.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`QuorumKVStore`) and pass the overlap/partition/sibling tests.
**Day 3**: Quick-Fire rounds; write the R+W>N proof from memory.
**Day 4**: Rehearse the tombstone-resurrection walkthrough and sloppy-quorum tradeoff.
**Day 5**: Drill the extended rounds (durable quorums, anti-entropy, ConsistentRead economics).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
