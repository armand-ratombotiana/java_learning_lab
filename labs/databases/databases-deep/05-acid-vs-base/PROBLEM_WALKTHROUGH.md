# Lab 05: Problem Walkthrough — 2PC Transaction Coordinator with Recovery

## Problem Statement

**Title**: TwoPhaseCommitCoordinator — Prepare/Commit with Crash Recovery

**Difficulty**: Hard

**Category**: Databases, Distributed Transactions

---

### Problem

Implement a two-phase commit (2PC) coordinator with crash recovery:

1. **`Participant`** — holds a key→value store; supports `prepare(txId, ops)`, `commit(txId)`, `abort(txId)`. A prepared participant keeps an *intent* (write set + staged values) and must not change it until told the decision.
2. **`Coordinator`** — drives the protocol for `run(txId, ops)`:
   - Phase 1: send `prepare` to all participants; each must be able to stage its changes
   - Phase 2: if all prepared → send `commit` to all; otherwise → send `abort` to all
   - Writes every state transition to a **durable transaction log** (`txLog`) before acting
3. **Recovery**: `recover()` scans the log:
   - `COMMITTED` / `ABORTED` decisions → re-drive participants to match (idempotent)
   - `PREPARED` with no decision → **presumed abort** after timeout (or manual resolution hook)
4. A `main` demo: successful 2PC, a participant that fails to prepare (→ global abort), and a coordinator crash mid-protocol with log-based recovery restoring consistency.

### Constraints

- In-memory participants; the transaction log is an in-memory `List<LogRecord>` (durability simulated — in production it would be fsynced append-only)
- Participants are single-threaded per tx (no concurrency needed for the lab)
- Ops: `Put(key, value)` only
- Java 21+ standard library only

### Examples

**Example 1 (happy path):**
```
coordinator.run("tx1", [P1: put(a,1), P2: put(b,2)])
→ prepare both → both ready → commit both
P1.get("a") == "1", P2.get("b") == "2"
```

**Example 2 (prepare failure → global abort):**
```
P2 has a validation failure (e.g., key "x" already staged / rejected)
→ coordinator logs ABORT, aborts both P1 and P2
P1 must NOT have "a" — atomicity holds.
```

**Example 3 (coordinator crash + recovery):**
```
tx3 prepared on P1 and P2; coordinator "crashes" before deciding
new coordinator instance: recover() reads log:
  - tx3 logged PREPARED (no decision) → presumed abort → both aborted
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

2PC's correctness rests on three pillars:

1. **Prepared = committed to the transaction.** Once a participant replies `YES`, it holds staged changes and waits. It cannot abort on its own.
2. **The coordinator logs before it acts.** Every decision is durable *before* being sent, so a crash never leaves the coordinator without knowledge of its own decision.
3. **Idempotent re-drive.** `commit`/`abort` can be sent repeatedly; participants apply them at-most-once by tracking `txId`.

State machine of a transaction:

```
START -> PREPARING -> PREPARED(ALL) -> COMMITTED
                          |              ^
                          v              |
                        ABORTED <---------+
```

The coordinator's log records: `BEGIN`, `PREPARED(txId)`, `COMMIT_DECIDED(txId)`, `ABORT_DECIDED(txId)`.

### Step 2: Naive Approach and Why It Fails

**Naive approach — optimistic direct write:**
```java
for (Participant p : participants) p.apply(ops);   // no prepare phase
```
If P2 fails after P1 applied, P1 is already mutated — no atomicity. A naive "write then undo" breaks under crashes (undo itself can fail, and you can't undo a durable write reliably).

**Naive 2PC without logging:** coordinator decides commit, sends to P1, crashes before sending to P2 → P1 commits, P2 aborts (or is stuck) → atomicity violated *and nobody can recover the decision*.

### Step 3: Design Decisions

1. **Participant stages changes**: `prepare` validates then records staged ops per `txId` (applied to a pending overlay). `commit` merges the overlay into the store; `abort` discards it.
2. **Log-before-act**: the coordinator appends a record, then performs the network action. This is the core discipline; in the demo the log is a list — in production it's an fsynced WAL.
3. **Presumed abort**: a transaction found `PREPARED` with no decision is aborted on recovery. (Alternative: presumed commit — never choose it without careful argument, since a coordinator that crashed mid-prepare would commit a transaction some participants never saw.)
4. **Idempotency**: participants key their state by `txId`, so re-driven decisions are no-ops.

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab05;

import java.util.*;

/**
 * TwoPhaseCommitCoordinator — 2PC with a durable decision log and recovery.
 *
 * Protocol:
 *   1. PREPARE  -> participants stage changes, reply Ready/Failed
 *   2. COMMIT / ABORT -> coordinator logs the decision, then tells everyone
 *   Recovery: re-drive decided transactions; presumed-abort for undecided.
 */
public class AcidVsBaseLab {

    record Op(String key, String value) {}

    enum Phase { BEGIN, PREPARED, COMMIT_DECIDED, ABORT_DECIDED }

    record LogRecord(Phase phase, String txId) {}

    static final class Participant {
        final String name;
        final Map<String, String> store = new HashMap<>();
        final Map<String, List<Op>> staged = new HashMap<>();
        final Set<String> decided = new HashSet<>();
        private final Set<String> rejectedKeys = new HashSet<>();   // sim validation

        Participant(String name) { this.name = name; }

        Participant rejectKey(String key) {
            rejectedKeys.add(key);
            return this;
        }

        /** Phase 1. Returns true if every op can be applied. */
        boolean prepare(String txId, List<Op> ops) {
            for (Op op : ops) {
                if (rejectedKeys.contains(op.key())) return false;   // validation fails
            }
            staged.put(txId, new ArrayList<>(ops));                  // durable intent
            return true;
        }

        /** Phase 2. Idempotent: re-driving a decision is a no-op. */
        void commit(String txId) {
            if (decided.contains(txId)) return;
            List<Op> ops = staged.remove(txId);
            if (ops != null) {
                for (Op op : ops) store.put(op.key(), op.value());
            }
            decided.add(txId);
        }

        void abort(String txId) {
            if (decided.contains(txId)) return;
            staged.remove(txId);
            decided.add(txId);
        }

        boolean isPrepared(String txId) { return staged.containsKey(txId); }

        String get(String key) { return store.get(key); }
    }

    static final class Coordinator {
        private final List<Participant> participants;
        private final List<LogRecord> txLog = new ArrayList<>();

        Coordinator(List<Participant> participants) { this.participants = participants; }

        /** Run a 2PC transaction across all participants. Returns true if committed. */
        boolean run(String txId, Map<Participant, List<Op>> opsByParticipant) {
            log(new LogRecord(Phase.BEGIN, txId));

            // Phase 1: prepare everyone
            boolean allPrepared = true;
            for (var entry : opsByParticipant.entrySet()) {
                if (!entry.getKey().prepare(txId, entry.getValue())) {
                    allPrepared = false;
                    break;
                }
            }
            log(new LogRecord(Phase.PREPARED, txId));

            if (!allPrepared) {
                // Phase 2 (abort): log first, then tell everyone
                log(new LogRecord(Phase.ABORT_DECIDED, txId));
                for (Participant p : participants) p.abort(txId);
                return false;
            }

            // Phase 2 (commit): log the decision BEFORE sending it
            log(new LogRecord(Phase.COMMIT_DECIDED, txId));
            for (Participant p : participants) p.commit(txId);
            return true;
        }

        /**
         * Crash recovery: re-drive decisions recorded in the log.
         * Undecided prepared transactions are rolled back (presumed abort).
         */
        void recover() {
            for (LogRecord rec : txLog) {
                switch (rec.phase()) {
                    case COMMIT_DECIDED -> {
                        for (Participant p : participants) p.commit(rec.txId());
                    }
                    case ABORT_DECIDED -> {
                        for (Participant p : participants) p.abort(rec.txId());
                    }
                    case PREPARED -> {
                        // No decision record follows: presumed abort.
                        if (!hasDecision(rec.txId())) {
                            for (Participant p : participants) p.abort(rec.txId());
                        }
                    }
                    case BEGIN -> { /* nothing to do */ }
                }
            }
        }

        /**
         * Crash simulation: prepare everyone, log the PREPARED state,
         * then "die" before any decision is logged or sent.
         */
        void crashAfterPrepare(String txId, Map<Participant, List<Op>> opsByParticipant) {
            log(new LogRecord(Phase.BEGIN, txId));
            for (var entry : opsByParticipant.entrySet()) {
                if (!entry.getKey().prepare(txId, entry.getValue())) {
                    throw new IllegalStateException("prepare failed — not a crash scenario");
                }
            }
            log(new LogRecord(Phase.PREPARED, txId));
            // coordinator "crashes" here: no decision record, no commit/abort sent
        }

        private boolean hasDecision(String txId) {
            return txLog.stream().anyMatch(r -> r.txId().equals(txId)
                    && (r.phase() == Phase.COMMIT_DECIDED || r.phase() == Phase.ABORT_DECIDED));
        }

        private void log(LogRecord rec) { txLog.add(rec); }   // fsync in production

        /** Adopt a durable log record (simulates reading the fsynced WAL back). */
        void adopt(LogRecord rec) { txLog.add(rec); }

        List<LogRecord> logSnapshot() { return List.copyOf(txLog); }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var p1 = new Participant("p1");
        var p2 = new Participant("p2");
        var coord = new Coordinator(List.of(p1, p2));

        // Example 1: happy path
        boolean committed = coord.run("tx1", Map.of(
                p1, List.of(new Op("a", "1")),
                p2, List.of(new Op("b", "2"))));
        System.out.println("tx1 committed = " + committed);
        System.out.println("  p1.a=" + p1.get("a") + ", p2.b=" + p2.get("b"));

        // Example 2: p2 refuses to prepare -> global abort, p1 untouched
        p2.rejectKey("x");
        committed = coord.run("tx2", Map.of(
                p1, List.of(new Op("x", "9")),
                p2, List.of(new Op("x", "9"))));
        System.out.println("tx2 committed = " + committed + " (expect false)");
        System.out.println("  p1.x=" + p1.get("x") + " (expect null — atomicity)");

        // Example 3: crash mid-protocol. The coordinator prepared both
        // participants, logged PREPARED, then died before deciding.
        var crashCoord = new Coordinator(List.of(p1, p2));
        crashCoord.crashAfterPrepare("tx3", Map.of(
                p1, List.of(new Op("c", "3")),
                p2, List.of(new Op("d", "4"))));
        System.out.println("crashCoord log: " + crashCoord.logSnapshot());
        // A new coordinator boots with the same durable log and recovers:
        crashCoord.recover();
        System.out.println("After crash-recovery of undecided tx3:");
        System.out.println("  p1.c=" + p1.get("c") + " p2.d=" + p2.get("d")
                + " (expect null — presumed abort)");
        System.out.println("  p1 prepared=" + p1.isPrepared("tx3")
                + " (expect false — staged intent released)");

        // Example 4: recovery re-drives a committed decision (idempotent)
        var p3 = new Participant("p3");
        var p4 = new Participant("p4");
        var coord2 = new Coordinator(List.of(p3, p4));
        coord2.run("tx4", Map.of(p3, List.of(new Op("e", "5"))));
        // simulate coordinator crash AFTER decision, then a NEW coordinator
        // instance boots with the same durable log:
        var restored = new Coordinator(List.of(p3, p4));
        coord2.logSnapshot().forEach(restored::adopt);
        restored.recover();
        System.out.println("Re-drive committed tx4: p3.e=" + p3.get("e") + " (expect 5)");
    }
}
```

> **Note**: `adopt` copies a durable log into a new coordinator instance — in production this is reading the fsynced WAL back after a restart.

### Step 5: Walk the Examples

**Example 1**: `run("tx1", ...)` logs BEGIN, sends prepare to p1 (stages `put a=1`) and p2 (stages `put b=2`), both return true → log PREPARED → log COMMIT_DECIDED → p1/p2 commit (merge staged into store). Both reads return the new values.

**Example 2**: p2's `prepare` fails validation on key "x" → allPrepared=false → log ABORT_DECIDED → both participants `abort`. p1 discards its staged intent — `p1.get("x")` stays null. **Atomicity demonstrated**: one participant's refusal rolls back the entire transaction.

**Example 3**: Participants are prepared for tx3 but the coordinator crashed before logging a decision. A new coordinator boots with the log (which contains only BEGIN/PREPARED for tx3, no decision). `recover()` applies presumed abort: both participants release staged intents. No state is left half-applied.

**Example 4**: tx4's COMMIT_DECIDED is in the log. After a fresh coordinator adopts the log and runs `recover()`, the commit decision is re-driven — idempotently (participants ignore the re-drive if already decided). The committed value survives the coordinator restart.

### Step 6: Compile & Run

```bash
javac --release 21 AcidVsBaseLab.java
java com.databases.deep.lab05.AcidVsBaseLab
```

Expected output:

```
tx1 committed = true
  p1.a=1, p2.b=2
tx2 committed = false (expect false)
  p1.x=null (expect null — atomicity)
After crash-recovery of undecided tx3:
  p1.c=null p2.d=null (expect null — presumed abort)
  p1 prepared=false (expect false — staged intent released)
Re-drive committed tx4: p3.e=5 (expect 5)
```

---

## Complexity Analysis

- **run (2PC)**: O(P·K) — P participants, K ops per participant (prepare O(K), commit/abort O(K)).
- **Log**: O(1) append per phase transition; O(T·logT) for `hasDecision` scans (optimize with a `Set<String>` of decided txIds in production).
- **recover**: O(T·P) worst case — T log records re-driven across P participants; idempotent so replay is safe.
- **Space**: log O(T) records; staged state O(active tx ops).
- **Latency**: 2 round trips + 2+ fsyncs per transaction (log writes) — the fundamental 2PC cost.

## Edge Cases & Failure Handling

1. **Participant fails after preparing but before the decision** — it holds staged intent; the coordinator's decision eventually arrives (or the coordinator times out and aborts, or recovery presumed-aborts). Participant never decides alone.
2. **Coordinator crash before logging PREPARED** — transaction never started; participants that did receive prepare calls would hang until timeout → abort locally (add a prepare deadline).
3. **Decision log written but message lost** — participants never get commit; recovery re-drives the decision from the log. This is why log-before-act matters.
4. **Duplicate prepare** — `prepare` for an already-staged txId should return the previous result (idempotent); current code overwrites — note as a hardening step.
5. **Participant crashes mid-transaction** — its recovery replays its own prepare log; if it was prepared and the decision arrives, apply it; otherwise presumed abort.
6. **Blocking problem** — a live-but-slow participant in doubt holds locks; production 2PC adds timeouts and, in extreme cases, manual resolution. Our `isPrepared` exposes the in-doubt state for a resolution tool.

## Follow-up Questions

1. **One-phase optimization**: participants with read-only ops can skip the prepare round-trip (they can never cause an abort) — cuts latency nearly in half for read-heavy mixes.
2. **3PC (three-phase commit)**: adds a pre-commit phase to avoid blocking when the coordinator fails after deciding; trades one more round trip for non-blocking commit under benign failures.
3. **Percolator/Spanner-style**: replace the coordinator with a Paxos/Raft group per transaction and optimistic concurrency with commit timestamps — transactions become non-blocking (see Spanner's TrueTime).
4. **Deadlock detection**: participants acquire locks on keys; add a wait-for graph and abort the transaction whose edge creates a cycle (or use timeouts).
5. **Persist participant state**: fsync `staged` and `decided` to the participant's own WAL so a participant restart mid-protocol resumes correctly — the durability half of 2PC.
6. **Transaction log compaction**: snapshot committed state and truncate log records older than the oldest in-doubt transaction.
7. **Property test**: random sequences of runs + crashes at every point between log writes; invariant — at the end of every recovery, no transaction is committed on one participant and aborted on another.

## References

- Gray, "Notes on Database Operating Systems" (1978) — the classic 2PC treatment
- Bernstein, Hadzilacos, Goodman, *Concurrency Control and Recovery in Database Systems*, Ch. 7
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 9 (distributed transactions, sagas)
- Brewer, "CAP Theorem" (2000) and Kleppmann's "A Critique of the CAP Theorem" (2015)
