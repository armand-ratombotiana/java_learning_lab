# Internals: How RDBMS Engines Work Under the Hood

## Page Layout
```
+-----------------------+
| Page Header (24 bytes)|  ← page type, checksum, LSN, free space pointer
+-----------------------+
| Item Pointer Array    |  ← (offset, length) pairs for each row
+-----------------------+
| Free Space            |
+-----------------------+
| Row Data (growing up) |
+-----------------------+
| Special Space         |  ← index-specific data (e.g., rightmost pointer in B-tree)
+-----------------------+
```

## B-Tree Index Structure
```
         [Root: 50, 100]
         /      |       \
  [10,30]   [60,80]   [120,150]
   /   \      /   \      /    \
  ...  ...   ...  ...   ...   ...
```
- **Fan-out**: Hundreds of keys per page (e.g., 400 keys for 8KB page)
- **Height**: Typically 3-4 levels for billions of rows
- **Leaf pages** contain pointers to heap tuples (TID = page+offset)

## MVCC Implementation (PostgreSQL-style)
- Each tuple has `xmin` (creating transaction) and `xmax` (deleting/updating transaction)
- Tuple visibility determined by comparing with transaction snapshot
- Dead tuples removed by **VACUUM** process
- No overwrites – UPDATE = DELETE + INSERT

## WAL (Write-Ahead Log)
```
Transaction Start → WAL record → Flush WAL → Modify page in buffer → Commit → WAL flush
```
- **Redo**: Reapply committed transactions after crash
- **Undo**: Roll back uncommitted transactions (Oracle, MySQL InnoDB)
- PostgreSQL uses REDO only + VACUUM for cleanup

## Lock Manager
- **Lock modes**: shared (S), exclusive (X), update (U), intention locks (IS, IX, SIX)
- **Lock escalation**: Row → Page → Table (memory optimization)
- **Deadlock detection**: Wait-for graph, cycle detection with timeout

## JPA / Hibernate Internals
- **Persistence Context** = first-level cache (Map<EntityClass+Id, Entity>)
- **Dirty checking**: Snapshot comparison on flush
- **Action queue**: Ordered inserts/updates/deletes on flush
- **Batch fetching**: `@BatchSize(size=N)` for N+1 mitigation
