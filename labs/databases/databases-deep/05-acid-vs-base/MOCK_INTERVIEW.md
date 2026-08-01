# Lab 05: Mock Interview — ACID vs BASE & Distributed Transactions

**Role**: Senior Database / Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: FAANG / financial-tech (payments, ledgers)

---

**Interviewer**: "Let's open with the fundamentals. What does ACID guarantee, concretely, and what does each letter buy you?"

**Candidate**: "ACID is the contract a transaction makes with the application. **Atomicity**: the transaction is all-or-nothing — if it aborts, no partial effects are visible. **Consistency**: the transaction moves the database from one valid state to another — invariants, constraints, and application rules hold (this one is the most misunderstood: it's the *application's* invariant, the database only enforces what you declare). **Isolation**: concurrent transactions behave as if serialized — the strongest form is serializability. **Durability**: once committed, the effects survive crashes. The practical payout: application code is dramatically simpler — you write 'do this, then that' and the engine guarantees the rest. The cost: coordination. And that coordination cost is exactly what breaks when you go distributed."

**Interviewer**: "Where does 2PC sit in that picture, and why is it the standard for distributed transactions?"

**Candidate**: "Two-phase commit is the classic protocol for atomicity across multiple participants. Phase 1 — **prepare**: the coordinator asks each participant to write an 'I can commit' intent record (a prepare log entry) and reply yes/no. Phase 2 — **commit/abort**: if everyone said yes, the coordinator tells all participants to commit; if anyone said no or timed out, everyone aborts. The key trick: once a participant has *prepared*, it cannot unilaterally change its mind — it must wait for the coordinator's decision, because other participants may already have committed. That makes prepare the point of no return. 2PC gives you atomicity; it does not by itself give you isolation — you still need locking or MVCC on top."

**Interviewer**: "What's the classic failure in 2PC — and what's the recovery mechanism?"

**Candidate**: "The coordinator crash is the classic one. If the coordinator dies after some participants prepared but before deciding, the participants are *in doubt* — they hold locks and resources and can't decide alone, because one participant committing while another aborts breaks atomicity. The fix is a **transaction log + recovery protocol**: the coordinator writes every state transition (prepare-sent, commit-decided, abort-decided) durably before acting. On restart, it reads the log: if a transaction had a *decided* record, re-drive the decision; if it was still in *prepare* phase, it can roll it back after a timeout. The participants also log their prepare records so they can resume after *their* crashes. The remaining hole — coordinator and all participants crash together before any decision — is closed with a *presumed abort* or *presumed commit* rule, where the coordinator's absence is treated as a decision."

**Interviewer**: "So why do people say 2PC doesn't scale?"

**Candidate**: "Three costs. (1) **Latency**: every transaction pays at least two round trips to every participant, plus fsyncs; commit latency is dominated by the *slowest* participant — tail latency hurts badly. (2) **Lock duration**: participants hold locks from prepare until decision; a slow or crashed participant blocks everyone — that's why 2PC is notorious for 'prepared transaction pileup' that freezes a database cluster. (3) **Availability**: the protocol needs all participants alive at prepare time; any single failure stalls the transaction. The result: for high-throughput microservice systems, 2PC is usually wrong. That's exactly the gap BASE and Sagas fill."

**Interviewer**: "Define BASE for me, and be precise about what it gives up."

**Candidate**: "BASE = **Basically Available, Soft state, Eventually consistent**. Basically available: the system responds even during partitions — reads may return stale data, writes may be buffered. Soft state: the system can change over time without new input, because replicas converge in the background. Eventually consistent: if writes stop, replicas converge to the same value. What it gives up is *synchronous* consistency — you cannot assume a read returns the latest committed write, and you must handle conflicts. It's not 'worse ACID' — it's a *different contract*: availability and partition tolerance first, consistency as a background process, which is the only coherent choice for wide-area, always-on systems."

**Interviewer**: "CAP: walk me through the theorem and the common misconception."

**Candidate**: "CAP says a distributed system can guarantee only two of Consistency, Availability, and Partition tolerance. The nuance most people miss: *you don't choose whether partitions happen* — networks partition regardless. So the real choice is between C and A *during* a partition: on partition, do you (C) refuse responses that might be stale — wait or error — or (A) answer with whatever data you have? Systems like CP databases (most SQL HA, ZooKeeper, etcd) choose consistency and availability inside partitions but sacrifice availability when quorum is lost; AP systems (Cassandra, DynamoDB in many configs) serve stale reads during partitions and reconcile later. And the trickier nuance: CAP is about *single operations*, not transactions — serializability across operations is a stronger property CAP doesn't even measure."

**Interviewer**: "Let's move to design. You're building a payment system: debit one account in service A, credit another in service B, each with its own database. 2PC or Saga? Walk through the decision."

**Candidate**: "For a *payment system*, I'd strongly prefer Saga — orchestration Saga with explicit compensation. Here's the reasoning: 2PC holds locks across two databases for the full transaction duration; at payment scale that means deadlocks, blocking, and prepared-transaction pileups, and it couples both services' availability — if A is down, B's transactions back up too. Saga breaks the transaction into local steps — debit(A), credit(B) — each committed locally and durably, with an orchestrator driving the sequence and a compensation (reverse-debit) on failure. If debit succeeds and credit fails, we compensate by crediting back the debited account. The tradeoff: no global isolation — another transaction can observe the intermediate 'debited but not credited' state — so the business must tolerate it (or use ledger-based reconciliation), and compensations must be exactly-once — idempotency keys on every step."

**Interviewer**: "A critic says: 'compensation isn't the same as rollback — you can't un-send an email.' What's your answer?"

**Candidate**: "Exactly — that's the heart of the saga tradeoff. Rollback via compensation is a *business-level* inverse operation: reverse the transfer, send a refund, issue a credit note. Some effects are genuinely non-invertible: you can't un-send a notification, you can't un-book a seat someone else took. So the design rules are: (1) order steps so the irreversible ones go *last*; (2) make each step idempotent so retries and compensations are safe; (3) design compensations to handle partial failure — compensate step 3 even if step 4 never ran; (4) accept that some steps need out-of-band human or reconciliation handling (payment processor webhook missing for 24h → alert + reconcile). The saga pattern doesn't remove failure — it converts it into a *well-defined, observable process*."

**Interviewer**: "What's the difference between orchestrated and choreographed Sagas?"

**Candidate**: "**Orchestration**: a central coordinator (the orchestrator) decides the next step, stores the saga state, and triggers compensations. Pros: single place to reason about the flow, easy to inspect ('saga 42 is on step 3'), easier to test. Cons: the orchestrator is a single component (not single point of failure, but a coordination bottleneck) and it can grow into a god service. **Choreography**: each service, after its local step, publishes an event; the next service subscribes and reacts; compensation is event-driven too. Pros: no central component, services stay decoupled, scales naturally. Cons: the flow is implicit — you can't see it in one place, it's harder to debug and version, and you can get event storms or loops. My rule: choreography for simple linear flows with few participants; orchestration the moment you have branching, retries, timeouts, or complex compensation order — which is most real systems."

**Interviewer**: "How do you test a distributed transaction system?"

**Candidate**: "Layers: (1) **Unit**: each step's happy path, idempotency (replaying a step), and its compensation inverse — verify debit-compensation nets to zero. (2) **Integration**: orchestration across in-memory fake participants, with injected failures — drop a message, crash a participant at every step boundary, double-deliver an event. (3) **Chaos**: kill the coordinator mid-transaction, kill participants between prepare and commit, force fsync failures; the invariant to verify is *atomicity*: never a state where one participant committed and another aborted the same saga. (4) **Reconciliation**: build a deterministic test that replays the transaction log and confirms final balances match the ledger invariant. The property I always assert: for every saga, the outcome is exactly one of {all steps applied, all steps compensated}."

**Interviewer**: "Final question: when IS 2PC the right answer?"

**Candidate**: "Three situations. (1) **Low volume, high value**: financial settlement, batch-like workloads where correctness dwarfs latency — a few hundred TPS is fine. (2) **Short-lived, small-scale coordination**: a transaction touching 2-3 databases with fast, reliable participants and sub-second locks. (3) **When the storage engine gives it to you cheaply**: engines like CockroachDB implement distributed transactions at the *range* level with consensus internally — you get 2PC semantics without running your own coordinator, and their optimized commit (parallel commits) makes it fast. Outside those, prefer Sagas, idempotency, and event-driven reconciliation — BASE done right beats ACID done badly."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| ACID precision | Correctly flagged Consistency as application invariant |
| 2PC mechanics | Prepare = point of no return; coordinator log + re-drive recovery |
| BASE nuance | Described as a different contract, not a downgrade |
| CAP depth | Partition is a given; choice is only during partition |
| Design judgment | Chose Saga for payments with explicit idempotency rules |
| Failure realism | Compensation ≠ rollback; irreversible steps go last |

### Candidate strengths
- The "prepare is the point of no return" explanation is exactly right and rarely said this clearly.
- CAP answer included the 'single operation' caveat — a senior differentiator.
- Gave a concrete testing strategy with an atomicity invariant, not just "write tests".

### Gaps to work on
- Didn't mention **read-your-writes during saga execution** — participants observe intermediate states; offer isolation mitigations (saga isolation levels, forwarding).
- Could have named the 2PC optimization 'read-only transactions skip prepare' (one-phase commit optimization).
- Missed **saga state machine storage** detail (orchestrator must persist state before sending each message).

## Follow-up study prompts
1. What is the "blocking problem" of 2PC and how do Paxos-based commit (Percolator, Spanner) fix it?
2. How do isolation levels interact with saga execution — what does "saga isolation" mean in practice?
3. Compare XA (JTA/Atomikos) vs SAGA frameworks (Axon, Temporal, Seata) — when is each used in JVM stacks?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's drill into isolation. What are the standard isolation levels, and where do the classic anomalies appear?"

**Candidate**: "Four standard levels. **Read uncommitted**: reads see uncommitted data — dirty reads possible. **Read committed**: reads see only committed data — dirty reads gone, but non-repeatable reads remain (two reads in one transaction can differ). **Repeatable read**: your reads are stable within the transaction — non-repeatable reads gone, but phantom reads remain (a range query can return different rows if another transaction inserts into the range). **Serializable**: full isolation — concurrent transactions behave as if executed one after another. The classic example ladder: dirty read → non-repeatable read → phantom. With MVCC engines: PostgreSQL's default is read committed (with statement-level snapshots); MySQL InnoDB's default is repeatable read (with next-key locking that actually blocks phantoms in many cases). The interview point: *isolation is a spectrum, and the level you choose is a concurrency-vs-guarantee tradeoff* — not a box to tick."

**Interviewer**: "How does MVCC implement read committed and repeatable read differently?"

**Candidate**: "MVCC keeps multiple row versions with visibility metadata (created xid, deleted xid). **Read committed**: each *statement* gets a fresh snapshot — a long transaction's two SELECTs can see different versions if another transaction committed between them. **Repeatable read**: the *transaction* gets one snapshot at the first query — all queries see the same version set. The cost difference is subtle: both are non-blocking (readers never block writers), but repeatable read must hold the snapshot's version references — long transactions pin old versions, delaying vacuum/GC (PostgreSQL's 'long transaction → bloat' problem). The engine-side detail: PostgreSQL labels its repeatable read 'snapshot isolation' — it prevents most anomalies but not full serializability (write-skew is possible); true serializable needs SSI (serializable snapshot isolation) with read-set tracking."

**Interviewer**: "Write skew — define it and give a concrete example."

**Candidate**: "Write skew: two concurrent transactions each read overlapping state, each writes based on it, and the *combined* result violates an invariant — even though neither transaction, alone, sees an anomaly. Classic example: a doctors-on-call table where the rule is 'at least one doctor must be on call'; T1 reads 'A and B on call', updates A to off-call; T2 reads 'A and B on call' (before T1 commits), updates B to off-call. Both commit — now nobody is on call. Under snapshot isolation this is invisible: each transaction's snapshot had two doctors. Fixes: (1) serializable isolation with SSI (PostgreSQL detects dangerous structures via read-write dependencies); (2) serialize on a common row (SELECT ... FOR UPDATE on a 'roster' row); (3) constraint checks at the DB level (a deferred constraint that validates post-commit). The lesson: *snapshot isolation is not serializable, and write skew is the proof*."

**Interviewer**: "Back to 2PC recovery — walk me through the coordinator crash at each possible moment. What does the log contain, and what does recovery do?"

**Candidate**: "The coordinator's log records every transition: BEGIN, PREPARE sent to each participant, each PREPARE-OK, COMMIT decision, ABORT decision. Crash analysis: (1) crash *before* sending any prepare — no participant knows the transaction; recovery simply forgets it (participants' local timeout aborts their prepared-but-uncoordinated entries... actually no participants prepared anything, so nothing to do). (2) Crash *after* some PREPARE-OKs but before deciding — the log shows 'prepared' with no decision; on recovery the coordinator must re-contact participants and re-drive the protocol (or abort after a timeout — the 'presumed abort' rule). (3) Crash *after* logging COMMIT but before any participant commits — recovery reads the COMMIT record and re-sends commit to everyone; a participant that already got it is idempotent. (4) The nasty case: coordinator and participants all crash, then recover — participants with prepared records must ask the coordinator; if it never comes back, they block (the blocking problem). The absolute rule: *the decision is logged durably before it is acted on* — that ordering is what makes recovery deterministic."

**Interviewer**: "Your lab implements 2PC in a single JVM. What's the strongest test of the protocol?"

**Candidate**: "The atomicity property: *no participant commits unless every participant can commit*. Tests: (1) happy path — all participants commit, state consistent everywhere. (2) A participant votes NO at prepare — assert *all* participants abort (even the ones that said yes). (3) Crash the coordinator after each phase — assert recovery re-drives the decision correctly: a committed saga stays committed everywhere; a prepared-but-undecided saga aborts (or re-drives). (4) Crash a participant after prepare — it must resume from its prepare record, not lose its vote. (5) The killer test: crash coordinator *after* logging COMMIT but *before* any participant receives phase-2 — recovery must complete the commit, and the assertion is 'all participants eventually committed exactly once'. Plus idempotency: replaying phase-2 messages must be a no-op. If your recovery can pass (3) and (5), the log-and-replay design is sound."

**Interviewer**: "Final: 'BASE done right beats ACID done badly' — what does 'done right' mean operationally?"

**Candidate**: "It means the *whole* system, not just the database: (1) **idempotency everywhere** — every write and every compensation carries an idempotency key; retries are the norm, not the exception. (2) **explicit conflict policy** — the team has written down what happens when two replicas diverge: LWW with an audit trail, per-field merge, or CRDTs — and the code implements *that* policy, not 'whatever the last write was'. (3) **reconciliation as a scheduled job** — nightly diffs between the eventual-consistent view and the source of truth, with alerts on drift. (4) **bounded staleness SLOs** — 'reads see writes within 5s p99' — measured, not assumed. (5) **observable convergence** — lag, conflict counts, and repair counts are dashboarded. BASE is only 'done right' when the team can answer: *what is my staleness bound, my conflict rate, and my reconciliation flow?* If you can't, you don't have BASE — you have a system with no contract at all."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Lead the ACID answer with a concrete example transaction — 'transfer $10 from A to B' — so the abstract letters land.
- Prepare the write-skew story *with the doctors example memorized* — it's the highest-yield isolation question.
- Practice narrating a 2PC log-recovery sequence on a whiteboard: log records, crash points, recovery actions.

### One-sentence takeaway
- "ACID and BASE are contracts, not quality labels — the engineering is choosing the contract your business can actually live with, then building the machinery to keep its promises."

### Self-check questions (run before the real interview)
1. Can I enumerate the four isolation levels with their exact anomalies?
2. Can I explain why snapshot isolation allows write skew, and give the doctors example?
3. Can I narrate 2PC recovery from a coordinator crash at each of the three crash points?
4. Can I list the five operational ingredients of 'BASE done right'?
5. Do I know when 2PC is genuinely the right answer (low-volume, high-value, engine-native)?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** What does each ACID letter actually guarantee?
**Hint.** Atomicity all-or-nothing; Consistency = app invariants; Isolation = as-if-serialized; Durability = survives crashes.

**Q2.** In 2PC, what does 'prepared' mean for a participant?
**Hint.** It has durably written 'can commit' and can no longer change its mind — the point of no return.

**Q3.** What is the 2PC blocking problem?
**Hint.** Prepared participants wait on a crashed/indecisive coordinator — locks held, no decision.

**Q4.** How does a coordinator recover after a crash?
**Hint.** Read the transaction log: decided → re-drive; prepared-undecided → abort (presumed abort) or re-drive.

**Q5.** Name the four saga isolation patterns.
**Hint.** Semantic locks, commutative updates, pessimistic reads/forwarding, versioned/rewritable state.

**Q6.** Why must a compensation be idempotent?
**Hint.** Retries + unknown-outcome steps (timed out but succeeded) converge only via idempotency keys.

**Q7.** Order the order-flow saga: which step is irreversible?
**Hint.** ship is the irreversible boundary — everything after it lives in a separate flow.

**Q8.** CAP: what is actually being chosen during a partition?
**Hint.** C (refuse potentially-stale answers) vs A (answer anyway) — partitions are a given.

**Q9.** When does 2PC make sense in production?
**Hint.** Low volume, high value, short scope, or engine-native (CockroachDB-style) — not the default.

**Q10.** What is write skew and which isolation level permits it?
**Hint.** Two transactions read overlapping state, write conflicting updates; snapshot isolation.

### Scoring
- **8-10 correct**: ready for the ACID/BASE loop.
- **5-7**: revise 2PC recovery and the isolation ladder.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`TwoPhaseCommitCoordinator`) and pass the crash-at-every-phase recovery tests.
**Day 3**: Quick-Fire rounds; draw the 2PC state machine with crash points on paper.
**Day 4**: Rehearse the isolation ladder (four levels + anomalies) and the doctors write-skew example.
**Day 5**: Drill the extended rounds (group commit... wait, that's replication — 2PC optimizations, saga isolation, outbox).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
