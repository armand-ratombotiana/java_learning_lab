# Production Scenarios: EBS Upgrade and Migration

## Scenario 1: ADOP Cutover Fails, Edition Switch Incomplete
**Context**: A company was applying a critical security patch using ADOP in the production EBS R12.2 environment.
**Problem**: The ADOP `cutover` phase failed at 95% with "Edition switch timed out. Edition not fully activated." The application was unavailable. The patch could neither be completed nor rolled back cleanly.
**Root Cause**: The cutover phase switches the edition from the patch edition to the production edition. A long-running background job held a lock on `FND_EDITION_OBJECTS`, preventing the edition switch. The ADOP timeout was 30 minutes.
**Solution**: 1) Identified the blocking session: `SELECT * FROM V$LOCK WHERE TYPE = 'TO'`. 2) Killed the blocking session using `ALTER SYSTEM KILL SESSION`. 3) Re-ran `adop phase=cutover` with `option=force_edition_switch`. 4) Re-ran `adop phase=finalize` to complete the patch. 5) Implemented pre-cutover checks: ensure no long-running jobs before cutover. 6) Increased ADOP cutover timeout to 60 minutes.
**Lessons Learned**: Kill long-running jobs before ADOP cutover. Monitor edition locks during cutover. Pre-allocate sufficient timeout for cutover phase. Document ADOP cutover failure recovery procedures. Test ADOP in environments that mirror production load.

## Scenario 2: Post-Upgrade Performance Degradation
**Context**: An EBS upgrade from R12.1.3 to R12.2.10 was completed successfully.
**Problem**: After the upgrade, key concurrent programs and forms ran 3-5x slower than before the upgrade. Period-end close took 12 hours instead of 4 hours.
**Root Cause**: The upgrade process invalidated all database objects. When objects were recompiled, the optimizer used the old R12.1 statistics. The `DBMS_STATS` were stale. The new R12.2 optimizer features (adaptive plans, SQL plan directives) were not properly initialized.
**Solution**: 1) Gathered fresh statistics for all schemas: `EXEC DBMS_STATS.GATHER_DATABASE_STATS(OPTIONS => 'GATHER AUTO')`. 2) Flushed the shared pool: `ALTER SYSTEM FLUSH SHARED_POOL`. 3) Re-ran concurrent programs and compared execution times. 4) Enabled SQL Plan Management to capture good plans. 5) Implemented post-upgrade performance baseline collection.
**Lessons Learned**: Always gather fresh statistics immediately after upgrade. Create performance baselines before and after upgrade. Plan for post-upgrade tuning period. Monitor SQL execution plans for regressions.

## Scenario 3: Cloud Migration Fails, Rollback Required
**Context**: A company was migrating EBS from on-premise to OCI using a lift-and-shift approach.
**Problem**: After migrating the application tier to OCI, users experienced 5-second latency for forms operations. The migration was supposed to be transparent. Performance was unacceptable.
**Root Cause**: The EBS application tier in OCI was in a different region (us-ashburn) than the on-premise database (their datacenter in us-west). The network latency between the application and database tiers was 80ms. EBS Forms is latency-sensitive and requires <5ms between tiers.
**Solution**: 1) Immediately rolled back to on-premise by pointing the application tier DNS back to the on-premise load balancer. 2) Re-planned the migration: co-locate application and database in the same OCI region. 3) Used OCI FastConnect for low-latency connection. 4) Migrated both application and database tiers together in the same availability domain. 5) Validated latency before production cutover.
**Lessons Learned**: Co-locate EBS application and database tiers in the same region. Validate network latency before migration. Have rollback plan for every migration step. Use dedicated connectivity (FastConnect/DirectConnect) for EBS. Test performance with representative workloads before cutover.

## Scenario 4: Migration Data Intercepted During Cloud Transfer
**Context**: A company transferred EBS database backup files to OCI for cloud migration.
**Problem**: An audit review found that the database backup files were transferred over the public internet without encryption. The files contained sensitive financial and PII data. Compliance violation was flagged.
**Root Cause**: The migration used `scp` without encryption enabled (`scp -o "StrictHostKeyChecking=no"` disabled warning but data was still in plaintext). The backup files were not encrypted before transfer. The network path traversed public internet segments.
**Solution**: 1) Immediately cancelled the in-progress transfer. 2) Encrypted backup files using TDE before transfer. 3) Used OCI FastConnect private peering for all data transfer. 4) Implemented `rsync` over SSH with data encryption verification. 5) Engaged security team for penetration testing of data transfer path. 6) Created secure data transfer protocol for all future migrations.
**Lessons Learned**: Encrypt all data before transfer over networks. Use private network connections for production data movement. Implement data transfer security as part of migration runbook. Conduct security review of migration procedures before execution.
