# Math Foundation: SQL and Set Theory

## SQL Operations as Set Operations

| SQL | Set Theory |
|---|---|
| `DISTINCT` | Eliminate duplicates (set, not multiset) |
| `UNION` | Set union |
| `INTERSECT` | Set intersection |
| `EXCEPT` | Set difference |
| `JOIN` | Generalized Cartesian product with filter |
| `WHERE` | Selection (σ) |
| `SELECT columns` | Projection (π) |

## SQL is Multiset (Bag) Algebra
Unlike pure set theory, SQL operates on **multisets** (bags) by default:
- `SELECT name FROM employees` returns duplicates if multiple employees have same name
- `SELECT DISTINCT name FROM employees` returns a set (no duplicates)

## Group Theory and Aggregation

A GROUP BY partitions the set into equivalence classes:
- **Reflexivity**: Each row is in its own group
- **Symmetry**: If A and B share group key, they're in same group
- **Transitivity**: If A shares key with B, and B with C, all three are together

## Equivalence Classes in WHERE
```sql
WHERE x = y AND y = 5
```
The optimizer deduces: x = 5 and y = 5, reducing scan predicates.

## Selectivity Estimation

**Selectivity(s)** = number of distinct values / total rows (for equality)
**Selectivity(r)** = (high - low) / (max - min) (for range)

Example: 1M employees, 50 departments → selectivity for `dept_id = ?` = 0.00002

## Cardinality Estimation (PostgreSQL)
- Statistics collected: `ANALYZE`
- Histograms: 100 equal-height buckets per column
- Most Common Values (MCV): Top N frequent values
- Correlation: Physical ordering vs logical ordering
