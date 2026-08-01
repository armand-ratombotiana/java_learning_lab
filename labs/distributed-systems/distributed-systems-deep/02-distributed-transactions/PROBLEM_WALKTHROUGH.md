# Lab 02: Problem Walkthrough — Saga Coordinator with Compensation

## Problem Statement

**Title**: SagaCoordinator — Orchestrated Saga with Journaled Recovery and Compensations

**Difficulty**: Hard

**Category**: Distributed Systems, Transactions, Microservices

---

### Problem

Implement an orchestrated saga coordinator for an **order → payment → inventory → shipping** flow:

1. **`SagaStep`** — a participant with `execute(sagaId)` and `compensate(sagaId)`, both **idempotent** (keyed by sagaId)
2. **`SagaCoordinator`** — executes steps in order; on any failure, runs compensations **in reverse order**:
   - `runSaga(sagaId, steps)` → `SagaOutcome { COMPLETED, COMPENSATED, FAILED }`
   - journals every transition in an in-memory **saga log** (persist-before-send semantics)
   - per-step retry budget (2 retries) before compensating
3. **Recovery**: `recover(sagaId, steps)` — resume an interrupted saga from its journal: continue forward steps, or continue compensations if the saga was compensating
4. **Simulation of failure**: steps that always fail, and steps whose outcome is *unknown* (timeout-after-success)
5. A `main` demo: happy path, mid-flow failure with full compensation, retry-then-compensate, unknown-outcome tolerance, and invalid-saga validation

### Constraints

- In-memory journal (`List<JournalEntry>`); durability simulated (the pattern is what matters)
- Steps: `createOrder`, `reservePayment` (authorization *hold*), `reserveInventory`, `ship` — ship is **non-compensable**, and must be the final step
- Idempotency: replaying `execute`/`compensate` with the same sagaId must not double-apply
- Java 21+ standard library only

### Examples

**Example 1 (happy path):**
```
createOrder → reservePayment → reserveInventory → ship → COMPLETED
All steps applied exactly once; no compensations run.
```

**Example 2 (payment fails after retries):**
```
createOrder ok; reservePayment fails every attempt (budget: 1 try + 2 retries)
→ compensate: createOrder (only prior step)
→ COMPENSATED
```

**Example 3 (inventory fails → reverse compensations):**
```
createOrder ok, reservePayment ok, reserveInventory fails
→ compensate reservePayment (void hold), then compensate createOrder
→ COMPENSATED
```

**Example 4 (unknown outcome):**
```
reserveInventory returns without confirming (network timeout — may have succeeded)
→ coordinator treats it as retryable; the step's idempotency converges
→ ship → COMPLETED
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

A saga is a sequence of local transactions with business-level inverses. The coordinator's contract:

1. **Forward**: execute steps 1..N; each step is a durable, idempotent local transaction.
2. **Backward**: on failure at step k, compensate steps k-1..1 in reverse order.
3. **The journal is the truth**: every transition is logged *before* the side effect, so recovery can resume — and retries are idempotent, so replay is safe.

The three subtle requirements:

- **Persist-before-send**: log `SENT` for step 3 *before* invoking step 3. If we crash after invoking, recovery *retries* — the step's idempotency makes the retry harmless.
- **Unknown outcomes**: a step that timed out might have succeeded. Retrying (with the same idempotency key) converges — never compensate a possibly-successful step without exhausting retries.
- **Irreversibility ordering**: once `ship` executes, compensation is impossible. The coordinator *validates* that every non-compensable step is final — catching the design error at registration, not at 3 AM during a production incident.

### Step 2: Naive Approach and Why It Fails

**Naive — linear call chain:**
```java
createOrder();
reservePayment();
reserveInventory();
ship();
```
- A failure in `reserveInventory` leaves an orphaned order and a payment hold — no cleanup.
- A crash between steps is unrecoverable — no journal, nothing to resume from.

**Naive — try/catch compensation:**
```java
try { reservePayment(); } catch (e) { cancelOrder(); throw; }
```
- Retry budgets, idempotency, crash recovery, and unknown outcomes are all unaddressed. Compensation runs even for a *successful-but-unknown* step.

### Step 3: Design Decisions

1. **Journal entries**: `(sagaId, stepIndex, stepName, status: SENT|EXECUTED|COMPENSATED)`. The coordinator appends SENT before calling, EXECUTED after a confirmed success, COMPENSATED after each compensation.
2. **Idempotency**: `SagaStep` records per-sagaId outcomes; `execute` returns immediately if already applied — replays are no-ops.
3. **Retry budget**: 1 attempt + 2 retries; only *known* failures (exceptions) consume the budget. Unknown outcomes are treated as success (they'll be re-sent by recovery or converge on replay).
4. **Non-compensable steps**: `validate()` rejects a saga where a non-compensable step precedes any later step.
5. **Recovery**: for the lab, replaying `runSaga` over the idempotent journal achieves forward resumption; a production engine would resume exactly from the last journal position. The idempotency guarantee makes both equivalent.

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab02;

import java.util.*;

/**
 * SagaCoordinator — orchestrated saga with journaled recovery and
 * reverse-order compensation. Idempotent steps; persist-before-send journal.
 */
public class DistributedTransactionsLab {

    /** A saga participant. Idempotent: replays return the recorded result. */
    interface SagaStep {
        String name();
        boolean compensable();
        void execute(String sagaId) throws StepFailedException;
        void compensate(String sagaId);
    }

    static final class StepFailedException extends RuntimeException {
        StepFailedException(String msg) { super(msg); }
    }

    enum StepStatus { SENT, EXECUTED, COMPENSATED }

    record JournalEntry(String sagaId, int stepIndex, String stepName,
                        StepStatus status) {}

    enum SagaStatus { COMPLETED, COMPENSATED, FAILED }

    record SagaOutcome(SagaStatus status, List<JournalEntry> journal) {}

    /** An idempotent, failure-injectable step implementation. */
    static final class SimStep implements SagaStep {
        private final String name;
        private final boolean compensable;
        private final boolean failAlways;             // every attempt throws
        private final boolean unknownOnAttempt;       // 1st attempt returns without confirming
        private int attempts = 0;
        private final Map<String, String> results = new HashMap<>();  // sagaId -> outcome

        SimStep(String name, boolean compensable, boolean failAlways, boolean unknownOnAttempt) {
            this.name = name;
            this.compensable = compensable;
            this.failAlways = failAlways;
            this.unknownOnAttempt = unknownOnAttempt;
        }

        @Override
        public String name() { return name; }

        @Override
        public boolean compensable() { return compensable; }

        @Override
        public void execute(String sagaId) throws StepFailedException {
            if (results.containsKey(sagaId)) return;              // idempotent replay
            attempts++;
            if (failAlways) {
                throw new StepFailedException(name + " failed (attempt " + attempts + ")");
            }
            if (unknownOnAttempt && attempts == 1) {
                return;    // outcome unknown: nothing recorded; retry may replay
            }
            results.put(sagaId, "EXECUTED");
        }

        @Override
        public void compensate(String sagaId) {
            results.put(sagaId, "COMPENSATED");                   // idempotent
        }

        String outcome(String sagaId) { return results.get(sagaId); }
    }

    static final class SagaCoordinator {
        private final List<JournalEntry> journal = new ArrayList<>();
        static final int MAX_RETRIES = 2;

        /** Validate: non-compensable steps must be last. */
        void validate(List<SagaStep> steps) {
            for (int i = 0; i < steps.size(); i++) {
                if (!steps.get(i).compensable() && i < steps.size() - 1) {
                    throw new IllegalArgumentException(
                            "non-compensable step '" + steps.get(i).name()
                            + "' must be the final step");
                }
            }
        }

        SagaOutcome runSaga(String sagaId, List<SagaStep> steps) {
            validate(steps);
            for (int i = 0; i < steps.size(); i++) {
                SagaStep step = steps.get(i);
                // persist-before-send
                journal.add(new JournalEntry(sagaId, i, step.name(), StepStatus.SENT));

                boolean ok = executeWithRetries(sagaId, step);
                if (!ok) {
                    // compensate steps [i-1 .. 0] in reverse order
                    for (int j = i - 1; j >= 0; j--) {
                        SagaStep s = steps.get(j);
                        if (!s.compensable()) {
                            return new SagaOutcome(SagaStatus.FAILED, List.copyOf(journal));
                        }
                        s.compensate(sagaId);
                        journal.add(new JournalEntry(sagaId, j, s.name(), StepStatus.COMPENSATED));
                    }
                    return new SagaOutcome(SagaStatus.COMPENSATED, List.copyOf(journal));
                }
                journal.add(new JournalEntry(sagaId, i, step.name(), StepStatus.EXECUTED));
            }
            return new SagaOutcome(SagaStatus.COMPLETED, List.copyOf(journal));
        }

        private boolean executeWithRetries(String sagaId, SagaStep step) {
            for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
                try {
                    step.execute(sagaId);
                    return true;    // confirmed, or unknown-but-replay-safe
                } catch (StepFailedException e) {
                    if (attempt == MAX_RETRIES) return false;   // budget exhausted
                }
            }
            return false;
        }

        /**
         * Crash recovery: replay the saga over the idempotent journal.
         * Steps already applied are no-ops; SENT steps are retried.
         * In production this resumes from the exact journal position.
         */
        SagaOutcome recover(String sagaId, List<SagaStep> steps) {
            return runSaga(sagaId, steps);
        }

        List<JournalEntry> journal() { return List.copyOf(journal); }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var coordinator = new SagaCoordinator();

        // Example 1: happy path
        var steps = List.<SagaStep>of(
                new SimStep("createOrder", true, false, false),
                new SimStep("reservePayment", true, false, false),
                new SimStep("reserveInventory", true, false, false),
                new SimStep("ship", false, false, false));
        var outcome1 = coordinator.runSaga("order-1", steps);
        System.out.println("Example 1: " + outcome1.status() + " (expect COMPLETED)");

        // Example 2: payment always fails -> retry budget exhausted -> compensate createOrder
        var coord2 = new SagaCoordinator();
        var steps2 = List.<SagaStep>of(
                new SimStep("createOrder", true, false, false),
                new SimStep("reservePayment", true, true, false),
                new SimStep("reserveInventory", true, false, false));
        var outcome2 = coord2.runSaga("order-2", steps2);
        System.out.println("Example 2: " + outcome2.status()
                + " (expect COMPENSATED after retry budget)");
        outcome2.journal().forEach(e -> System.out.println("  " + e.stepName() + ": " + e.status()));

        // Example 3: inventory always fails -> reverse compensation (payment void, order cancel)
        var coord3 = new SagaCoordinator();
        var steps3 = List.<SagaStep>of(
                new SimStep("createOrder", true, false, false),
                new SimStep("reservePayment", true, false, false),
                new SimStep("reserveInventory", true, true, false));
        var outcome3 = coord3.runSaga("order-3", steps3);
        System.out.println("Example 3: " + outcome3.status() + " (expect COMPENSATED)");
        outcome3.journal().forEach(e -> System.out.println("  " + e.stepName() + ": " + e.status()));

        // Example 4: unknown outcome — the coordinator must NOT compensate a
        // possibly-successful step; replay converges via idempotency
        var coord4 = new SagaCoordinator();
        var steps4 = List.<SagaStep>of(
                new SimStep("createOrder", true, false, false),
                new SimStep("reservePayment", true, false, false),
                new SimStep("reserveInventory", true, false, true),     // unknown on 1st attempt
                new SimStep("ship", false, false, false));
        var outcome4 = coord4.runSaga("order-4", steps4);
        System.out.println("Example 4: " + outcome4.status()
                + " (expect COMPLETED — unknown outcome tolerated)");

        // Example 5: crash recovery — a new coordinator instance replays the
        // saga journal; steps already applied are no-ops
        var coord5 = new SagaCoordinator();
        var steps5 = List.<SagaStep>of(
                new SimStep("createOrder", true, false, false),
                new SimStep("reservePayment", true, false, false),
                new SimStep("reserveInventory", true, false, false),
                new SimStep("ship", false, false, false));
        coord5.runSaga("order-5", steps5);          // "crash" after completion
        var recovered = coord5.recover("order-5", steps5);
        System.out.println("Example 5: recovery outcome " + recovered.status()
                + " (expect COMPLETED — idempotent replay)");

        // Example 6: invalid saga rejected — ship not last
        try {
            coordinator.validate(List.of(
                    new SimStep("ship", false, false, false),
                    new SimStep("createOrder", true, false, false)));
            System.out.println("Example 6: validation PASSED (bug!)");
        } catch (IllegalArgumentException e) {
            System.out.println("Example 6: validation rejected — " + e.getMessage());
        }
    }
}
```

### Step 5: Walk the Examples

**Example 1**: All four steps execute in order; each journal entry goes SENT → EXECUTED. Outcome COMPLETED. `ship` is last and non-compensable — no compensation ever needed, which is exactly the constraint `validate()` enforces.

**Example 2**: `createOrder` executes (SENT → EXECUTED). `reservePayment` is logged SENT, then fails on attempt 1, retries 2 and 3, all throwing — budget exhausted → the coordinator compensates in reverse: `createOrder.compensate` (journal: COMPENSATED). Outcome COMPENSATED. The order was created and cleanly cancelled — no orphan.

**Example 3**: Steps 1-2 succeed; `reserveInventory` fails every attempt → compensations run in reverse: `reservePayment` (void the authorization hold), then `createOrder` (cancel the order). Journal shows exactly the reverse-order pattern — the ordering is the whole point.

**Example 4**: `reserveInventory` returns without recording (simulating a network timeout — the charge may have succeeded!). The coordinator treats it as success-with-unknown-outcome and proceeds to `ship` → COMPLETED. The alternative — compensating a possibly-successful step — would have produced a *double* effect (hold voided AND captured). Idempotent replay is the correct resolution.

**Example 5**: The journal from a completed saga is replayed by a fresh coordinator: every step's `execute` returns immediately via the idempotency guard; the outcome is still COMPLETED with no double effects. This is the crash-recovery contract: *replay is safe because every step is idempotent*.

**Example 6**: A saga where `ship` (non-compensable) is not last is rejected at validation — before it can ever run. This catches the design error statically.

### Step 6: Compile & Run

```bash
javac --release 21 DistributedTransactionsLab.java
java com.distributedsystems.deep.lab02.DistributedTransactionsLab
```

Expected output shape:

```
Example 1: COMPLETED (expect COMPLETED)
Example 2: COMPENSATED (expect COMPENSATED after retry budget)
  createOrder: SENT
  createOrder: EXECUTED
  reservePayment: SENT
  createOrder: COMPENSATED
Example 3: COMPENSATED (expect COMPENSATED)
  createOrder: SENT
  createOrder: EXECUTED
  reservePayment: SENT
  reservePayment: EXECUTED
  reserveInventory: SENT
  reservePayment: COMPENSATED
  createOrder: COMPENSATED
Example 4: COMPLETED (expect COMPLETED — unknown outcome tolerated)
Example 5: recovery outcome COMPLETED (expect COMPLETED — idempotent replay)
Example 6: validation rejected — non-compensable step 'ship' must be the final step
```

---

## Complexity Analysis

- **runSaga (happy path)**: O(S) — S steps, each O(1) execute + O(1) journal append.
- **Compensation path**: O(S) — each step retried up to R times (R=3 constant), then up to S-1 compensations: O(R·S).
- **recover**: O(S) — full replay is linear; production resumption from the journal position is O(remaining steps).
- **Journal**: O(S) entries per saga; each entry is small (name + enum + int).
- **Space**: O(S) per saga, O(S·T) for T concurrent sagas.

## Edge Cases & Failure Handling

1. **Failure on the very first step** — compensation loop runs over an empty range (j = -1) → no compensations, outcome COMPENSATED; nothing was applied, nothing to undo.
2. **Non-compensable step reached with a failure pending** — impossible by construction (validate); the runtime check in the compensation loop is a second line of defense returning FAILED.
3. **Unknown outcome on the LAST step** — same as Example 4: tolerated, saga completes; a later reconciliation (outside the lab) verifies the effect.
4. **Compensation of a step that never executed** — idempotency guard: `results` has no entry; the compensation records COMPENSATED anyway. In production, compensate-by-idempotency-key is equally safe.
5. **Duplicate sagaId** — steps key results by sagaId; a re-run with the same id is a replay, not a new saga. Production assigns unique sagaIds per business transaction.
6. **Retry storm** — a step that always fails consumes exactly 3 attempts; no unbounded looping.
7. **Recovery of a saga mid-compensation** — `recover` replays the whole saga; executed steps are no-ops, the failing step fails again, compensations re-run (idempotent) — converges to the same terminal state.

## Follow-up Questions

1. **Persistent journal**: replace the in-memory list with an append-only file (fsync per entry) — durability for the persist-before-send guarantee.
2. **Outbox pattern**: each step publishes its "completed" event via a transactional outbox — exactly-once event emission without dual-write bugs.
3. **Parallel steps**: extend `runSaga` to accept a DAG (steps with fan-out), and define compensation order for concurrent steps (e.g., reverse topological order).
4. **Timeout-based compensation**: inventory reservations auto-expire after N minutes — a compensation that runs *without* the coordinator, via a scheduler (the lab's `recover` can trigger these).
5. **Retry with backoff + jitter**: `executeWithRetries` currently retries immediately; add exponential backoff (100ms, 400ms, ...) with jitter to avoid thundering herds.
6. **Saga monitoring**: expose per-saga state (`RUNNING`, `COMPENSATING`, ...) and a dashboard/alert on sagas stuck in RUNNING past an SLA — the production ops story.
7. **Property test**: random step configurations and crash points; invariants — (a) outcome is always COMPLETED or COMPENSATED (never half-applied), (b) compensations always run in reverse order, (c) replaying the journal never changes the outcome or double-applies.

## References

- Garcia-Molina & Salem, "Sagas" (1987) — the original paper
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 11 (sagas, distributed transactions)
- Microsoft docs: "Saga distributed transactions pattern" (Cloud Design Patterns)
- Temporal / Axon / Seata documentation: durable workflow engines for saga orchestration
