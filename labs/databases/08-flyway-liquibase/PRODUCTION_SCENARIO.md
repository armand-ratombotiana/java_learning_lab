# Production Scenarios: Database Migrations (Flyway/Liquibase — Oracle Focus)

## Scenario 1: Migration Fails Due to DDL Lock Timeout
**Context**: A Flyway migration was deploying a new column to a large Oracle table (500M rows).
**Problem**: The migration failed with "ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired". The table was locked by a concurrent transaction. The migration could not be retried automatically.
**Root Cause**: The migration used `ALTER TABLE orders ADD (discount NUMBER) MODIFY (status VARCHAR2(30))`. The table was being actively updated by application sessions. Oracle DDL requires an exclusive lock (sch-m) on the table. The lock request used NOWAIT and failed immediately.
**Solution**: 1) Manually waited for the blocking session to complete via `V$LOCK`. 2) Killed the blocking session if necessary: `ALTER SYSTEM KILL SESSION 'sid,serial#'`. 3) Re-ran the migration. 4) Changed migration approach: used `DBMS_REDEFINITION` for online table reorganization for future large ALTERs. 5) Scheduled migrations during maintenance windows with application stopped.
**Lessons Learned**: Use `DBMS_REDEFINITION` for online schema changes on large tables. Schedule migrations during low-traffic periods. Implement lock timeout handling in migration scripts. Monitor blocking locks before migration.

## Scenario 2: Rollback Disaster — Can't Undo Migration
**Context**: A Flyway migration added a NOT NULL column to an Oracle table that had NULL values.
**Problem**: After deployment, the application crashed because INSERT statements did not include the new column. Rollback was attempted but failed because the column had data.
**Root Cause**: The migration added `ALTER TABLE t ADD (new_col NUMBER NOT NULL)` and set a default. Existing rows got the default value. Rollback required dropping the column, but some applications had already inserted rows with the new column populated with non-default values.
**Solution**: 1) Could not roll back because data would be lost. 2) Fixed the application code to include the new column. 3) For the future: added columns as NULLABLE first, then backfilled data, then added NOT NULL constraint in a separate migration. 4) Implemented a "safe add column" pattern: V1: add nullable, V2: backfill, V3: add NOT NULL. 5) Documented undo procedures for every migration.
**Lessons Learned**: Never add NOT NULL columns in one step — use three-step pattern. Always have a tested rollback procedure. Make migrations additive and reversible. Keep migration undo scripts in version control.

## Scenario 3: Out-of-Order Migration Causes Issues
**Context**: Two developers created migrations V2.1 and V2.2 in separate branches. Both were deployed.
**Problem**: The schema ended up with both migrations applied, but V2.2 created a column that V2.1 had already created with a different name. The application used the V2.2 column name but the data was in the V2.1 column.
**Root Cause**: The `flyway.locations` picked up both migrations. V2.1 and V2.2 were both applied because they had different version numbers. There was no governance on migration naming. The out-of-order check was disabled.
**Solution**: 1) Identified the discrepancy using a diff tool comparing DEV and PROD schemas. 2) Created a fix migration (V2.3) to rename the column. 3) Enabled `flyway.out-of-order=false` to prevent future issues. 4) Implemented branch-based isolation: migrations in different feature branches must not share version numbers. 5) Created pre-deployment schema comparison in CI/CD pipeline.
**Lessons Learned**: Always enable `outOfOrder=false` in production. Implement migration naming conventions. Use schema comparison in CI/CD. Review migrations as a team before deployment.

## Scenario 4: Tablespace Full During Migration
**Context**: A migration was adding a large index on a 200GB Oracle table.
**Problem**: The migration failed with "ORA-01653: unable to extend table SYS_xxxxxx by 8192 in tablespace USERS". The tablespace ran out of space. The index was partially built.
**Root Cause**: The index build required 20GB of temporary space. The `USERS` tablespace had only 5GB remaining. The `TEMP` tablespace for sorting was also undersized. Oracle does not roll back partially built indexes automatically.
**Solution**: 1) Added 30GB datafile to USERS tablespace. 2) Added 10GB to TEMP tablespace. 3) Dropped the partially built index: `DROP INDEX idx_name`. 4) Re-ran the migration with increased tablespace. 5) Created tablespace monitoring alert at 80% usage.
**Lessons Learned**: Pre-calculate migration space requirements. Monitor tablespace usage before migrations. Add datafiles preemptively for large index builds. Automate tablespace health checks before deployment.
