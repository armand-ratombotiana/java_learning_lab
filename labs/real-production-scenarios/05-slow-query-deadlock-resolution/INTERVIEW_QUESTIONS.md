# Lab 05 — Slow Query / Deadlock Resolution: Interview Questions

**Q1: A batch job that normally takes 1 hour now takes 8 hours. What could cause this?**

**Answer:** 1) Missing index on a JOIN column (most common). 2) Stale optimizer statistics causing poor execution plan. 3) Plan regression — optimizer chose a different plan than before. 4) Data volume growth — table grew 10x but query wasn't optimized for it. 5) Cartesian product from missing JOIN condition. 6) Lock contention from concurrent operations. 7) Temp space spill to disk (hash join overflow). 8) Storage performance degradation.

**Q2: How do you read an Oracle execution plan in DBMS_XPLAN?**

**Answer:** Run `SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('sql_id'));`. Look for: TABLE ACCESS FULL on large tables (> 1M rows), HASH JOIN producing more rows than inputs (Cartesian), SORT operations using temp space, high cost estimates. Key columns: Operation (what), Name (table/index), Rows (estimated rows), Cost (relative), Time (estimated). Each indentation level shows parent-child relationship.

**Q3: Explain composite index design. What column order produces the best index?**

**Answer:** Column order matters: 1) Equality conditions first (WHERE col = ?), 2) Range conditions second (WHERE col > ?), 3) ORDER BY columns last. High-selectivity columns (many unique values) before low-selectivity (few unique values). Example: WHERE status='PENDING' AND created_at > ? ORDER BY created_at → index on (status, created_at DESC). Status is low selectivity but used in equality; created_at is range + order.

**Q4: You see "direct path read/write temp" as the top wait event. What does this indicate?**

**Answer:** This indicates the query is spilling to temp space because hash joins or sorts cannot fit in memory. The PGA (Program Global Area) is too small for the operation. Root causes: missing index causing large hash join, insufficient PGA memory, or Cartesian product producing massive intermediate result. Fix: add indexes to reduce data volume, increase PGA_AGGREGATE_TARGET, or rewrite query to avoid large hash joins.

**Q5: How do you detect a plan regression in Oracle?**

**Answer:** 1) Compare AWR reports between baseline (normal execution) and problem execution. 2) Check SQL Plan Baseline to see if plan changed. 3) Query DBA_HIST_SQLSTAT for same SQL_ID across different time periods. 4) Check DBA_HIST_SQL_PLAN to see if plan hash value changed. 5) Use SQL Performance Analyzer to regression-test new plans. Fix: create SQL Plan Baseline to pin the known-good plan, or gather fresh statistics.

**Q6: Design a batch job that processes 50M rows without blocking other operations.**

**Answer:** 1) Use Partition Exchange Loading — load into staging table, swap partition. 2) Process in batches of 10,000 rows with COMMIT between batches. 3) Use batch processing window during low traffic. 4) Implement read-only mode for reporting queries during batch. 5) Use parallel DML with careful resource management. 6) Monitor temp space, undo space, and archive log generation. 7) Add checkpoint and restart capability for the batch job.

**Q7: What is the difference between hash join and nested loop join?**

**Answer:** Hash join: builds hash table on one input (usually smaller table), probes with other input. Efficient for large datasets with no indexes. Requires memory for hash table (may spill to disk). Nested loop: iterates through outer table, looks up each row in inner table using index. Efficient when inner table has index and outer table is small. Hash join good for "large join large" with no index; nested loop good for "small join large" with index on large table.

**Q8: How do you use Oracle hints to fix a suboptimal execution plan?**

**Answer:** Use LEADING hint to force join order (e.g., `/*+ LEADING(t sh) */`). Use USE_NL for nested loop, USE_HASH for hash join. Use INDEX hint to force index usage (`/*+ INDEX(sh IDX_SH_REF) */`). Use PARALLEL for parallel execution. Only use hints as temporary fix — permanent fix is proper indexing, statistics, and query design. Document why hint is needed and set up monitoring to detect when hint is no longer needed.

**Q9: Walk through generating and interpreting an AWR report.**

**Answer:** Run `@$ORACLE_HOME/rdbms/admin/awrrpt.sql`, select snapshot range (before and during problem). Key sections: Load Profile (DB Time vs CPU), Top 5 Timed Events (identify wait class), SQL ordered by Elapsed Time (find problem query), SQL ordered by Disk Reads (I/O issues), I/O Stats (temp file I/O), Advisory Statistics (PGA/SGA recommendations). For a slow batch job, expect: "direct path read temp" as top event, one SQL consuming > 90% of DB time, high disk reads.

**Q10: Your batch job creates a Cartesian product. How do you identify and fix it?**

**Answer:** Identify: the execution plan shows HASH JOIN with rows estimate > sum of both input tables (e.g., 50M × 5M = 250M rows estimate). The SQL likely has a missing JOIN condition — the JOIN clause is missing a key column, or the ON condition is incomplete. Fix: add the missing JOIN condition. Verify: re-examine the plan — rows estimate should be approximately equal to the larger table (for one-to-many) or correct cardinality.

**Q11: How does Oracle's Cost-Based Optimizer decide between full table scan and index scan?**

**Answer:** The CBO compares the cost of each option using: 1) Table statistics: number of rows, blocks, average row length. 2) Index statistics: distinct keys, clustering factor, leaf blocks. 3) System statistics: CPU speed, I/O throughput. 4) Selectivity of predicates: how many rows each WHERE condition filters. Full table scan is cheaper when: large percentage of rows selected (> 5-10%), table is small, index clustering factor is poor, or query requires most columns (making index access expensive).

**Q12: Tell me about a time you resolved a database performance incident. (STAR)**

**Answer:** Situation: Financial settlement batch job expanded from 1 hour to 8+ hours, blocking all downstream processing. Task: As the DBA on call, I needed to restore the batch processing window. Action: I generated an AWR report and found one SQL consuming 99% of DB time with a full table scan on a 50M-row table. The execution plan showed a Cartesian product from a missing join condition in a newly deployed schema change. I added the missing index and used a LEADING hint to fix the plan temporarily while the permanent index was built. Result: Batch job completed in 45 minutes again. I added composite index, set up AWR baseline, and implemented SQL Plan Baseline to prevent regression.

**Q13: How would you set up monitoring to detect batch job performance regression?**

**Answer:** 1) Track batch job duration per run (Grafana + Prometheus). 2) Track SQL elapsed time per batch SQL_ID (AWR snapshots). 3) Alert on duration > 1.5x baseline. 4) Track execution plan hash values — alert if they change. 5) Track optimizer statistics freshness — alert if stats are > 7 days stale. 6) Track temp space usage per query — rising trend indicates plan regression. 7) Track I/O per query — sudden increase indicates full scan where index scan is expected.

**Q14: How does Partition Exchange Loading improve batch performance?**

**Answer:** PEL loads data into a staging table then swaps partitions as a metadata-only operation — milliseconds instead of hours. Benefits: 1) No DML against main table (no locking, no redo). 2) Can rebuild indexes offline on staging table. 3) No fragmentation on main table. 4) Can roll back quickly by swapping back. 5) Zero-downtime data loading. Trade-off: requires partitioned table, additional storage for staging area, careful coordination with concurrent operations.

**Q15: What Oracle tools are essential for query tuning and how do you use them?**

**Answer:** 1) DBMS_XPLAN — display execution plans. 2) AWR (Automatic Workload Repository) — historical performance analysis. 3) ASH (Active Session History) — real-time session analysis. 4) SQL Tuning Advisor — automated index and rewrite recommendations. 5) SQL Monitor — real-time SQL execution monitoring. 6) SQL Plan Baseline — pin known-good plans. 7) SQL Performance Analyzer — regression test plan changes. Learn in this order: DBMS_XPLAN → AWR → ASH → SQL Monitor → SQL Tuning Advisor.
