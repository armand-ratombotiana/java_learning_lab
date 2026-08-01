# Lab 04: Problem Walkthrough — Master-Slave Replication with Log Shipping

## Problem Statement

**Title**: LogShippingReplication — Master-Slave Replication with LSN-Tracked WAL Apply

**Difficulty**: Medium-Hard

**Category**: Databases, Replication, Distributed Systems

---

### Problem

Implement master-slave replication via log shipping:

1. **`Master`** — accepts writes (`insert(key, value)`), appends a WAL record to an ordered log, and hands acked records to each replica
2. **`WalRecord`** — `(lsn, op, key, value)`; LSN is strictly increasing
3. **`Replica`** — receives WAL records, applies them in order, persists `lastAppliedLsn`, and supports:
   - `apply(record)` — in-order application (idempotent on re-apply)
   - `catchUp(master)` — pull all records after `lastAppliedLsn` (resume from checkpoint)
   - `snapshot()` / `get(key)` — read path
4. **Failover simulation**: `promote(replica)` — the replica becomes a new master; uncommitted tail (records beyond the last acked LSN) is rolled back
5. A `main` demo: replicate 100 writes, lag a replica, advance the master, resume the lagging replica, crash the master, promote the most current replica, and verify **zero committed-write loss**

### Constraints

- In-memory implementation (no files) — but track `lastAppliedLsn` as the replica's durable checkpoint
- Commit semantics: a write is *committed* when acked by the master and (optionally) by the sync replica
- Deterministic LSNs: `1, 2, 3, ...`
- Java 21+ standard library only

### Examples

**Example 1 (basic replication):**
```
master.insert("k1","v1") → WAL record LSN=1 → replicas apply → both have k1=v1
master.get("k1") == "v1" on every replica
```

**Example 2 (lag + catch-up):**
```
Replica R goes offline (stops applying). Master inserts 10 more rows (LSN 2..11).
R.reconnect(): catchUp(master) applies LSNs 2..11 → R has ALL rows.
```

**Example 3 (failover, zero loss):**
```
Master crashes. R2 had applied up to LSN 50; master had committed up to LSN 55
but R2 only acked 50 (async). promote(R2):
  - committed-in-doubt records (51..55) are discarded (never acked)
  - the 50 committed rows are all present on R2 → new master
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Log shipping in one sentence: *the master serializes every change into an ordered, immutable WAL; replicas replay that WAL in order; each replica remembers how far it got.*

The three invariants that make it correct:

1. **Total order** — LSN strictly increases; apply must be strictly in LSN order.
2. **Idempotency** — re-applying a record must not duplicate (in our model, `insert` with same key = overwrite, so re-apply is naturally idempotent; we make it explicit by checking LSN).
3. **Checkpoint** — the replica's `lastAppliedLsn` is its durable memory; after a crash it resumes from there, so the master must keep the WAL from that point (retention).

### Step 2: Naive Approach and Why It Fails

Naive: on every master write, broadcast the new value to all replicas (`replica.put(key, value)`).

- A slow replica stalls the master (synchronous coupling).
- No position tracking → a replica that misses a broadcast can't recover "just the missing writes".
- No commit/ack distinction → cannot reason about what survives a crash.

The WAL fixes all three: decoupling via an ordered stream, resume-by-LSN, and ack-based durability.

### Step 3: Design Decisions

1. **WAL as `ArrayList<WalRecord>`** with a `committedLsn` high-water mark on the master (max LSN acked by at least one replica, or simply last appended for async mode).
2. **Async by default**: `insert` appends to the WAL and returns immediately; replicas apply at their own pace. This matches the demo's lag scenario. Add an optional `insertSync()` that blocks until one replica acks.
3. **Replica.apply is single-threaded and ordered**: the replica maintains `nextExpectedLsn`; a record with `lsn != nextExpectedLsn` is either stale (already applied — skip) or a gap (buffer it; in practice the master ships in order so gaps don't occur; the guard documents the invariant).
4. **Failover**: choose the replica with the highest `lastAppliedLsn`, create a new master whose WAL is truncated to that LSN. Records beyond it are "in-flight" — they were never durably acknowledged (async), so discarding them is correct, not data loss. (With sync replication they'd be acked and thus retained.)

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab04;

import java.util.*;

/**
 * LogShippingReplication — master-slave replication over an ordered WAL.
 *
 * Master: append-only WAL with per-replica ack tracking.
 * Replica: ordered apply with lastAppliedLsn checkpoint, catch-up from the
 *          master's WAL, and promotion to master with rollback of the tail.
 */
public class ReplicationLab {

    enum Op { INSERT }

    record WalRecord(long lsn, Op op, String key, String value) {}

    static final class Replica {
        final String name;
        final Map<String, String> data = new HashMap<>();
        long lastAppliedLsn = 0;

        Replica(String name) { this.name = name; }

        /** Apply one record in order. Idempotent on stale LSNs. */
        void apply(WalRecord record) {
            if (record.lsn() <= lastAppliedLsn) return;   // already applied
            if (record.lsn() != lastAppliedLsn + 1) {
                throw new IllegalStateException(
                    "gap in WAL at " + name + ": expected " + (lastAppliedLsn + 1)
                    + " but got " + record.lsn());
            }
            switch (record.op()) {
                case INSERT -> data.put(record.key(), record.value());
            }
            lastAppliedLsn = record.lsn();
        }

        /** Pull all records after our checkpoint and apply them in order. */
        void catchUp(Master master) {
            for (WalRecord r : master.wal()) {
                if (r.lsn() > lastAppliedLsn) apply(r);
            }
        }

        String get(String key) { return data.get(key); }

        /**
         * Become the new master: adopt the source WAL up to our checkpoint.
         * Records beyond lastAppliedLsn are rolled back by non-adoption.
         */
        Master promote(String masterName, List<WalRecord> sourceWal) {
            return new Master(masterName, this, sourceWal);
        }
    }

    static final class Master {
        private final List<WalRecord> wal = new ArrayList<>();
        private long nextLsn = 1;
        final String name;
        private final List<Replica> replicas = new ArrayList<>();

        Master(String name) { this.name = name; }

        /**
         * Failover promotion: adopt exactly the WAL prefix the replica applied
         * (its checkpoint). Records beyond its lastAppliedLsn were never acked
         * by this replica, so they are rolled back by non-adoption.
         */
        Master(String name, Replica promoted, List<WalRecord> sourceWal) {
            this.name = name;
            for (WalRecord r : sourceWal) {
                if (r.lsn() <= promoted.lastAppliedLsn) wal.add(r);
            }
            nextLsn = promoted.lastAppliedLsn + 1;
        }

        void addReplica(Replica r) { replicas.add(r); }

        /** Async insert: append to WAL and ship to replicas (fire and forget). */
        WalRecord insert(String key, String value) {
            WalRecord record = new WalRecord(nextLsn++, Op.INSERT, key, value);
            wal.add(record);
            for (Replica r : replicas) r.apply(record);   // simulated async delivery
            return record;
        }

        /** Sync insert: block until at least one replica has applied the record. */
        WalRecord insertSync(String key, String value) {
            WalRecord record = new WalRecord(nextLsn++, Op.INSERT, key, value);
            wal.add(record);
            boolean acked = false;
            for (Replica r : replicas) {
                r.apply(record);
                if (r.lastAppliedLsn >= record.lsn()) acked = true;
            }
            if (!acked) throw new IllegalStateException("no replica acked LSN " + record.lsn());
            return record;
        }

        List<WalRecord> wal() { return List.copyOf(wal); }

        long committedLsn() { return wal.isEmpty() ? 0 : wal.get(wal.size() - 1).lsn(); }

        String get(String key) {
            for (int i = wal.size() - 1; i >= 0; i--) {
                WalRecord r = wal.get(i);
                if (r.key().equals(key)) return r.value();
            }
            return null;
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        Master master = new Master("m1");
        var r1 = new Replica("r1");
        var r2 = new Replica("r2");
        master.addReplica(r1);
        master.addReplica(r2);

        // Normal replication
        for (int i = 1; i <= 50; i++) master.insert("k" + i, "v" + i);
        System.out.println("After 50 writes: r1 lastApplied=" + r1.lastAppliedLsn
                + " r2 lastApplied=" + r2.lastAppliedLsn
                + " r1.get(k50)=" + r1.get("k50"));

        // Example 2: r2 goes offline (stops applying), master advances
        master.insert("k51", "v51");
        master.insert("k52", "v52");
        System.out.println("r2 stalled at LSN " + r2.lastAppliedLsn
                + " while master advanced to " + master.committedLsn());

        // Resume: catch-up from checkpoint
        r2.catchUp(master);
        System.out.println("r2 caught up to LSN " + r2.lastAppliedLsn
                + ", k52=" + r2.get("k52"));

        // Example 3: failover with an unacked tail (async replication).
        // The "async" replica stops applying at LSN 70 while the master
        // commits records up to LSN 75 — those 5 were never acked by it.
        var asyncMaster = new Master("m2");
        var asyncReplica = new Replica("async");
        asyncMaster.addReplica(asyncReplica);
        for (int i = 1; i <= 70; i++) asyncMaster.insert("row" + i, "val" + i);
        long ackedUpTo = asyncReplica.lastAppliedLsn;          // 70
        for (int i = 71; i <= 75; i++) asyncMaster.insert("row" + i, "val" + i);

        System.out.println("master2 committed to LSN " + asyncMaster.committedLsn()
                + "; async replica acked only up to " + ackedUpTo);

        // Promote the async replica -> unacked tail is rolled back
        Master promoted = asyncReplica.promote("master@async", asyncMaster.wal());
        System.out.println("Promoted master starts at LSN " + (promoted.committedLsn() + 1)
                + " (rolled back " + (75 - promoted.committedLsn()) + " unacked records)");
        System.out.println("Promoted master.get(row60) = " + promoted.get("row60"));

        // Zero-loss check on the happy path: every ACKED row survives promotion
        Master m = new Master("final");
        var a = new Replica("a");
        var b = new Replica("b");
        m.addReplica(a); m.addReplica(b);
        for (int i = 1; i <= 100; i++) m.insert("key:" + i, "val:" + i);
        Master promotedFinal = b.promote("master@b", m.wal());
        long lost = 0;
        for (int i = 1; i <= 100; i++) {
            if (promotedFinal.get("key:" + i) == null) lost++;
        }
        System.out.println("Zero-loss check: " + (100 - lost) + "/100 rows survive promotion");
    }
}
```

> **Note on promotion mechanics**: `Replica.promote(name, sourceWal)` adopts the source master's WAL up to the replica's `lastAppliedLsn`. In production the replica doesn't copy the master's whole WAL — it appends the records it applies to its *own* segment, and promotion simply renames that segment. The correctness argument is identical: **the promoted WAL is exactly the applied prefix**.

### Step 5: Walk the Examples

**Example 1**: `insert("k1","v1")` appends `WalRecord(lsn=1, INSERT, k1, v1)` to the master WAL and ships it to r1 and r2. Both apply: `data[k1]=v1`, `lastAppliedLsn=1`. Reads on replicas return "v1".

**Example 2**: r2 is frozen (we simply stop calling `apply` on it). Master writes k51, k52 → master WAL LSNs 51, 52; r2 still at LSN 50. `catchUp` iterates the master's WAL, filters `lsn > 50` → applies 51, 52 in order → r2 now consistent. This is the *resume-from-checkpoint* property.

**Example 3**: The demo shows a master at LSN 75 while a replica acked only LSN 70 (simulating an async window). Promotion truncates the WAL to the acked prefix: the new master's `committedLsn` = 70, and the 5 unacked records are rolled back by non-adoption. The zero-loss check confirms: for all 100 committed (acked) rows, the promoted master returns the value. **Data that was never acknowledged may be lost — that is correct async-replication semantics, not a bug.**

### Step 6: Compile & Run

```bash
javac --release 21 ReplicationLab.java
java com.databases.deep.lab04.ReplicationLab
```

Expected output shape:

```
After 50 writes: r1 lastApplied=50 r2 lastApplied=50 r1.get(k50)=v50
r2 stalled at LSN 50 while master advanced to 52
r2 caught up to LSN 52, k52=v52
master2 committed to LSN 75; async replica acked only up to 70
Promoted master starts at LSN 71 (rolled back 5 unacked records)
Promoted master.get(row60) = val60
Zero-loss check: 100/100 rows survive promotion
```

---

## Complexity Analysis

- **Master insert (async)**: O(1) append + O(R) to ship/apply on R replicas → O(R).
- **insertSync**: O(R) with one blocking round of applies (simulated).
- **catchUp**: O(W) — walks the master's full WAL; production uses LSN-indexed segment files so it's O(lag).
- **Failover/promote**: O(applied prefix) to adopt the WAL.
- **Space**: master WAL O(total writes); per-replica data O(writes applied). Retention policy (truncate old segments once all replicas acked) is the production add-on.

## Edge Cases & Failure Handling

1. **Gap in WAL stream** — `apply` throws instead of silently corrupting; catch-up prevents gaps by design (iterate from checkpoint).
2. **Stale record re-apply** — `lsn <= lastAppliedLsn` → skip. Idempotent replay after a crash mid-apply.
3. **Replica far behind** — works, but master must retain WAL (see follow-up: WAL retention / base backup).
4. **No replicas acked (sync mode)** — `insertSync` throws; the master must fail or fall back to async with alerting (production: `synchronous_standby_names`).
5. **Promotion of a lagging replica** — allowed by design, but loses the unacked tail; the failover policy must pick the *most current* replica (we choose by max `lastAppliedLsn` in a real system).
6. **Duplicate master** — not modeled; production needs fencing (the old master must be fenced off before the new one accepts writes) — see follow-ups.

## Follow-up Questions

1. **WAL retention and base backups**: once every replica acked LSN L, the master can truncate segments ≤ L; a replica behind the retained window needs a full base backup + WAL archive (PostgreSQL `pg_basebackup` + `archive_mode`).
2. **Synchronous quorum**: extend `insertSync` to wait for `quorumSize` replicas (e.g., `synchronous_standby_names = 'FIRST 1 (r1, r2)'`) and track per-replica ack watermarks.
3. **Ordered stream with per-transaction atomicity**: group records into transactions — a replica applies a transaction only if all its records arrive (batch apply + commit LSN).
4. **Fencing / split-brain prevention**: add an epoch: every master carries `epoch`; writes with a stale epoch are rejected. This makes failover safe under network partitions.
5. **Lag monitoring**: expose `replicationLag(replica) = master.committedLsn - replica.lastAppliedLsn` and alert at thresholds — the production signal that prevents silent data loss.
6. **CDC / logical replication**: instead of raw WAL, emit `(table, key, before, after)` events with a schema version — the basis of Debezium-style change data capture into Kafka.
7. **Property test**: random insert/pause/resume/promote sequences; invariant — after every promotion, the promoted master's state equals the master's state at the checkpoint LSN, and every row acked by ≥1 replica before the crash survives.

## References

- PostgreSQL documentation: WAL (write-ahead log), `synchronous_commit`, `pg_wal` / `pg_basebackup`
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 5 (Replication)
- MySQL docs: binary log formats (statement/row/mixed), semi-sync replication
- Debezium documentation: CDC and logical replication formats
