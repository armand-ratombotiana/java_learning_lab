# Lab 06: Mock Interview — NoSQL Internals (LSM-Tree Storage Engines)

**Role**: Senior Database Engineer / Storage Engineer
**Duration**: 45 minutes
**Company style**: Database vendor (RocksDB, Cassandra, ScyllaDB, ClickHouse, TiKV)

---

**Interviewer**: "Why do so many NoSQL engines — RocksDB, Cassandra, HBase, LevelDB — use an LSM-tree instead of a B-tree?"

**Candidate**: "Because their workloads are write-dominated, and LSM-trees turn random writes into sequential writes. The core idea: writes go to an in-memory structure (the memtable) and are batched out as immutable sorted files (SSTables) that are merged in the background. Nothing is ever updated in place on disk — all writes are appends, so disks see sequential I/O, which is an order of magnitude cheaper than random I/O. B-trees need random page writes for every insert, and page splits under high write concurrency create contention. LSM engines routinely achieve 10-100x higher write throughput. The price: reads may have to check multiple files, and background merges cause write amplification — which is why the compaction strategy is the heart of an LSM design."

**Interviewer**: "Walk me through the write path, end to end."

**Candidate**: "Four stages. (1) The write is appended to the WAL (write-ahead log) for durability — a sequential append, the only fsync. (2) It's inserted into the **memtable**, an in-memory sorted structure (usually a skip list). (3) When the memtable exceeds a size threshold, it's frozen and flushed as an immutable **SSTable**: sorted key-value runs, often with index blocks and bloom filters. The frozen memtable is still readable, and reads consult it along with the on-disk runs. (4) Background **compaction** merges overlapping SSTables into bigger sorted runs, discarding obsolete versions. That's the whole engine — WAL → memtable → SSTables → compaction."

**Interviewer**: "A read now has to look in the memtable plus multiple SSTables. How do you keep reads fast?"

**Candidate**: "Three techniques, in order of cost. **Bloom filters** per SSTable answer 'is this key even in this file?' in O(1) memory — they eliminate most file probes. **Level-based organization** — with leveled compaction, each level's keyspace is roughly disjoint (except within a level), so at most one file per level can contain a given key: a read touches at most L files where L is the number of levels — typically 6-7. **Metadata blocks**: each SSTable stores an index of key ranges, so we binary search within a file. The read path becomes: memtable (1 probe) → active memtable → bloom-filtered file probes (usually 0-2) → binary search in the file. RocksDB and Cassandra both follow exactly this shape."

**Interviewer**: "Let's talk compaction — that's where the real engineering lives. Compare size-tiered and leveled compaction."

**Candidate**: "**Size-tiered** (Cassandra default): when a tier has enough SSTables of similar size, merge them into one bigger table. Write amplification is low and the write path stays fast, but read amplification can spike — a key may live in many overlapping files. **Leveled** (RocksDB default): files are organized into levels; level L+1 is 10x the size of level L, and compactions move data down one level at a time, so each level is roughly disjoint. Reads become predictable (one file per level), but the same data is rewritten at every level — higher write amplification (RocksDB ~10-30x, tunable via `level_compaction_dynamic_level_bytes`). The engineering question is always: which knob do you turn for *your* workload — write-heavy with occasional reads? Size-tiered. Read-heavy, latency-sensitive? Leveled. Cassandra's `STCS` vs `LCS` vs `TWCS` (time-windowed, for time-series) is the same tradeoff catalog."

**Interviewer**: "What is write amplification, precisely, and how do you measure it?"

**Candidate**: "Write amplification (WAF) = bytes written to storage ÷ bytes written by the application. If the app writes 1 GB and the engine writes 30 GB to disk, WAF = 30. Sources: WAL writes (small constant), memtable flush (1x), and compaction rewrites (the big one). With leveled compaction and size ratio 10, a value written at level 0 gets rewritten ~10x per level transition on its way down — the classic estimate is that leveled LSM WAF ≈ total levels × size ratio. High WAF wears out SSDs (limited P/E cycles), eats write bandwidth, and adds latency spikes when compaction catches up. You measure it with engine metrics (RocksDB `Compaction` statistics, Cassandra `WriteLatency`/`TotalWriteTime`) — and you tune it by adjusting memtable size, compaction strategy, and `bloom_locality`."

**Interviewer**: "How do deletes work in an LSM? There's no in-place update..."

**Candidate**: "A delete inserts a **tombstone** — a delete marker with the key and a sequence number — into the memtable, then the tombstone flows down through compaction like any other record. Reads see the tombstone and report 'not found.' Physical removal happens only when a compaction merges the tombstone with the actual data record and drops both. Two classic failure modes: (1) **deletes resurrect** — a tombstone at level 5 is compacted away while an older value still sits at level 6, and a read finds the level-6 value. The fix is tombstone GC with guarantees: don't drop a tombstone until the levels *below* it that could contain the key have been compacted past it (RocksDB's `full_compaction` and `key_range` guarantees). (2) **tombstone storms** — range deletes of huge key ranges generate tombstones for every key; that's why RocksDB has `DeleteRange` — one logical tombstone for a range, checked on read."

**Interviewer**: "What about snapshot isolation and MVCC in this design?"

**Candidate**: "LSM engines are naturally MVCC-friendly because every write creates a *new version* rather than mutating — the memtable and each SSTable hold different versions of the same key, ordered by sequence number. A snapshot is just a sequence-number watermark: reads at snapshot S only consider records with seq ≤ S. Compaction must respect snapshots — never drop a record still visible to an open snapshot, which is why compactions carry a `snapshot list` and skip merging anything newer than the oldest live snapshot. This is how RocksDB gets consistent point-in-time backups and Cassandra gets `read_repair`-compatible versions."

**Interviewer**: "How does the LSM design handle range scans, which B-trees do natively?"

**Candidate**: "A range scan is a merge across runs: open an iterator per relevant SSTable (memtable included), then a k-way merge delivers keys in sorted order. Because each file is internally sorted, the merge is O(total keys) with a heap of iterators. Leveled compaction helps again — with disjoint levels you mostly need the memtable plus one file per level. RocksDB's `Iterator` and Cassandra's `RowIterator` both implement this. Range scans on hot ranges (e.g., time-series intervals) benefit enormously from time-windowed compaction (TWCS), which keeps adjacent timestamps in the same files."

**Interviewer**: "Give me the failure modes of LSM engines that you've seen or would design against."

**Candidate**: "Top five. (1) **Compaction stall**: the write path blocks because the L0 is full and the background threads can't keep up — the classic 'writes suddenly 100x slower' incident; mitigated with rate limiting, more compaction threads, and backpressure. (2) **Write amplification spiral**: too many small SSTables → too much merging → disk saturation → everything slows. (3) **Memtable flush burst**: huge peak write, several flushes at once, L0 pileup — same stall family. (4) **Bloated bloom filters**: memory pressure pushes filters out of cache and reads degrade silently. (5) **JVM/GC pause amplification** in Java engines (Cassandra) — background compactions allocate, GC pauses spike latency. Designing against these = exposing metrics, capping L0 size, and testing with realistic write skew."

**Interviewer**: "Last one: when would you *not* use an LSM engine?"

**Candidate**: "When reads dominate and you need predictable low-latency point/range access: B-trees give ~1-2 page reads per lookup, no read amplification, and better cache utilization for hot rows. Also when in-place updates and strong constraint checks (unique indexes, FK checks) dominate — LSM's versioned nature makes uniqueness checks expensive (must scan tombstones/versions). And for small datasets, the engine complexity isn't justified. That's why the answer isn't 'LSM vs B-tree' but 'LSM for write-heavy append/ingest workloads, B-tree for read-heavy OLTP' — and why engines like TiDB offer both."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Core mechanism | WAL → memtable → SSTable → compaction in the right order |
| Read path | Bloom filters, level disjointness, one-file-per-level |
| Compaction depth | Size-tiered vs leveled, WAF math, TWCS |
| MVCC understanding | Sequence numbers, snapshot watermarks, compaction snapshot lists |
| Operational reality | Named compaction stalls, tombstone resurrection, GC pauses |
| Judgment | Knew when LSM is wrong |

### Candidate strengths
- The tombstone-resurrection explanation was precise and rarely offered unprompted.
- Gave measurable WAF and tied it to SSD wear — shows real operations experience.
- Correctly positioned the choice as workload-driven, not ideological.

### Gaps to work on
- Didn't mention **range tombstone `DeleteRange`** until asked — consider leading with it in the delete answer.
- Could have quantified bloom filter memory: ~10 bits/key ≈ 1% false-positive rate.
- Missed the **single-writer memtable / concurrent flush** subtlety in RocksDB.

## Follow-up study prompts
1. Why does RocksDB's default `level_compaction_dynamic_level_bytes` reduce WAF vs static levels?
2. How does Cassandra's `TWCS` work for time-series, and why does it break for out-of-order writes?
3. How does a merge iterator (k-way heap) handle duplicate keys across runs — and why does the *newest* sequence number win?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deep on the memtable. What data structure, and what happens at the write-path concurrency level?"

**Candidate**: "The canonical memtable is a **skip list** — sorted, O(log n) insert/lookup, and it supports a lock-free-ish append pattern: RocksDB allows concurrent writes via a *single writer group* per memtable — writers form a batch group; one writer becomes the leader and inserts the whole batch, the rest wait — that's the `WriteBatch` group-commit pattern. Reads go through a *sequence number* watermark: each write gets a monotonically increasing seq; a read at snapshot S sees only seq ≤ S. The important failure mode: memtable flush is not free — freezing the active memtable (a `SwitchMemtable`) is cheap, but if the flush threads can't keep up, the active memtable hits its size cap and the *write path stalls* — that's the L0-stall I mentioned. Production tuning: `write_buffer_size`, `max_write_buffer_number`, and the stall conditions are the memtable knobs."

**Interviewer**: "Bloom filters — walk me through the math. How much memory for a given false-positive rate?"

**Candidate**: "A bloom filter: k hash functions over an m-bit array; insert sets k bits; lookup checks k bits — any zero → definitely absent; all ones → 'probably present'. The classic formula: optimal k = (m/n)·ln2, and the false-positive rate ≈ (1 − e^(−kn/m))^k, minimized at k = ln2·(m/n). With the standard 10 bits per key (m/n = 10), you get roughly 1% false positives. With 7 bits per key it's ~5%; 20 bits → ~0.05%. The key-number that matters for LSM reads: a bloom false positive sends you to disk for a real lookup — at 1% FP and 10 levels, expected extra disk probes are tiny. The cost is memory: 10 bits/key across 1 billion keys = 1.25 GB of filters — which is why filters are per-SSTable (only hot files keep them resident) and why RocksDB's `cache_index_and_filter_blocks` setting matters."

**Interviewer**: "What happens when two SSTables contain the same key? Walk me through read correctness at the merge-iterator level."

**Candidate**: "The merge iterator holds one cursor per run (memtable + each SSTable). At each step it finds the minimum key among the cursors; **duplicates**: the run with the *highest sequence number* wins — because sequence numbers are assigned in write order, the highest seq is the newest write. The iterator must then *skip* the older duplicates for that key (advance those cursors past the key) before returning. This is why correctness depends on seq numbering and why compaction 'drop older versions' is safe: a compacted run merges duplicates and keeps only the newest version per key — *unless* an open snapshot still references the old version, which is why compactions check the snapshot list before dropping anything. The one-liner: *newest-seq wins at read time, snapshots pin versions at compaction time*."

**Interviewer**: "Range deletes — `DeleteRange`. How does it work and where do the subtleties bite?"

**Candidate**: "A range delete stores one *range tombstone* `[start, end)` with a seq instead of a per-key tombstone for every key in the range. Reads check: if the largest range tombstone covering the key has seq > the key's seq, the key is invisible. The subtleties: (1) the tombstone lives in *one* SSTable but must suppress keys in *many* files with lower seqs — the merge iterator must search the range-tombstone set across files, not just per-file; (2) compaction must eventually combine the tombstone with the covered keys — but only when it can *prove* no file with lower seq still contains covered keys (RocksDB tracks `range_del` file metadata and `max_covered_seq`); (3) `DeleteRange` is the fix for the 'delete 1 million keys' storm — one logical tombstone instead of a million per-key ones, at the cost of read-path complexity. The interview answer: range tombstones are the classic example of *batching at the logical level* — the storage engine's hardest correctness problems are all in 'when can I forget this?'"

**Interviewer**: "Final deep-dive: a workload of 80% writes, 20% point reads, 100-byte values, on SSD. Configure the engine — and justify every knob."

**Candidate**: "Goal: high write throughput, bounded read latency, low WAF (SSD wear). Knobs: (1) **memtable**: `write_buffer_size` 64-256 MB with 2-4 memtables — bigger memtables = fewer flushes = lower WAF; (2) **compaction**: leveled with `level_compaction_dynamic_level_bytes` — bounded read amplification, tunable WAF; target WAF ~10-20; (3) **bloom filters**: 10 bits/key for point reads — the 80/20 workload pays for the memory; (4) **WAL**: `wal_bytes_per_sync` to batch fsyncs; disable WAL only if data loss is acceptable (it usually isn't); (5) **block cache**: a few GB for hot SSTable index/filter blocks; (6) **rate limiting**: `max_background_compactions` and compaction-rate limits so compaction doesn't steal write path bandwidth. The interview point: *each knob is a leg of the read/write/WAF tradeoff triangle — you can't optimize all three; you pick the workload's corner and tune the others to its shape*."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Memorize the bloom-filter math cold (10 bits/key ≈ 1% FP) — it was right but hesitantly delivered.
- Prepare a small whiteboard of the LSM write path (WAL → memtable → flush → compaction) to anchor every answer.
- Practice the WAF derivation: 'leveled compaction with ratio 10 rewrites data ~10-30x — measure with engine stats.'

### One-sentence takeaway
- "An LSM engine is a bet that writes are sequential appends and reads are amortized by filters — every design decision is a tradeoff on the read-amplification/write-amplification/space triangle."

### Self-check questions (run before the real interview)
1. Can I draw the full LSM write and read paths and name the cost at each step?
2. Can I compute bloom-filter memory for a given FP rate, and explain why filters are per-SSTable?
3. Can I explain why *newest-seq wins* at merge time and why snapshots pin versions at compaction time?
4. Can I compare STCS vs LCS vs TWCS with concrete workload examples?
5. Can I justify a full RocksDB/Cassandra config for an 80/20 write-heavy workload?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Why are LSM writes fast?
**Hint.** Sequential appends: WAL + memtable; disk never sees random in-place updates.

**Q2.** Order the write path.
**Hint.** WAL append (fsync) → memtable → freeze → flush to SSTable → background compaction.

**Q3.** What keeps LSM reads fast?
**Hint.** Bloom filters (skip files), leveled disjointness (one file per level), in-file binary search.

**Q4.** Define write amplification and its main source.
**Hint.** Bytes written ÷ bytes written by app; compaction rewrites dominate (leveled ≈ levels × ratio).

**Q5.** How does a delete work, and what can go wrong?
**Hint.** Tombstone with a seq; resurrection if the tombstone is compacted away while an older version remains below.

**Q6.** STCS vs LCS — one sentence each.
**Hint.** STCS: low WAF, high read amplification; LCS: predictable reads, higher WAF.

**Q7.** How do snapshots work in an LSM?
**Hint.** Sequence-number watermark; compaction skips versions visible to open snapshots.

**Q8.** What is the L0-stall?
**Hint.** Too many level-0 files; the write path blocks until compaction catches up.

**Q9.** Why is a unique-index check expensive in an LSM?
**Hint.** Must find any existing version incl. tombstones across runs — not a local in-place lookup.

**Q10.** When is a B-tree better than an LSM?
**Hint.** Read-heavy OLTP: ~1-2 page reads, no read amplification, better hot-row caching.

### Scoring
- **8-10 correct**: ready for the storage-engine loop.
- **5-7**: revise the read path and compaction families.
- **<5**: re-read the walkthrough before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`LsmStorageEngine`) with tombstones and compaction; run the differential read test.
**Day 3**: Quick-Fire rounds; derive the WAF estimate for leveled compaction on paper.
**Day 4**: Rehearse the merge-iterator duplicate rule (newest seq wins) and snapshot pinning.
**Day 5**: Drill the extended rounds (memtable concurrency, bloom math, DeleteRange, config design).
**Day 6**: Mock interview, 45 minutes, no notes.
**Day 7**: Score against the Debrief table; study the follow-up prompts.
