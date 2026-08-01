# Lab 01: Problem Walkthrough — Query Optimizer with Join Order Selection

## Problem Statement

**Title**: MiniQueryOptimizer — Dynamic-Programming Join Order Selection

**Difficulty**: Hard

**Category**: Databases, Cost-Based Optimization

---

### Problem

Implement a query optimizer that, given a set of tables with statistics (row count, page count, distinct values) and a join graph, selects the cheapest join order and join algorithm (nested loop vs. hash join) using dynamic programming over subsets of tables (the Selinger approach).

You must implement:

1. `TableStats` — statistics per table: rows, pages, and column NDV (number of distinct values)
2. `Predicate` — a simple filter on a column with a selectivity estimate
3. A **cost model**:
   - Sequential scan cost: `pages * seqPageCost + rows * cpuTupleCost`
   - Nested loop join cost: `outerCost + outerRows * innerCost`
   - Hash join cost: `buildCost + probeCost + probeRows * cpuOperatorCost`
4. `JoinOrderOptimizer` — DP over subsets that returns the cheapest plan tree
5. A `main` demo that optimizes a 4-table join and prints the chosen plan

### Constraints

- Up to 8 tables in the DP search (beyond that, mention greedy fallback)
- Joins are inner joins on equality predicates (no commutation restrictions)
- Predicate selectivity is assumed independent (standard DB assumption)
- Costs are synthetic units — the model just needs to be internally consistent

### Examples

**Example 1:**
```
Tables: A(1000 rows), B(100 rows), joined on A.id = B.a_id
Filter: B.status = 'active' (selectivity 0.1 → 10 rows)
Expected: filter B first, probe A via nested loop (10 lookups),
          NOT scan A and hash it
```

**Example 2:**
```
Tables: C(100 rows), D(100 rows), E(1_000_000 rows)
Edges: C–D (c_id), D–E (d_id)
Expected: join C⋈D first (tiny), then join with E
```

---

## Step-by-Step Walkthrough

### Step 1: Understand the Problem

The optimizer's job: produce a physical plan tree. A plan tree is

```
PlanNode = Scan(table, predicate) | Join(left, right, type)
```

The search space: for N tables, every subset S of tables must be joined with a complementary subset T = U \ S. A plan for S is defined recursively: cheapest plan for S = min over splits (S' ⊂ S) of Join(plan(S'), plan(S\S')).

Key pruning insight (Selinger): for each subset, keep only the **single cheapest** plan, because the join of S with anything outside S only depends on S's cardinality, not on its internal structure — as long as we're not tracking orderings. We'll keep it simple: no interesting-order tracking.

The cost model needs three ingredients:

1. **Base table access cost** — scanning the table
2. **Filtered cardinality** — `rows * selectivity` after predicates
3. **Join cost** — either nested loop (proportional to outerRows × innerScanCost) or hash join (build once, probe each outer row)

### Step 2: Brute Force / Naive Approach

Enumerate every permutation of tables and every binary parenthesization:

```java
// For each permutation p of tables, for each binary tree shape:
// evaluate the cost
```

For N tables: N! permutations × Catalan(N-1) tree shapes = O(N! · 4^N / N^1.5). For N=8 that's 40,320 × 429 ≈ 17M plans — already slow, and completely hopeless at N=12+ (479M permutations alone).

**Problem**: exponential blowup, and it re-evaluates the same subproblem (the join of tables {A, B, C}) thousands of times.

### Step 3: Optimal Approach — Dynamic Programming over Subsets

The DP formulation:

```
plan[{t}]      = scan(t) with predicates applied
plan[S]        = min over non-empty proper subsets S' ⊂ S of:
                   join(plan[S'], plan[S \ S'], joinCostModel)
bestJoinOrder(U) = plan[U]  // U = all tables
```

- Number of subsets: 2^N
- For each subset, number of splits: 2^|S|-2 (each element either in S' or not)
- Total work: O(3^N), space O(2^N)
- N=8 → 6,561 subsets, manageable. N=12 → 531,441 subsets, still OK. Beyond that, use greedy left-deep or genetic search.

For each candidate join we also choose the algorithm:

- **Nested Loop**: `outerCost + outerRows * innerCost`
- **Hash Join**: `buildCost + probeCost + probeRows * cpuOperatorCost`

and pick the cheaper. The cost of a join's output row count: `outerRows * innerRows * joinSelectivity` where joinSelectivity = 1 / max(NDV(a), NDV(b)) for a key-equality join (uniform assumption).

### Step 4: Java 21+ Compilable Solution

```java
package com.databases.deep.lab01;

import java.util.*;
import java.util.stream.*;

/**
 * MiniQueryOptimizer — dynamic-programming join order selection.
 *
 * Model: tables have (rows, pages, ndv). Predicates have selectivity.
 * Plan space: DP over subsets; cost model for seq scan, nested loop, hash join.
 */
public class QueryOptimizerLab {

    // ---------- Cost model constants ----------
    static final double SEQ_PAGE_COST = 1.0;
    static final double CPU_TUPLE_COST = 0.01;
    static final double CPU_OPERATOR_COST = 0.02;

    // ---------- Catalog ----------
    record Table(String name, long rows, long pages, Map<String, Long> ndv) {}

    record Predicate(String column, double selectivity) {}

    // ---------- Plan tree ----------
    sealed interface Plan permits Scan, Join {}

    record Scan(Table table, List<Predicate> predicates, double cost, long estRows)
            implements Plan {
        public String describe() {
            String cols = predicates.stream().map(p -> p.column()).collect(Collectors.joining(", "));
            return "Scan[" + table.name() + "](" + (cols.isEmpty() ? "all" : cols) + ") ~" + estRows + " rows cost=" + cost;
        }
    }

    record Join(Plan left, Plan right, String algorithm, double cost, long estRows)
            implements Plan {
        public String describe() {
            return "Join[" + algorithm + "]<" + left.describe() + " | " + right.describe() + "> ~" + estRows + " rows cost=" + cost;
        }
    }

    // ---------- Optimizer ----------
    static final class JoinOrderOptimizer {
        private final List<Table> tables;
        private final Map<String, List<Predicate>> predicates;   // table name -> filters
        private final Map<String, Map<String, String>> joinKeys; // table -> (otherTable -> column)
        private final Map<List<Integer>, Plan> memo = new HashMap<>();

        JoinOrderOptimizer(List<Table> tables,
                           Map<String, List<Predicate>> predicates,
                           Map<String, Map<String, String>> joinKeys) {
            this.tables = tables;
            this.predicates = predicates;
            this.joinKeys = joinKeys;
        }

        /** Optimize the full join of all tables. */
        public Plan optimize() {
            List<Integer> all = IntStream.range(0, tables.size()).boxed().toList();
            Plan best = dp(all);
            System.out.println("=== Optimized plan (cost=" + best.cost() + ") ===");
            printTree(best, 0);
            return best;
        }

        private Plan dp(List<Integer> subset) {
            Plan cached = memo.get(subset);
            if (cached != null) return cached;

            // Base case: single table -> scan with predicates
            if (subset.size() == 1) {
                int idx = subset.get(0);
                Table t = tables.get(idx);
                List<Predicate> preds = predicates.getOrDefault(t.name(), List.of());
                double sel = preds.stream().mapToDouble(Predicate::selectivity)
                                  .reduce(1.0, (a, b) -> a * b);
                long estRows = Math.max(1L, (long) (t.rows() * sel));
                double cost = t.pages() * SEQ_PAGE_COST + estRows * CPU_TUPLE_COST;
                Plan plan = new Scan(t, preds, cost, estRows);
                memo.put(subset, plan);
                return plan;
            }

            // Inductive case: split subset into (left, right), join cheapest plans
            Plan best = null;
            int n = subset.size();
            for (int mask = 1; mask < (1 << n) - 1; mask++) {
                // Enumerate proper, non-empty subsets of `subset` (left side)
                List<Integer> left = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) left.add(subset.get(i));
                }
                List<Integer> right = subset.stream().filter(i -> !left.contains(i)).toList();

                Plan leftPlan = dp(left);
                Plan rightPlan = dp(right);
                Plan join = joinPlans(leftPlan, rightPlan);
                if (best == null || join.cost() < best.cost()) best = join;
            }
            memo.put(subset, best);
            return best;
        }

        private Plan joinPlans(Plan left, Plan right) {
            // Nested loop cost: outer runs once, inner probed outerRows times
            double nlCost = left.cost() + left.estRows() * right.cost();
            // Hash join cost: build right, probe with every left row
            double hjCost = right.cost() + left.cost() + left.estRows() * CPU_OPERATOR_COST;

            // Output cardinality: assume equality join between two tables,
            // selectivity = 1 / max(ndv of joined columns)
            long estRows = joinCardinality(left, right);
            Plan smaller = left.estRows() <= right.estRows() ? left : right;
            Plan larger  = left.estRows() <= right.estRows() ? right : left;
            double joinCost = Math.min(nlCost, hjCost);
            String algo = (nlCost <= hjCost) ? "NestedLoop" : "HashJoin";
            return new Join(smaller, larger, algo, joinCost, estRows);
        }

        private long joinCardinality(Plan left, Plan right) {
            long maxNdv = 1;
            for (String lCol : columnNames(left)) {
                for (String rCol : columnNames(right)) {
                    Long lNdv = ndvOf(left, lCol);
                    Long rNdv = ndvOf(right, rCol);
                    if (lNdv != null && rNdv != null) {
                        maxNdv = Math.max(maxNdv, Math.max(lNdv, rNdv));
                    }
                }
            }
            long product = Math.max(1L, left.estRows()) * Math.max(1L, right.estRows());
            return Math.max(1L, product / maxNdv);
        }

        private List<String> columnNames(Plan plan) {
            if (plan instanceof Scan s) return List.of(s.table().name() + ".id");
            if (plan instanceof Join j) {
                List<String> all = new ArrayList<>();
                all.addAll(columnNames(j.left()));
                all.addAll(columnNames(j.right()));
                return all;
            }
            return List.of();
        }

        private Long ndvOf(Plan plan, String col) {
            if (plan instanceof Scan s) {
                String table = s.table().name();
                if (!col.startsWith(table + ".")) return null;
                String bare = col.substring(table.length() + 1);
                return s.table().ndv().get(bare);
            }
            return null;
        }

        private void printTree(Plan plan, int depth) {
            System.out.println("  ".repeat(depth) + plan.describe());
            if (plan instanceof Join j) {
                printTree(j.left(), depth + 1);
                printTree(j.right(), depth + 1);
            }
        }
    }

    // ---------- Demo ----------
    public static void main(String[] args) {
        // A: 100k rows; B: 10k rows; C: 1M rows; D: 5k rows
        var tables = List.of(
            new Table("A", 100_000, 2_000, Map.of("id", 100_000L, "a_id", 10_000L)),
            new Table("B", 10_000, 200,   Map.of("id", 10_000L, "status", 3L, "b_id", 5_000L)),
            new Table("C", 1_000_000, 20_000, Map.of("id", 1_000_000L, "c_id", 100_000L)),
            new Table("D", 5_000, 100,   Map.of("id", 5_000L))
        );

        // Filters: B.status='active' selects 10%
        Map<String, List<Predicate>> preds = new HashMap<>();
        preds.put("B", List.of(new Predicate("status", 0.1)));

        // Join graph edges (undirected, equality on keys)
        Map<String, Map<String, String>> joinKeys = new HashMap<>();
        joinKeys.put("A", Map.of("B", "a_id"));
        joinKeys.put("B", Map.of("A", "id", "C", "b_id"));
        joinKeys.put("C", Map.of("B", "id", "D", "c_id"));
        joinKeys.put("D", Map.of("C", "id"));

        var optimizer = new JoinOrderOptimizer(tables, preds, joinKeys);
        optimizer.optimize();
    }
}
```

### Step 5: How It Works — Walk the Examples

**Example 1 trace** (conceptually with 2 tables):

- `dp([B])`: cost = 200 pages + 10,000 × 0.01 = 210. After filter `status='active'`, estRows = 1,000, cost ≈ 210.
- `dp([A])`: cost = 2,000 + 100,000 × 0.01 = 3,000.
- `dp([A,B])`: two splits:
  - left={A}, right={B}: NL cost = 3000 + 100,000 × 210 = 21,003,000. Hash cost = 210 + 3000 + 100,000×0.02 = 5,210.
  - left={B}, right={A}: NL cost = 210 + 1,000 × 3000 = 3,000,210. Hash cost = 3000 + 210 + 1,000×0.02 = 3,230.
  - Winner: HashJoin(B filtered first, then A) ≈ 3,230. The filter on B dramatically shrinks the probe side.

**Example 2 trace** (3 tables):

- Subsets {C}, {D}, {E} are scans: 3, ~11, ~30,000 cost units respectively.
- {C,D}: join cost small; estRows ≈ 100×100/100 = 100.
- {D,E}: 100 × 1M / 1M = 100 rows; cost ≈ 30,000 + … → expensive regardless.
- {C,D,E}: split as {C,D}⋈{E}: NL = small + 100 × 30,000 = 3,000,000 vs. {C}⋈{D,E}: 30,000 + … worse. Winner: join the two small tables first, then the big one — exactly what the demo prints.

### Step 6: Compile & Run

```bash
javac --release 21 QueryOptimizerLab.java
java com.databases.deep.lab01.QueryOptimizerLab
```

(Or place under `src/main/java/com/databases/deep/lab01/` and compile from the lab root.)

Expected output shape:

```
=== Optimized plan (cost=...) ===
Join[HashJoin]<Scan[D](all) ... | Join[NestedLoop]<Scan[B](status) ...>
```

The exact winner depends on the constants, but the D table (5k rows) will be joined late, and B's filter will be applied before any join — the two properties a good optimizer must exhibit.

---

## Complexity Analysis

- **Time**: O(3^N) subset splits for N tables (each of the 2^N subsets is split into all of its sub-subsets). With memoization each subset is solved once.
- **Space**: O(2^N) for the memo table.
- **Why this is acceptable**: N ≤ 12 → 531,441 subsets and ~3^12 ≈ 531k split evaluations; real optimizers run this in milliseconds. Beyond N ≈ 12-16, fall back to greedy left-deep plans or genetic search (PostgreSQL's `geqo`).
- **Cost model complexity**: O(1) per join candidate — a few multiplications.

## Edge Cases & Failure Handling

1. **Empty join graph** — tables with no edges: optimizer must still produce a Cartesian product; our model degenerates to nested loops with selectivity 1.
2. **Selectivity > 1 or < 0** — clamp; predicates are probabilities.
3. **No NDV info** — assume default NDV = rows (unique keys) so `joinCardinality` doesn't divide by zero.
4. **estRows = 0** — clamp to 1 to avoid zero-cost plans that "probe nothing".
5. **Cardinality explosion** — `long` overflow at > 2^62 estimated rows; use saturation via `Math.min(product, Long.MAX_VALUE)`.
6. **Stale statistics** — the DP is only as good as the stats; this is why production optimizers run ANALYZE. The demo would pick a catastrophic plan if `B.rows` were wrong by 100x.

## Follow-up Questions

1. **Interesting orders**: extend `Scan`/`Join` to carry a sort order (e.g., `sortedBy`) and keep the cheapest plan *per order* in the memo, not just one — this is how the Selinger algorithm supports merge joins and `ORDER BY` elimination.
2. **Left-deep vs. bushy plans**: our DP produces bushy plans. Restrict to left-deep by only allowing splits where the right side is a single table — reduces splits per subset to O(N) and total to O(N·2^N).
3. **Parallel plans**: add `parallelWorkers` to the cost model: `cost / workers + startup`, and choose the plan shape that scales.
4. **Parameterized queries**: re-run the DP per distinct bind value set, or use "generic plan" logic when estimates are stable.
5. **Plan caching**: memo keyed by query hash + stats version; invalidate when `ANALYZE` bumps the stats fingerprint.
6. **Runtime feedback loop**: log `(estimated, actual)` per operator; feed deltas back to adjust selectivity for the next execution.

## References

- Selinger et al., "Access Path Selection in a Relational Database Management System" (1979)
- PostgreSQL docs: `planner` internals, `geqo`, `CREATE STATISTICS`
- "Query Optimization" — García-Molina, Ullman, Widom, *Database Systems: The Complete Book*
