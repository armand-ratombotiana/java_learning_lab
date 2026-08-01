# Lab 05: Mock Interview — CRDTs (G-Counter, PN-Counter, Merge Semantics)

**Role**: Senior Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: CRDT-focused / distributed databases (Redis, Riak, Automerge)

---

**Interviewer**: "Define a CRDT. What problem does it solve, and what are the two main classes?"

**Candidate**: "A CRDT — conflict-free replicated data type — is a data structure where every replica can be updated *concurrently without coordination*, and replicas can be merged, and the merge **always converges**: any two replicas that have seen the same set of updates converge to the same state, and merges are commutative, associative, and idempotent (a join-semilattice). The problem it solves: in a replicated system, concurrent updates create conflicts; the usual answer is consensus (coordinate every write) or LWW (silently lose one write). CRDTs move the conflict resolution *into the data type* — the merge IS the resolution, deterministic, no coordinator, no lost updates. The two classes: **state-based (CvRDT)**: replicas exchange their full state and merge with a `merge(a, b)` that must be commutative/associative/idempotent — e.g., a G-Counter is a vector of per-node counts merged element-wise by max. **operation-based (CmRDT)**: replicas broadcast *operations* (e.g., 'increment', 'add element'); delivery must be reliable, ordered per-replica, and deliver-at-most-once — causal delivery suffices; operations are applied deterministically to local state. State-based is easier to reason about (any topology, any timing, merges just work); op-based is smaller payloads (just the op, not the state) but requires delivery guarantees. The senior distinction: *the merge function IS the conflict resolution* — you choose the CRDT whose merge matches your domain semantics, not the other way around."

**Interviewer**: "Walk me through G-Counter and PN-Counter. What's the actual representation?"

**Candidate**: "**G-Counter** (grow-only counter): a vector of N integers, one per replica (or per client). `increment()` bumps *your own* entry. `merge(a, b)` takes the element-wise max. This works because you can only ever add to your own entry — max is correct and monotonic. It's a counter that only grows — hence 'G'. **PN-Counter** (positive-negative counter): two G-Counters — `p` for increments, `n` for decrements. `incr()` → p++; `decr()` → n++; `value()` = sum(p) - sum(n). Merge is the pair-wise merge. Why two G-Counters? Because a single vector with element-wise *max* can't represent a decrement — max is monotonic and would resurrect old values (a replica that saw `value=5` then `4` would still hold 5 locally, and max would re-inflate). The P/N split makes decrements *monotonic in the representation* even though the *value* goes down: p grows monotonically, n grows monotonically, and their difference is the value. The mental model: CRDTs make **state monotonic under merge**, and you get non-monotonic *values* (decrements, removals, edits) by pairing monotonic pieces — like subtraction (PN), or tombstones for sets."

**Interviewer**: "So if the merge is max-based, how do you handle removals in general? What's the tombstone cost?"

**Candidate**: "Removals in a convergent system are the hard part — removing an element must not resurrect under a later merge (when a stale replica that never saw the removal merges back in). The classic answer: **tombstones** — a removed element isn't deleted, it's marked removed (e.g., a G-Set of removed IDs). The merge keeps tombstones forever, so removals win. The cost: **metadata grows without bound** — every removed element stays in state forever; for a set of chat messages, tombstones outnumber live elements and the state bloat is real. That's exactly why Automerge/Yjs (text CRDTs) keep tombstones but run **garbage collection when there's a single writer** (no one else can resurrect the element, so tombstones can be dropped — GC of tombstones requires proving the replica set is quiescent/closed). For counters, P/N is the tombstone-free trick. The engineering rule: prefer CRDT representations that avoid tombstones (P/N counters, additive sets when possible), and where tombstones are unavoidable, GC them only under provable quiescence."

**Interviewer**: "Compare the CRDT approach to, say, last-writer-wins or two-phase locking for the 'update a user profile' problem. When is a CRDT the *wrong* tool?"

**Candidate**: "LWW: cheap, total order assumed; loses the loser's update silently — fine for 'last edit wins' fields (nickname), wrong for anything with merges (a list of favorite restaurants edited by two people — LWW drops one person's entire list). 2PL/transactions: correct for arbitrary invariants ('balance must never go negative'), but needs a coordinator and locks — unavailable under partitions. CRDTs: perfect when updates commute and merge has domain meaning (counters, sets, shopping carts, collaborative text — where 'everyone's edits survive' is the requirement). CRDTs are the **wrong** tool when: (1) the invariant is *global and non-commutative* — 'balance ≥ 0' across all accounts; a G-Counter or LWW can't express it; you need a transaction or a carefully-ordered log; (2) the merge semantic is arbitrary business logic — if 'merge' means 'run this 10-step reconciliation', a CRDT can't encode it; (3) you need *strong* consistency (read-your-writes across replicas instantly) — CRDTs are eventually consistent, convergence is async; (4) state must be bounded and updates unbounded without GC support (tombstone growth). The interview answer: CRDTs excel in the 'merge semantics exist and are commutative' niche — which is big (counters, sets, text, carts) — but they don't replace transactions for invariants."

**Interviewer**: "How do you *verify* that a CRDT is correct? What's the strongest property, and how do you test it?"

**Candidate**: "The strongest property: **strong eventual consistency** — any two replicas that have delivered the same updates converge to identical state; more precisely, if two replicas have *merged* each other's state, they're equal forever after. That's the convergence theorem for CvRDTs (merge is a semilattice join) and CmRDTs (causal delivery). Testing: (1) **convergence tests** — run concurrent update sequences from several replicas, merge in *different orders* (a merges with b first vs c first), assert all replicas converge to the same final state; (2) **commutativity/associativity/idempotence tests** — property-based: random interleavings and merge orderings; the merge result must be identical; (3) **replay/stale replica tests** — a replica that slept through 100 updates merges in at the end and lands on the same state (no resurrection — tombstones do their job); (4) **real-world**: Jepsen-style network partitions with concurrent increments — after healing, counters agree. The lab lesson: the test IS the definition — if any merge order produces a different result, you don't have a CRDT, you have a bug."

**Interviewer**: "Design: multi-region 'likes' counter with decrement support (users can unlike). Millions of likes per day. Design the counter."

**Candidate**: "**PN-Counter with per-region shards.** Design: N = number of regions (+1 for clients if needed). Each region holds its own entries — no, better: *each region owns a shard of the vector* and replicates the *whole* vector via gossip; increments go to the local region's entry; `value()` = sum(p) - sum(n) computed from any replica's merged state. Merge: pair-wise max of the p-vectors and n-vectors. Why per-region entries and not per-user or per-like: (1) the vector size is O(regions) — tiny and constant, so gossip payloads are small and bounded; (2) increments are local (write path hits one region), and *any* replica can serve reads once gossip has spread; (3) no per-item metadata — a million likes don't create a million vector entries, just N entries in one vector. Caveats: a region partition means its increments stay local until gossip reconnects — eventual convergence within gossip latency; if per-user write locality matters (same user liking from many regions), keep per-client entries *only when* the count of writers is small. The interview answer: CRDT design is about **choosing the granularity of the monotonic state** — regions for scale, writers for write locality — and the vector is the merge-friendly representation either way."

**Interviewer**: "Final: 'merge' — in a state-based CRDT, is the merge commutative, associative, and idempotent, and why do those three properties matter? Give a concrete failure for each."

**Candidate**: "All three are required for convergence under arbitrary message interleavings. **Commutative** — `merge(a,b) = merge(b,a)`: replicas exchange states in any order; without it, the 'first' replica to merge would win and results would depend on direction. **Associative** — `merge(merge(a,b),c) = merge(a, merge(b,c))`: gossip is multi-hop and messages arrive in any order; a merge can happen in any grouping — a relay that merges b and c first must produce the same result as one that merges a and b first. **Idempotent** — `merge(a,a) = a`: messages are duplicated in real networks (retransmissions, overlap in gossip digests); re-merging must be a no-op. Concrete failures: max and element-wise max satisfy all three — that's why G-Counter works; *sum* is commutative and associative but NOT idempotent — if you summed state you'd double-count on every redelivery — a classic bug; a merge that 'keeps the first' violates commutativity; one that increments a counter per merge violates idempotence. The practical rule: every merge in the code must be a **join in a semilattice** — max, union, or equivalent — and that's exactly what a property-based test on merge ordering catches."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Definitions | CvRDT vs CmRDT, semilattice/join characterization |
| Representations | G-Counter vector + max; PN-Counter as pair of G-Counters |
| Removals | Tombstones, monotonicity, GC under quiescence |
| Fit | When CRDTs are wrong (invariants, bounded state, strong consistency) |
| Verification | Convergence/commutativity tests, property-based testing |
| Design | Granularity choice for the monotonic state |

### Candidate strengths
- "The merge function IS the conflict resolution" — the core insight, stated early.
- Explained *why* P/N split works (monotonic representation for non-monotonic values) rather than just reciting the structure.
- Concrete failure examples for each semilattice property — rare and precise.

### Gaps to work on
- Didn't mention **join-semilattice** formalism until the end; could define CvRDT convergence theorem up front.
- Could have named **operation-based delivery requirements** (causal order, exactly-once) in the first answer — it was implicit.
- Automerge/Yjs example was good but brief; text-editing CRDTs (sequence CRDTs, interleaving problem) deserved one sentence.

## Follow-up study prompts
1. Prove the CvRDT convergence theorem: merge as a semilattice join ⇒ convergence after mutual merges.
2. Why can't a G-Set of tombstones be garbage-collected safely without quiescence? Construct the resurrection scenario.
3. Compare CRDT text editing (Yjs/ Automerge) — what does "interleaving anomaly" mean, and how do sequence CRDTs fix it?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on sets — the classic 'remove is hard' CRDT. Walk me through OR-Set and why it's correct."

**Objective**: "OR-Set (observed-remove set): each element is stored with a *unique tag* (a UUID or (replica, counter) pair); `add(e)` inserts `(e, newTag)`; `remove(e)` marks *all current tags of e* as removed (a tombstone list per element). Merge: union of tags; an element exists iff it has at least one *live* tag. Why it's correct: a concurrent add and remove — remove sees the pre-add tags and tombstones them; the concurrent add's *new tag* is not in the tombstone list, so the element survives the merge — **add-wins semantics**. If you want remove-wins, reverse it: removes get unique tags and an element is gone if any remove-tag covers it. The resurrection failure the tags prevent: a naive 'add/remove sets' CRDT (element in add-set, not in remove-set) resurrects removed elements after a stale replica merges — tags make each operation unique, so the merge knows exactly which additions a removal covered."

**Interviewer**: "The interleaving anomaly in sequence/text CRDTs — what is it, exactly, and how do the modern designs avoid it?"

**Candidate**: "Sequence CRDTs (RGA, Yjs, Automerge) order elements between 'atoms' (characters); concurrent inserts at the same position are ordered deterministically by their ID comparison. The **interleaving anomaly**: a *consecutive* insert by one user (typing 'abc') can be interleaved with a concurrent insert by another user at the same position, producing 'a1b2c3' or worse 'a1b2c3' vs '1a2b3c' — the two users' texts weave together into unreadable output, even though no edit is lost. The cause: the naive 'insert after the same anchor, order by ID' rule scatters the *contiguous* characters of each insert among each other. The fixes: (1) **Yjs's relative positions + integrity**: each character stores its left-origin (the position it was inserted relative to); concurrent inserts *at the same origin* are grouped as a contiguous block, ordered by the origin's ID — so a user's typed run stays contiguous; (2) Automerge's similar left/right insertion tracking with the same intent. The interview point: *naive per-character ordering is wrong; correctness requires grouping by insertion operation (contiguity preservation), and that's what separates production text CRDTs from toy implementations*."

**Interviewer**: "Delta-state CRDTs — what problem do they solve and how do they work?"

**Candidate**: "State-based CRDTs exchange *full state* (O(S) payload per merge) — fine for counters, painful for large sets/maps. Op-based CRDTs exchange small ops but require exactly-once, causally ordered delivery. **Delta-state CRDTs** get both: each local update computes a *delta* — the minimal change to the state (e.g., for a G-Counter, `{A: 1}` instead of the whole vector) — and replicas exchange deltas; receiving a delta merges it like a state merge, and the *delta merge* is itself a join. The kicker: deltas can be **joined** (a node accumulates un-acked deltas and sends their join), and a peer that missed messages recovers by state transfer — so delta-CRDTs degrade gracefully to state-based under partitions and run op-sized under normal operation. The cost: deltas must be shipped with some causal discipline (receive order matters — a delta references its predecessor), so they're usually sent on a causal channel or the deltas are made *idempotent and joinable* so any order works. It's the practical answer to 'state-based payloads are too big' — and it's what production systems use."

**Interviewer**: "CRDT composition — how do you build a map of counters, a per-user likes store? What breaks in composition?"

**Candidate**: "Composition is the CRDT design superpower: a **map** of CRDTs is a CRDT — `MapCRDT<K, V extends CRDT>` merges by merging each value under its key; a **register-with-timestamps** map gives per-field LWW; a **G-Counter per key** gives per-item counters; nested maps compose to trees. What breaks: (1) **key removal** — removing a map key requires tombstones (the same remove problem at the container level); (2) **merge granularity** — the map must merge *values*, not replace by key (replacing the value wholesale loses concurrent updates to the value — the classic composition bug); (3) **boundedness** — a map of counters where keys are user-ids grows unboundedly with writer count. The engineering answer: *compose CRDTs the way you compose data types, but respect three rules — values merge recursively, removals are tombstoned, and the metadata (per-key entries) must be bounded or sharded*."

**Interviewer**: "Final: 'CRDTs are eventually consistent' — how do you give a *stronger* read guarantee on top, e.g., read-your-writes?"

**Candidate**: "Eventually-consistent does not mean *unboundedly* stale — you layer guarantees on the merge machinery: (1) **session affinity + local write acknowledgment**: a client's writes are acknowledged by the replica it's talking to *and* by the local merge — a read through the same replica sees its own writes immediately (read-your-writes for that session); (2) **version floors**: the client keeps the version of its last write and refuses reads that return older versions — retry on another replica; (3) **synchronous gossip to a quorum before ack** (strongly consistent CRDT reads — e.g., 'merge with the value quorum before responding'): a write that's merged with a quorum is guaranteed visible to reads that also touch a quorum — you get linearizable-ish reads at the cost of write latency; (4) **state verification**: 'has the target replica merged my version?' as an explicit check. The interview point: *CRDT convergence is asynchronous, but you can push the ack point anywhere on the merge timeline — 'ack after quorum merge' is exactly how you buy strong semantics without abandoning the CRDT*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Memorize the OR-Set tag mechanics precisely (add creates a new tag; remove tombstones *seen* tags; concurrent add survives) — it was directionally right but hedged.
- Prepare the interleaving-anomaly example concretely ('abc' vs '123' at the same position) — the most visual answer in the interview.
- Rehearse the composition rules (recursive merge, tombstone removals, bounded metadata).

### One-sentence takeaway
- "A CRDT is a choice of *merge semantics* — every representation (tags, tombstones, P/N splits) exists to make the merge monotonic, and every design decision is 'what should a concurrent add and remove mean?'"

### Self-check questions (run before the real interview)
1. Can I implement OR-Set semantics in pseudocode and prove add-wins under concurrent remove?
2. Can I explain why naive sequence CRDTs interleave and how Yjs/Automerge group insertions?
3. Can I describe delta-CRDTs and when they degrade to state-based?
4. Can I compose a map-of-counters CRDT and name the three composition rules?
5. Can I explain how to buy stronger read guarantees on top of eventual consistency?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** State the convergence theorem for CvRDTs.
**Hint.** Merge is a join in a semilattice (commutative/associative/idempotent) ⇒ mutual merges converge.

**Q2.** Why can't a single integer with `max` be a G-Counter?
**Hint.** Max can't represent additive increments from different replicas — need per-replica entries.

**Q3.** Why does a PN-Counter use two G-Counters?
**Hint.** Decrements must be monotonic in state, not value — n grows like p; value = p − n.

**Q4.** What is a tombstone, and when can it be GC'd?
**Hint.** Removal marker; only safe to drop under provable quiescence (no stale replica can resurrect).

**Q5.** In an OR-Set, what does a remove actually delete?
**Hint.** Only the tags it sees — a concurrent add's fresh tag survives (add-wins).

**Q6.** What is the interleaving anomaly?
**Hint.** Concurrent contiguous inserts at one position weave together ('a1b2c3') — fixed by grouping insertions by origin.

**Q7.** State-based vs op-based — the one-line tradeoff.
**Hint.** State: any topology, full-state payloads; op: small payloads, needs causal/exactly-once delivery.

**Q8.** What is a delta-CRDT?
**Hint.** State-based with op-sized diffs — degrades gracefully to state exchange under partitions.

**Q9.** Name the three composition rules for nested CRDTs.
**Hint.** Values merge recursively, removals are tombstoned, metadata is bounded or sharded.

**Q10.** How do you get read-your-writes on a CRDT?
**Hint.** Session affinity to the acked replica, version floors on reads, or ack-after-quorum-merge.

### Scoring
- **8-10 correct**: ready for the CRDT loop.
- **5-7**: revise the semilattice properties and P/N representation.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`CrdtsLab`) and pass the convergence/merge-order tests.
**Day 3**: Quick-Fire rounds; prove idempotence failure of sum on paper.
**Day 4**: Rehearse the OR-Set tag mechanics and the interleaving-anomaly example.
**Day 5**: Drill the extended rounds (sequence CRDTs, deltas, composition, stronger guarantees).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
