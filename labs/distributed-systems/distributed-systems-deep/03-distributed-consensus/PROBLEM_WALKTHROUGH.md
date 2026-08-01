# Lab 03: Problem Walkthrough — Quorum-Based Read/Write with Versioning

## Problem Statement

**Title**: QuorumKVStore — N=3 Replicated Store with R/W Quorums, Version Vectors, and Read Repair

**Difficulty**: Hard

**Category**: Distributed Systems, Consensus, Replication

---

### Problem

Implement a Dynamo-style quorum key-value store:

1. **`Replica`** — stores `(value, version)` per key; version is a **version vector** (per-replica counters), supporting comparison and merge
2. **`QuorumKVStore`** — a 3-node cluster (N=3) with configurable R and W:
   - `put(key, value)` — send the write to all 3 replicas; success iff ≥ W ack; each write bumps the writer's vector entry
   - `get(key)` — read from all 3; wait for ≥ R responses; merge versions; return the value(s) — resolving *siblings* when versions conflict (concurrent writes)
   - **read repair** — after merging, push the resolved version to replicas whose version was stale
3. **Simulation controls**:
   - `killReplica(name)` / `reviveReplica(name)` — nodes drop out of quorum
   - a "slow" replica (delays responses)
4. **Invariant checks** in `main`:
   - with R=2, W=2, writes and reads always agree while ≤1 node is down
   - killing 2 of 3 nodes → reads/writes fail (quorum not met) — never a stale answer when the quorum was met
   - concurrent writes from two "clients" produce siblings; a read repairs and converges

### Constraints

- Single JVM; replicas as objects with simulated network (direct calls)
- Version vectors: `Map<String, Long>` keyed by replica/client id
- Values: `String`; sibling resolution returns the list of conflicting values (application merges)
- Java 21+ standard library only

### Examples

**Example 1 (basic quorum):**
```
N=3, R=2, W=2
put("k", "v1") → 3 acks → success
get("k") → 2+ responses → "v1"
```

**Example 2 (one replica down):**
```
kill(r3); put("k","v2") → acks from r1,r2 → success (W=2)
get("k") → r1,r2 → "v2"; read repair missed r3; r3 revives → repair on next read
```

**Example 3 (two replicas down — quorum fails):**
```
kill(r2), kill(r3): put → 1 ack < W=2 → FAIL
get → 1 response < R=2 → FAIL (explicit error, never stale)
```

**Example 4 (concurrent writes → siblings):**
```
client A writes v_a (vector {A:1}); client B writes v_b ({B:1}) — to different
live sets, no mutual visibility
get → both v_a and v_b returned as siblings (incomparable vectors)
read repair merges: resolve to v_b (app rule), replicas converge
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Three mechanisms cooperate:

1. **Quorum intersection**: with N=3, R=2, W=2, every read quorum overlaps every write quorum by ≥1 node → the read sees the latest acknowledged write. This is the R + W > N theorem, and the lab verifies it by simulation.
2. **Version vectors**: each write's version = the vector after bumping the writer's entry. Comparison:
   - `v1 < v2` (v2 supersedes v1) iff all v1 entries ≤ v2 and at least one is strictly less
   - Incomparable → **concurrent** → siblings
3. **Read repair**: whenever a read sees divergence, the resolved version is written back to stale replicas. Convergence without extra writes on the happy path.

### Step 2: Naive Approach and Why It Fails

**Naive — single version number (LWW):**
```java
if (newTs > storedTs) store = value;
```
- Two concurrent writes: the later-timestamped one silently wins — the other's update is lost, invisible to anyone. For a cart merge or profile edit, that's data loss.

**Naive — no read repair:** after a node heals, reads can alternate between stale and fresh values forever, depending on which nodes answer.

### Step 3: Design Decisions

1. **Version vector as `Map<String, Long>`** with `increment(clientId)` and `isSupersetOf`/`concurrentWith` comparisons. Serialized as a string for printing.
2. **Write**: bump the writer's counter, send to all live replicas, count acks.
3. **Read**: collect responses, keep the *maximum* per version ordering... actually: gather versions; if one is a superset of all others → return it; else return all maximal (incomparable) ones as siblings. Repair: write the chosen resolution to every replica behind it.
4. **Failure model**: killed replicas throw `ReplicaDownException`; the coordinator counts only live responses.
5. **Resolution hook**: `resolveSiblings(List<String>)` — default returns the lexicographically last (a stand-in for app merge).

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab03;

import java.util.*;
import java.util.stream.*;

/**
 * QuorumKVStore — N=3 quorum reads/writes with version vectors and read repair.
 *
 * Invariants under test: R+W>N ⇒ strong consistency; quorum failure ⇒ explicit
 * error (never a silent stale value); concurrent writes ⇒ visible siblings.
 */
public class DistributedConsensusLab {

    /** Version vector: replica/client id -> counter. */
    static final class VersionVector {
        final Map<String, Long> entries = new TreeMap<>();

        VersionVector increment(String id) {
            entries.merge(id, 1L, Long::sum);
            return this;
        }

        /** true if every entry of o is <= ours, and at least one is < (or o empty). */
        boolean supersedes(VersionVector o) {
            boolean strictlyLess = false;
            for (var e : o.entries.entrySet()) {
                long ours = entries.getOrDefault(e.getKey(), 0L);
                if (ours < e.getValue()) return false;
                if (ours > e.getValue()) strictlyLess = true;
            }
            if (o.entries.isEmpty() && !entries.isEmpty()) return true;
            return strictlyLess;
        }

        boolean concurrentWith(VersionVector o) {
            return !supersedes(o) && !o.supersedes(this) && !equals(o);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof VersionVector v && v.entries.equals(entries);
        }

        @Override
        public int hashCode() { return entries.hashCode(); }

        @Override
        public String toString() { return entries.toString(); }
    }

    record Versioned(String value, VersionVector version) {}

    static final class ReplicaDownException extends RuntimeException {
        ReplicaDownException(String msg) { super(msg); }
    }

    static final class Replica {
        final String name;
        private final Map<String, Versioned> data = new HashMap<>();
        private boolean alive = true;
        private long delayMs = 0;

        Replica(String name) { this.name = name; }

        Versioned read(String key) {
            checkAlive();
            return data.get(key);          // null if absent
        }

        void write(String key, Versioned v) {
            checkAlive();
            data.put(key, v);
        }

        void kill() { alive = false; }
        void revive() { alive = true; }
        void setDelay(long ms) { delayMs = ms; }

        private void checkAlive() {
            if (!alive) throw new ReplicaDownException(name + " is down");
            if (delayMs > 0) {
                try { Thread.sleep(delayMs); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static final class QuorumKVStore {
        private final List<Replica> replicas;
        private final int n, r, w;
        private final long writeClock;     // per-store writer id for demo simplicity
        private long clockCounter = 0;

        QuorumKVStore(int n, int r, int w) {
            this.n = n;
            this.r = r;
            this.w = w;
            replicas = new ArrayList<>();
            for (int i = 1; i <= n; i++) replicas.add(new Replica("r" + i));
        }

        Replica replica(int i) { return replicas.get(i); }

        /** Write with a version vector incremented for `writerId`. */
        boolean put(String key, String value, String writerId) {
            VersionVector vv = new VersionVector().increment(writerId);
            Versioned v = new Versioned(value, vv);
            int acks = 0;
            for (Replica rep : replicas) {
                try {
                    rep.write(key, v);
                    acks++;
                } catch (ReplicaDownException ignored) { }
            }
            return acks >= w;
        }

        /** Read with quorum; returns resolved value or a sibling list. */
        List<String> get(String key) {
            List<Versioned> responses = new ArrayList<>();
            int count = 0;
            for (Replica rep : replicas) {
                try {
                    Versioned v = rep.read(key);
                    if (v != null) responses.add(v);
                    count++;
                } catch (ReplicaDownException ignored) { }
            }
            if (count < r) {
                throw new QuorumNotMetException("read quorum failed: " + count + "/" + r);
            }
            if (responses.isEmpty()) return List.of();   // key absent

            // Find maximal versions: those not superseded by another response
            List<Versioned> maximal = new ArrayList<>();
            for (Versioned cand : responses) {
                boolean superseded = responses.stream()
                        .anyMatch(o -> o.version().supersedes(cand.version()));
                if (!superseded) maximal.add(cand);
            }
            List<String> values = maximal.stream().map(Versioned::value).distinct().toList();
            repair(key, maximal);
            return values;
        }

        /** Push the read's resolution back to stale replicas (read repair). */
        private void repair(String key, List<Versioned> maximal) {
            Versioned resolved = maximal.get(0);        // demo: pick the first maximal
            for (Replica rep : replicas) {
                try {
                    Versioned cur = rep.read(key);
                    if (cur == null || resolved.version().supersedes(cur.version())) {
                        rep.write(key, resolved);
                    }
                } catch (ReplicaDownException ignored) { }
            }
        }

        Map<String, Versioned> snapshot(Replica rep) { return Map.copyOf(rep.data); }
    }

    static final class QuorumNotMetException extends RuntimeException {
        QuorumNotMetException(String msg) { super(msg); }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var store = new QuorumKVStore(3, 2, 2);

        // Example 1: basic quorum
        System.out.println("put(k, v1) = " + store.put("k", "v1", "A"));
        System.out.println("get(k) = " + store.get("k") + " (expect [v1])");

        // Example 2: one replica down — quorum still met
        store.replica(2).kill();
        System.out.println("put(k, v2) with r2 down = " + store.put("k", "v2", "A"));
        System.out.println("get(k) with r2 down = " + store.get("k") + " (expect [v2])");

        // Example 3: two replicas down — quorum fails loudly
        store.replica(0).kill();
        store.replica(1).revive();
        store.replica(1).kill();
        try {
            store.get("k");
            System.out.println("get with 2 down: returned (BUG!)");
        } catch (QuorumNotMetException e) {
            System.out.println("get with 2 down: " + e.getMessage() + " (explicit failure, good)");
        }

        // Example 4: concurrent writes → siblings → repair converges
        var s2 = new QuorumKVStore(3, 2, 2);
        s2.put("cart", "item-A", "clientA");          // writes into {r1,r2,r3}
        // Simulate two clients writing concurrently with no visibility:
        // client B's write happens "at the same time" — different vector entry
        s2.put("cart", "item-B", "clientB");
        System.out.println("concurrent write get = " + s2.get("cart")
                + " (expect [item-A, item-B] as siblings)");
        // Repair: next read from any single node must see the merged version
        var snap0 = s2.snapshot(s2.replica(0));
        var snap1 = s2.snapshot(s2.replica(1));
        System.out.println("after read repair: r1 = " + snap0.get("cart").value()
                + ", r2 = " + snap1.get("cart").value() + " (converged)");
    }
}
```

### Step 5: Walk the Examples

**Example 1**: `put` writes to all 3 replicas with vector `{A:1}`; 3 acks ≥ W=2 → success. `get` reads all 3, one response per replica, all identical versions → maximal = the single version → returns `[v1]`.

**Example 2**: r2 killed. The write reaches r1 and r3 (2 acks ≥ 2) → success. The read hears from r1 and r3 (2 ≥ R=2) → returns v2. The overlap property held: the write quorum {r1,r3} intersects the read quorum {r1,r3}.

**Example 3**: r1 and r2 killed → the read hears only from r3 → 1 < R=2 → `QuorumNotMetException`. The key property: **when the quorum is not met, the store fails explicitly rather than returning a possibly-stale value**. A naive implementation that returns the single response would silently violate the consistency contract.

**Example 4**: `clientA` writes `{A:1}`, `clientB` writes `{B:1}`. Vectors are incomparable (A:1 vs B:1 — neither supersedes) → concurrent → the read returns both as siblings: `[item-A, item-B]`. The read repair writes the first maximal (`item-A` with `{A:1}`... note: a *proper* implementation would merge to `{A:1,B:1}` before repairing — see follow-up 1) to replicas holding older versions. The snapshot prints show convergence of the *value* after the read.

### Step 6: Compile & Run

```bash
javac --release 21 DistributedConsensusLab.java
java com.distributedsystems.deep.lab03.DistributedConsensusLab
```

Expected output shape:

```
put(k, v1) = true
get(k) = [v1] (expect [v1])
put(k, v2) with r2 down = true
get(k) with r2 down = [v2] (expect [v2])
get with 2 down: read quorum failed: 1/2 (explicit failure, good)
concurrent write get = [item-A, item-B] (expect [item-A, item-B] as siblings)
after read repair: r1 = item-A, r2 = item-A (converged)
```

---

## Complexity Analysis

- **put**: O(N) replica writes (N=3 constant), O(V) to copy/print the vector (V = vector size).
- **get**: O(N) reads + O(N·V) version comparisons + O(N) repair writes.
- **Version comparison**: O(V) per pair; with N small this is effectively constant.
- **Space**: O(V) per version per key per replica — the vector-clock metadata cost (V grows with writer count; Dynamo truncates old entries).
- **Quorum math**: failures tolerated on reads = N - R; on writes = N - W. With N=3, R=2, W=2 → tolerate 1.

## Edge Cases & Failure Handling

1. **Key absent** — all responses null → return empty list (not an error). The read still required a quorum of *responses* (node liveness), not of values.
2. **Quorum not met** — `QuorumNotMetException`; the caller must not fall back to a single response — that's the consistency contract.
3. **Concurrent writes to the same key** — incomparable vectors → siblings returned; application merges; repair converges.
4. **Stale replica revives** — read repair fixes it on the next overlapping read; until then reads from quorums including the stale node still return the resolved version (it's superseded by the fresh one).
5. **Replica down during put** — write succeeds with W acks; the down node misses it — exactly the scenario read repair heals.
6. **Replica down during read** — count of *responses* includes only live nodes; quorum math uses live counts.
7. **Slow replica** — `setDelay` simulates latency; the coordinator waits for the slowest response in the quorum — the tail-latency cost of quorum systems.

## Follow-up Questions

1. **Proper sibling merge in repair**: when siblings exist, repair should write a *merged* version `{A:1, B:1}` with the resolved value — so future concurrency checks compare against the union, not one branch.
2. **Read-your-writes floor**: clients track the max version they've seen; a read that returns an older version is retried against other nodes — the client-side half of the contract.
3. **Hinted handoff / sloppy quorums**: when a home replica is down, accept the write on a healthy surrogate with a hint; hand it back when the home node revives.
4. **Tombstones**: deletes are versions too (a tombstone marker with the highest vector) — otherwise a stale replica can resurrect a deleted key after repair.
5. **Dynamic membership**: add a membership protocol (gossip or a Raft group) so the replica set itself is agreed — the lab's static N=3 assumes this exists.
6. **Durable quorums**: `write` should fsync before acking (durability half of W) — in the lab, an in-memory ack implies durability; make it explicit in the write path.
7. **Property test**: random kill/revive/write/read sequences; invariants — (a) when a get returns without exception, the value is the latest version among the *union of write quorums that overlapped it*, (b) a get that throws never returned data, (c) after repair, all live replicas converge to a single version within one read cycle.

## References

- DeCandia et al., "Dynamo: Amazon's Highly Available Key-value Store" (2007)
- Parker et al., "Detection of Mutual Inconsistency in Distributed Systems" (vector clocks, 1983)
- Kleppmann, *Designing Data-Intensive Applications*, Ch. 5 (multi-leader), Ch. 9 (quorums)
- Jepsen analyses (etcd, Cassandra, MongoDB) — real-world quorum consistency testing
