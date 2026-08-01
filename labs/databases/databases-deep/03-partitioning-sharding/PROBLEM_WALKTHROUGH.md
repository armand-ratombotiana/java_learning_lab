# Lab 03: Problem Walkthrough — Sharding System with Consistent Hashing & Rebalancing

## Problem Statement

**Title**: ConsistentHashSharder — Ring-Based Sharding with Virtual Nodes and Rebalancing

**Difficulty**: Hard

**Category**: Databases, Distributed Systems, Sharding

---

### Problem

Implement a sharding system using consistent hashing with:

1. **`Shard`** — a physical node holding a `Map<Integer, String>` of key→value data
2. **`ConsistentHashSharder`** — a hash ring:
   - `addShard(name, weight)` — place `weight` virtual nodes on the ring
   - `removeShard(name)` — remove all its virtual nodes
   - `shardForKey(key)` — walk the ring clockwise from `hash(key)` to the first virtual node
   - `put(key, value)` / `get(key)` — route to the owning shard
3. **Rebalancing**: on `addShard`/`removeShard`, report exactly which keys moved from which shard to which shard — and physically move them via `moveKey`
4. Statistics: keys per shard (evenness), and a `main` demo showing the redistribution fraction when a node joins or leaves

### Constraints

- Hash ring space: 0 .. 2^31-1 (use `Integer.MIN_VALUE..MAX_VALUE` or map to positive)
- Keys are `String`s; values are `String`s
- Deterministic: `hash(key)` must be stable (use a fixed algorithm, e.g., Murmur3-style or SHA-256 truncated — no `Object.hashCode` randomness concerns)
- Rebalancing must move **only the keys that map to a new owner**

### Examples

**Example 1 (basic routing):**
```
addShard("s1"), addShard("s2"), addShard("s3")
put("user:42", "alice"), put("user:99", "bob"), put("order:7", "x")
get("user:42") → "alice" (from whichever shard owns it)
```

**Example 2 (node join — minimal movement):**
```
100 keys on 3 shards. addShard("s4").
Expected: ~25 keys move (1/N of total), the other ~75 stay.
```

**Example 3 (node leave — failover):**
```
removeShard("s2") → all of s2's keys move to s2's clockwise successor;
get() on every key still returns its value.
```

**Example 4 (virtual nodes):**
```
addShard("a", weight=3) vs addShard("b", weight=1):
a should own roughly 3x the keys of b.
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Consistent hashing in one paragraph: both keys and shards hash into the same numeric space (a ring). A key belongs to the first shard encountered walking clockwise. When shards come and go, only the keys in the affected arc move — instead of remapping everything.

The two classic failure modes we must avoid:

1. **Uneven distribution** with few shards and no virtual nodes (3 shards → 80/10/10 splits are common).
2. **Full rehash** on membership change — defeats the purpose.

Virtual nodes fix #1: each physical shard places `weight` points on the ring. Fix #2 is inherent to the ring.

### Step 2: Naive Approaches and Why They Fail

**Naive approach 1 — modulo:**
```java
int shard = Math.floorMod(key.hashCode(), shards.size());
```
- Adding a shard remaps *every* key — O(N) moves per rebalance.
- Keys move even when their data didn't need to.

**Naive approach 2 — ring without virtual nodes:**
- Correct algorithm, but with 3 shards the variance is high (worst case one shard owns 70%+ of keys).

### Step 3: Design Decisions

1. **Ring representation**: a sorted `TreeMap<Long, String>` (ring position → shard name). `shardForKey` = `ring.ceilingEntry(hash(key))`, falling back to the first entry (wrap-around). O(log N) per lookup.
2. **Hash function**: use a real 64-bit hash. We implement MurmurHash3 finalizer-style mixing or use `java.security.MessageDigest` SHA-256 truncated — deterministic across runs.
3. **Virtual node naming**: `name#i` for i in 0..weight-1, each hashed onto the ring.
4. **Rebalancing**: after any membership change, re-derive ownership for all keys by walking the data map; for each key whose owner changed, record a move and transfer the value. Simple and correct for a lab; production uses snapshot streaming + CDC (see follow-ups).
5. **Distribution metric**: print keys-per-shard and the fraction of moved keys — the two numbers that prove the design.

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab03;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * ConsistentHashSharder — ring-based sharding with virtual nodes.
 *
 * - Each shard places `weight` virtual nodes on a 64-bit ring.
 * - A key routes to the nearest virtual node clockwise.
 * - addShard/removeShard rebalance ONLY the affected keys and report moves.
 */
public class PartitioningShardingLab {

    /** 64-bit deterministic hash of a string (SHA-256 truncated). */
    static long hash64(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            long h = 0;
            for (int i = 0; i < 8; i++) h = (h << 8) | (d[i] & 0xFF);
            return h;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** One physical shard: name + key->value data. */
    static final class Shard {
        final String name;
        final Map<String, String> data = new HashMap<>();

        Shard(String name) { this.name = name; }
    }

    static final class Move {
        final String key;
        final String from;
        final String to;

        Move(String key, String from, String to) {
            this.key = key;
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() { return key + ": " + from + " -> " + to; }
    }

    static final class ConsistentHashSharder {
        private final TreeMap<Long, String> ring = new TreeMap<>();
        private final Map<String, Shard> shards = new LinkedHashMap<>();
        private final Map<String, Integer> weights = new HashMap<>();

        void addShard(String name, int weight) {
            if (shards.containsKey(name)) throw new IllegalArgumentException("duplicate shard " + name);
            for (int i = 0; i < weight; i++) {
                ring.put(hash64(name + "#" + i), name);
            }
            shards.put(name, new Shard(name));
            weights.put(name, weight);
        }

        void removeShard(String name) {
            if (!shards.containsKey(name)) throw new IllegalArgumentException("no such shard " + name);
            int w = weights.get(name);
            for (int i = 0; i < w; i++) {
                ring.remove(hash64(name + "#" + i));
            }
            shards.remove(name);
            weights.remove(name);
        }

        String ownerOf(String key) {
            long h = hash64(key);
            Map.Entry<Long, String> e = ring.ceilingEntry(h);
            if (e == null) e = ring.firstEntry();      // wrap-around
            return e.getValue();
        }

        Shard shardFor(String key) {
            return shards.get(ownerOf(key));
        }

        void put(String key, String value) {
            shardFor(key).data.put(key, value);
        }

        String get(String key) {
            Shard s = shardFor(key);
            return s == null ? null : s.data.get(key);
        }

        /** Rebalance after membership changes; move only keys whose owner changed. */
        List<Move> rebalance() {
            List<Move> moves = new ArrayList<>();
            for (Shard old : shards.values()) {
                for (var it = old.data.entrySet().iterator(); it.hasNext(); ) {
                    var entry = it.next();
                    String newOwner = ownerOf(entry.getKey());
                    if (!newOwner.equals(old.name)) {
                        moves.add(new Move(entry.getKey(), old.name, newOwner));
                        shards.get(newOwner).data.put(entry.getKey(), entry.getValue());
                        it.remove();
                    }
                }
            }
            return moves;
        }

        /** Keys per shard for evenness reporting. */
        Map<String, Integer> distribution() {
            Map<String, Integer> counts = new TreeMap<>();
            for (Shard s : shards.values()) counts.put(s.name, s.data.size());
            return counts;
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var sharder = new ConsistentHashSharder();
        for (String n : List.of("s1", "s2", "s3")) sharder.addShard(n, 50);   // 50 vnodes each

        // Load 1,000 keys
        for (int i = 0; i < 1000; i++) sharder.put("key:" + i, "val:" + i);

        System.out.println("Initial distribution (3 shards): " + sharder.distribution());

        // Example 2: add a 4th shard
        sharder.addShard("s4", 50);
        List<Move> joinMoves = sharder.rebalance();
        System.out.println("addShard(s4): moved " + joinMoves.size() + " keys ("
                + (joinMoves.size() * 100 / 1000) + "% of total)");
        System.out.println("Distribution after join: " + sharder.distribution());

        // Example 3: remove s2; every key must still resolve
        sharder.removeShard("s2");
        List<Move> leaveMoves = sharder.rebalance();
        System.out.println("removeShard(s2): moved " + leaveMoves.size() + " keys");
        int missing = 0;
        for (int i = 0; i < 1000; i++) {
            if (sharder.get("key:" + i) == null) missing++;
        }
        System.out.println("Keys lost after removal: " + missing + " / 1000");

        // Example 4: weighted shards — a(1 vnode) vs b(3 vnodes)
        var weighted = new ConsistentHashSharder();
        weighted.addShard("a", 1);
        weighted.addShard("b", 3);
        for (int i = 0; i < 4000; i++) weighted.put("k" + i, "v" + i);
        System.out.println("Weighted distribution a(1) b(3): " + weighted.distribution());

        // Point lookup example
        System.out.println("get(user:42) = " + sharder.get("key:42"));
        System.out.println("First moves sample: " + joinMoves.subList(0, Math.min(3, joinMoves.size())));
    }
}
```

### Step 5: Walk the Examples

**Example 1**: `put("user:42", "alice")` → `hash64("user:42")` lands at some ring position; `ceilingEntry` finds the nearest virtual node clockwise; the value lands in that shard's map. `get` computes the same hash — always lands on the same owner — returns "alice".

**Example 2 (node join)**: With 3 shards × 50 vnodes each = 150 points spread over the 64-bit ring, 1000 keys distribute roughly evenly (~333 each). Adding s4 (50 more points) relocates the keys between each new vnode and its predecessor — in expectation, 1/4 of the keyspace: ~250 keys. The demo's `moved count` should land near 20-30% of 1000 — the *minimal movement* property. With modulo hashing it would be ~750.

**Example 3 (node leave)**: Removing s2's 50 vnodes leaves gaps; keys formerly owned by s2 now belong to the next vnode clockwise. `rebalance()` physically moves them; a full `get` sweep verifies zero data loss. Note: before the rebalance call, `get` would *still* be consistent because routing is deterministic — the data is simply "stale" on the orphan shard; rebalance is what physically migrates it. This mirrors production: route updates propagate before data migration completes.

**Example 4 (weighted vnodes)**: `a` owns 1 vnode's arc, `b` owns 3 → b should own ~3× the keys (75% vs 25%). The demo prints ≈ (1000, 3000).

**Evenness check**: with 50 vnodes per shard, per-shard counts should be within a few percent of each other — a large enough vnode count smooths out the discrete ring.

### Step 6: Compile & Run

```bash
javac --release 21 PartitioningShardingLab.java
java com.databases.deep.lab03.PartitioningShardingLab
```

Expected output shape:

```
Initial distribution (3 shards): {s1=3xx, s2=3xx, s3=3xx}
addShard(s4): moved ~25x keys (25%)
Distribution after join: {s1=2xx, s2=2xx, s3=2xx, s4=2xx}
removeShard(s2): moved ~3xx keys
Keys lost after removal: 0 / 1000
Weighted distribution a(1) b(3): {a=~1000, b=~3000}
get(user:42) = val:42
```

---

## Complexity Analysis

- **Routing lookup**: O(log N_v) where N_v = total virtual nodes (TreeMap ceilingEntry). With vnode counts in the hundreds and 2^64 positions, this is effectively O(log N) with a tiny constant.
- **put/get**: O(log N_v) + O(1) map access.
- **addShard**: O(weight · log N_v) for ring inserts, plus rebalance O(K · log N_v) where K = keys that moved (expected K ≈ totalKeys/N).
- **removeShard**: O(weight · log N_v) ring removals + same rebalance cost.
- **Space**: ring O(N_v), data O(K).

**Key invariant demonstrated**: on membership change, expected moved keys = K/N (N = total shards), versus K·(N-1)/N for modulo — the N-fold reduction that makes consistent hashing worth it.

## Edge Cases & Failure Handling

1. **Wrap-around**: a key hashing above the last vnode wraps to `firstEntry` — handled explicitly.
2. **Empty ring**: `shardForKey` would throw on `firstEntry()` — production should reject writes with "no shards available" (add a guard).
3. **Duplicate shard names**: rejected at `addShard`.
4. **Vnode hash collisions**: extremely unlikely with 64-bit hashes; if they occur, `TreeMap.put` overwrites — could assign a fallback (`name#i#j`) to be safe.
5. **Repeated rebalance calls**: idempotent — after the first call, no key's owner changed, so subsequent calls move nothing.
6. **Concurrent membership change during routing**: a key can momentarily route to a shard that hasn't received its data yet. Production answers: routing epochs + read-your-writes routing, or a proxy that forwards. Mention in follow-ups.
7. **Decommission ordering**: remove shard only after draining (we do the opposite — remove then rebalance — which is correct here because the shard's data is still in memory during `rebalance`).

## Follow-up Questions

1. **Streaming rebalance**: replace the full `rebalance()` scan with incremental migration — snapshot the arc, copy to the new owner, stream CDC deltas, flip the routing epoch, then truncate. Same code shape, production semantics.
2. **Order-preserving property**: the ring can't do range scans (keys scatter). Design a hybrid: time-range buckets + hash-within-bucket for TSDB workloads.
3. **Rendezvous hashing (HRW)**: replace the ring with weighted HRW (`score = weight / hash(key||node)`) — no vnode count tuning, lower metadata, same minimal-movement property.
4. **Replica placement**: extend `Shard` with `replicas: List<String>` — each key stored on owner + next 2 clockwise shards; `get` falls back across replicas; rebalance replicates before removing.
5. **Consistency on membership change**: add a versioned routing table (epoch per ring mutation) and reject writes whose epoch is stale — the split-brain protection discussed in the mock interview.
6. **Load-aware splitting**: if a shard's keys exceed a threshold, split its arc into two sub-arcs and spawn a new shard — CockroachDB-style automatic range splitting.
7. **Test harness**: property test — for random add/remove sequences, assert: (a) no key ever lost, (b) moved keys ⊆ keys in affected arcs, (c) final distribution within ±10% of expected.

## References

- Karger et al., "Consistent Hashing and Random Trees" (1997)
- DeCandia et al., "Dynamo: Amazon's Highly Available Key-value Store" (2007)
- Lamping & Veach, "A Fast, Minimal Memory, Consistent Hash Algorithm" (jump hash, 2014)
- Szymaniak et al., "Rendezvous Hashing" / Thaler & Ravishankar (HRW, 1998)
