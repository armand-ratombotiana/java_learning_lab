# PostgreSQL Internals

## Page Layout
```
+---------------------------+
| PageHeaderData (24 bytes) |  ← pd_lsn, pd_checksum, pd_lower, pd_upper
+---------------------------+
| ItemIdData array          |  ← (lp_off, lp_flags, lp_len) per tuple
+---------------------------+
| Free space                |
+---------------------------+
| HeapTupleData (growing up)|
+---------------------------+
| Special space             |  ← index-specific (e.g., B-tree rightmost link)
+---------------------------+
```

## Tuple Header
- **xmin**: Transaction ID that created this tuple
- **xmax**: Transaction ID that deleted/updated this tuple
- **t_cid**: Command ID within the creating transaction
- **t_ctid**: Points to next tuple version (for HOT updates)

## MVCC Visibility Rules
```
Visibility = (xmin committed AND xmin < snapshot.xmin) OR
             (xmin = current_tx AND xmax IS NULL) OR
             (xmin committed AND xmax IS NULL)
```

## WAL Record Format
```
+------------------+----------------+------------------+
| XLogRecord (24B) | Block references | Main data       |
+------------------+----------------+------------------+
```
- **LSN (Log Sequence Number)**: Byte offset in WAL file
- **Redo/Undo**: PostgreSQL uses REDO-only (no undo needed due to MVCC)
- **Full Page Writes**: Entire pages logged during first modification after checkpoint

## Vacuum Process
1. Scans heap pages for dead tuples
2. Removes index entries pointing to dead tuples
3. Defragments pages (compacts remaining tuples)
4. Updates Free Space Map (FSM)
5. Updates visibility map (VM) for index-only scans

## Buffer Manager
- **Clock-sweep algorithm**: Eviction strategy approximating LRU
- **Shared hash table**: Maps (relation, fork, block) to buffer descriptors
- **Dirty pages**: Flushed by background writer and checkpointer

## JPA / Hibernate with PostgreSQL specifics
- **Sequence-based ID generation**: `GenerationType.SEQUENCE` preferred (no table locking)
- **Native arrays**: `@Type(ArrayType.class)` for PostgreSQL array columns
- **JSONB**: `@Type(JsonBinaryType.class)` from hibernate-types
