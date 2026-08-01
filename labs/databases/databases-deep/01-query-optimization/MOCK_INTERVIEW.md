# Lab 01: Mock Interview — Query Optimization & Join Order Selection

**Role**: Senior Database Engineer
**Duration**: 45 minutes
**Company style**: FAANG / database vendor (PostgreSQL, MySQL, CockroachDB)

---

**Interviewer**: "Welcome. Today we're going to talk about query optimization. Can you start by explaining what a query optimizer actually does, end to end?"

**Candidate**: "Sure. The optimizer takes a parsed SQL query — a declarative statement — and produces an execution plan. That means three jobs. First, *rewriting*: it normalizes the tree, folds constants, expands views and simplifies predicates. Second, *plan enumeration*: it generates alternative join orders and access paths. Third, *cost estimation*: it assigns a numeric cost to each candidate plan using statistics and physical operator models, then picks the cheapest. The output is a physical plan tree of operators like SeqScan, IndexScan, Nested Loop, Hash Join, and Aggregate."

**Interviewer**: "Where does join order selection fit in that pipeline?"

**Candidate**: "Join ordering is the heart of enumeration. For a query joining N tables there are N! permutations, but the planner must pick the cheapest. The classic approach is the Selinger dynamic-programming algorithm: build plans bottom-up, keep only the cheapest plan per subset of tables per interesting order, and prune everything else. The key insight is optimal substructure — the cheapest plan for a subset doesn't depend on how we join it with other tables, as long as we track the output ordering and other physical properties."

**Interviewer**: "So dynamic programming makes join ordering tractable. How does the number of plans grow?"

**Candidate**: "Naively, DP over subsets is O(3^N) considering all binary partitions of each subset, but with pruning it's far less in practice. Without pruning, join enumeration is exponential — that's why optimizers cap the number of tables, typically 12 to 32, and fall back to greedy or genetic strategies for very large joins. PostgreSQL uses a dynamic-programming search for up to 12 relations in its default `geqo_threshold` configuration, and then switches to a genetic algorithm."

**Interviewer**: "Let's get concrete. Walk me through how you'd estimate the cost of a two-table hash join."

**Candidate**: "A hash join has two phases: build and probe. Cost is roughly: `cost = seqScan(buildSide) + seqScan(probeSide) + N_build * hashCost + N_probe * probeCost`. More precisely it's `seq_page_cost * pages + cpu_tuple_cost * tuples + cpu_operator_cost * tuples` per input, plus the hash overhead. The planner picks the smaller input as the build side to minimize hash table memory. And with `random_page_cost` being roughly 4x `seq_page_cost`, the planner heavily favors sequential scans unless an index can shrink the number of tuples read dramatically."

**Interviewer**: "What statistics drive those estimates, and what happens when they're wrong?"

**Candidate**: "The core statistics are: relation cardinality (rows), pages, per-column NDV (distinct values), null fraction, average width, and a histogram for non-uniform distributions. The planner computes selectivity for each predicate — the fraction of rows that pass it — and multiplies base cardinalities. This is where errors compound: a 10x error on one filter multiplies through joins and aggregates. When statistics are stale — say, autovacuum hasn't run after a bulk load — you get catastrophic mis-estimates: the planner picks nested loops expecting 10 rows and instead executes 10 million probes. That's the classic 'query was fine yesterday, then someone loaded data' incident."

**Interviewer**: "How do you handle correlation between columns? Classic example: `WHERE state = 'CA' AND occupation = 'engineer'` where the two are highly correlated."

**Candidate**: "Independence is the default assumption — `selectivity(p1 AND p2) = sel(p1) * sel(p2)`. With correlated columns that grossly underestimates. PostgreSQL has a few defenses: extended statistics (`CREATE STATISTICS` with `WITH (dependencies)`) captures cross-column dependencies, and MCV lists can store combinations of values. Without those, the estimate can be off by orders of magnitude. If I suspected correlation, I'd create extended statistics or, as a pragmatic fix, use a combined index and let the planner's index correlation stats help."

**Interviewer**: "Good. Let's switch to a design problem. Design a query optimizer feature: *adaptive join selection*. When a query's cardinality estimates are known to be unreliable, the plan should adapt at runtime."

**Candidate**: "I'd break it into three pieces. One: *parametric queries* — when the same prepared statement is executed with different bind values, we detect when the optimizer consistently mis-estimates and switch to a plan tuned for the actual parameter ranges, like PostgreSQL's extended query protocol does with generic plans, or Oracle's adaptive cursor sharing. Two: *runtime feedback* — operators report actual row counts back to the executor; if a nested loop's inner side cardinality is 100x the estimate, the executor can switch to a hash join mid-execution. Oracle's adaptive plans and Spark's adaptive query execution do exactly this: they re-optimize at shuffle boundaries. Three: *learning* — persist per-query mis-estimation deltas to a feedback table, and on subsequent executions, seed the planner with corrected selectivity, like the `pg_stat_statements` + auto-tuning extensions."

**Interviewer**: "What are the risks of runtime plan changes?"

**Candidate**: "Three risks. *Determinism*: the same query can produce different results timing-wise, which confuses application monitoring — you need a `plan_guid` to track which plan ran. *Overhead*: every switch point costs a checkpoint of state; you should only re-optimize at expensive barriers like shuffle or build phases, not per tuple. *Oscillation*: if feedback is noisy, the plan can flip-flop between two alternatives and thrash. Mitigations are a hysteresis window — only switch if the actual cost differs from estimate by more than a threshold for several consecutive executions — and backoff."

**Interviewer**: "Let me give you a concrete query. `SELECT * FROM A JOIN B ON A.id = B.a_id WHERE B.status = 'active'`. A has 100M rows, B has 1M rows, `status = 'active'` selects 10% of B. What join order do you pick and why?"

**Candidate**: "The key insight is the join is essentially a lookup of B's rows into A by primary key `A.id`. Filtering first: B's active rows are 100K. So plan: scan B, filter `status='active'` (100K rows), then for each of those 100K rows do an index lookup on A's PK. That's a nested-loop-with-index join, 100K probes each O(log n). Alternatively, hash join with B as the build side: scan B (1M), hash 100K rows, scan A (100M) probing the hash table — 100M probes, but sequential scan of A. If A is 100M rows and mostly not needed, the index-nested-loop is better because A's scan is the dominant cost. So: *filter, then reduce, then probe the larger table via index*. The optimizer would choose the same if `A.id` has a PK and statistics are fresh."

**Interviewer**: "How would you rewrite or hint this query if the optimizer stubbornly chose the wrong plan?"

**Candidate**: "First try without hints: rewrite the join order in SQL (though most optimizers reorder anyway), push the filter as a derived table, or increase statistics targets on `B.status`. For PostgreSQL, `SET enable_hashjoin = off` is a blunt instrument for diagnosis, not production. For a surgical fix, I'd use `/*+ Leading(B A) */` style hints where supported, or in PostgreSQL land, I'd rely on `EXPLAIN` to see the mis-estimate and fix the underlying statistics. The correct fix is always better statistics, not hints — hints rot when data distribution changes."

**Interviewer**: "How do you measure whether an optimizer change actually improved things?"

**Candidate**: "A regression harness with three components. A corpus of representative queries — a workload capture from production. A golden set of plans: we lock in the current best plans as a baseline. And metrics: end-to-end latency percentiles (p50, p95, p99), optimizer wall-clock time, plan stability across runs (did the same query pick the same plan?), and cost-estimate error ratio — actual rows vs estimated rows per operator. We also run a 'plan diff' tool that flags any query whose chosen plan changed, so reviewers can eyeball whether the change is safe."

**Interviewer**: "Last question: explain the 'Moiré pattern' problem in cost-based optimization."

**Candidate**: "The Moiré pattern refers to how small estimation errors create alternating stripes of wrong choices across the plan space — like two fine grids overlapping. A tiny selectivity error near a cost cliff flips the decision between two very different plans, and because the error propagates through every downstream operator, you get plans that are wildly wrong rather than mildly wrong. The classic example: two competing join orders whose estimated costs are within 1% but whose *actual* costs differ by 100x. The mitigation is robust estimation — acknowledging that cost models are fuzzy near decision boundaries and preferring plans that are 'good enough' over a wider range of cardinalities, which is the idea behind adaptive and uncertainty-aware planning."

---

## Debrief

### What the interviewer looked for

| Area | Signal |
|------|--------|
| Pipeline understanding | Explained rewrite → enumeration → costing as one flow |
| Algorithm knowledge | Named Selinger DP, subset pruning, O(3^N) growth |
| Cost model depth | Knew `seq_page_cost`, `random_page_cost`, cpu tuple costs |
| Statistics intuition | Understood selectivity compounding, stale stats, correlation |
| Systems design | Gave layered adaptive design with failure-mode analysis |
| Hands-on skill | Walked a concrete query to a plan with justification |

### Candidate strengths
- Articulated DP-based join enumeration and when it breaks down.
- Diagnosed the correlated-columns trap and named the fix (extended statistics).
- Gave a structured adaptive-planning answer with mitigations, not just features.

### Gaps to work on
- Did not mention join reordering constraints like outer joins (commutativity is not free).
- Could have mentioned `work_mem` and spill-to-disk effects on hash join cost.
- Slight hesitation on hint syntax portability — keep one dialect per platform.

## Follow-up study prompts
1. Why can't a left outer join be freely commuted with an inner join in enumeration?
2. What is a "join collapse" and how does `join_collapse_limit` change enumeration?
3. How do bitmap index scans change the cost model for multi-column predicates?

---

## Extended Rounds — Deeper Dives

**Interviewer**: "Let's go deeper on cost models. Walk me through a nested loop join cost calculation with real numbers. Table A: 100K rows, 2000 pages. Table B: 10K rows, 200 pages. Join A ⋈ B on A.id = B.a_id with an index on B.a_id, selectivity of the join per probe = 1."

**Candidate**: "Cost = outer scan + inner probes. Outer: scan A (2000 pages × seq_page_cost 1.0 = 2000) + 100K × cpu_tuple_cost (0.01 → 1000). Per outer row, probe B's index: index descent ~3 page reads × random_page_cost 4.0 = 12, plus heap dereference ~1 page × 4 = 4, so ~16 per probe; 100K probes × 16 = 1.6M. Total ≈ 1.6M. Alternative: hash join — scan both (2000 + 200 pages + tuple costs ≈ 3.3K), build hash (100K × hash cost), probe (10K × 0.1 cost each — wait, careful: the probe side is the *outer* table A with 100K rows). Hash join: build on B (smaller, 10K), probe A's 100K rows, each probe O(1). Total ≈ 3.3K + overhead ≈ ~10-15K units — 100x cheaper than the nested loop. So the planner picks the hash join. The number that matters: the break-even point where 100K × per-probe-cost exceeds the full-scan cost — that's where index nested loops lose."

**Interviewer**: "What role do histograms actually play in selectivity estimation? Show me with an example of `WHERE salary > 50000`."

**Candidate**: "Without a histogram the planner assumes uniformity: selectivity = (max - 50000) / (max - min). With a 100-bucket equi-depth histogram, the planner finds the bucket containing 50000, interpolates within it: buckets_below + fraction_of_the_boundary_bucket, divided by total. Equi-depth (equal row counts per bucket) is preferred over equi-width because it spends resolution where the data is dense. The boundary case that breaks histograms: most common values (MCV lists) — if '50000' is a value with millions of rows but the histogram bucket treats it as uniform, you mis-estimate by orders of magnitude; that's why MCVs are stored separately and why PostgreSQL's default is `default_statistics_target = 100` buckets."

**Interviewer**: "How does the optimizer handle *parameterized* plans — `PREPARE` + `EXECUTE` with different values?"

**Candidate**: "Two strategies: **custom plans** — re-plan per parameter value, best accuracy, more planning overhead; **generic plans** — plan once using the parameter's data type statistics (e.g., assume 1/NDV selectivity for `col = $1`), reuse for all executions. PostgreSQL starts with custom plans and after 5 executions of a prepared statement compares the estimated custom cost against the generic plan cost, switching to generic if it wins consistently. The classic failure: a query that is selective for most values but not for a skewed one — the generic plan is built for the *average* case and does a seq scan that explodes when executed with the hot value. That's 'generic-plan mis-estimation', and the fix is often to force custom planning (`plan_cache_mode = force_custom_plan`) or add a range-specific index."

**Interviewer**: "Parallel query — how does parallelism change the cost model?"

**Candidate**: "Parallelism changes *cost*, not *plan shape*. A seq scan with 4 workers has its cost divided: `parallel_workers` scale the scan cost by ~1/workers (plus a startup cost for the leader and per-worker tuple costs). The planner then chooses parallel plans only when the estimated cost is above `min_parallel_table_scan_size`. The subtlety: operators don't all parallelize — a hash join can parallelize build and probe, but a nested loop with an index probe is typically serial on the inner side; an ORDER BY needs a parallel gather-merge. And the danger: the *estimated* parallel speedup is optimistic — contention, I/O saturation, and skew (one worker gets 90% of the tuples via a skewed hash key) make real speedups far below linear. Spark has this same issue with 'adaptive execution' re-tuning partitions at runtime."

**Interviewer**: "Final deep-dive: what is 'interesting order' in Selinger-style DP, and why does it matter?"

**Candidate**: "An interesting order is an output ordering that downstream operators might exploit — a sort order (e.g., `ORDER BY`), a group-by key order (for hash-aggregate vs sort-aggregate), or a join-key order (for merge joins). The optimizer keeps not just the cheapest plan per subset but the cheapest plan *per interesting order*, because a slightly more expensive plan that produces a needed order can be much cheaper *overall* — e.g., a merge join that avoids a sort. This is the famous 'physical properties' tracking: the DP table is keyed by (subset, interesting order) instead of just subset. It multiplies the plan space — that's why the state is 'subset × order' and why pruning rules (dominance) matter: a plan with order X and cost 100 dominates a plan with no order and cost 95 only if some operator needs order X."

---

## Post-Interview Self-Assessment

### What the candidate would do differently
- Bring a written cost-model cheat sheet (seq_page_cost, random_page_cost, tuple costs) — the arithmetic in the nested-loop round took longer than it should.
- Prepare a two-sided story for `EXPLAIN ANALYZE` output: read a real plan and narrate it line by line, including row-count estimate vs actual.
- Practice one vendor's hint syntax end-to-end so the answer doesn't hedge on portability.

### One-sentence takeaway
- "The optimizer is a cost estimator with a search — and every interview answer should trace the line from statistics to selectivity to operator cost to the chosen plan."

### Self-check questions (run before the real interview)
1. Can I compute the cost of a hash join vs index-nested-loop for a concrete pair of tables, out loud, in under two minutes?
2. Can I name what breaks when `autovacuum` is disabled and explain the incident shape?
3. Can I explain *why* DP enumeration is O(3^N) and what the pruning criteria are?
4. Do I know the difference between `EXPLAIN` and `EXPLAIN ANALYZE` output — which number is estimate, which is actual?
5. Can I describe adaptive execution in both Spark and PostgreSQL-family systems without conflating them?

---

## Quick-Fire Practice Rounds (30 minutes)

Answer each in under 60 seconds. Then check the hint line.

**Q1.** Why does the planner prefer a seq scan at >5-10% selectivity?
**Hint.** Index probes cost random_page_cost ≈ 4x seq; above the break-even the index's random I/O loses to a linear read.

**Q2.** What three stats does a planner need for `WHERE a = 5 AND b BETWEEN 1 AND 10`?
**Hint.** NDV for equality, histogram for the range, and correlation/dependency between a and b.

**Q3.** Name one case where DP enumeration is infeasible and what replaces it.
**Hint.** >12-32 tables: GEQO (genetic), greedy, or randomized search.

**Q4.** Why is a hash join's build side chosen as the smaller input?
**Hint.** Memory for the hash table and probe cost scale with build side size.

**Q5.** What does `EXPLAIN (ANALYZE, BUFFERS)` add over plain `EXPLAIN`?
**Hint.** Actual rows/time AND buffer reads — the truth behind the estimates.

**Q6.** When do nested-loop joins beat hash joins?
**Hint.** Small outer + indexed inner, or when the inner lookup is selective per probe (e.g., PK lookups).

**Q7.** What is a parameterized nested loop?
**Hint.** Inner scan re-planned per outer row's value — good for correlated subqueries.

**Q8.** What breaks when autovacuum is disabled for a week?
**Hint.** Stale statistics → catastrophic cardinality mis-estimates → plan flips.

**Q9.** Why does `LIMIT 10` change the plan shape?
**Hint.** Top-N stops early — index order + early termination beats sort-and-scan.

**Q10.** How do you detect a plan regression in production?
**Hint.** Plan hash diffing per query (pg_stat_statements `planid`), latency + row-mismatch alerts.

### Scoring
- **8-10 correct**: ready for the query-optimizer loop.
- **5-7**: revise histograms, cost constants, and the DP enumeration bounds.
- **<5**: re-read the walkthrough's complexity section before the interview.

## One-Week Preparation Plan

**Day 1-2**: Implement the lab (`QueryOptimizerLab`) and verify the DP join enumeration runs on 4 tables; re-derive the O(3^N) subset argument.
**Day 3**: Run the Quick-Fire rounds; write out the cost model cheat sheet (seq/random page cost, cpu costs) from memory.
**Day 4**: Practice `EXPLAIN` narration — take 5 queries, predict, run, compare.
**Day 5**: Drill the extended rounds (cost arithmetic, interesting orders, adaptive planning) out loud.
**Day 6**: Simulate the interview: 45 minutes, no notes, record yourself.
**Day 7**: Review the recording against the Debrief table; fill the gaps with the follow-up prompts.
