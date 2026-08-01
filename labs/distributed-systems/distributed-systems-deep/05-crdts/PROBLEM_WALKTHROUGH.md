# Lab 05: Problem Walkthrough — G-Counter and PN-Counter CRDTs with Merge

## Problem Statement

**Title**: CounterCRDTs — State-Based G-Counter and PN-Counter with Convergent Merge

**Difficulty**: Medium

**Category**: Distributed Systems, CRDTs, Replication

---

### Problem

Implement state-based CRDT counters:

1. **`GCounter`** — grow-only counter:
   - `increment(id)` — bump the entry for replica id (default `"me"`)
   - `value()` — sum of all entries
   - `merge(GCounter other)` — element-wise max
2. **`PNCounter`** — positive-negative counter built from two G-Counters:
   - `increment(id)` / `decrement(id)`
   - `value()` — sum(p) - sum(n)
   - `merge(PNCounter other)` — merge both halves
3. **`main` demo + assertions**:
   - **concurrent increments from two replicas**: after merging in *any order*, both replicas converge to the same value (no lost increments)
   - **decrement test**: PN-Counter value goes down; and a *stale replica* merging in later (one that never saw the decrements) must NOT resurrect the old higher value
   - **idempotence**: merging the same state twice changes nothing
   - **duplicate-merge order test**: `merge(a,b)` then `merge(c)` equals `merge(b,c)` then `merge(a)` — commutativity/associativity spot check
4. (Optional stretch) `GSet` with tombstones + a *non-commutative* buggy merge (`sum`) to demonstrate the property failures in contrast.

### Constraints

- Java 21+ standard library only
- State exchanged as immutable snapshot (`Map<String, Long>`); merge never mutates its argument
- Vector entries keyed by replica/client id strings

### Examples

**Example 1 (concurrent increments):**
```
replica A: increment("A") x3        → A state {A:3}
replica B: increment("B") x2        → B state {B:2}
A.merge(B); B.merge(A)              → both {A:3, B:2} → value 5
```

**Example 2 (decrements don't resurrect):**
```
replica A: incr x5, decr x2         → p {A:5}, n {A:2} → value 3
replica B (stale): incr x5 only     → p {A:5}, n {}   → value 5
B.merge(A)                          → p {A:5}, n {A:2} → value 3  (NOT 5)
```

**Example 3 (idempotence):**
```
A.merge(B); A.merge(B)              → value unchanged after second merge
```

**Example 4 (merge order):**
```
merge(merge(A,B),C) == merge(merge(B,C),A) → same value and same state
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

A state-based CRDT works because its merge is a **join in a semilattice** — commutative, associative, idempotent — which makes convergence a theorem:

- **Commutative** `merge(a,b)=merge(b,a)` → merge direction doesn't matter
- **Associative** `merge(merge(a,b),c)=merge(a,merge(b,c))` → any gossip relay order works
- **Idempotent** `merge(a,a)=a` → duplicated messages are harmless

`max` per entry satisfies all three. `sum` satisfies commutativity and associativity but **not idempotence** — the classic bug — and merging by sum double-counts entries whenever a merge is repeated.

The G-Counter's trick: each replica only ever bumps *its own* entry, so element-wise max never loses an increment (another replica's entry can't be reduced by anyone but itself, and its own merges only raise it).

The PN-Counter's trick: decrements are implemented as increments of a *separate* grow-only half. The value (difference) goes down, but the *state* is still monotonic — stale replicas merging in later can't resurrect removed counts.

### Step 2: Naive Approach and Why It Fails

**Naive 1 — a single shared integer with `max`:**
```java
void merge(int other) { value = Math.max(value, other); }
```
Concurrent increments from A and B: A merges `value=3`, B merges `value=2`; result 3 — B's increment is silently lost. Max doesn't know increments are *additive*.

**Naive 2 — a single integer with `sum`:**
```java
void merge(int other) { value += other; }
```
Duplicate merges double-count — merge the same peer state twice and the counter inflates. Sum is not idempotent.

**Naive 3 — a shared integer with `max` + decrements:** decrement `value=4`; stale replica with `value=5` merges in → `max(5,4)=5` — the decrement was resurrected. Removals/decrements need a monotonic *representation* (P/N halves), not a monotonic *value*.

### Step 3: Design Decisions

1. **Immutable merge**: `merge` returns a new CRDT (or mutates one side but *never* its argument) — safe exchange semantics.
2. **Entry keys**: replica/client ids — the vector size is O(writers). Fine for the lab; real deployments shard by region (see Follow-up 1).
3. **PNCounter = composition**: implement it by delegating to two G-Counters — the composition pattern is the point (complex CRDTs are built from simple monotonic ones).
4. **Demo assertions**: use `System.out.println` + explicit PASS/FAIL markers so the output doubles as a test report; use `assert`-style checks (via `Objects.equals` on states) printed clearly.
5. **Buggy contrast**: a `BuggySumCounter` whose merge sums (violates idempotence) — demonstrating that *merge order and duplication* are what the semilattice properties protect.

### Step 4: Java 21+ Compilable Solution

```java
package com.distributedsystems.deep.lab05;

import java.util.*;

/**
 * CounterCRDTs — state-based G-Counter and PN-Counter with convergent merges.
 *
 * Demonstrates: element-wise max merge (commutative/associative/idempotent),
 * P/N decomposition for decrements, and the failure modes of a sum-based merge.
 */
public class CrdtsLab {

    /** G-Counter: per-replica counts, merged element-wise by max. */
    static final class GCounter {
        final Map<String, Long> state = new HashMap<>();

        GCounter increment(String id) {
            state.merge(id, 1L, Long::sum);
            return this;
        }

        long value() {
            return state.values().stream().mapToLong(Long::longValue).sum();
        }

        GCounter merge(GCounter other) {
            other.state.forEach((id, v) -> state.merge(id, v, Math::max));
            return this;
        }

        Map<String, Long> snapshot() { return Map.copyOf(state); }

        @Override
        public boolean equals(Object o) {
            return o instanceof GCounter g && g.state.equals(state);
        }

        @Override
        public int hashCode() { return state.hashCode(); }

        @Override
        public String toString() { return "G" + state; }
    }

    /** PN-Counter: two G-Counters (positives, negatives); value = sum(p) - sum(n). */
    static final class PNCounter {
        final GCounter p = new GCounter();
        final GCounter n = new GCounter();

        PNCounter increment(String id) { p.increment(id); return this; }
        PNCounter decrement(String id) { n.increment(id); return this; }

        long value() { return p.value() - n.value(); }

        PNCounter merge(PNCounter other) {
            p.merge(other.p);
            n.merge(other.n);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PNCounter c && c.p.equals(p) && c.n.equals(n);
        }

        @Override
        public int hashCode() { return Objects.hash(p, n); }

        @Override
        public String toString() { return "PN(p=" + p + ", n=" + n + ") = " + value(); }
    }

    /** Deliberately wrong: sum-based merge — not idempotent, inflates on re-merge. */
    static final class BuggySumCounter {
        long value = 0;
        BuggySumCounter increment(long by) { value += by; return this; }
        BuggySumCounter merge(BuggySumCounter other) {
            value += other.value;             // BUG: violates idempotence
            return this;
        }
    }

    private static void check(String label, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + label);
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        System.out.println("== Example 1: concurrent increments converge ==");
        var a = new GCounter().increment("A").increment("A").increment("A"); // {A:3}
        var b = new GCounter().increment("B").increment("B");               // {B:2}
        a.merge(b);
        b.merge(a);
        check("A value 5", a.value() == 5);
        check("B value 5", b.value() == 5);
        check("A and B converge", a.equals(b));
        System.out.println("  A=" + a + "  B=" + b);

        System.out.println("\n== Example 2: decrements do not resurrect ==");
        var live = new PNCounter().increment("A").increment("A").increment("A")
                                  .increment("A").increment("A")
                                  .decrement("A").decrement("A");          // value 3
        var stale = new PNCounter().increment("A").increment("A")
                                   .increment("A").increment("A")
                                   .increment("A");                        // value 5
        stale.merge(live);                                                 // stale learns the decrements
        check("stale now 3 (no resurrection)", stale.value() == 3);
        System.out.println("  stale after merge: " + stale);

        System.out.println("\n== Example 3: idempotence ==");
        var a2 = new GCounter().increment("A").increment("B");
        var before = a2.snapshot();
        a2.merge(new GCounter().increment("A"));
        a2.merge(new GCounter().increment("A"));          // re-merge the same op
        check("re-merge unchanged", a2.snapshot().equals(before));
        System.out.println("  state stays " + a2.snapshot());

        System.out.println("\n== Example 4: merge order (commutativity/associativity) ==");
        var x = new GCounter().increment("A");
        var y = new GCounter().increment("B").increment("B");
        var z = new GCounter().increment("C").increment("C").increment("C");
        var order1 = new GCounter().merge(x).merge(y).merge(z);
        var order2 = new GCounter().merge(z).merge(y).merge(x);
        check("merge(x,y,z) == merge(z,y,x)", order1.equals(order2));
        check("value 6 either way", order1.value() == 6 && order2.value() == 6);
        System.out.println("  " + order1 + "  vs  " + order2);

        System.out.println("\n== Contrast: sum-based merge breaks idempotence ==");
        var bugA = new BuggySumCounter().increment(3);
        var bugB = new BuggySumCounter().increment(2);
        bugA.merge(bugB);
        bugA.merge(bugB);                                  // same merge again — message duplicate
        check("buggy counter stays 5", bugA.value == 5);   // EXPECT FAIL: it is 10
        System.out.println("  buggy value = " + bugA.value + " (double-counted)");
    }
}
```

### Step 5: Walk the Examples

**Example 1**: A has `{A:3}`, B has `{B:2}`. `a.merge(b)` → `max` per entry → `{A:3, B:2}` = 5; `b.merge(a)` → the same state — **both converge to 5**, and since merges are symmetric, neither direction loses an increment. If the merge were `sum` (per-entry), A would become `{A:3, B:2}`=5 but B would become `{A:3, B:2}`=5 as well here — the real sum failure shows under *duplication* (Example 4's contrast).

**Example 2**: `live` decremented twice → p=`{A:5}`, n=`{A:2}` → 3. `stale` only ever saw increments → p=`{A:5}`, n=`{}` → 5. When `stale` merges `live`, the *n* half merges by max → n=`{A:2}` → 3. The decrements are represented as monotonic growth of `n`, so the stale replica converges down — a plain `max` on the *value* would have stayed at 5.

**Example 3**: merging `{A:1}` into `{A:1,B:1}` twice — the second merge is a no-op because max is idempotent: `max(1,1)=1`. A `sum`-based merge would double-count A on every re-delivery.

**Example 4**: any order of merging x={A:1}, y={B:2}, z={C:3} lands on `{A:1,B:2,C:3}` — commutativity (any sequence) and associativity (any grouping) both hold; value 6 in both orders.

**Contrast**: `bugA` (3) merges `bugB` (2) → 5; merge again → 10. Real networks duplicate messages (retransmissions, overlapping gossip digests), so sum-based merges inflate — this is why CRDT merges must be max/union-style joins.

### Step 6: Compile & Run

```bash
javac --release 21 CrdtsLab.java
java com.distributedsystems.deep.lab05.CrdtsLab
```

Expected output shape:

```
== Example 1: concurrent increments converge ==
PASS A value 5
PASS B value 5
PASS A and B converge
  A=G{A=3, B=2}  B=G{A=3, B=2}

== Example 2: decrements do not resurrect ==
PASS stale now 3 (no resurrection)
  stale after merge: PN(p=G{A=5}, n=G{A=2}) = 3

== Example 3: idempotence ==
PASS re-merge unchanged
  state stays {A=1, B=1}

== Example 4: merge order (commutativity/associativity) ==
PASS merge(x,y,z) == merge(z,y,x)
PASS value 6 either way
  G{A=1, B=2, C=3}  vs  G{A=1, B=2, C=3}

== Contrast: sum-based merge breaks idempotence ==
FAIL buggy counter stays 5   (intended demonstration)
  buggy value = 10 (double-counted)
```

---

## Complexity Analysis

- **merge**: O(S) where S = state size (number of vector entries) — the full-state exchange cost of CvRDTs; on the wire this is the payload (G-Counter: N longs, N = replica count).
- **value**: O(S).
- **increment/decrement**: O(1) — a single map update.
- **Space**: O(S) per replica — the entry count grows with the number of distinct writers (why production systems shard by region or cap entries — see Follow-up 1).
- **Convergence**: after mutual merges, replicas are equal in O(1) merge round (state-based) — vs op-based, where convergence requires delivering all ops with causal order.

## Edge Cases & Failure Handling

1. **Merging into yourself** — `a.merge(a)`: max is idempotent → no-op; sum-based would double — the buggy contrast is exactly this case.
2. **Merging empty state** — no entries: max with missing keys (treated as 0 via `getOrDefault` semantics of `merge`) → no-op.
3. **Replica id collisions** — two writers using the same id both bump the same entry: merges are still correct (max), but increments from *distinct* writers become conflated — ids must be globally unique per writer.
4. **Negative values from decrement-before-increment** — PN-Counter allows `value() < 0` (n grows while p is empty) — correct representationally, but the domain must accept it; a strict non-negative counter needs a different CRDT (or client-side guard).
5. **Entry count growth** — every new writer id adds an entry; unbounded writer ids grow state unboundedly (tombstone-like bloat, but bounded per writer count in practice).
6. **Large states** — full-state merge payloads grow with S; for big deployments use op-based CRDTs or delta-state exchange (Follow-up 2).
7. **Integer overflow** — `long` counts; saturation or BigInteger for pathological rates (a real 'likes' counter across regions can hit overflow only at absurd scale, but design for it).

## Follow-up Questions

1. **Sharding the vector**: with millions of writers, cap entries by assigning increments to a fixed set of region/partition ids (like a real deployment) — vector size becomes O(partitions), not O(writers).
2. **Delta-state CRDTs**: exchange only the *delta* since the last merge (`max` of missing entries) instead of full state — the payload optimization between state- and op-based approaches (Almeida et al., "delta-CRDTs").
3. **G-Set / OR-Set with tombstones**: add `add`/`remove` with a tombstone map; verify removal never resurrects under stale merges; then reason about GC: when can tombstones be dropped?
4. **Op-based (CmRDT) version**: broadcast `increment(id)` ops with per-replica causal delivery — compare payload size and convergence requirements vs the state-based version.
5. **Composition**: build a `GCounterMap` (per-key counters) by composing the simple type — the pattern that production systems (e.g., Redis) use for multi-key metrics.
6. **Property-based tests**: random concurrent increment/decrement sequences from N replicas with random merge topologies (including re-merges and stale replicas joining late) — invariant: all replicas converge; and the buggy sum merge must fail the same suite.
7. **The 'likes with unlikes' design**: multi-region PN-Counter with per-region entries — add a latency table showing read/write behavior under a region partition, and show convergence after healing.

## References

- Shapiro, Preguiça, Baquero, Zawirski, "A Comprehensive Study of Convergent and Commutative Replicated Data Types" (2011) — the CRDT paper
- Almeida, Shoker, Baquero, "Delta State Replicated Data Types" (2016)
- Kleppmann, "Conflict-Free Replicated Data Types" (talk + *Designing Data-Intensive Applications*, Ch. 5)
- Automerge/Yjs docs — sequence CRDTs and tombstone GC in production text editing
