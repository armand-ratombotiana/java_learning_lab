# Lab 05 — Slow Query / Deadlock: LeetCode Connections

## Algorithmic Concepts

| Concept | Query Optimization Application |
|---------|-------------------------------|
| Index vs. sequential scan | Binary search in sorted array vs. linear scan |
| Composite index design | Multi-level sorting — sorting by multiple columns |
| Nested loop join | For each element in list A, find matching in sorted list B |
| Hash join | Build hash set from small list, probe with large list |
| Merge join | Merge two sorted lists |
| Cartesian product | Cross join — O(n*m) for all pairs |
| Partition pruning | Divide and conquer on partitioned data |

## LeetCode Problems

**Q1: Binary Search (LeetCode 704) — Index Scan vs. Full Scan**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Binary search in sorted array |
| **Query Connection** | An index range scan is binary search on the index B-tree. A full table scan is linear search on the table. Binary search (index) is O(log n), linear (full scan) is O(n). This is why indexes make queries fast. |
| **Algorithmic Lesson** | Binary search = index scan. Linear search = full table scan. The choice depends on selectivity and data size. |

**Q2: Merge Sorted Array (LeetCode 88) — Merge Join**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Merge two sorted arrays efficiently |
| **Query Connection** | A merge join is exactly this algorithm: sort both inputs by the join key, then walk through both simultaneously, matching rows. It's efficient for large datasets when inputs are already sorted. |
| **Algorithmic Lesson** | Merge join is O(n+m) for sorted inputs. Hash join is O(n+m) but requires hash table memory. Nested loop is O(n*m) for unindexed inner table. |

**Q3: Two Sum (LeetCode 1) — Hash Join**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find two numbers summing to target |
| **Query Connection** | The hash map approach (store values for O(1) lookup) is exactly how hash joins work: build a hash table on one table, probe with the other. The "build" table is the smaller input; the "probe" table is the larger. |
| **Algorithmic Lesson** | Hash join = build hash on small table, probe with large. The hash table must fit in memory — otherwise it spills to disk (temp space). |

**Q4: Kth Largest Element in an Array (LeetCode 215) — Top-N Queries**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find the Kth largest element |
| **Query Connection** | SQL's ORDER BY ... LIMIT N is similar — finding the top N rows. Without an index, Oracle must sort the entire result set (O(n log n)). With an index on the ORDER BY column, it can do an index range scan in reverse to read only N rows (O(N)). |
| **Algorithmic Lesson** | Sorting is expensive. Indexes on ORDER BY columns eliminate the sort operation. Quickselect finds Kth element in O(n) average, similar to how an index can find the Kth value. |

**Q5: Design In-Memory File System (LeetCode 588) — Partitioning**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Design filesystem with directories and files |
| **Query Connection** | Partitioning is like organizing files into directories. When you query with a WHERE clause, partition pruning restricts which "directories" (partitions) to scan — just like directory-level file search. |
| **Algorithmic Lesson** | Hierarchical organization (filesystem, partition tree) enables efficient pruning. The partition key is the top-level directory — queries without it need to scan all partitions. |

**Q6: Find Median from Data Stream (LeetCode 295) — Optimizer Statistics**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Maintain median of streaming data |
| **Query Connection** | The optimizer needs statistics (histograms) to estimate selectivity — which is like estimating the median. Without histograms, the optimizer assumes uniform distribution, leading to bad cardinality estimates and suboptimal plans. |
| **Algorithmic Lesson** | Accurate statistics are essential for optimal decisions. The optimizer is only as good as its statistics — garbage in, garbage out. |

**Q7: Interval List Intersections (LeetCode 986) — Partition Pruning**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find intersections between two interval lists |
| **Query Connection** | Partition pruning is similar — the query specifies a date range, and the optimizer only scans partitions whose date ranges overlap. This avoids scanning irrelevant partitions. |
| **Algorithmic Lesson** | Interval intersection is the algorithmic foundation of partition pruning. Understanding how to efficiently find overlapping intervals helps design effective partition strategies. |

**Q8: All O`one Data Structure (LeetCode 432) — Clustering Factor**

| Aspect | Connection |
|--------|-----------|
| **Problem** | O(1) get max/min with inc/dec operations |
| **Query Connection** | The clustering factor measures how closely the index order matches the table row order. If index entries are in the same order as table rows (low clustering factor), index range scans are efficient. If not, each index entry requires a separate I/O to fetch the table row, making index access more expensive. |
| **Algorithmic Lesson** | Good clustering factor = index efficient. High clustering factor = index may be worse than full scan. Reorganizing tables improves clustering factor. |
