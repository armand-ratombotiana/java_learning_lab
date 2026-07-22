# Production Scenarios: Relational Databases (Oracle Focus)

## Scenario 1: Buffer Busy Waits on Hot Block
**Context**: An OLTP system processing 10,000 transactions per second experienced performance degradation.
**Problem**: AWR report showed `buffer busy waits` as the top wait event, consuming 40% of DB time. Application response time increased from 2ms to 200ms.
**Root Cause**: A hot block in an index segment was causing contention. Multiple sessions were simultaneously trying to read/write the same block. The index root block for a sequence-generated primary key was the hot spot.
**Solution**: 1) Identified the hot block using `V$SESSION_WAIT` and `DBA_EXTENTS`. 2) Reversed the index to use reverse key index: `CREATE INDEX ... REVERSE`. 3) Alternatively, used hash-partitioned global index to distribute blocks. 4) For sequence-based contention, increased `CACHE` size on the sequence from 20 to 1000. 5) Monitored AWR after fix to confirm reduction in buffer busy waits.
**Lessons Learned**: Monitor buffer busy waits with AWR. Use reverse key indexes for sequence-generated keys. Increase sequence cache for high-concurrency OLTP. Consider hash partitioning for hot indexes.

## Scenario 2: ORA-01555 Snapshot Too Old
**Context**: A nightly batch job generates a large report by querying 6 hours of transaction data.
**Problem**: The batch job failed with `ORA-01555: snapshot too old` error. The report had to be re-run multiple times to complete.
**Root Cause**: The undo tablespace was undersized (2GB). The query's read consistency needed undo data from 6 hours ago, but undo retention was only 30 minutes. The undo was overwritten before the query completed. The query was a long-running SELECT that crossed undo retention boundaries.
**Solution**: 1) Increased undo tablespace to 16GB. 2) Set `UNDO_RETENTION` to 14400 (4 hours). 3) Enabled `GUARANTEE` mode for undo retention: `ALTER TABLESPACE UNDOTBS1 RETENTION GUARANTEE`. 4) Added `/*+ PARALLEL */` hint to the batch query to complete faster. 5) Optimized the query to reduce execution time using materialized views.
**Lessons Learned**: Size undo tablespace based on longest-running query duration. Set undo retention to exceed max query execution time. Monitor `V$UNDOSTAT` for undo pressure. Use guaranteed undo retention for critical batch windows.

## Scenario 3: Deadlock Chain in Order Management
**Context**: An order management system experienced periodic application hangs.
**Problem**: ASH report showed `enq: TX - row lock contention` as top event. Multiple sessions were in a deadlock chain. Orders were backing up.
**Root Cause**: The application updated the ORDERS table in a non-consistent order across sessions. Session A locked ORDER_001 and waited for ORDER_002, while Session B locked ORDER_002 and waited for ORDER_001. A classic deadlock.
**Solution**: 1) Identified deadlock graph from `V$LOCK` and alert log (ORA-00060). 2) Implemented application-level lock ordering: always lock lower order_id first. 3) Added deadlock retry logic in the application: catch ORA-00060 and retry. 4) Changed update logic to use `SELECT ... FOR UPDATE NOWAIT` to fail fast instead of waiting. 5) Monitored `V$WAIT_CHAIN` for future deadlock detection.
**Lessons Learned**: Enforce consistent lock ordering in application code. Implement retry logic for deadlock errors. Monitor `V$WAIT_CHAIN` proactively. Use NOWAIT to fail fast instead of blocking.

## Scenario 4: Data Guard Failover Fails
**Context**: A primary database crashed due to storage failure. Automatic failover to Data Guard standby was attempted.
**Problem**: The Data Guard failover failed with "ORA-00353: log corruption". The standby could not be activated. RPO was exceeded — 30 minutes of data lost.
**Root Cause**: The standby was running in maximum performance mode with asynchronous redo transport. The last 5 archived redo logs were not yet applied to the standby. One of the logs was corrupted during network transfer. No gaps were detected by `V$ARCHIVE_GAP` because the corrupted log was physically present but had checksum errors.
**Solution**: 1) Identified the gap: `SELECT * FROM V$ARCHIVE_GAP`, `SELECT THREAD#, SEQUENCE#, BLOCKS, BLOCK_SIZE FROM V$ARCHIVED_LOG WHERE STATUS = 'C'`. 2) Used `RECOVER STANDBY DATABASE UNTIL CANCEL` to apply available logs. 3) Skipped the corrupted log using `RECOVER MANAGED STANDBY DATABASE SKIP CORRUPT LOG`. 4) Activated standby with `ALTER DATABASE ACTIVATE STANDBY DATABASE`. 5) Implemented log checksum validation: `DB_LOSSY_CHECKSUM=TRUE` and `DB_BLOCK_CHECKSUM=TYPICAL`.
**Lessons Learned**: Use synchronous redo transport for critical databases. Enable log checksums to detect corruption. Monitor `V$ARCHIVE_GAP` and `V$ARCHIVED_LOG` for gaps. Test failover process quarterly. Document manual failover recovery procedures.
