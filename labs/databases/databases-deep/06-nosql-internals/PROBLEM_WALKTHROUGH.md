# Lab 06: Problem Walkthrough — LSM-Tree Storage Engine with Compaction

## Problem Statement

**Title**: LsmStorageEngine — Memtable, SSTables, and Merging Compaction

**Difficulty**: Hard

**Category**: Databases, NoSQL, Storage Engines

---

### Problem

Implement an LSM-tree storage engine with:

1. **`MemTable`** — an in-memory sorted map of `(key, seq, value)` with tombstones (delete markers)
2. **`SSTable`** — an immutable sorted run: `(keys[], values[], seqs[])` plus a minimum key for range checks
3. **`LsmStorageEngine`**:
   - `put(key, value)` — append to WAL (simulated), insert into memtable; auto-flush when full
   - `delete(key)` — insert a tombstone
   - `get(key)` — search memtable → newest SSTable → oldest SSTable (newest wins)
   - `flush()` — freeze memtable, write SSTable
   - `compact()` — merge all SSTables into one sorted run, dropping superseded versions and tombstones whose data is below them
4. Statistics: `stats()` reporting memtable size, SSTable count, and **records before/after compaction**
5. A `main` demo: put/update/delete lifecycle, flush, compaction reducing record count, and a resurrect-delete scenario.

### Constraints

- Keys are `String`s, values are `String`s; sequence numbers are `long` and strictly increase
- Tombstones are represented as `null` values with a flag
- In-memory only (WAL durability simulated by an append list)
- Java 21+ standard library only

### Examples

**Example 1 (read path newest-wins):**
```
put("k", "v1")   (seq 1)
put("k", "v2")   (seq 2)
flush()          → SSTable [k@seq2]
get("k") == "v2"
```

**Example 2 (tombstone):**
```
put("k", "v1"); flush(); delete("k"); flush()
get("k") == null   (tombstone wins over the older value)
```

**Example 3 (compaction):**
```
put("a","1"); put("b","1"); flush(); put("a","2"); delete("b"); flush()
before compact: 4 records (2 versions of a + b + tombstone)
compact():
  - a resolved to seq-2 version -> 1 record
  - b: tombstone merges with value -> dropped entirely
after compact: 1 record ("a" → "2")
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

An LSM engine is a *layered* structure:

```
  ┌─────────────┐
  │   WAL       │  append-only, durable
  ├─────────────┤
  │  MemTable   │  in-memory sorted map (mutable)
  ├─────────────┤
  │  SSTables   │  immutable sorted runs (0..N)
  └─────────────┘
```

**Write**: memtable insert (O(log M)), async flush.
**Read**: newest data wins — memtable first, then SSTables from newest to oldest.
**Compaction**: merge runs, keep newest version per key, resolve tombstones.

The key invariant: *within one SSTable and across the pipeline, the same key may appear multiple times; the one with the highest seq wins*. Nothing is overwritten until compaction — that's the write-path win.

### Step 2: Naive Approach and Why It Fails

**Naive approach — in-place map:**
```java
Map<String, String> store = new HashMap<>();
```
- Every update is a random overwrite — no batching, no sequential I/O, and a delete is irreversible (no tombstones → can't distinguish "never existed" from "deleted, need to tell a replica").
- No snapshotting: can't freeze and merge versions.

**Naive LSM without tombstones**: delete = remove key. Then compaction of a stale file could resurrect it (the classic bug). Tombstones exist precisely to prevent resurrection: a delete must survive until compaction has merged every older version.

### Step 3: Design Decisions

1. **Record model**: `(key, seq, value)` where `value == null` means tombstone. A read returns `null` only after seeing a tombstone *at a higher seq than any value*.
2. **SSTable as flat arrays** (sorted by key, then seq) — simple, and merge-able with a k-way merge. Real engines add index blocks + bloom filters; we note them in follow-ups.
3. **Compaction = merge with newest-seq-wins**: walk all runs with a heap of iterators; per key, keep only the highest seq; a tombstone drops the key entirely if it's the highest seq for that key (its older values die with it).
4. **Flush threshold**: `put` triggers `flush()` when memtable size ≥ threshold, keeping the demo deterministic.

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab06;

import java.util.*;

/**
 * LsmStorageEngine — a compact LSM tree.
 *
 * Writes batch in a memtable; flush creates immutable SSTables;
 * compaction merges runs newest-seq-wins and resolves tombstones.
 */
public class NoSqlInternalsLab {

    record Entry(String key, long seq, String value) {
        boolean isTombstone() { return value == null; }
    }

    /** Immutable sorted run: sorted by key, then seq ascending. */
    static final class SSTable {
        final long id;
        final List<Entry> entries;

        SSTable(long id, List<Entry> entries) {
            this.id = id;
            this.entries = List.copyOf(entries);
        }

        int size() { return entries.size(); }
    }

    static final class LsmStorageEngine {
        private final TreeMap<String, Entry> memtable = new TreeMap<>();
        private final List<SSTable> sstables = new ArrayList<>();
        private final List<Entry> wal = new ArrayList<>();    // simulated WAL
        private long seq = 0;
        private long nextTableId = 1;
        private final int memtableLimit;

        LsmStorageEngine(int memtableLimit) { this.memtableLimit = memtableLimit; }

        void put(String key, String value) {
            append(new Entry(key, ++seq, value));
        }

        void delete(String key) {
            append(new Entry(key, ++seq, null));    // tombstone
        }

        private void append(Entry e) {
            wal.add(e);                              // durable append (fsync here)
            memtable.put(e.key(), e);                // memtable upsert
            if (memtable.size() >= memtableLimit) flush();
        }

        /** Freeze the memtable into an immutable SSTable. */
        void flush() {
            if (memtable.isEmpty()) return;
            sstables.add(0, new SSTable(nextTableId++,
                    new ArrayList<>(memtable.values())));
            memtable.clear();
        }

        /** Newest-wins read across memtable + SSTables (newest file first). */
        String get(String key) {
            Entry best = memtable.get(key);
            for (SSTable t : sstables) {              // list is newest-first
                if (best != null && best.seq() > tailSeq(t)) continue;
                for (Entry e : t.entries) {
                    if (e.key().compareTo(key) > 0) break;
                    if (e.key().equals(key) && (best == null || e.seq() > best.seq())) {
                        best = e;
                    }
                }
            }
            return (best == null || best.isTombstone()) ? null : best.value();
        }

        private long tailSeq(SSTable t) {
            return t.entries.isEmpty() ? 0 : t.entries.get(t.entries.size() - 1).seq();
        }

        /**
         * Merge all SSTables (and memtable) into one run.
         * Per key: keep the highest seq. Tombstone + older value => dropped.
         */
        void compact() {
            flush();                                   // include any hot data
            List<Entry> merged = new ArrayList<>();
            Map<String, Entry> bestByKey = new TreeMap<>();
            for (SSTable t : sstables) {
                for (Entry e : t.entries) {
                    Entry prev = bestByKey.get(e.key());
                    if (prev == null || e.seq() > prev.seq()) bestByKey.put(e.key(), e);
                }
            }
            for (var e : bestByKey.values()) {
                if (!e.isTombstone()) merged.add(e);   // tombstones die in compaction
            }
            sstables.clear();
            sstables.add(new SSTable(nextTableId++, merged));
        }

        long totalRecords() {
            long n = memtable.size();
            for (SSTable t : sstables) n += t.size();
            return n;
        }

        int tableCount() { return sstables.size(); }

        String stats() {
            return "memtable=" + memtable.size()
                    + " sstables=" + sstables.size()
                    + " total=" + totalRecords();
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var engine = new LsmStorageEngine(3);          // flush every 3 keys

        // Example 1: newest wins across flush boundary
        engine.put("k", "v1");
        engine.put("k", "v2");
        engine.flush();
        System.out.println("get(k) = " + engine.get("k") + " (expect v2)");

        // Example 2: tombstone overrides older value
        engine.put("a", "1");
        engine.flush();
        engine.delete("a");
        engine.flush();
        System.out.println("get(a) after delete = " + engine.get("a") + " (expect null)");

        // Example 3: compaction removes superseded versions and tombstones
        var e2 = new LsmStorageEngine(10);
        e2.put("a", "1");
        e2.put("b", "1");
        e2.flush();
        e2.put("a", "2");
        e2.delete("b");
        e2.flush();
        System.out.println("before compact: " + e2.stats());      // 4 records
        e2.compact();
        System.out.println("after  compact: " + e2.stats());      // 1 record
        System.out.println("  get(a) = " + e2.get("a") + " (expect 2)");
        System.out.println("  get(b) = " + e2.get("b") + " (expect null)");

        // Resurrect-delete scenario: tombstone in OLD file, value in NEW file.
        // Correct engine must see the new value (newer seq wins).
        var e3 = new LsmStorageEngine(100);
        e3.put("x", "v0");
        e3.delete("x");           // seq 2 tombstone
        e3.flush();               // file: [x@1=v0, x@2=tombstone]
        e3.put("x", "v1");        // seq 3 — write AFTER the delete: newer
        e3.flush();
        System.out.println("resurrect check: get(x) = " + e3.get("x")
                + " (expect v1 — later write beats old tombstone)");
    }
}
```

### Step 5: Walk the Examples

**Example 1**: `put("k","v1")` seq 1, `put("k","v2")` seq 2 — the memtable holds only the newest (TreeMap upsert keeps the Entry with seq 2). Flush writes `[k@2=v2]`. `get` finds it → "v2". Even before flush, the memtable upsert already implements newest-wins for the mutable layer.

**Example 2**: `put("a","1")` seq 1 → flush → SSTable `[a@1]`. `delete("a")` seq 2 → memtable `[a@2=tombstone]` → flush → SSTable `[a@2=tombstone]` (newer file). `get("a")` searches memtable (empty), then newest file — finds tombstone seq 2 > seq 1 → returns null. **The tombstone must outlive the value until compaction merges them.**

**Example 3**: SSTable 1: `[a@1=1, b@1=1]`; SSTable 2: `[a@2=2, b@2=tombstone]`. `compact()` merges per key, keeping highest seq: `a → a@2=2`, `b → b@2=tombstone` (dropped — the value is gone, nothing to resurrect). Result: 1 record. Both `get("a")` = "2" and `get("b")` = null remain correct *after* the compaction — data reduced without changing answers.

**Resurrect scenario**: file `[x@1=v0, x@2=tomb]`, then a *new* write `x@3=v1` after the delete. Read: x@3 (memtable) beats the tombstone — "v1". The tombstone only kills versions *older than itself*. This is the exact semantics real engines implement (delete wins against data written before the delete; later writes win against the tombstone).

### Step 6: Compile & Run

```bash
javac --release 21 NoSqlInternalsLab.java
java com.databases.deep.lab06.NoSqlInternalsLab
```

Expected output:

```
get(k) = v2 (expect v2)
get(a) after delete = null (expect null)
before compact: memtable=0 sstables=2 total=4
after  compact: memtable=0 sstables=1 total=1
  get(a) = 2 (expect 2)
  get(b) = null (expect null)
resurrect check: get(x) = v1 (expect v1 — later write beats old tombstone)
```

---

## Complexity Analysis

- **put/delete**: O(log M) memtable insert (M = memtable keys) + O(1) WAL append.
- **get**: O(log M) memtable lookup, then O(T·S) worst case scanning T SSTables with S entries each (with bloom filters + level disjointness it drops to O(T) probes with O(S·log) search — see follow-ups).
- **flush**: O(M log M) — memtable is already sorted (TreeMap), so O(M) materialization.
- **compact**: O(K log T) with a T-way merge (K = total records) — our simple version is O(K) per key via a map, plus O(K) output.
- **Space**: memtable O(M), SSTables O(total live + tombstoned records), WAL O(total writes).

## Edge Cases & Failure Handling

1. **Empty flush** — guarded: `flush()` returns early on an empty memtable.
2. **Tombstone at highest seq in compaction** — dropped with its value; never resurrects because no older file remains (they were all merged in the same pass).
3. **Write after tombstone** — new seq beats the tombstone (resurrect scenario covered in the demo).
4. **Same key across many files** — read scans all files; compact() collapses to one entry — the demonstration of why background compaction bounds read amplification.
5. **Memtable overflow mid-batch** — flush triggers mid-`append`; subsequent puts start a fresh memtable; ordering is preserved by seq.
6. **WAL durability (simulated)** — the `wal` list plays the role of the fsynced log; a real engine replays the WAL into the memtable on startup — see follow-up 5.

## Follow-up Questions

1. **Bloom filters**: give each SSTable a bitset of its keys (`add` on flush, `mightContain` on read) — cuts file probes for point reads to ~0 in the common case.
2. **Leveled compaction**: split `sstables` into `List<List<SSTable>>` levels with a 10x size ratio; compact level L into L+1 — the read amplification bound becomes one file per level.
3. **WAL replay**: on engine startup, re-apply `wal` into the memtable (and discard flushed-but-uncompacted records via seq checkpoints) — durability without data loss.
4. **Merge iterators**: implement `Iterator<Entry> iterator()` as a k-way merge (priority queue of per-file iterators) so range scans `rangeScan(low, high)` work across runs in sorted order.
5. **Snapshot reads**: `snapshot()` captures `seq`; `getAt(key, snapshotSeq)` ignores records with seq > snapshotSeq — MVCC for consistent backups.
6. **Write amplification metric**: track `bytesWritten` (flush + compaction output) vs `bytesAccepted` (put payload) and expose WAF — then verify leveled vs size-tiered strategies.
7. **Property test**: random put/delete/flush/compact sequences; invariant — `get` returns the same value before and after any `compact()`, and after any flush without intervening writes.

## References

- O'Neil et al., "The Log-Structured Merge-Tree (LSM-Tree)" (1996)
- RocksDB wiki: "LSM", "Compaction", "Bloom Filter", "DeleteRange"
- Cassandra docs: "How are SSTables structured?", STCS vs LCS vs TWCS
- Dong et al., "Optimal Memory Management in LSM" / "From WiscKey to Bourbon" (FAST) for modern depth
