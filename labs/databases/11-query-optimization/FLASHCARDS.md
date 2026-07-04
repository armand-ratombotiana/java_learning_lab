# Flashcards: Query Optimization

**Q:** What is a Seq Scan?
**A:** Full sequential table scan – reads every row from disk.

**Q:** What is an Index Scan?
**A:** Index lookup to find matching rows, then fetches from heap.

**Q:** What is an Index Only Scan?
**A:** All needed data is in the index itself – no heap visit needed.

**Q:** What does `EXPLAIN ANALYZE` do?
**A:** Shows execution plan WITH actual execution times and row counts.

**Q:** What is the N+1 problem?
**A:** One parent query + N child queries (one per parent row).

**Q:** How to fix N+1 in JPA?
**A:** `JOIN FETCH`, `@EntityGraph`, or `@BatchSize`.

**Q:** What is a composite index?
**A:** Index on multiple columns. Column order matters!

**Q:** What is a covering index?
**A:** Index that includes all columns needed by a query (plus INCLUDE columns).
