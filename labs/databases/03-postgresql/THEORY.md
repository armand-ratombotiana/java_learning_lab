# Theory: PostgreSQL

## Architecture (Process Model)
```
Postmaster (supervisor)
    ├── Backend process (per client connection)
    ├── Writer (shared buffer → disk)
    ├── WAL Writer (WAL buffer → WAL segment)
    ├── Checkpointer (checkpoint)
    ├── Autovacuum (cleanup dead tuples)
    ├── Stats Collector (query statistics)
    ├── Archiver (WAL archiving)
    └── Background workers (extensions, replication)
```

## Shared Memory
- **Shared Buffers**: Cache for data pages (default 128MB, recommend 25% RAM)
- **WAL Buffer**: Write-ahead log cache (default 16MB)
- **CLOG**: Commit log (transaction status)
- **LWLock**: Lightweight locks for shared structures

## MVCC in PostgreSQL
- Each tuple has: `xmin` (creating XID), `xmax` (deleting XID), `ctid` (physical location)
- `t_infomask` bitfield for tuple state
- No UNDO log (unlike Oracle) – VACUUM cleans dead tuples
- `HOT` (Heap-Only Tuple) updates optimize index usage

## Data Type Categories

| Category | Types |
|---|---|
| Numeric | INTEGER, BIGINT, NUMERIC/DECIMAL, REAL, DOUBLE, MONEY |
| Character | VARCHAR(n), CHAR(n), TEXT |
| Binary | BYTEA |
| Date/Time | DATE, TIME, TIMESTAMP, TIMESTAMPTZ, INTERVAL |
| Geometric | POINT, LINE, LSEG, BOX, PATH, POLYGON, CIRCLE |
| Network | INET, CIDR, MACADDR |
| JSON | JSON, JSONB |
| Arrays | TEXT[], INTEGER[], etc. |
| Range | INT4RANGE, TSRANGE, DATERANGE, etc. |
| Bit String | BIT(n), BIT VARYING(n) |
| Text Search | TSVECTOR, TSQUERY |
| UUID | UUID |
| XML | XML |

## Index Types

| Type | Use Case |
|---|---|
| B-tree | Default, equality + range |
| Hash | Equality only |
| GiST | Geometric, full-text, custom |
| GIN | JSONB, arrays, full-text |
| BRIN | Large, physically-ordered tables |
| SP-GiST | Partitioned trees (quadtree, k-d tree) |
