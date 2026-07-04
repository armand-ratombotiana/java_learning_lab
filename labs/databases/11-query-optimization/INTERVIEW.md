# Interview: Query Optimization

## Common Questions

**Q:** How do you identify and fix a slow query?
**A:** 1) Find it via pg_stat_statements or slow query log. 2) Run EXPLAIN (ANALYZE, BUFFERS). 3) Identify the bottleneck (seq scan, sort, join). 4) Add missing indexes, rewrite the query, or update statistics. 5) Verify with another EXPLAIN ANALYZE.

**Q:** Explain how B-tree indexes work.
**A:** Balanced tree with branching factor ~100-500. Leaf pages contain index entries + pointers to heap tuples. Search, insert, delete are O(log N). Supports equality, range, prefix matching, and sorting.

**Q:** How does the database optimizer choose between index scan and seq scan?
**A:** Based on statistics and cost estimation. Index scan is chosen when the estimated number of matching rows is small (<5-10% of table). Seq scan is preferred for larger fractions because random I/O costs more than sequential.

**Q:** What's the difference between a hash join and a merge join?
**A:** Hash join builds a hash table on one relation and probes it – good for equi-joins on medium tables. Merge join requires sorted inputs and works well for large tables or pre-sorted data.

**Q:** How do you troubleshoot N+1 queries in a Spring Data JPA application?
**A:** Enable SQL logging (`spring.jpa.show-sql=true`), count queries, identify lazy loads in loops. Fix with `JOIN FETCH` in `@Query`, `@EntityGraph`, or `@BatchSize`. Verify query count drops from N+1 to 1.
