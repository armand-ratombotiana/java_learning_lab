# Flashcards: PostgreSQL

## Card 1
**Q**: What is the default PostgreSQL isolation level?
**A**: READ COMMITTED

## Card 2
**Q**: What does `xmin` track in PostgreSQL MVCC?
**A**: The transaction ID that created the tuple

## Card 3
**Q**: Which index type is best for JSONB queries?
**A**: GIN (Generalized Inverted Index)

## Card 4
**Q**: What is a TOAST table used for?
**A**: Storing large values (>2KB) out of line, with compression

## Card 5
**Q**: What is the difference between `->` and `->>` in JSONB?
**A**: `->` returns JSONB, `->>` returns text

## Card 6
**Q**: What port does PostgreSQL listen on by default?
**A**: 5432

## Card 7
**Q**: What does `VACUUM` do?
**A**: Reclaims storage from dead tuples, updates free space map

## Card 8
**Q**: What is WAL?
**A**: Write-Ahead Log – records all changes before data page writes

## Card 9
**Q**: What is the recommended `shared_buffers` size?
**A**: ~25% of total RAM

## Card 10
**Q**: What does `EXPLAIN ANALYZE` show?
**A**: Query plan with actual execution times and row counts

## Card 11
**Q**: What is a CTE (WITH query)?
**A**: Common Table Expression – temporary named query result

## Card 12
**Q**: What is `pg_stat_statements` used for?
**A**: Tracking query execution statistics (calls, time, rows)

## Card 13
**Q**: How does streaming replication work?
**A**: Primary ships WAL segments to standby in real-time

## Card 14
**Q**: What isolation level prevents phantom reads?
**A**: REPEATABLE READ or SERIALIZABLE

## Card 15
**Q**: What is the difference between `BIGSERIAL` and `SERIAL`?
**A**: `BIGSERIAL` is 8-byte (max ~9 quintillion), `SERIAL` is 4-byte (max ~2.1B)

## Card 16
**Q**: What does `CREATE INDEX CONCURRENTLY` do differently?
**A**: Creates index without blocking writes on the table

## Card 17
**Q**: What is a partial index?
**A**: Index on only rows matching a WHERE condition

## Card 18
**Q**: What is the purpose of `pg_hba.conf`?
**A**: Host-based authentication – controls client access

## Card 19
**Q**: What is BRIN index best for?
**A**: Large tables with naturally ordered data (e.g., time-series)

## Card 20
**Q**: What is RLS (Row-Level Security)?
**A**: Policy-based row access control within a table
