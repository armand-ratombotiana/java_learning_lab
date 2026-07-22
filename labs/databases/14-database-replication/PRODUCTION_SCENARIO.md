# Production Scenarios: Database Replication (Oracle Focus)

## Scenario 1: Data Guard Gap Not Resolving
**Context**: An Oracle Data Guard physical standby database showed increasing gap with the primary.
**Problem**: The gap grew from 0 to 500 archive logs over 24 hours. `V$ARCHIVE_GAP` showed multiple gaps. The standby was increasingly stale.
**Root Cause**: The archive log deletion policy on the primary (`RMAN CONFIGURE ARCHIVELOG DELETION POLICY TO BACKED UP 1 TIMES TO DEVICE TYPE DISK`) was deleting archive logs after one backup. The standby had not yet applied some of these logs. The gap could not be filled because the logs were deleted from the primary.
**Solution**: 1) Restored missing archive logs from RMAN backup: `RMAN> RESTORE ARCHIVELOG FROM LOGSEQ 1000 UNTIL LOGSEQ 1500`. 2) Registered the restored logs on the standby. 3) Applied the gap: `RECOVER MANAGED STANDBY DATABASE DISCONNECT`. 4) Changed deletion policy: `ARCHIVELOG DELETION POLICY TO APPLIED ON ALL STANDBY`. 5) Implemented gap monitoring: alert when gap > 10 logs.
**Lessons Learned**: Never delete archive logs until applied on all standbys. Use `APPLIED ON ALL STANDBY` deletion policy. Monitor `V$ARCHIVE_GAP` proactively. Maintain longer archive log retention for gap recovery.

## Scenario 2: GoldenGate Replication Lag
**Context**: Oracle GoldenGate replicating data from OLTP to a reporting database in real-time.
**Problem**: Reporting queries showed data that was 30 minutes stale. GoldenGate lag monitor showed 1800 seconds of lag during peak hours.
**Root Cause**: The Extract process was reading 10K TPS from the OLTP system's redo logs, but the Replicat process could only apply 2K TPS to the target. The target had no indexes matching the OLTP system. The Replicat was single-threaded for a large volume table.
**Solution**: 1) Increased Replicat parallelism: used integrated Replicat with `MAX_APPLY_PARALLELISM 8`. 2) Added indexes on target to match OLTP primary key lookups. 3) Batched operations: increased Replicat's `BATCHTRANSOPS` to 1000. 4) Tuned Extract: used `TRANLOGOPTIONS EXTRACTUSER` for faster log mining. 5) Monitored lag: `INFO REPLICAT *, DETAIL`.
**Lessons Learned**: Tune GoldenGate parallelism for high-throughput replication. Ensure target has matching indexes. Use integrated Replicat for parallel apply. Monitor Replicat lag per table. Size trail files appropriately.

## Scenario 3: RAC Node Failure — Data Guard Failover Mistakenly Initiated
**Context**: Oracle RAC with 2 nodes and a Data Guard physical standby.
**Problem**: One RAC node experienced a network timeout for 30 seconds. The Data Guard broker incorrectly detected a primary failure and initiated a failover to the standby. The standby became the new primary. The old primary (still running) became an orphan.
**Root Cause**: The `DataGuard` broker's `FastStartFailoverThreshold` was set to 30 seconds. The RAC node's network blip exceeded this threshold. The observer (third server) confirmed the primary was "down" and triggered failover. The RAC node was actually still processing transactions.
**Solution**: 1) Restored the old primary as a standby using `ALTER DATABASE RECOVER MANAGED STANDBY DATABASE`. 2) Increased `FastStartFailoverThreshold` to 120 seconds. 3) Implemented observer on a separate network path. 4) Configured `DataGuard` with `DelayMinsDbFlashback=60` to allow flashback recovery. 5) Tested failover scenarios to validate threshold settings.
**Lessons Learned**: Set appropriate failover thresholds for transient network issues. Use flashback database for easy standby recovery. Place observers on diverse network paths. Test failover scenarios to avoid false positive failovers. Monitor RAC interconnect health.

## Scenario 4: Bidirectional GoldenGate Conflict
**Context**: Two data centers, both accepting writes, with Oracle GoldenGate bidirectional replication between them.
**Problem**: The same customer record was updated in both data centers simultaneously. GoldenGate flagged a conflict. Replication stopped for that table.
**Root Cause**: The application allowed the same customer to be updated in either data center. Both updates happened within seconds of each other. GoldenGate's default conflict resolution (`USEMAX`) picked the later timestamp, but the timestamps were identical (same second). The transaction was logged as an error in `GGSEXIT`.
**Solution**: 1) Resolved the conflict: applied the change from Data Center A and re-synced Data Center B. 2) Implemented conflict resolution: `MAP owner.table, TARGET owner.table, COMPARECOLS (ALL), RESOLVECONFLICT (INSERTROW, USEMAX(LAST_UPDATED))`. 3) Added application-level routing: a specific data center owns writes for each customer (active-active split). 4) Used `USEDELTA` resolution: compare individual column values and merge differences. 5) Monitored `GGSEXIT` log for conflict detection and alerting.
**Lessons Learned**: Active-active replication requires conflict resolution strategy. Use application-level write routing when possible. Test conflict resolution with realistic scenarios. Monitor GoldenGate replication errors in real-time. Maintain conflict resolution procedures.
