# Lab 07: Mock Interview — Time-Series Databases

**Role**: Senior Database Engineer / Systems Engineer
**Duration**: 45 minutes
**Company style**: FAANG / observability vendor (InfluxDB, Prometheus, TimescaleDB, VictoriaMetrics, Druid)

---

**Interviewer**: "What makes time-series data different from regular relational data — why does it deserve its own database category?"

**Candidate**: "Four properties distinguish it. (1) **Append-mostly with time-ordering**: writes arrive roughly in timestamp order; in-place updates are rare, which is why LSM-style or columnar layouts fit. (2) **Immutable, versioned data**: once written, a point is rarely changed — you *insert*, then *aggregate*, then *delete by retention*; the workload is never read-modify-write. (3) **Time as the primary axis**: every query is a time-range filter plus a grouping by time bucket — so the storage layout should cluster by time and the query engine should be optimized for range scans over intervals. (4) **Cardinality and series**: data is organized into *series* — unique (metric, tag-set) combinations; the number of series, not rows, drives memory costs, and high-cardinality tag sets are the classic killer. OLTP engines optimize for random access by key; time-series engines optimize for sequential access by time — completely different physical design."

**Interviewer**: "Let's talk about the storage layout. How do TSDBs store points efficiently?"

**Candidate**: "The canonical answer is **time-ordered columnar or delta-encoded blocks**. Prometheus and InfluxDB both chunk by time: a *block* or *shard* covers a time window, and within it, data is grouped per series. Two big wins: (1) **delta/delta-of-delta encoding** for timestamps — consecutive timestamps in a series compress to a few bits each (Gorilla encoding); (2) **XOR compression** for float values, since successive samples change slowly. This is the Gorilla paper's insight — time-series is compressible because it's *smooth*. Raw points shrink by 10-20x. On top of that, each series block gets a bloom-filtered or sorted index for tag lookup. InfluxDB's TSM files, Prometheus's chunk format, and VictoriaMetrics' blocks all follow this shape."

**Interviewer**: "What is downsampling, and why is it essential? Walk me through the design decisions."

**Candidate**: "Downsampling (rollups) precomputes aggregates over coarser time buckets — e.g., keep raw data for 7 days, 1-minute rollups for 30 days, 1-hour rollups for a year. It's essential because storage cost and query latency both scale with the number of points, and nobody needs 1-second resolution on a 2-year-old graph. The design decisions: (1) **aggregate functions** — you must store *summaries that compose*: min/max/mean/count are composable, percentiles (p95) are NOT composable from lower-resolution samples — that's why TSDBs store histograms (DDSketch, t-digest, or fixed buckets) for quantiles. (2) **window alignment** — buckets must be aligned to wall-clock boundaries (e.g., :00/:15/:30/:45) or rollups drift. (3) **when** — real-time continuous rollups (VictoriaMetrics' `downsampling`, TimescaleDB continuous aggregates) vs batch (Druid's compaction). (4) **retention layering** — each resolution tier has its own retention; the coarser the tier, the longer it lives."

**Interviewer**: "Retention policies — how do they work at the storage level, and what's the 'right' way to delete old data?"

**Candidate**: "Retention is a *drop by time range*, and the right way is to make old data cheap to drop. The layout must be time-partitioned — separate blocks per time window — so retention = delete whole blocks, a metadata operation, not a row-by-row DELETE. If retention deletes within a block, you get tombstone storms and fragmentation; that's why TTL-based partition dropping is a first-class feature in InfluxDB (`RETENTION POLICY`), TimescaleDB (hypertable chunks), and Cassandra TWCS (drop whole SSTables). Design decisions: (1) retention granularity matches the partition size; (2) multiple tiers have multiple retentions; (3) deletion is a background job — never synchronous in the write path; (4) there must be a manual 'purge now' for GDPR-style requests and a dry-run audit."

**Interviewer**: "Out-of-order writes: your pipeline has a 5-minute network blip and then delivers delayed points. How does the engine handle that?"

**Candidate**: "Out-of-order (late) writes are the case that separates TSDBs from naive append logs. If you've already flushed a time block to disk, a late point for that window must go somewhere: InfluxDB's approach is a separate in-memory buffer for late points and block rewriting when they flush; Prometheus historically *discards* points older than the head chunk by default (`out_of_order_time_window` now supports a window); VictoriaMetrics keeps an in-memory out-of-order merge. The engineering tradeoff: late-write support costs read-time merging and write amplification. My design rule: bound the late window (e.g., accept points ≤ 24h late), buffer them in memory, merge into the affected block on flush; beyond the window — reject with a clear error or route to a dead-letter path, and surface the counter loudly."

**Interviewer**: "High cardinality is the classic TSDB failure mode. What exactly breaks, and what do you do?"

**Candidate**: "Cardinality = number of distinct series. A naive implementation keeps an in-memory map from (metric, tag-set) to series ID — with 10 million unique tag combinations, that map eats gigabytes and GC pressure kills the process; Prometheus historically OOM'd at ~10M series. Writes to unseen series allocate new entries; queries over high-cardinality tags scan huge numbers of series. Mitigations: (1) **series ID hashing + block-local indexes** (each block indexes only its own series, and old blocks drop out with retention); (2) **separate the hot and cold maps** — recent series in memory, older in per-block files; (3) **tag cardinality limits** (drop or reject the highest-cardinality labels, e.g., `cardinality.limit` in VictoriaMetrics); (4) **design-time control**: ban unbounded labels (user_id, request_id) on metric series — move them to logs; keep metrics dimensional but bounded (host, region, status). The interview answer: measure cardinality per metric first (`topk` cardinality queries), cap it, and redesign labels that blow past it."

**Interviewer**: "How do you query it? What's the shape of a time-series query engine — e.g., what happens for 'average CPU by host over the last hour, 1-minute buckets'?"

**Candidate**: "A pipeline of four stages. (1) **Time-range prune**: the query planner drops blocks outside [now-1h, now] — with hourly blocks that's 1-2 blocks touched. (2) **Series selection**: resolve the metric + tag matchers to a set of series IDs via the index. (3) **Vector scan**: for each series, decode the chunk (Gorilla decompress), apply the selector (`cpu`), and bucket by 1-minute windows — the decode is the hot loop, so vectorized, SIMD-friendly code matters (that's why VictoriaMetrics and ClickHouse are fast). (4) **Aggregation**: average across hosts per bucket, aligned to wall-clock boundaries. The output is a time-ordered vector per bucket. The subtle bit: **alignment and missing buckets** — you must return `null`/gaps for empty buckets or the graph misleads; and **rate vs value** semantics (PromQL `rate()` handles counter resets)."

**Interviewer**: "Compare a few real systems briefly — where would you place Prometheus, InfluxDB, TimescaleDB, and ClickHouse?"

**Candidate**: "**Prometheus**: pull-based, in-process, superb for metric *monitoring* — scrape model, PromQL, alerting built in; weaker for long-term storage (mitigated by Thanos/Mimir) and for non-metric data. **InfluxDB**: push-based, writes-first, great for IoT sensor ingest, flux querying, and v3 moved to a columnar engine; strong retention/downsampling story. **TimescaleDB**: a Postgres extension — hypertables, full SQL, continuous aggregates; the right choice when you need time-series *plus* relational joins and ACID. **ClickHouse**: columnar OLAP — the fastest for massive-scale analytics and joins over events; overkill for simple monitoring, unbeatable for log/event analytics at scale. My rule: monitoring metrics → Prometheus family; sensor/IoT ingest with heavy writes → InfluxDB or Cassandra TWCS; time-series embedded in an app with SQL needs → TimescaleDB; large-scale analytics → ClickHouse/Druid."

**Interviewer**: "Design question: a fleet of 100K devices sends a reading every 10 seconds — 10K writes/sec — and you must serve 1-year queries with sub-second p95 latency. Sketch the system."

**Candidate**: "Four layers. **Ingest**: a stateless writer pool accepting batches, buffering, and appending to the WAL; hash devices to shards by device ID (consistent hashing) to spread load; each shard owns a time-partitioned store. **Storage per shard**: 1-hour blocks, Gorilla-compressed, with in-memory hot blocks; retention drops blocks > 1 year. **Index**: per-block series indexes (device, metric) with bloom filters; a registry maps device → shard. **Query path**: the coordinator fans out the time-range to blocks, then to shards; each shard returns encoded vector chunks; the coordinator decodes, buckets (aligned to wall-clock), aggregates, and returns. Downsampling: raw 10s → 1m → 1h tiers. The numbers work: 100K devices × 1 series = 100K series; 10K writes/sec is trivial for a 3-node sharded cluster; 1-year queries hit the 1h tier — a few hundred points per series — decode is microseconds."

**Interviewer**: "What's the one operational metric you'd alert on in a TSDB?"

**Candidate**: "Beyond latency and error rate: **cardinality growth** — `rate(series_created_total)` trending up is the early warning for the OOM-and-degrade spiral; and **late-write rate** (rejected or buffered out-of-order points) — a spike there predicts silent data gaps. Both are 'slow-burn' metrics that tell you the system is heading toward a cliff before users feel it."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Data model clarity | Time-ordering, append-mostly, series = metric × tags |
| Compression depth | Gorilla delta-of-delta + XOR, 10-20x compression |
| Downsampling | Composable aggregates, histogram buckets for percentiles |
| Retention | Partition-aligned block dropping, multi-tier |
| Failure modes | Out-of-order writes, cardinality OOM, tombstone storms |
| Architecture | Wrote a concrete 4-layer system with numbers |

### Candidate strengths
- Knew the Gorilla paper's encoding specifics — a strong differentiator for storage interviews.
- Correctly flagged percentiles as non-composable from coarse samples.
- The 100K-device sketch had concrete numbers, which is what senior answers do.

### Gaps to work on
- Didn't mention **PromQL vs SQL differences** in query semantics (rate, counter resets, lookback windows).
- Could have noted **timezone/dst alignment** hazards for rollups.
- Missed mention of **continuous query / materialization backfilling** (rollup gaps when downsampling lags).

## Follow-up study prompts
1. How does PromQL `rate()` handle counter resets, and why is it wrong for gauges?
2. What are the storage and query implications of "series id reuse" (delete + recreate same tags)?
3. How do DDSketch / t-digest differ, and when is a fixed histogram better?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Walk me through Gorilla encoding in detail — timestamps and values — and give me the compression numbers."

**Candidate**: "Gorilla (Facebook's in-memory TSDB) compresses each series' stream. **Timestamps**: delta-of-delta encoding — store the first timestamp raw, then the first delta, then encode each successive *delta of the delta* with a variable-bit scheme: 0 bits if dd = 0, 1 bit if dd is ±1 or ±2... precisely: if dd == 0 emit '0'; if dd ∈ [−63, 64] emit '10' + 7 bits; if dd ∈ [−255, 256] emit '110' + 9 bits; else emit '111' + 32 bits. Since sensors sample at steady intervals, dd is mostly 0 → most timestamps cost 1 bit. **Values**: XOR with the previous value; if the XOR is 0 → 1 bit ('0'); else '1' + a scheme that reuses the previous XOR's position/size when the change is local, or a fresh 32-bit header. Slow-changing floats → mostly-identical XORs → a few bits per sample. The headline number: ~1.37 bytes per sample for 60s-interval monitoring data vs 16 bytes raw — a ~10-12x compression. That's the difference between RAM-resident and disk-bound."

**Interviewer**: "Downsampling correctness: you roll up 1-second data to 1-minute averages. What can silently go wrong?"

**Candidate**: "Four silent hazards. (1) **Alignment drift** — buckets must align to wall-clock boundaries; a rolling 60s window that starts at data-arrival time produces shifting boundaries and non-comparable rollups. (2) **Non-composable aggregates** — average-of-averages is wrong unless counts are equal; you must store (sum, count) pairs and recompute, not average the averages; min/max are fine; percentiles are the trap — p95 of p95s is meaningless, hence histogram sketches. (3) **Rate/gap semantics** — a 60-second gap in raw data: is the 1-minute bucket 'missing', 'zero', or 'carry the last value'? PromQL's `rate()` has specific gap-handling (lookback windows, counter resets); if your rollup job fills gaps with zeros, it invents 60 seconds of flat-zero data — silently wrong graphs. (4) **Out-of-order late data crossing the rollup boundary** — a point that arrives 30 minutes late belongs to an already-rolled bucket; if the rollup already ran, the late point is either lost or the bucket must be recomputed — one of the two must be explicit."

**Interviewer**: "High-cardinality queries: 'group by user_id on an events metric with 50 million series'. What happens, and what do you change?"

**Candidate**: "What happens: the query touches a huge fraction of the series index, opens millions of series cursors, and either OOMs the query node or takes minutes. The changes, in order: (1) **re-label the data** — user_id is a dimension that belongs in logs (with a database index), not in a metric tag set; metrics should carry bounded dimensions (service, host, region, status, bucketized latency); this is a data-modeling fix, not an engine fix. (2) **series sampling/aggregation** — if user_id must exist, aggregate per-user metrics in the app into pre-computed distributions (histograms), so the metric count is bounded by the bucket count. (3) **query guardrails** — cap the number of series a query may select (`max_federate_series`, query cancellation); a 50M-series GROUP BY should fail fast with a clear error, not degrade the cluster. (4) **engine changes** — push the aggregation down (VictoriaMetrics' `downsampling`, ClickHouse's `AggregatingMergeTree`) so the fan-out happens in storage. The interview rule: *cardinality is a data-modeling property — the engine only exposes what the labels allow*."

**Interviewer**: "What does a query need from the index? Design the series index for a TSDB."

**Candidate**: "A series index maps (metric, label-set) → series ID, and supports tag matching — `metric{host=~"web-.*"}`. Two layers. (1) **Identity**: a global or per-block map from label-set to series ID — exact-match lookup via hashing (a per-block inverted index); cardinality lives here. (2) **Selection**: inverted index per label — `host` → posting list of series IDs; a query intersects posting lists (AND) or unions (OR), then resolves to series IDs, then to chunks. Prometheus's approach: a per-block `postings` structure with XOR-compressed posting lists; VictoriaMetrics keeps per-day/per-hour inverted indexes so retention drops old indexes for free. The design constraint: the index must be *memory-bounded and per-block* — a global in-memory series map is the OOM trap. And the per-block structure gives you the clean story: retention = drop block = drop its index too."

**Interviewer**: "Final deep-dive: your lab implements 60-second blocks with rollups and retention in Java. What's the single most important correctness test?"

**Candidate**: "The **lossless round-trip property**: for any input stream, (a) every accepted point is queryable exactly once (no gaps, no duplicates) within its retention window; (b) rollups are recomputable from raw — if you drop raw data after rolling up, the rollup must be reconstructable to the same value the query would have computed at rollup time. Concretely: (1) property test with random point streams — insert, query every bucket, assert returned points match inserted points; (2) out-of-order injection — late points land in the correct bucket and rollups stay consistent (assert rollup = recomputation from raw at every step); (3) retention boundary — drop block B, assert queries for B's range return clean empty (not stale data), and adjacent blocks are unaffected; (4) idempotent re-ingestion — replaying the same point must not double-count (the dedupe key: series + timestamp). If those four pass, the storage contract is sound — everything else is performance."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Rehearse the Gorilla encoding numbers (1.37 bytes/sample, 10-12x) so the compression answer is instant.
- Prepare one concrete rollup-schema answer (raw 1s → 1m with sum/count → 1h) including the alignment rule.
- Practice 'cardinality is a data-modeling property' as a first-sentence reflex — it reframes the hardest question instantly.

### One-sentence takeaway
- "Time-series data is compressible because it is smooth and queryable because it is partitioned — the entire TSDB design follows from those two facts."

### Self-check questions (run before the real interview)
1. Can I sketch Gorilla's delta-of-delta timestamp encoding with the bit-width table?
2. Can I enumerate the four silent downsampling hazards and the fix for each?
3. Can I design the per-block series index and explain why global indexes are an OOM trap?
4. Can I lay out the four storage-contract tests (round-trip, late writes, retention, idempotency)?
5. Can I place Prometheus/InfluxDB/TimescaleDB/ClickHouse for a given workload in two sentences each?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Why is time-series compressible?
**Hint.** Smoothness: delta-of-delta timestamps (~1 bit) + XOR floats (Gorilla) — 10-20x.

**Q2.** What is a 'series'?
**Hint.** Unique (metric, tag-set) combination — cardinality counts series, not rows.

**Q3.** Which aggregate functions compose for rollups?
**Hint.** min/max/sum/count (and mean via sum+count); percentiles do NOT — use histograms.

**Q4.** How do you implement retention cheaply?
**Hint.** Time-partitioned blocks — retention = drop whole blocks, never row-by-row DELETE.

**Q5.** What breaks with out-of-order writes?
**Hint.** Late points for flushed blocks — buffer + merge, bounded late window, else reject loudly.

**Q6.** What is the classic TSDB OOM cause?
**Hint.** Unbounded series cardinality — the in-memory series map grows with unique tag combinations.

**Q7.** How do you fix a 50M-series 'group by user_id' query?
**Hint.** Re-label (user_id belongs in logs), pre-aggregate to histograms, cap query series, push down.

**Q8.** What must a query engine return for empty time buckets?
**Hint.** Explicit gaps/null — fabricating zeros invents data.

**Q9.** Name the four-stage query pipeline.
**Hint.** Time-prune → series selection → vector scan/decode → aggregation, aligned to wall clock.

**Q10.** Which system for IoT sensor ingest?
**Hint.** InfluxDB or Cassandra TWCS — write-first, retention-tiered; Prometheus for monitoring metrics.

### Scoring
- **8-10 correct**: ready for the TSDB loop.
- **5-7**: revise compression and downsampling semantics.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`TimeSeriesDb`) with 60s blocks, rollups, and retention; pass the round-trip test.
**Day 3**: Quick-Fire rounds; memorize the Gorilla encoding numbers (1.37 bytes/sample).
**Day 4**: Rehearse the downsampling-hazards list (alignment, composability, gaps, late data).
**Day 5**: Drill the extended rounds (series index design, cardinality, storage-contract tests).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
