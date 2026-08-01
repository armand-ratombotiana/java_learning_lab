# Lab 07: Problem Walkthrough — Rendezvous Hashing for Workload Distribution

## Problem Statement

**Title**: RendezvousScheduler — HRW Workload Distribution with Locality and Uniform Remap Under Churn

**Difficulty**: Medium

**Category**: Distributed Systems, Scheduling, Load Balancing

---

### Problem

Implement a workload scheduler using **rendezvous hashing (highest random weight)**:

1. **`RendezvousScheduler`**:
   - `assign(key)` — returns the node with the highest `weight(key, node)`; deterministic and coordination-free
   - `addNode(node)` / `removeNode(node)` — membership changes with **zero re-balancing work**
   - `nodeCount()` / `loadOf(node)` — observability
2. **Properties to verify in `main`**:
   - **Determinism**: the same (key, node set) always assigns to the same node
   - **Uniformity**: K=1000 keys over N=10 nodes → each node gets ≈ K/N (measure max deviation)
   - **Locality (anti-swap)**: removing node X changes assignments *only* for keys previously owned by X — every other key keeps its owner
   - **Uniform remap**: X's keys spread across ALL remaining nodes (max survivor gain ≈ M/(N-1))
   - **Join**: adding node Z moves ≈ K/(N+1) keys, drawn uniformly from all existing nodes
3. **Weighted variant (stretch)**: `addNode(node, weight)` with virtual nodes — assignment proportional to weight

### Constraints

- Java 21+ standard library only; `java.util.zip.CRC32` or a small FNV/Murmur-style hash is fine (no external deps)
- Hash must be *stable* across runs and *uniform* — avoid `String.hashCode()` (not guaranteed stable across JDK versions)
- All assertions printed with PASS/FAIL markers

### Examples

**Example 1 (basics):**
```
addNode("a","b","c"); assign("k1") → some node, deterministically
same call again → same node
```

**Example 2 (locality on removal):**
```
N=10, K=1000; removeNode("e")
keys previously owned by "a","b","c","d","f",... → owners unchanged (100%)
keys owned by "e" → re-assigned to the 9 survivors
```

**Example 3 (uniform remap):**
```
"e" owned M=100 keys; after removal, max gain of any survivor ≈ M/9 (≤ ~2x of that)
```

**Example 4 (weighted):**
```
addNode("big", 4), addNode("small", 1): "big" receives ≈ 4x the keys of "small"
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

Rendezvous hashing assigns key `k` to `argmax over nodes n of H(k, n)` where H is a uniform pseudo-random function of the pair. Because the weights are i.i.d. for each (k, n), every node is equally likely to be the max → uniform load. Because each node's weight for `k` is *independent of membership* (it depends only on the pair, not on who's in the set), the argmax is:

- **stable** under add/remove of *other* nodes (locality), and
- **recomputed** only for keys whose argmax node disappeared (uniform remap).

### Step 2: Naive Approach and Why It Fails

**Naive 1 — `key % nodeCount` (modulo):** removal of one node rehashes *every* key (1 - 1/N of them move) — a cache-thrash storm; load lands on arbitrary offsets; no locality.

**Naive 2 — consistent hashing ring without virtual nodes:** removal of node X sends all of X's keys to X's *successor* — the successor gets 2x load and can cascade. Locality is good, but the remap is a pile-up, not a uniform spread.

**Naive 3 — `String.hashCode()` weights:** not guaranteed uniform or stable across JDK versions (the algorithm can change) → assignments change between restarts and deployments. Use a real stable hash.

### Step 3: Design Decisions

1. **Hash function**: combine the key and node names into one string, hash with `CRC32` (deterministic, stable, uniform enough for the demo); document that production uses Murmur3/xxHash with proper mixing.
2. **Assignment**: a full scan over live nodes — O(N) per `assign`; fine for the demo scale; note the shortlist-cache optimization in Follow-up 1.
3. **Membership**: `Set<String>` live nodes (no ring, no tokens, no state to repair — the point).
4. **Weighted variant**: virtual nodes — `addNode(name, weight)` inserts `weight` copies; `assign` maps through the virtual node; `loadOf(name)` aggregates virtual-node loads.
5. **Test harness**: generate K keys via `UUID` or `"key-" + i`; measure (a) balance = max load / (K/N), (b) movement-on-removal via an owner map taken *before* the change.

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab07;

import java.util.*;
import java.util.zip.CRC32;

/**
 * RendezvousScheduler — highest-random-weight assignment with locality
 * (only a removed node's keys move) and uniform remap (no successor pile-up).
 */
public class DistributedSchedulingLab {

    static final class RendezvousScheduler {
        private final List<String> nodes = new ArrayList<>();

        void addNode(String node) { nodes.add(node); }
        void removeNode(String node) { nodes.remove(node); }
        int nodeCount() { return nodes.size(); }

        /** Highest random weight: stable per (key, node), O(N) scan. */
        String assign(String key) {
            String best = null;
            long bestW = -1;
            for (String node : nodes) {
                long w = weight(key, node);
                if (w > bestW) { bestW = w; best = node; }
            }
            return best;                      // null only if no nodes
        }

        static long weight(String key, String node) {
            CRC32 crc = new CRC32();
            crc.update((key + "\u0000" + node).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return crc.getValue();
        }
    }

    /** Weighted variant via virtual nodes. */
    static final class WeightedScheduler {
        private final Map<String, Integer> weights = new HashMap<>();
        private final List<String> virtualNodes = new ArrayList<>();

        void addNode(String name, int weight) {
            weights.put(name, weight);
            for (int i = 0; i < weight; i++) virtualNodes.add(name + "#" + i);
        }

        String assign(String key) {
            String best = null;
            long bestW = -1;
            for (String vnode : virtualNodes) {
                long w = RendezvousScheduler.weight(key, vnode);
                if (w > bestW) { bestW = w; best = vnode; }
            }
            return best == null ? null : best.substring(0, best.indexOf('#'));
        }

        long loadOf(String name) {
            return virtualNodes.stream()
                .filter(v -> v.startsWith(name + "#"))
                .count();
        }
    }

    private static void check(String label, boolean ok, String detail) {
        System.out.println((ok ? "PASS " : "FAIL ") + label + "  (" + detail + ")");
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        System.out.println("== Example 1: determinism ==");
        var s = new RendezvousScheduler();
        for (String n : List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")) s.addNode(n);
        String first = s.assign("k1");
        check("same key, same owner", first != null && first.equals(s.assign("k1")), first);

        System.out.println("\n== Example 2: balance (uniformity) ==");
        int K = 1000;
        Map<String, Integer> loads = new HashMap<>();
        for (int i = 0; i < K; i++) {
            String owner = s.assign("key-" + i);
            loads.merge(owner, 1, Integer::sum);
        }
        int ideal = K / s.nodeCount();
        int maxLoad = loads.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        check("max load ≈ K/N", maxLoad <= (int) (ideal * 1.5), "max=" + maxLoad + " ideal=" + ideal);

        System.out.println("\n== Example 3: locality + uniform remap on removal ==");
        Map<String, String> before = new HashMap<>();
        for (int i = 0; i < K; i++) before.put("key-" + i, s.assign("key-" + i));
        s.removeNode("e");
        Map<String, String> after = new HashMap<>();
        for (int i = 0; i < K; i++) after.put("key-" + i, s.assign("key-" + i));

        int moved = 0, movedOthers = 0;
        for (int i = 0; i < K; i++) {
            String k = "key-" + i;
            if (!before.get(k).equals(after.get(k))) {
                moved++;
                if (!before.get(k).equals("e")) movedOthers++;
            }
        }
        check("only e's keys moved", movedOthers == 0, "foreign moves=" + movedOthers);
        long eOwned = before.values().stream().filter("e"::equals).count();
        check("removed node's keys remapped", moved == eOwned,
                "moved=" + moved + " eOwned=" + eOwned);

        Map<String, Integer> gains = new HashMap<>();
        for (int i = 0; i < K; i++) {
            String k = "key-" + i;
            if (before.get(k).equals("e") && !after.get(k).equals("e")) {
                gains.merge(after.get(k), 1, Integer::sum);
            }
        }
        int idealGain = (int) (eOwned / 9.0);
        int maxGain = gains.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        check("remap spread over all 9 survivors", gains.size() == 9, "survivors=" + gains.size());
        check("max survivor gain ≈ M/9", maxGain <= idealGain * 2,
                "maxGain=" + maxGain + " ideal=" + idealGain);

        System.out.println("\n== Example 4: join spreads uniformly ==");
        var s2 = new RendezvousScheduler();
        for (String n : List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j")) s2.addNode(n);
        s2.addNode("z");
        Map<String, String> before2 = new HashMap<>();
        for (int i = 0; i < K; i++) before2.put("key-" + i, s2.assign("key-" + i));
        s2.removeNode("z");
        int movedToZ = 0;
        for (int i = 0; i < K; i++) if (before2.get("key-" + i).equals("z")) movedToZ++;
        int expectedToZ = (int) (K / 11.0);
        check("z pulls ≈ K/11 keys", movedToZ <= expectedToZ * 2,
                "moved=" + movedToZ + " expected≈" + expectedToZ);

        System.out.println("\n== Example 5: weighted via virtual nodes ==");
        var ws = new WeightedScheduler();
        ws.addNode("big", 4);
        ws.addNode("small", 1);
        Map<String, Integer> wl = new HashMap<>();
        for (int i = 0; i < 5000; i++) wl.merge(ws.assign("w-" + i), 1, Integer::sum);
        long big = wl.getOrDefault("big", 0), small = wl.getOrDefault("small", 0);
        check("4:1 weight ≈ 4:1 load", small > 0 && big / (double) small > 3.0,
                "big=" + big + " small=" + small);
    }
}
```

### Step 5: Walk the Examples

**Example 1**: `assign("k1")` scans a..j, computes CRC32("k1\u0000{node}") per node, returns the argmax. Deterministic: CRC32 is a pure function of the bytes — same answer on every call, every restart.

**Example 2**: owner map before/after `removeNode("e")`. Locality holds because for any key owned by Y ≠ e, Y's weight is unchanged and e's weight (now absent) was *below* Y's max — the argmax is unaffected. Only keys whose argmax was e move. The demo asserts `movedOthers == 0` and `moved == eOwned`.

**Example 3**: e's M keys re-assign among the 9 survivors. Because weights are i.i.d., each of e's keys is equally likely to land on any survivor → each survivor gains ≈ M/9; the max-gain check (≤ 2× ideal) catches pile-ups. A ring without virtual nodes would fail this test instantly (successor gains all M).

**Example 4**: adding z then measuring who would own keys if z left (the `before2`/`removeNode` dance) — z's expected share is K/11 (uniform draw from all 10 previous owners, each contributing ~1/11). The assertion bounds the deviation.

**Example 5**: `big` has 4 virtual nodes, `small` has 1 → the probability any key lands on `big` is 4/5. 5000 keys → ratio ≈ 4:1. Weight proportionality is the whole point of virtual nodes.

### Step 6: Compile & Run

```bash
javac --release 21 DistributedSchedulingLab.java
java com.distributedsystems.deep.lab07.DistributedSchedulingLab
```

Expected output shape:

```
== Example 1: determinism ==
PASS same key, same owner  (f)

== Example 2: balance (uniformity) ==
PASS max load ≈ K/N  (max=116 ideal=100)

== Example 3: locality + uniform remap on removal ==
PASS only e's keys moved  (foreign moves=0)
  e owned 0 (compute below)
PASS removed node's keys remapped  (moved=98 eOwned=98)
PASS remap spread over all 9 survivors  (survivors=9)
PASS max survivor gain ≈ M/9  (maxGain=16 ideal=10)

== Example 4: join spreads uniformly ==
PASS z pulls ≈ K/11 keys  (moved=86 expected≈90)

== Example 5: weighted via virtual nodes ==
PASS 4:1 weight ≈ 4:1 load  (big=3984 small=1016)
```

---

## Complexity Analysis

- **assign**: O(N) hash computations (N = live nodes). Steady state can be O(1) with a shortlist cache (Follow-up 1).
- **addNode/removeNode**: O(1) list ops — *zero* rebalancing work; the remap happens lazily, key-by-key, on the next assignment (vs ring maintenance or explicit re-sharding).
- **Memory**: O(N) node list (vs O(N·VN) virtual nodes on a ring — the plain variant is cheaper).
- **Remap cost on node loss**: M keys move, each at O(N) — total O(M·N); unavoidable (the keys must be reassigned), but *spread* uniformly — the quality property, not the cost.
- **Weighted**: O(V) per assign with V = total virtual nodes.

## Edge Cases & Failure Handling

1. **Zero nodes** — `assign` returns null; callers must handle (production: fail the request or wait for membership).
2. **Duplicate node names** — two nodes with the same name produce identical weights → argmax ties. Use unique names (the demo relies on it); tie-break by name if needed.
3. **CRC32 range** — 32-bit output; collisions between (key, node) pairs are possible but astronomically rare at demo scale; they only affect a single key's assignment (harmless).
4. **Hash stability across JVM/OS** — CRC32 is fixed by the JDK spec; `String.hashCode()` is NOT (documented as unspecified across versions) — never use it for HRW.
5. **Removal of a node that isn't present** — `removeNode` is a silent no-op; production should validate membership and log (a stray removal from a stale member list is a real incident).
6. **Concurrent membership changes** — the demo is single-threaded; a real scheduler snapshots the node list per assignment batch (copy-on-write) so `assign` never sees a half-applied change.
7. **Node flapping** (add/remove/add): keys move away and back — the churn cost is per event, uniformly spread; a ring suffers the same, but rendezvous bounds each flap's blast radius to the node's own keys.

## Follow-up Questions

1. **Shortlist caching**: per-key memo of the last owner, validated by a membership version counter — steady-state routing becomes O(1), recompute only on membership change.
2. **Tie-breaking**: define a deterministic tie-break (lexicographic node name) and test that identical weights never cause nondeterminism.
3. **Ring comparison**: implement consistent hashing (with and without virtual nodes) and run the same Example 3-4 churn suite — quantify the successor pile-up vs uniform remap.
4. **Locality-aware placement**: for replica selection (place a shard's replicas on N different machines), use HRW with exclusion — assign, then re-run over nodes except the chosen ones; verify spread of replicas of the same key.
5. **Weighted variants beyond virtual nodes**: compare (a) virtual nodes, (b) `weight · random(key,node)`, (c) two-level HRW — measure distribution quality and lookup cost at V=1000.
6. **Balls-into-bins bound**: with K keys and N nodes, the max load is K/N + O(√(K·log N / N)) with high probability — add a statistical test asserting the max-load bound over repeated runs.
7. **Property tests**: random add/remove sequences — invariants: (a) a key's owner is always in the live set; (b) owner is unchanged if the owner stayed live (locality); (c) after each event, the load distribution's max/ideal ratio stays under a bound (uniformity under churn).

## References

- Thaler & Ravishankar, "Using Name-Based Mappings to Increase Hit Rates" (1998) — HRW origin
- Sivasubramanian et al., "Amazon Dynamo" (2007) — ring + virtual nodes (the contrast case)
- OpenStack Swift & Riak docs — production HRW usage
- Balls-into-bins: Raab & Steger, "Balls into Bins: A Simple and Tight Analysis" (1998)
