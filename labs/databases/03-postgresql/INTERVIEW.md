# Interview: PostgreSQL

## Beginner

**Q**: Explain MVCC in PostgreSQL.
**A**: MVCC (Multi-Version Concurrency Control) allows multiple transactions to see different snapshots of data simultaneously. Each tuple stores `xmin` (creating transaction) and `xmax` (deleting transaction). Readers never block writers, and writers never block readers. Old tuple versions remain visible to snapshots that started before the update/deletion.

**Q**: What index types does PostgreSQL support?
**A**: B-tree (default, equality + range), Hash (equality only), GiST (geospatial, full-text), GIN (JSONB, arrays, full-text), SP-GiST (quad-trees, k-d trees), BRIN (large naturally-ordered data).

**Q**: How do you connect Java to PostgreSQL via JDBC?
**A**: Use the URL format `jdbc:postgresql://host:port/database` with the `org.postgresql.Driver` class. Set properties for user, password, SSL parameters.

## Intermediate

**Q**: Describe the PostgreSQL query processing pipeline.
**A**: SQL Text → Parser (tokens → AST) → Analyzer (semantic check, types) → Rewriter (views, rules) → Planner (join orders, scan paths) → Optimizer (cost estimation) → Executor (iterates plan nodes, returns rows).

**Q**: What is autovacuum and how do you tune it?
**A**: Autovacuum automatically reclaims dead tuple storage. Tune via `autovacuum_vacuum_threshold`, `autovacuum_vacuum_scale_factor`, `autovacuum_vacuum_cost_limit`. Set per-table for heavy-update tables: `ALTER TABLE t SET (autovacuum_vacuum_scale_factor = 0.01)`.

**Q**: Explain the difference between `Hibernate.dialect=PostgreSQLDialect` vs `PostgreSQL10Dialect`.
**A**: `PostgreSQL10Dialect` enables identity column support (GENERATED AS IDENTITY), `PostgreSQL95Dialect` enables ON CONFLICT (upsert). Always use the dialect matching your PostgreSQL version.

## Advanced

**Q**: How does HOT (Heap-Only Tuple) update work?
**A**: When an updated row fits in the same page and no indexed columns change, PostgreSQL creates a HOT chain. The new tuple points to the old via `t_ctid`. This avoids index maintenance and reduces bloat. The pruning process removes dead HOT chain members.

**Q**: Design a high-availability PostgreSQL architecture for <1s RPO.
**A**: Use Patroni with etcd/Consul for automatic failover. Configure:
- Synchronous replication (1+ sync standby, `synchronous_commit = on`)
- Replication slots with `max_slot_wal_keep_size`
- `wal_level = logical` for future migration
- pgBackRest for WAL archiving with S3
- Connection pooling via PgBouncer for fast failover detection
- `pg_stat_replication` monitoring for lag measurement

**Q**: How do you debug a query performing slowly in production?
**A**: 1) Get query from slow log or `pg_stat_statements`. 2) Run `EXPLAIN (ANALYZE, BUFFERS, TIMING)`. 3) Check for sequential scans on large tables, high loop counts, inaccurate row estimates. 4) Update statistics with `ANALYZE`. 5) Increase `default_statistics_target` if estimates are wrong. 6) Check for missing indexes or wrong join order. 7) Review `work_mem` for disk sorts/hashes.

**Q**: Explain the PostgreSQL VACUUM process in detail.
**A**: VACUUM scans heap pages, builds a dead tuple list, removes index entries via index AM callbacks, compacts remaining tuples on each page, updates FSM (Free Space Map) and VM (Visibility Map). It does NOT return space to OS (except with `VACUUM FULL` which rewrites the table). Autovacuum uses cost-based throttling to avoid I/O spikes.
