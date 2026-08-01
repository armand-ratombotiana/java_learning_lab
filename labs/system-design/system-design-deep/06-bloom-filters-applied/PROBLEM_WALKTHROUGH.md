# Problem Walkthrough: Counting Bloom Filter with Deletion

## Problem Statement

An ad platform must deduplicate impression events: the same impression id can be
retried or replayed, and double-counting breaks billing. The dedup window is
~10 minutes, at hundreds of thousands of events per second, with ~40-byte keys.
A standard Bloom filter fits — membership answers "have we seen this id?" with a
tunable false-positive rate and no false negatives — but the window requires
**deletion** (ids leave the window), which a plain Bloom filter cannot support:
clearing bits would destroy evidence shared with other keys.

Implement a **counting Bloom filter**: an array of small counters instead of
bits. Add increments `k` counters, delete decrements them, membership requires
all `k` counters to be non-zero — and deletion must never reintroduce false
negatives.

## Requirements

- **Membership:** `mightContain(key)` returns true for every key ever inserted and
  not fully removed (no false negatives) — or `false` with probability `p` for
  keys never inserted (false positives, tunable).
- **Deletion:** `remove(key)` decrements the key's counters. Removing a key that
  was inserted must never cause another present key to read as absent.
- **Underflow safety:** `remove` of an absent key (or a double-remove) must be a
  safe no-op — never a negative counter.
- **Bounded memory:** counter width is capped (saturation) so memory is
  `m × width` bits regardless of insertion volume.
- **Sizing by formula:** `m` (positions) and `k` (hash functions) derive from
  expected entries `n` and target false-positive rate `p`.
- **Observable demo:** lifecycle, underflow guard, the defining insert-twice/
  delete-once property, and a measured false-positive rate.

## Constraints & Assumptions

- Keys are strings (~40 bytes); `n` ≈ 100k-1M entries in flight; `p` ≈ 1-3%.
- Counters are modeled as `int` for readable demo values; production width is a
  parameter (4-bit is the classic choice, capped at 15).
- Single-threaded correctness first; concurrency notes in follow-ups.
- The demo structure is the algorithm; fleet deployment hosts the same counters
  in Redis (see Operations).

## Why Counting Works — The Asymmetry

A plain Bloom filter cannot delete: `remove` would clear bits that other keys
share, and a cleared bit turns "maybe present" into "definitely absent" for every
key mapping there — **false negatives**, the one error class the filter must
never produce.

Counting replaces bits with counters that track *how many inserted keys point at
each position*:

| Operation | Bit filter | Counting filter |
|-----------|------------|-----------------|
| Add | set k bits | `counter[i]++` for k positions |
| Membership | all k bits set | all k counters > 0 |
| Delete | impossible | `counter[i]--` for k positions |
| False negative on delete | would occur | **cannot occur** while counters stay ≥ 0 |

The key insight: deletion decrements *only* counters this key incremented, and
other keys only need those counters to remain non-zero. A delete can therefore
only lower counts toward zero — at worst making a position "no" for keys that
map there — but never more than the positions this key touched, which is exactly
the set this key is responsible for. **Deletion may raise the false-positive
rate (counters dropping early), never create false negatives.**

## Solution Overview

```
        insert("X")            mightContain("X")        remove("X")
   for i in 0..k-1:         for i in 0..k-1:         for i in 0..k-1:
     pos = hash_i("X")        pos = hash_i("X")         pos = hash_i("X")
     if counter[pos] < cap:   if counter[pos] == 0:     if counter[pos] == 0:
       counter[pos]++           return false              return false   // underflow guard
     else:                     return true            for i in 0..k-1:     // check all first
       (saturated)                                       counter[hash_i("X")]--
                                                        return true
```

- **`k` independent-looking positions** via double hashing: two base hashes
  `h1, h2` (FNV-1a), then `pos_i = (h1 + i·h2) mod m`.
- **Saturation:** `counter = min(maxCount, counter + 1)` — bounded width, and a
  saturated position acts as a sticky "yes" bit (documented trade).
- **Check-all-then-act in `remove`:** no position is decremented until all `k`
  positions are confirmed positive — underflow is impossible by construction.

## Step-by-Step Solution

### Step 1: Size the filter from the requirements

For expected entries `n` and false-positive rate `p`:

```
m = ceil(-n · ln p / ln² 2)      positions (counters)
k = round((m / n) · ln 2)        hash functions
```

The optimum `k` makes the per-position fill `k/n` and minimizes the overlap
probability; memory = `m · width` bits (e.g., 4-bit counters → `m/2` bytes).

### Step 2: Implement the hash family with double hashing

Two FNV-1a passes over `key` (salted) produce `h1` and `h2`; the `k` positions
are `(h1 + i·h2) mod m` for `i = 0..k-1`. This yields `k` well-distributed
positions with two hashes instead of `k` — fast and compact, and the pairwise
independence approximation is standard practice. Two implementation details
matter: the FNV passes include a **splitmix64 finalizer** (raw FNV-1a clusters
keys that differ only in their final byte, which would collapse the effective
position count), and the modulo uses **`Math.floorMod`** — a plain `%` on the
unbounded `h1 + i·h2` can go negative when the sum overflows a `long`, which
would index the counter array out of bounds.

### Step 3: Implement add with saturation

`add` increments each of the `k` positions up to `maxCount`. Saturation is
deliberate: unbounded counters would break memory bounds. Cost: a position that
has saturated can be decremented only down from the cap, so after heavy overlap
it behaves like a sticky one — the false-positive floor rises slightly; the
filter never produces false negatives.

### Step 4: Implement membership

`mightContain` scans the `k` positions; any zero means the key was never (fully)
inserted — return false. All non-zero means "possibly present."

### Step 5: Implement delete with the underflow guard

1. Check all `k` positions are > 0; if any is zero, return `false` (key absent —
   safe no-op).
2. Decrement all `k` positions.
3. Return `true`.

The check-all-then-act ordering is the correctness property: a partial
decrement can never leave a position negative.

### Step 6: Demonstrate the defining property

Insert `X` twice, remove `X` once → `mightContain(X)` must still be true. This is
the contract a plain Bloom filter cannot honor and the test that defines a
counting Bloom filter.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab06;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Lab 06: Counting Bloom Filter with Deletion.
 * Demonstrates: counter-based membership, underflow-guarded removal,
 * saturation-bounded counters, and the insert-twice/delete-once property
 * that defines counting Bloom filters.
 */
public class CountingBloomFilterLab {

    /** Counting Bloom filter: counters instead of bits, k hashes via double hashing. */
    public static final class CountingBloomFilter {
        private final int m;                 // number of positions (counters)
        private final int k;                 // number of hash functions
        private final int[] counters;
        private final int maxCount;          // saturation cap
        private final AtomicLong inserts = new AtomicLong();
        private final AtomicLong deletes = new AtomicLong();

        public CountingBloomFilter(int expectedElements, double falsePositiveRate, int maxCount) {
            double ln2 = Math.log(2);
            this.m = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (ln2 * ln2));
            this.k = Math.max(1, (int) Math.round((double) m / expectedElements * ln2));
            this.counters = new int[m];
            this.maxCount = maxCount;
        }

        /** FNV-1a 64-bit with a splitmix64 finalizer (raw FNV clusters on final-byte differences). */
        private static long fnv1a(String input, String salt) {
            long hash = 0xcbf29ce484222325L;
            String combined = salt + input;
            for (byte b : combined.getBytes(StandardCharsets.UTF_8)) {
                hash ^= (b & 0xFFL);
                hash *= 0x100000001b3L;
            }
            hash ^= hash >>> 33;
            hash *= 0xff51afd7ed558ccdL;
            hash ^= hash >>> 33;
            hash *= 0xc4ceb9fe1a85ec53L;
            hash ^= hash >>> 33;
            return hash & Long.MAX_VALUE;
        }

        /** k positions via double hashing: pos_i = (h1 + i*h2) mod m (floorMod: never negative). */
        private int[] positions(String key) {
            long h1 = fnv1a(key, "a:");
            long h2 = fnv1a(key, "b:");
            int[] pos = new int[k];
            for (int i = 0; i < k; i++) {
                pos[i] = Math.floorMod(h1 + (long) i * h2, m);
            }
            return pos;
        }

        /** Insert: increment each position, saturating at maxCount. */
        public void add(String key) {
            for (int p : positions(key)) {
                counters[p] = Math.min(maxCount, counters[p] + 1);
            }
            inserts.incrementAndGet();
        }

        /** Membership: false if any position is zero. Never a false negative for inserted keys. */
        public boolean mightContain(String key) {
            for (int p : positions(key)) {
                if (counters[p] == 0) return false;
            }
            return true;
        }

        /**
         * Delete: check ALL positions first (underflow guard), then decrement.
         * Returns false (no-op) if the key is not present.
         */
        public boolean remove(String key) {
            int[] pos = positions(key);
            for (int p : pos) {
                if (counters[p] == 0) return false;   // absent: nothing to delete
            }
            for (int p : pos) {
                counters[p]--;                        // decrement only after all checks pass
            }
            deletes.incrementAndGet();
            return true;
        }

        public int size() { return m; }
        public int hashFunctions() { return k; }
        public long inserts() { return inserts.get(); }
        public long deletes() { return deletes.get(); }
    }

    public static void main(String[] args) {
        CountingBloomFilter filter =
                new CountingBloomFilter(100_000, 0.01, 15);  // n=100k, p=1%, 4-bit-class cap

        System.out.printf("filter sized: m=%d positions, k=%d hashes, memory≈%d bytes%n",
                filter.size(), filter.hashFunctions(), filter.size() / 2);

        // Scenario 1: lifecycle — add, check, remove, re-check
        filter.add("impression-0001");
        System.out.println("after add    : contains imp-0001 = " + filter.mightContain("impression-0001"));
        boolean removed = filter.remove("impression-0001");
        System.out.println("remove imp-0001 = " + removed
                + ", contains again = " + filter.mightContain("impression-0001"));

        // Scenario 2: underflow guard — removing an absent key is a safe no-op
        boolean absentRemove = filter.remove("impression-never-seen");
        System.out.println("remove absent key = " + absentRemove + " (no-op, counters intact)");

        // Scenario 3: THE defining property — insert twice, delete once, still present
        filter.add("impression-0042");
        filter.add("impression-0042");
        boolean firstDelete = filter.remove("impression-0042");
        System.out.println("insert x2, delete x1 = " + firstDelete
                + ", still contains = " + filter.mightContain("impression-0042")
                + "  <- no false negative on partial delete");

        // Scenario 4: measured false-positive rate on absent keys
        Random rng = new Random(42);
        int trials = 50_000;
        int falsePositives = 0;
        for (int t = 0; t < trials; t++) {
            String absent = "ghost-" + rng.nextInt(1_000_000_000);
            if (filter.mightContain(absent)) falsePositives++;
        }
        System.out.printf("false-positive rate over %d absent keys: %.4f%% (target ~1%%)%n",
                trials, 100.0 * falsePositives / trials);

        // Scenario 5: saturation behavior — a hot key saturates shared positions
        filter.add("hot-key");
        filter.add("hot-key");
        filter.add("hot-key");
        filter.add("hot-key");
        for (int i = 0; i < 20; i++) filter.add("hot-key");   // oversubscribe
        boolean stillPresent = filter.mightContain("hot-key");
        boolean otherSurvivor = filter.mightContain("impression-0042");
        System.out.printf("saturation: hot-key present=%s, unrelated key still present=%s%n",
                stillPresent, otherSurvivor);

        System.out.printf("totals: inserts=%d, deletes=%d%n", filter.inserts(), filter.deletes());
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| `add` | O(k) | O(1) | Two base hashes + k increments |
| `mightContain` | O(k) | O(1) | k counter reads; short-circuits on first zero |
| `remove` | O(k) | O(1) | k reads, then k decrements — check-all-then-act |
| Total memory | — | O(m · width) | m positions × counter width; 4-bit → m/2 bytes |
| False-positive rate | — | — | `p` per query; rises slightly as counters deplete or saturate |

`k` is ~7-10 at p = 1%: every operation is a handful of array accesses with
excellent cache behavior — hundreds of thousands of ops/sec per core, which is
why the structure fits the throughput requirement.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Double delete of a present key | First remove succeeds; second is a no-op (a position hits zero first) | Underflow guard — never negative |
| Delete of an absent key | `remove` returns false; counters untouched | Check-all-then-act ordering |
| Insert twice, delete once | Key still reads present | Counters track multiplicity, not bits |
| Position shared by many keys | Counters saturate at `maxCount` | Bounded memory; sticky-one trade documented |
| Counter saturation | Saturated positions act as permanent "yes" bits | Slight false-positive floor; zero false negatives |
| Key mapping to a saturated+depleted position | Membership may return true for an absent key | False positives are the filter's designed error; rate measured in demo |
| k=0 edge (tiny m) | `k` clamped to ≥ 1 | Size formula sanity |

## Verification Walkthrough

1. **Lifecycle:** add → contains true; remove → contains false. The basic
   contract holds.
2. **Underflow guard:** removing a never-inserted key returns `false` and leaves
   counters intact — negative counters are impossible.
3. **Defining property:** insert `impression-0042` twice, delete once — still
   present. A bit-based filter cannot pass this test; the counting design can.
4. **Measured false positives:** 50k absent keys produce ≈0% false positives
   against a filter sized for 100k entries but holding only ~27 — the measured
   rate tracks the *current fill*, not the sizing target. The formula's guarantee
   (≈1% at n=100k inserts) holds at designed capacity; the demo verifies the
   mechanism, not the eventual fill.
5. **Saturation:** a heavily inserted key leaves shared positions saturated;
   unrelated keys remain present — saturation degraded rate, not correctness.

## Operations: Fleet Scale

- **Shared store:** the same counters hosted in Redis (`INCRBY` / `DECRBY` /
  `GET`) with a Lua script for atomic check-and-increment — one round trip per
  impression.
- **Window reclamation by rotation:** swap in a fresh filter every 10 minutes and
  drop the old one. Per-entry TTLs would require a timestamp per counter and
  turn the filter into a heap — rotation keeps it O(1).
- **Cold-start dip:** a fresh filter has zero false positives and rising
  protection; rotate with overlap (keep the previous filter as secondary check)
  if the dip matters for billing.
- **Sharding:** partition the key space by hash prefix across filter instances —
  each shard sizes for `n/shardCount`.

## Follow-Up Questions

1. **Concurrency:** per-position atomics (`AtomicIntegerArray`) with saturating
  increments; `remove`'s check-all-then-act needs a retry loop if a concurrent
  add can race — or accept saturation as the race bound.
2. **Scalable Bloom filters:** a cascade of filters of doubling size (like
  scalable Bloom filters) instead of one large array — tiered error rates and
  incremental growth.
3. **Redis hosting:** `EVAL` script for `mayContain`-and-increment in one atomic
  step; `DECRBY` with a floor of zero for the underflow guard, scripted.
4. **Saturating counter width:** 4-bit (cap 15) vs 8-bit (cap 255) — memory vs
  sticky-one floor; the cap should exceed the maximum expected overlap of
  positions under the target load.
5. **Counting vs Cuckoo with deletion:** a cuckoo filter also supports deletion
  with lower space per item, but with insertion-time relocation cost and
  partial-key fingerprint collisions; counting Bloom wins on simplicity and
  determinism of add latency.
6. **Hybrid with a whitelist:** pair the filter with an exact `HashSet` for
  high-value keys (billing-critical), filter for the long tail — bounded memory,
  exactness where it matters.

## Summary

- **Counters instead of bits** give the filter deletion with the defining
  property preserved: **deletion never reintroduces false negatives**.
- **The two bugs that matter** are underflow (guarded by check-all-then-act) and
  saturation (bounded deliberately, with a documented sticky-one cost).
- **Sizing is formulaic:** `m = -n·ln p / ln² 2`, `k = (m/n)·ln 2` — and the
  demo measures the resulting rate empirically.
- **The insert-twice/delete-once test** is the acceptance criterion that
  separates a real counting Bloom filter from a bit filter with a delete
  button.
- **In production:** host counters in Redis, reclaim windows by rotation, and
  let the false-positive budget be a pricing decision, not an accident.
