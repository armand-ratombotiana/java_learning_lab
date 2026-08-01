# Lab 02: Mock Interview — Distributed Transactions (Saga Pattern)

**Role**: Senior Backend / Distributed Systems Engineer
**Duration**: 45 minutes
**Company style**: E-commerce / fintech / microservices

---

**Interviewer**: "Let's start with context. When do you reach for a Saga instead of a distributed transaction?"

**Candidate**: "The trigger is the microservice boundary: when a business transaction spans multiple services, each owning its own database, the classic ACID transaction is unavailable — there's no single coordinator with locks on all the data. Options are 2PC (strong atomicity, but locks across services, blocking failures, and tight coupling of availability) or Sagas — a sequence of local transactions, each committed to its own database, with *compensating* actions that undo earlier steps if a later step fails. I reach for Sagas when: the flow is long-lived (seconds to minutes — you can't hold locks that long), the participants are independent services with their own data sovereignty, or the latency/availability cost of 2PC is unacceptable. The tradeoff you must accept: **no global isolation** — intermediate states are visible, and the final state is eventually consistent."

**Interviewer**: "Walk me through the Saga execution model — orchestration vs choreography — and when you'd pick each."

**Candidate**: "**Orchestration** (I prefer it for most systems): a dedicated Saga coordinator (or 'saga execution engine') holds the saga state machine, calls each participant's command API, and on failure invokes compensations in reverse order. Pros: the flow is explicit and inspectable — you can answer 'where is saga 42 and what did it do?'; retries, timeouts, and idempotency are centralized; easy to test. Cons: the orchestrator is a component that must be operated (and it can become a god service if you centralize *all* business logic — keep it a *flow engine*, not a business layer). **Choreography**: each service, after committing its local transaction, publishes a domain event; the next service reacts; compensation is event-driven ('payment-failed' → subscribers compensate). Pros: zero central component, maximal decoupling. Cons: the flow is implicit and scattered — debugging a stuck saga means replaying events across services; loops and duplicate events need careful design. My rule: linear flows with 2-3 steps → choreography can work; anything with branching, timeouts, retries, or complex compensation order → orchestrate."

**Interviewer**: "What exactly must a compensation do to be correct? What's the hardest part?"

**Candidate**: "A compensation is a *business-level inverse* of a step: cancel-order for create-order, refund for charge, restock for reserve-inventory. The hardest part is that it must be **idempotent and safe under partial failure**. Three requirements: (1) **Idempotency** — every command and compensation carries an idempotency key; replaying a refund must not double-refund. (2) **Compensation-of-compensation**: if a compensation itself fails (the payment gateway is down), the saga must *retry* with backoff, not give up — and eventually escalate to manual/queue-based handling. (3) **Non-invertible steps**: you cannot un-send an SMS. The design rule: order steps so irreversible effects happen last, and treat any step after the first irreversible one as 'can't be compensated, must be reconciled.' And there's a subtle one: the compensation must run even when the original step's *outcome is unknown* — e.g., the charge API timed out but actually succeeded; the refund must be written against the idempotency key so the outcome converges."

**Interviewer**: "What does the saga state machine look like? How do you store and recover it?"

**Candidate**: "Per-saga state: status (STARTED / STEP-EXECUTING / STEP-COMPENSATING / COMPLETED / FAILED), the step index, and a journal of outcomes. The rule that makes recovery possible: **persist before you send** — the orchestrator writes 'about to execute step 3' to its saga log (its own database, same local transaction as its own state) *before* invoking the service. Then a crash anywhere is recoverable: on restart, read the log; if step 3 was marked sent-but-not-acknowledged, retry it idempotently; if it was acknowledged, proceed to step 4; if the saga is in compensating state, continue compensations from the journal. This is exactly why saga engines (Temporal, Axon, Seata, Cadence) are built on durable workflow state — the saga log *is* the source of truth, and the participants are just effects of it."

**Interviewer**: "Isolation — a critic says 'your saga shows intermediate states, so it's not transactional at all.' How do you respond?"

**Candidate**: "Saga guarantees *atomicity* (all-or-nothing at the business level via compensation) but not *isolation* — and the industry answer is **saga isolation patterns**. (1) **Semantic locks**: the saga marks affected rows 'in-progress' — a concurrent read either blocks or sees a notice, so nobody acts on half-committed data (reserve-inventory with a status column). (2) **Forward recovery / contingency**: instead of undoing, complete the remaining steps with corrective data. (3) **Commutative updates**: order steps so they commute — then interleaving doesn't matter. (4) **Pessimistic view on the read side**: readers of saga state see a 'pending' flag and treat it accordingly. The honest answer: you trade isolation for availability and decoupling, and you *design* the business to tolerate it — which is why payment ledgers and inventory systems are built saga-first."

**Interviewer**: "Give me a concrete saga design for the classic 'order → payment → inventory → ship' flow."

**Candidate**: "Steps: 1) create-order (status PENDING), 2) reserve-funds at the payment service (authorization hold), 3) reserve-inventory (deduct stock, hold), 4) ship (irreversible-ish). Compensations, reversed: 4) ship can't be compensated → handle via return flow; 3) release-inventory (restock), 2) void-authorization (release the hold), 1) mark-order CANCELLED. Design points: each step is idempotent keyed by orderId; the payment authorization is an *hold* not a capture — so compensation is a void, cheap and safe; inventory reservation is time-bounded (auto-expire after 30 min — a *timeout as compensation*); the orchestrator journals every transition; on any failure it runs compensations 3→1 and marks the saga FAILED with an audit trail. The killer detail: **the ship step is where irreversibility starts** — so the saga must be COMPLETED before ship, and anything after ship lives in a separate 'fulfillment' flow with its own error handling."

**Interviewer**: "Timeouts and retries — how do you keep a saga from retrying forever or compensating prematurely?"

**Candidate**: "Three levers. (1) **Per-step timeouts with retry budgets**: each step gets N retries with exponential backoff + jitter; only after exhausting the budget does the saga start compensating. Premature compensation is the classic bug — a slow-but-successful step gets compensated while it's still about to succeed, and now you're compensating a success. Mitigation: idempotency keys make a delayed success harmless — the compensation and the success reconcile on the same key. (2) **Compensation retry too**: compensations have their own budget, and after that, the saga enters `COMPENSATION_FAILED` with a dead-letter queue and a reconciliation job — never silently give up. (3) **Global saga deadline**: a saga that exceeds its overall SLA (e.g., 5 minutes) fails closed even without a step failure — protects against a step that neither succeeds nor fails in time."

**Interviewer**: "How do you test a saga system?"

**Candidate**: "The property to verify: *for every saga, the final outcome is exactly one of {COMPLETED, COMPENSATED}* — never stuck, never double-applied. Test layers: (1) **Unit**: each step's success/failure/idempotency, each compensation's inverse — verify reserve-then-release nets to zero stock. (2) **Orchestrator integration with fault injection**: crash the orchestrator at every journal line (there's a test hook that kills it after each write), inject participant failures at every step, double-deliver commands — then verify recovery converges. (3) **Deterministic replay**: run the saga against a recorded event log to check state-machine transitions. (4) **Jepsen-style chaos**: kill participants mid-saga, partition the network, and assert the saga journal ends in a consistent terminal state. The golden test everyone should run: a payment step whose response is *lost* (not failed) — the idempotency key must make the retry converge to exactly one charge."

**Interviewer**: "Final question: how does a saga differ from an event-sourced workflow? Where do they overlap?"

**Candidate**: "They're orthogonal layers. Event sourcing is a *data model*: state changes are stored as an append-only event log, and current state is a projection. A saga is a *transaction protocol*: how a multi-service flow reaches all-or-nothing. You can combine them — the saga journal itself can be event-sourced (the orchestrator's events ARE its state), and participants can be event-sourced too (compensation = emit a compensating event). The overlap: both care about 'the log is the source of truth' and both need idempotency and replay. In practice, saga engines (Temporal/Axon) are built on workflow logs that behave event-sourced, and event-sourced systems use saga semantics for cross-aggregate flows. The interview answer: saga answers *who does what and in what order when things fail*; event sourcing answers *how state is stored and derived*."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Trigger conditions | Knew 2PC's lock/availability costs and when sagas fit |
| Pattern mechanics | Orchestration vs choreography with honest tradeoffs |
| Correctness depth | Idempotency keys, persist-before-send, unknown-outcome handling |
| Isolation | Named the four saga isolation patterns |
| Operational design | Retry budgets, deadlines, dead-letter escalation |
| Testing | Outcome-invariant testing, crash injection at journal lines |

### Candidate strengths
- The "payment hold vs capture" design detail (void is a cheap compensation) is exactly how real systems work.
- Persist-before-send is the deepest correctness point in saga recovery, and you named it early.
- Correctly placed the saga-log-as-source-of-truth (Temporal/Axon) architecture.

### Gaps to work on
- Didn't mention **saga and outbox pattern** together — publishing 'step completed' events must use the transactional outbox to avoid dual-write issues.
- Could have discussed **parallel saga steps** (fan-out) and how compensation order handles them.
- Missed **saga monitors/observability** (tracing across participants, saga dashboards).

## Follow-up study prompts
1. How does the transactional outbox pattern guarantee exactly-once event emission for saga steps?
2. What does a saga state machine need to support parallel steps and their compensation order?
3. Compare Seata (AT/TCC modes) with Temporal for saga orchestration in JVM stacks.

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on the outbox. Your saga's participants publish events. How do you publish *and* commit the local transaction atomically?"

**Candidate**: "The dual-write problem: if a service commits its DB transaction and then publishes to a broker, a crash between the two loses the event — the downstream never knows the step happened. The **transactional outbox** pattern: in the *same local transaction* that mutates business state, insert an outbox row ('event type, payload, id'); a **relay** (CDC like Debezium, or a polling publisher) reads outbox rows and publishes them, marking them published. The guarantees: (1) exactly-once *emission* from the DB's perspective — the event exists iff the transaction committed; (2) at-least-once *delivery* to the broker — the relay retries until acked; consumers must dedupe by event id. The relay must publish *in order* per aggregate (or rely on consumer-side ordering), and the outbox table must be cleaned (batch delete after ack) to avoid unbounded growth. In a saga, every 'step completed' event must flow through the outbox — otherwise a crash after commit but before publish leaves the saga permanently stuck."

**Interviewer**: "Idempotency — the exact mechanism. How do you make 'charge the card' idempotent across retries and compensations?"

**Candidate**: "Three layers. (1) **Idempotency key**: the client (or orchestrator) generates a unique key per logical operation — `charge:{sagaId}:{stepId}` — and sends it with the command. The server stores `(key → outcome)` in its database *in the same transaction as the charge*; on a retry, it looks up the key: if present, replay the recorded outcome without re-charging. (2) **Unique constraint as the arbiter**: the DB enforces one row per key — concurrent duplicate requests race, and the constraint lets exactly one win; the loser returns the winner's outcome (or waits). (3) **Compensation symmetry**: the refund carries the *same* key family — `refund:{sagaId}:{stepId}` — so if the charge actually succeeded but the response was lost, the refund reconciles against the real charge and nets to zero; double-compensation is prevented because the refund key is also unique. The critical detail: *the idempotency record must be written transactionally with the effect* — if the effect happens and the record doesn't (or vice versa), idempotency is broken."

**Interviewer**: "A saga step is a *fan-out*: one saga must update 5 warehouses in parallel, and any failure compensates all. How does the state machine handle it?"

**Candidate**: "The state machine needs a **parallel-step construct**: the journal records 'step 3 is a fan-out of 5 sub-steps'; each sub-step has its own idempotency key and outcome record. Completion semantics: the fan-out completes when all sub-steps complete *or* the quorum condition is met (if the business allows 3-of-5 with degraded fulfillment, that's a configurable completion policy — but compensation semantics must be defined per policy). Compensation: on any sub-step failure, compensate *all completed sub-steps* (not just the failed one's siblings) — the ordering rule is 'compensate in reverse completion order, and never compensate a sub-step twice'. Crash recovery must replay the fan-out: on restart, re-issue commands for sub-steps whose outcome is unknown (idempotency makes this safe). The design point: *parallelism multiplies the failure surface — every sub-step needs its own retry budget, and the fan-out must be journaled as a unit so recovery knows the intended completion policy*."

**Interviewer**: "Isolation in sagas — give me the concrete patterns with a real example."

**Candidate**: "Four patterns from the literature (Garcia-Molina & Salem): (1) **Semantic locks**: the inventory reservation marks rows 'HELD' — concurrent readers see the hold and don't promise that inventory; release = compensation. (2) **Commutative updates**: design steps so order doesn't matter — e.g., a cart adds items in any order; interleaving is harmless. (3) **Pessimistic reads / forwarding**: readers of in-progress saga state get a 'pending' marker; operations that would conflict forward to the compensating path or wait. (4) **Rewritable/versioned state**: keep the old value alongside the new — a read that lands mid-saga can be answered with the pre-saga value plus a notice. Concrete example: the order flow — the order row carries status PENDING; a 'getOrder' API returns it with status; a 'cancelOrder' by the user (outside the saga) is refused while status is PENDING (or allowed and *forwarded* as a compensation trigger). The point: sagas don't have DB isolation, so *the application must create it at the business level*."

**Interviewer**: "Final: how do you operate a saga system — the observability and operations story?"

**Candidate**: "Five pieces. (1) **Saga dashboard**: per-saga state, current step, duration, retry count — the orchestrator's journal is the data source; every saga is queryable by ID. (2) **Distributed tracing**: a trace id per saga spans all participants — every command and compensation carries it; you can answer 'what did this saga touch?' (3) **Metrics**: saga completion rate, compensation rate (a rising compensation rate is a business alarm — customers are failing), retry distribution, dead-letter depth. (4) **Dead-letter + reconciliation**: a saga stuck in COMPENSATION_FAILED lands in a dead-letter queue with a replay button; nightly reconciliation jobs diff saga outcomes against participant states (e.g., 'saga says refunded, gateway says refunded?') — drift alerts. (5) **Versioning**: sagas are versioned; an in-flight saga at version 1 must finish under version-1 semantics while new sagas run version 2 — the journal records the version and the engine honors it. The one-liner: *a saga system is a database of workflows — operate it like one: queryable state, alarms on the money paths, and a reconciliation loop*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Lead the compensation answer with the 'unknown outcome' case (timed-out-but-succeeded) — it's the deepest correctness point and took a while to arrive.
- Prepare a concrete idempotency-key SQL sketch (unique constraint + outcome column) for the mechanism question.
- Rehearse the fan-out state machine: journaling a parallel group, completion policy, reverse-order compensation.

### One-sentence takeaway
- "A saga is a durable state machine whose journal is the source of truth — idempotency makes retries safe, persist-before-send makes crashes recoverable, and compensation makes failure a process, not a catastrophe."

### Self-check questions (run before the real interview)
1. Can I explain the dual-write problem and the outbox fix in two minutes?
2. Can I specify the idempotency mechanism down to the unique constraint?
3. Can I enumerate the four saga isolation patterns with an example each?
4. Can I design the fan-out step construct and its compensation order?
5. Can I describe the operations story: dashboard, tracing, metrics, dead-letter, reconciliation?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Saga vs 2PC — the one-line decision.
**Hint.** Long-lived + independent services + availability → saga; short, low-volume, strong atomicity → 2PC.

**Q2.** What does a compensation undo, and what can't it?
**Hint.** Business-level inverse (refund, restock); non-invertible effects (email) go last or become reconciliation.

**Q3.** What is 'persist before you send'?
**Hint.** Journal the next step before invoking it — crash recovery re-drives from the journal.

**Q4.** Why must every step have an idempotency key?
**Hint.** Retries + unknown-outcome (timeout-but-succeeded) converge on the key; unique constraint arbitrates.

**Q5.** Orchestration or choreography for a 5-step branching flow?
**Hint.** Orchestration — inspectable, testable, compensation order explicit.

**Q6.** What is the transactional outbox?
**Hint.** Event rows written in the same local transaction; CDC/relay publishes — no dual-write loss.

**Q7.** How do you handle a step that never succeeds and never fails?
**Hint.** Per-step timeout + retry budget → compensate; global saga deadline fails closed.

**Q8.** What happens if a compensation itself fails?
**Hint.** Retry with backoff, then COMPENSATION_FAILED + dead-letter + reconciliation job.

**Q9.** What does 'atomicity without isolation' mean in a saga?
**Hint.** All-or-nothing via compensation, but intermediate states visible — business-level isolation patterns required.

**Q10.** How is a saga's state stored?
**Hint.** A durable journal (its own DB table/event log) — the source of truth for recovery.

### Scoring
- **8-10 correct**: ready for the saga loop.
- **5-7**: revise idempotency and recovery mechanics.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`SagaCoordinator`) and pass the crash-mid-step recovery tests.
**Day 3**: Quick-Fire rounds; draw the order-flow saga with compensations from memory.
**Day 4**: Rehearse the outbox, fan-out, and saga-isolation answers.
**Day 5**: Drill the extended rounds (idempotency mechanism, parallel steps, observability).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
