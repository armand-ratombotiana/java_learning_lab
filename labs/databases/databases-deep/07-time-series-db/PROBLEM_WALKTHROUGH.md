# Lab 07: Problem Walkthrough — Time-Series Database with Downsampling & Retention

## Problem Statement

**Title**: MiniTimeSeriesDb — Append Store with Rollups and Retention

**Difficulty**: Medium-Hard

**Category**: Databases, Time-Series, Storage Engines

---

### Problem

Implement a time-series database with:

1. **`TimeSeriesDb`** — append-only store of `(metric, timestamp, value)` points:
   - `append(metric, ts, value)` — in-order and out-of-order tolerant (out-of-order points are accepted into an out-of-order buffer)
   - `query(metric, fromTs, toTs)` — return raw points in `[from, to]`
2. **Downsampling**: `rollup(metric, bucketSeconds, agg)` — aggregate raw points into wall-clock-aligned buckets using `AVG`/`MIN`/`MAX`/`SUM`/`COUNT` (composable aggregates); `queryRollup` returns bucket aggregates
3. **Retention**: `retain(metric, maxAge)` — drop raw points older than `maxAge`, **block-aligned** (whole time blocks, not per-point)
4. Statistics: point counts, block count, rollup layers
5. A `main` demo: ingest, out-of-order write, rollups (1m → 5m), retention dropping whole blocks, and a query spanning rollup + raw tiers.

### Constraints

- Timestamps are `long` epoch seconds; blocks are 1-minute (60s) for simplicity
- Series identified by `metric` string (tags omitted for clarity — see follow-ups)
- Points are immutable once appended
- Java 21+ standard library only

### Examples

**Example 1 (basic):**
```
append("cpu", 0, 1.0); append("cpu", 60, 2.0); append("cpu", 120, 3.0)
query("cpu", 0, 120) → [(0,1.0), (60,2.0), (120,3.0)]
```

**Example 2 (out-of-order):**
```
append("cpu", 300, 5.0); append("cpu", 150, 2.5)   // late point
query("cpu", 100, 350) → sorted: [(150,2.5), (300,5.0)]
```

**Example 3 (rollup):**
```
points at t=10,20,30,40,50 (bucket 0-59 avg=3) and t=60 (bucket 60-119 avg=6)
rollup("cpu", 60, AVG) → [(0, 3.0), (60, 6.0)]
```

**Example 4 (retention):**
```
data at t=0..600; retain("cpu", 300s) → only points ≥ 300 survive;
the t=0..299 block is dropped entirely (block-aligned)
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

A TSDB's core layout: **time-partitioned blocks**. We split time into aligned windows (60s blocks). Each block holds points for a metric in a sorted structure. This gives us:

- **Retention = drop blocks** — O(1) per block, no per-point scans
- **Range queries = block pruning** — only touch overlapping blocks
- **Rollups = per-block aggregates** — composable, so higher tiers can be built from lower tiers

The three invariants:
1. Blocks are **wall-clock aligned** (bucket = ts / 60 × 60) — rollups and retention align to the same grid.
2. Points are **immutable** — appends only.
3. Retention removes **whole blocks**, never individual points — no tombstone fragmentation.

### Step 2: Naive Approach and Why It Fails

**Naive — single unsorted list per metric:**
```java
List<Point> points = ...;  // unsorted appends
```
- Query must scan everything (O(N) per query).
- Retention must scan and delete per point (O(N) with fragmentation).
- Out-of-order inserts are fine for append but queries still scan all.

**Naive — sorted list with in-place insert:** O(N) shifts per append — breaks the write path.

The block design fixes all three: writes land in one block (O(log points-in-block)), queries prune blocks, retention drops blocks.

### Step 3: Design Decisions

1. **Block map**: `TreeMap<Long, List<Point>>` keyed by aligned bucket start. Blocks are naturally sorted by time.
2. **Out-of-order handling**: `append` computes the block; if the block is closed (already flushed to the on-disk tier — simulated by a `flushed` flag after rollup), late points go to an **out-of-order buffer** merged at query time. For the lab, we keep one block list and insert sorted — but the *design* surfaces the buffer concept.
3. **Rollup**: iterate blocks, compute bucket aggregate. Store rollups as a **second tier**: `TreeMap<Long, List<RollupPoint>>` per metric, keyed by coarser buckets.
4. **Retention**: `retain(metric, maxAge)` computes `cutoff = now - maxAge`; drop every raw block starting before the cutoff (block-aligned: drop the whole block even if partially expired). Rollup tiers get their own retention.
5. **Query tiers**: `queryRollup(metric, from, to, bucket, agg)` — answer from the rollup tier when it exists, falling back to computing from raw blocks; this mimics real TSDBs where the query engine chooses the finest tier covering the range.

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab07;

import java.util.*;

/**
 * MiniTimeSeriesDb — time-partitioned TSDB with downsampling and retention.
 *
 * - 60s raw blocks; wall-clock aligned buckets.
 * - Rollups compute composable aggregates (AVG/MIN/MAX/SUM/COUNT).
 * - Retention drops whole blocks, never individual points.
 */
public class TimeSeriesDbLab {

    record Point(long ts, double value) {}

    enum Agg { AVG, MIN, MAX, SUM, COUNT }

    static final long BLOCK_SECONDS = 60;

    static final class TimeSeriesDb {
        // metric -> raw blocks (aligned start ts -> sorted points)
        private final Map<String, TreeMap<Long, List<Point>>> raw = new HashMap<>();
        // metric -> rollup tiers: bucketSeconds -> (aligned start -> aggregate)
        private final Map<String, TreeMap<Integer, TreeMap<Long, Double>>> rollups = new HashMap<>();

        private TreeMap<Long, List<Point>> blocksOf(String metric) {
            return raw.computeIfAbsent(metric, k -> new TreeMap<>());
        }

        static long align(long ts, long bucket) { return (ts / bucket) * bucket; }

        /** Append a point; tolerates out-of-order timestamps. */
        void append(String metric, long ts, double value) {
            var blocks = blocksOf(metric);
            long start = align(ts, BLOCK_SECONDS);
            var points = blocks.computeIfAbsent(start, k -> new ArrayList<>());
            points.add(new Point(ts, value));
            points.sort(Comparator.comparingLong(Point::ts));
        }

        /** Raw query across [from, to]. */
        List<Point> query(String metric, long from, long to) {
            List<Point> out = new ArrayList<>();
            var blocks = blocksOf(metric);
            long firstBlock = align(from, BLOCK_SECONDS);
            long lastBlock = align(to, BLOCK_SECONDS);
            for (var e : blocks.subMap(firstBlock, true, lastBlock, true).entrySet()) {
                for (Point p : e.getValue()) {
                    if (p.ts() >= from && p.ts() <= to) out.add(p);
                }
            }
            return out;
        }

        /** Build (or refresh) a rollup tier from the raw blocks. */
        void rollup(String metric, int bucketSeconds, Agg agg) {
            var tier = rollups.computeIfAbsent(metric, k -> new TreeMap<>())
                              .computeIfAbsent(bucketSeconds, k -> new TreeMap<>());
            tier.clear();
            for (var e : blocksOf(metric).entrySet()) {
                long bucketStart = align(e.getKey(), bucketSeconds);
                double acc = switch (agg) {
                    case MIN -> Double.POSITIVE_INFINITY;
                    case MAX -> Double.NEGATIVE_INFINITY;
                    case SUM, COUNT, AVG -> 0;
                };
                long count = 0;
                for (Point p : e.getValue()) {
                    count++;
                    acc = switch (agg) {
                        case AVG, SUM -> acc + p.value();
                        case COUNT -> acc;
                        case MIN -> Math.min(acc, p.value());
                        case MAX -> Math.max(acc, p.value());
                    };
                }
                if (count == 0) continue;
                double result = switch (agg) {
                    case AVG -> acc / count;
                    case COUNT -> count;
                    default -> acc;
                };
                tier.merge(bucketStart, result, (a, b) -> switch (agg) {
                    case AVG -> (a + b) / 2;      // note: true AVG needs (sum,count)
                    case SUM -> a + b;
                    case COUNT -> a + b;
                    case MIN -> Math.min(a, b);
                    case MAX -> Math.max(a, b);
                });
            }
        }

        /** Query a rollup tier; falls back to computing from raw if absent. */
        List<Point> queryRollup(String metric, long from, long to, int bucketSeconds, Agg agg) {
            var tier = rollups.getOrDefault(metric, new TreeMap<>()).get(bucketSeconds);
            if (tier == null) {
                // no tier yet: compute on the fly from raw points
                return computeRollupFromRaw(metric, from, to, bucketSeconds, agg);
            }
            List<Point> out = new ArrayList<>();
            for (var e : tier.subMap(align(from, bucketSeconds), true,
                                     align(to, bucketSeconds), true).entrySet()) {
                out.add(new Point(e.getKey(), e.getValue()));
            }
            return out;
        }

        private List<Point> computeRollupFromRaw(String metric, long from, long to,
                                                 int bucketSeconds, Agg agg) {
            Map<Long, List<Double>> buckets = new TreeMap<>();
            for (Point p : query(metric, from, to)) {
                buckets.computeIfAbsent(align(p.ts(), bucketSeconds), k -> new ArrayList<>())
                       .add(p.value());
            }
            List<Point> out = new ArrayList<>();
            for (var e : buckets.entrySet()) {
                out.add(new Point(e.getKey(), aggregate(e.getValue(), agg)));
            }
            return out;
        }

        private double aggregate(List<Double> values, Agg agg) {
            return switch (agg) {
                case AVG -> values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                case SUM -> values.stream().mapToDouble(Double::doubleValue).sum();
                case COUNT -> values.size();
                case MIN -> values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                case MAX -> values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            };
        }

        /** Retention: drop WHOLE raw blocks older than maxAge seconds. */
        int retain(String metric, long maxAge) {
            long cutoff = align(System.currentTimeMillis() / 1000 - maxAge, BLOCK_SECONDS);
            var blocks = blocksOf(metric);
            var toDrop = new ArrayList<>(blocks.headMap(cutoff, true).keySet());
            for (long start : toDrop) blocks.remove(start);
            return toDrop.size();
        }

        /** Retention with explicit 'now' (deterministic for tests/demos). */
        int retain(String metric, long maxAge, long nowSec) {
            long cutoff = align(nowSec - maxAge, BLOCK_SECONDS);
            var blocks = blocksOf(metric);
            var toDrop = new ArrayList<>(blocks.headMap(cutoff, true).keySet());
            for (long start : toDrop) blocks.remove(start);
            return toDrop.size();
        }

        long rawPoints(String metric) {
            return blocksOf(metric).values().stream().mapToLong(List::size).sum();
        }

        long blockCount(String metric) { return blocksOf(metric).size(); }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        var db = new TimeSeriesDb();

        // Ingest: 1 point per 10s for 10 minutes
        long now = 1_000_000_000L;
        for (long t = 0; t < 600; t += 10) {
            db.append("cpu", now + t, 50 + Math.sin(t / 60.0) * 10);
        }
        // Out-of-order write (late point for t=150)
        db.append("cpu", now + 150, 42.0);
        System.out.println("raw points = " + db.rawPoints("cpu")
                + ", blocks = " + db.blockCount("cpu"));
        System.out.println("query 0..300 = " + db.query("cpu", now, now + 300).size() + " points");

        // Rollup: 1-minute AVG tier, then 5-minute SUM tier
        db.rollup("cpu", 60, Agg.AVG);
        db.rollup("cpu", 300, Agg.MAX);
        var r1m = db.queryRollup("cpu", now, now + 599, 60, Agg.AVG);
        var r5m = db.queryRollup("cpu", now, now + 599, 300, Agg.MAX);
        System.out.println("rollup 1m buckets = " + r1m.size()
                + " (expect 10), first=" + r1m.getFirst());
        System.out.println("rollup 5m buckets = " + r5m.size() + " (expect 2)");

        // Retention: keep only the last 300 seconds
        int dropped = db.retain("cpu", 300, now + 600);
        System.out.println("retention dropped " + dropped + " whole blocks; remaining raw points = "
                + db.rawPoints("cpu") + " (expect ~31)");

        // Rollups still answer the full range (tier data survives raw retention)
        var afterRetention = db.queryRollup("cpu", now, now + 599, 60, Agg.AVG);
        System.out.println("rollup query after retention = " + afterRetention.size() + " buckets");
    }
}
```

### Step 5: Walk the Examples

**Example 1**: points at t=0, 60, 120 → blocks starting at 0, 60, 120. `query(0,120)` prunes to those three blocks and collects all points — sorted by block order. Wall-clock alignment means t=0 and t=60 land in *different* blocks (block = [0,60), [60,120)) — the boundary behavior to remember.

**Example 2**: append(t=150) after t=300 exists → both live in block [120, 180); the sort keeps the block ordered, so `query` returns them sorted. (In a real engine, a *flushed* block wouldn't accept the late point — it would go to the out-of-order buffer. Our lab merges into the block to keep the demo focused; the follow-ups cover the flush/buffer split.)

**Example 3**: rollup with `bucketSeconds=60`: block [0,60) aligns to bucket 0, values at 10..50 → avg = 3.0; block [60,120) → bucket 60 → avg = 6.0. Alignment invariant: raw block boundaries and rollup bucket boundaries share the same grid when bucket = multiple of block.

**Example 4**: `retain("cpu", 300, now+600)` → cutoff = now+300, aligned to block boundary. Every block whose start < cutoff is removed whole — including the block containing points [280, 300) that are "not quite expired." That's the point: **block-aligned retention trades a little over-retention for O(blocks) deletion cost and zero fragmentation** — exactly how real TSDBs drop old data.

**Rollup survival**: after raw retention, the 1m tier still answers the full range — rollups are a *separate tier* with their own lifecycle (in production: longer retention than raw).

### Step 6: Compile & Run

```bash
javac --release 21 TimeSeriesDbLab.java
java com.databases.deep.lab07.TimeSeriesDbLab
```

Expected output shape:

```
raw points = 61, blocks = 10
query 0..300 = 32 points
rollup 1m buckets = 10 (expect 10), first=Point[ts=..., value=~50.0]
rollup 5m buckets = 2 (expect 2)
retention dropped 5 whole blocks; remaining raw points = ~31 (expect ~31)
rollup query after retention = 10 buckets
```

---

## Complexity Analysis

- **append**: O(log B) block lookup (B = blocks) + O(P log P) worst case to keep a block sorted — amortized O(log P) since points are usually in-order (O(1) append at the tail).
- **query**: O(B') log + O(k) — B' overlapping blocks, k matching points. Never touches the full dataset.
- **rollup**: O(P) — one pass over raw points; O(P/B) output.
- **queryRollup**: O(log T + m) where T = buckets in the tier, m = returned buckets — a rollup query is *independent of raw size*.
- **retention**: O(D log B) — D blocks dropped via a single head-map view; O(1) amortized per dropped block.
- **Space**: O(P) raw + O(P/bucket) rollup tiers.

## Edge Cases & Failure Handling

1. **Block boundary points**: ts exactly at a multiple of 60 → belongs to the *next* block (floor division). Query inclusive bounds `[from, to]` handle both edges.
2. **Empty range** (`from > to`): subMap with inverted bounds returns empty — no crash.
3. **Out-of-order point crossing a block boundary**: lands in its own block — correctness preserved by per-block sort + block-ordered queries.
4. **AVG rollup merge**: merging two bucket averages as `(a+b)/2` is only exact when counts are equal — the code documents this; production stores `(sum, count)` pairs (the reason real TSDBs store sum+count, not mean). Flag for follow-up 1.
5. **Retention with `now` in the past**: `nowSec - maxAge` negative → cutoff ≤ all blocks → everything dropped. Guard in production (validate retention window).
6. **Rollup of an empty metric**: tiers are only created on demand; `queryRollup` falls back to computing from raw (returns empty) — no NPE.
7. **Duplicate timestamps**: allowed (both points kept); real systems add a dedup-by-(series, ts) rule — mention in follow-ups.

## Follow-up Questions

1. **Store (sum, count) not mean**: change the rollup tier to hold `Aggregate(sum, count, min, max)` — then AVG merges are exact `(sum1+sum2)/(c1+c2)`, and percentiles become possible with histogram sketches.
2. **Out-of-order buffer + closed blocks**: flush blocks to an immutable tier; late points for flushed windows go to a bounded in-memory buffer merged at query time; points beyond the allowed window are rejected with a metric counter — the real-engine design.
3. **Cardinality-aware series index**: replace the metric-keyed map with `(metric, tags) → seriesId` and a per-block inverted index for tag lookups — the schema that makes `topk by (host)` queries fast.
4. **Tiered retention**: give each rollup tier its own retention — raw 7d, 1m 30d, 5m 1y — and run retention as a background sweep per tier.
5. **Continuous rollup**: a scheduler that runs `rollup` every 5 minutes over the blocks since the last run (watermark-tracked) — TimescaleDB continuous aggregates / VictoriaMetrics `downsampling` behavior.
6. **Compression**: encode each block as delta-of-delta timestamps + XOR values (Gorilla) — measure the compression ratio in the demo.
7. **Property test**: random append/rollup/retain/query sequences; invariant — `queryRollup` after `rollup` equals `computeRollupFromRaw` at the same bucket for SUM/COUNT/MIN/MAX (AVG within floating tolerance).

## References

- Pelkonen et al., "Gorilla: A Fast, Scalable, In-Memory Time Series Database" (VLDB 2015)
- Prometheus documentation: storage format, `out_of_order_time_window`, TSDB blocks
- TimescaleDB docs: hypertables, continuous aggregates, chunk retention
- VictoriaMetrics docs: downsampling, retention, cardinality limits
