# Lab 02: Mock Interview — Indexing Strategies

**Role**: Senior Database Engineer
**Duration**: 45 minutes
**Company style**: FAANG / database vendor (PostgreSQL, MySQL InnoDB, MongoDB)

---

**Interviewer**: "Let's start with the fundamentals. What is a B-tree and why do databases use it rather than, say, a binary search tree or a hash table?"

**Candidate**: "A B-tree is a balanced, multi-way search tree. Each node holds up to M keys and M+1 children, so the tree is wide and shallow — height is O(log_M N) instead of O(log_2 N). That's exactly what a disk-based database needs, because each node visit is a page read, and page reads are the dominant cost. With a 4KB or 8KB page and 8-16 byte keys, a node can hold hundreds of keys, so a billion-row table needs only 3-4 levels. Contrast with a BST: height ~30 for a billion nodes, 30 page reads per lookup. Hash tables give O(1) point lookups but can't do range scans, ordered iteration, or prefix matching — which is why B-trees are the default index structure for primary keys and secondary indexes."

**Interviewer**: "Walk me through what happens internally when you insert a key that overflows a leaf page."

**Candidate**: "You insert into the leaf in sorted position. If the leaf has room, done — a single-page write. If the leaf is full, you split: the page's keys are divided roughly in half, the right half goes to a new page, and the middle key is promoted to the parent. If the parent is full too, the split propagates upward; in the worst case the root splits and the tree grows one level. Splits are the expensive part — they cost two page writes plus a parent update, and in a transactional engine, the split page and parent must be locked, often with a latch upgrade. This is why fill factor matters: leaving 10-20% of each page free (PostgreSQL `FILLFACTOR`) staggers splits and reduces contention in hot-insert workloads like sequential keys."

**Interviewer**: "What about deletions? Any subtlety with page merges?"

**Candidate**: "Deleting a key from a full-enough leaf is cheap. But when a leaf underflows — below half full — the tree may rebalance by merging with a sibling or redistributing keys from it. Crucially, many implementations *defer* merges: PostgreSQL's btree, for example, marks pages half-dead and recycles them lazily rather than merging eagerly, because merges cause write amplification and lock contention. The invariant you must never break is the root-to-leaf height staying uniform — all leaves at the same depth."

**Interviewer**: "Let's talk about range scans, since that's your lab's focus. How does a range scan `WHERE price BETWEEN 10 AND 50` actually execute against a B-tree?"

**Candidate**: "A range scan is two point-lookups joined by leaf traversal. First, descend to the leaf containing the low bound (10), following the first key ≥ 10. Then iterate leaf-to-leaf following the sibling pointers — this is why leaves are linked in a doubly-linked list. Each next-key read is sequential. The cost is O(log N + K) where K is the number of matching entries: logarithmic descent, then linear output. If the index is non-clustered, each entry then requires a heap/table lookup (rowid dereference), and if the range is wide, the planner will prefer a sequential scan instead — the classic 'index is worse than full scan for low-selectivity predicates' tradeoff."

**Interviewer**: "How does a *covering* index change that story?"

**Candidate**: "A covering index contains all columns needed by the query, so the engine never touches the heap — an index-only scan. In PostgreSQL that's only possible with a visibility map (all-visible pages); otherwise it must check the heap for MVCC visibility anyway. The win is huge: index pages are much denser than heap pages, so a covering scan reads far fewer pages. The cost is on write: every INSERT/UPDATE must maintain more index columns, and page splits get more frequent as index rows widen. The rule of thumb: cover the hot read path, don't index everything 'just in case.'"

**Interviewer**: "Let's discuss index maintenance from the optimizer's perspective. When does the planner choose your index, and when does it ignore it?"

**Candidate**: "The planner estimates two things: the *selectivity* — what fraction of rows match — and the *access path cost* — page reads via index + heap dereferences vs. a sequential scan. With `random_page_cost = 4` vs `seq_page_cost = 1`, an index lookup on random pages is 4x more expensive per page than sequential. The break-even is typically around 5-10% selectivity for a non-covering index; below that, index scan wins; above that, full scan wins. Bitmap scans are the middle ground: collect page pointers from the index, sort them, then read heap pages sequentially — great for 10-40% selectivity. That's also why a covering index with range support can win at much higher selectivity."

**Interviewer**: "Your lab implements a B-tree in Java. What's the single most common bug in B-tree implementations?"

**Candidate**: "The split logic. Specifically: (1) off-by-one in how many keys move to the new node, (2) forgetting to update the minimum key of the new node in the parent — the classic 'parent points at a key that moved' bug, (3) failing to split the parent recursively, and (4) in range scans, breaking the leaf sibling chain. My strategy: model a node as keys + children arrays, write a `splitChild(parent, index)` helper that takes the middle key out, and test with random insertions followed by full-range scans verifying sorted order — that catches every structural bug."

**Interviewer**: "Composite indexes — how do you design one for a query like `WHERE state = 'CA' AND age BETWEEN 20 AND 30 ORDER BY last_name`?"

**Candidate**: "The general design rules: equality columns first, then range columns, and order by that sorts the output. So `(state, age, last_name)` is the classic choice: `state` narrows to a small subtree, `age` is a range within it, and `last_name` provides the ordered output without a separate sort. But it's query-set-dependent: if another hot query filters only `age`, a separate `(age)` index pays off. Also consider the 5-column limit heuristic — beyond that, indexes are usually too fat. And with modern engines, INCLUDE columns (PostgreSQL `INCLUDE`, SQL Server `INCLUDE`, MySQL 8.0+ functional/covered) let you store payload without affecting the key ordering."

**Interviewer**: "Compare B-trees to LSM trees for a write-heavy workload."

**Candidate**: "B-trees optimize reads: every page is updated in place, so a point read is O(log N) with great cache locality. But every insert can cause a random page write — write amplification and page-split contention on hot keys. LSMs batch writes in a memory table, flush sorted runs to disk, and merge them in the background: sequential writes, high write throughput, 10-100x better insert performance. The tradeoffs: read amplification (you may check multiple runs — bloom filters mitigate), write amplification from compaction, and tombstone/GC complexity. Modern engines blur the line — RocksDB hybrid, InnoDB adaptive flushing, TiDB's LSM + B-tree hybrid for point queries. For the interview, the one-liner: B-tree for read-heavy with range scans; LSM for write-heavy ingestion."

**Interviewer**: "You have a table with a low-cardinality column like `status` with 4 values and 100M rows. Someone adds a plain B-tree index on it and wonders why `WHERE status = 'x'` is slow. Explain, and propose a fix."

**Candidate**: "A plain B-tree on a 4-value column gives selectivity 25% — way above the index scan break-even, so the planner ignores it, or worse, a bitmap scan wastes time. The fix: don't use a B-tree for low-cardinality predicates alone; use partial indexes (`WHERE status = 'active'` indexes only active rows), or a bitmap index as Oracle offers, or better — restructure the query so the low-cardinality filter is combined with a high-cardinality one, letting a composite index drive. In practice: `CREATE INDEX idx_orders_status_created ON orders(status, created_at)` — equality on status, range on time — and the index becomes useful."

**Interviewer**: "Final question: how do you detect and fix index bloat?"

**Candidate**: "Bloat = dead entries and empty pages from UPDATE/DELETE churn. Detection: `pgstattuple` for exact bloat, `pg_stat_user_indexes` for usage; compare index size to its table ratio, and check `idx_scan` counts to find unused indexes. Fixes: `REINDEX` to rebuild compactly, tune fill factor for update-heavy tables, and drop unused indexes (track with `pg_stat_user_indexes` over 2-4 weeks). In MySQL, `OPTIMIZE TABLE` or online `ALTER TABLE ... ALGORITHM=INPLACE` rebuilds. And crucially — set an alert: index bloat is silent until it doubles I/O on every hot path."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Data structure depth | Explained height O(log_M N), page-level reasoning |
| Insert path | Split, propagate, root growth, fill factor |
| Range scans | Sibling pointers, O(log N + K), heap dereference |
| Optimizer interaction | Selectivity break-even, random vs seq page cost |
| Tradeoff awareness | B-tree vs LSM, covering vs payload, composite rules |

### Candidate strengths
- Consistently tied data-structure behavior back to page I/O and disk latency.
- Named concrete PostgreSQL/MySQL mechanisms (fill factor, visibility map, `pgstattuple`).
- Gave test-first strategy for the lab implementation (random inserts + full-range verification).

### Gaps to work on
- Did not mention concurrent B-tree access (latching, crabbing protocol, lock coupling) — worth a read: Lehman & Yao.
- Could have described how unique indexes use the B-tree (search to leaf, check duplicates before insert).
- Slight missed opportunity: range scan of a composite index with a range on the *first* column.

## Follow-up study prompts
1. Describe the Lehman-Yao B-link tree and why it allows lock-free search during splits.
2. Why does PostgreSQL store an index entry for every row version (MVCC), and how does that inflate indexes?
3. How does a skip-scan / loose-index scan use a B-tree to answer `GROUP BY` without sorting?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's talk concurrency. B-trees in a database are accessed by many transactions at once. What's the standard technique for safe concurrent navigation, and what's the Lehman-Yao insight?"

**Candidate**: "The standard is **lock coupling (crabbing)**: to descend, acquire a latch on the child *before* releasing the latch on the parent — each step holds two latches briefly. This guarantees no one can be split under you while you traverse. The subtlety: splits invalidate the parent's pointer, so a search that lands on the wrong node must detect the split and re-descend from the parent (or retry). Lehman-Yao's B-link tree eliminates most of that: every node carries a 'high key' — the maximum key that could be there — and a sibling pointer; a search that overshoots a node's high key simply follows the sibling link. The result: searches never block on splits; writers only latch the nodes they physically modify. That's why it's the classic answer to 'concurrent B-tree access' and the basis of many production implementations."

**Interviewer**: "How does a B-tree index interact with MVCC? Why do updates bloat indexes in PostgreSQL?"

**Candidate**: "MVCC means an UPDATE is a delete + insert of a new row version. If the updated column is indexed, the index gets a new entry for the new version; the old version's entry stays until vacuum removes it. So an update-heavy table's index grows proportional to update *rate*, not live rows — that's index bloat. PostgreSQL's `REINDEX` rebuilds, but the deeper answer is that the visibility map + HOT (heap-only tuples) avoid index churn when the update doesn't touch indexed columns. The takeaway: measure `idx_bloat` with `pgstattuple`, tune `FILLFACTOR` down for hot-update tables, and prefer HOT-compatible updates when possible."

**Interviewer**: "You're asked to index a table with `WHERE name LIKE 'sm%'`. Which structure and why? What about `LIKE '%smith'`?"

**Candidate**: "For a prefix `LIKE 'sm%'`, a standard B-tree works: the range scan is `>= 'sm' AND < 'sn'` — the planner translates the prefix into a bounded range, which a B-tree loves. PostgreSQL even has `text_pattern_ops` to allow index use without locale-aware C-collation surprises. For a suffix `LIKE '%smith'` a B-tree is useless — the predicate is unanchored. Options: a trigram index (`pg_trgm` GIN) which breaks text into trigrams and indexes them; a reverse index (store `reverse(name)` and index that, then `LIKE 'htims%'` on the reversed column); or full-text search if semantics allow. The pattern: *anchor the predicate, then it becomes a range; unanchored, switch structure (trigram/GIN) or reverse the string*."

**Interviewer**: "GIN indexes — when do they beat B-trees, and what's the tradeoff?"

**Candidate**: "GIN (generalized inverted index) is for *multi-valued* data: an array column, JSONB paths, full-text lexemes. Each row's value is decomposed into keys, and the index stores key → posting list of rows. A query `WHERE tags @> '{postgres}'` becomes: look up the key, intersect posting lists — logarithmic in the number of distinct keys, not rows. Tradeoffs: (1) writes are slow — every element of every inserted array touches the index (PostgreSQL mitigates with a pending list that's bulk-flushed); (2) posting lists get long for hot values, so update costs grow with popularity; (3) the index is bigger than a B-tree per row. Rule of thumb: B-tree for scalar predicates, GIN for containment/overlap on collections — and if your GIN-write path hurts, batch the inserts."

**Interviewer**: "Final: your lab's B-tree does range scans. How would you test it properly, and what would a property-based test assert?"

**Candidate**: "Three layers. (1) **Structural invariants** after every mutation: all leaves at the same depth; every node except the root is at least half full; sorted order within every node; parent keys form correct boundaries (the min key of a child ≥ the parent's separator). (2) **Range-scan correctness**: insert random keys, then for random [lo, hi] ranges assert the scan returns exactly the sorted set of keys in range — the strongest single test, since it exercises traversal, splits, merges, and sibling pointers at once. (3) **Persistence/crash behavior** (if the tree is disk-backed): apply a prefix of the log, verify state matches, replay idempotently. Plus the classic randomized differential test: compare your tree against `TreeMap` on 10K random ops — insert/delete/lookup/range-scan — and assert identical results. That differential test catches essentially every structural bug, including the ones you didn't think of."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Draw the split sequence on the whiteboard *before* explaining it — the interview feedback was that the leaf-split story was dense.
- Prepare a comparison table of page sizes and fan-outs for a real engine (PostgreSQL 8KB, InnoDB 16KB) — quantifies everything.
- Practice the differential-test pitch: 'I compare my B-tree against TreeMap on random ops' is a great closing line for any data-structure interview.

### One-sentence takeaway
- "Every B-tree question is a page-I/O question: the structure exists to make each logical lookup cost O(log_M N) physical reads."

### Self-check questions (run before the real interview)
1. Can I simulate an insert that causes a root split, step by step, including the parent key promotion?
2. Can I explain why `random_page_cost > seq_page_cost` changes index decisions, with a numeric example?
3. Can I describe lock coupling and what B-link trees add, without a book in hand?
4. Do I know when a covering index beats a wider one, and when INCLUDE columns are better than key columns?
5. Can I articulate the B-tree vs LSM tradeoff in terms of write amplification vs read amplification?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Why is B-tree height O(log_M N) and why does that matter on disk?
**Hint.** M keys per node → log base M; each level = one page read; fan-out ~hundreds.

**Q2.** What exactly happens when a full leaf splits?
**Hint.** Half the keys → new page; middle key promoted; parent updated; propagate if full.

**Q3.** When does a range scan become worse than a full scan?
**Hint.** When K (matching rows) is large enough that heap dereferences cost more than the seq scan.

**Q4.** What makes an index 'covering' and when does the engine still touch the heap?
**Hint.** All needed columns in index; PostgreSQL checks the visibility map — non-all-visible pages need heap checks.

**Q5.** How do you build an index for `WHERE status='active' AND created_at > ?`?
**Hint.** Composite (status, created_at) — equality first, range second; or a partial index on status='active'.

**Q6.** Why does a B-tree on a 4-value column with 100M rows not get used?
**Hint.** 25% selectivity is above the index break-even — seq scan is cheaper.

**Q7.** What is index bloat, how is it measured, and how is it fixed?
**Hint.** Dead versions from UPDATE/DELETE churn; `pgstattuple`; REINDEX + fill-factor tuning.

**Q8.** Name the concurrency protocol for safe B-tree descent.
**Hint.** Lock coupling / crabbing; Lehman-Yao B-link relaxes it.

**Q9.** B-tree or LSM for a time-series ingest pipeline?
**Hint.** LSM — sequential writes, compaction; TWCS-friendly; B-tree for point/range read-heavy OLTP.

**Q10.** Why would `LIKE '%x'` not use your B-tree, and what fixes it?
**Hint.** Unanchored predicate — no range; trigram index or reversed-column index.

### Scoring
- **8-10 correct**: ready for the indexing loop.
- **5-7**: revise split mechanics and the selectivity break-even.
- **<5**: re-read the walkthrough's B-tree sections before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab B-tree (order M=4) and run the differential test against `TreeMap`.
**Day 3**: Quick-Fire rounds; draw the split sequence on paper from memory (leaf → internal → root).
**Day 4**: Read the Lehman-Yao B-link paper notes; rehearse lock coupling out loud.
**Day 5**: Drill the extended rounds (MVCC bloat, GIN, LIKE patterns) with a whiteboard.
**Day 6**: Mock interview, 45 minutes, no notes, record yourself.
**Day 7**: Score against the Debrief table; study the gaps via the follow-up prompts.
