# Production Scenarios: Database Security (Oracle Focus)

## Scenario 1: TDE Master Key Lost After Server Recovery
**Context**: An Oracle database with TDE encrypted tablespaces needed to be recovered to a new server.
**Problem**: After restoring the database to a new server, the TDE-encrypted tablespaces could not be opened. `ALTER TABLESPACE USERS OPEN` failed with "TDE master key not found". All encrypted data was inaccessible.
**Root Cause**: The TDE master key is stored in an Oracle Wallet (EWALLET.P12 file). The wallet file was not backed up as part of the RMAN backup strategy. The new server did not have the wallet file. Without the master key, TDE cannot decrypt the tablespace encryption keys.
**Solution**: 1) Located the original wallet file from a separate backup of `$ORACLE_BASE/admin/$ORACLE_SID/wallet`. 2) Copied the wallet file to the new server. 3) Opened the wallet: `ADMINISTER KEY MANAGEMENT SET KEYSTORE OPEN`. 4) Verified encrypted tablespaces were accessible. 5) Implemented automated wallet backup as part of RMAN backup. 6) Added wallet file to disaster recovery runbook.
**Lessons Learned**: Always include Oracle Wallet in backup and DR strategy. Test TDE recovery procedures regularly. Document wallet location and backup procedures. Implement automated wallet backup monitoring.

## Scenario 2: VPD Policy Causing Performance Regression
**Context**: A new VPD policy was deployed to enforce row-level security for a multi-tenant application.
**Problem**: After deploying the VPD policy, all queries on the ORDERS table slowed down by 10x. AWR showed high CPU usage from the VPD policy function execution.
**Root Cause**: The VPD policy function was called for every query on the ORDERS table. The function performed a complex lookup to determine the user's tenant: querying 5 tables with joins. Each query on ORDERS (even simple SELECT COUNT(*)) executed the policy function, which added 50ms of overhead.
**Solution**: 1) Simplified the policy function: cached the tenant ID in a package variable (`APPS.XX_TENANT_ID`) rather than querying each time. 2) Used `DBMS_RLS.ADD_POLICY` with `STATIC_POLICY=TRUE` for tenant-based security. 3) Created a session-level context: `DBMS_SESSION.SET_CONTEXT('TENANT_CTX', 'TENANT_ID', tenant_id)`. 4) Ensured policy function runs only once per session, not per query. 5) Monitored VPD overhead via `V$SQL` and `DBMS_RLS` statistics.
**Lessons Learned**: Optimize VPD policy functions — cache results. Use session contexts for static security attributes. Use static policies when security predicates don't change per query. Test VPD performance with realistic query volumes.

## Scenario 3: Database Vault Blocking DBA Maintenance
**Context**: Oracle Database Vault was enabled to prevent privileged user access to application data.
**Problem**: The DBA could not run `ALTER TABLE` to add a column for a critical application fix. Database Vault blocked the DDL because the DBA was not an authorized realm owner.
**Root Cause**: Database Vault realms protect application schemas. The DBA user (SYSTEM) was not added as a realm participant for the `APPS` schema. Database Vault's realm enforcement blocked any DDL on protected objects by non-authorized users.
**Solution**: 1) Temporarily disabled the realm: `EXEC DVSYS.DBMS_MACADM.DELETE_REALM('APPS_REALM')` — had to use `DV_OWNER` role. 2) Added the DBA user to the realm as authorized: `DBMS_MACADM.ADD_AUTH_TO_REALM('APPS_REALM', 'SYSTEM', NULL)`. 3) Re-ran the `ALTER TABLE` successfully. 4) Created a Database Vault maintenance procedure: schedule maintenance with DV_OWNER. 5) Documented Database Vault procedures for all DBA operations.
**Lessons Learned**: Plan Database Vault realms to include authorized maintenance accounts. Document DV bypass procedures for emergency maintenance. Schedule maintenance during DV owner shifts. Test DD operations with DV enabled in staging.

## Scenario 4: Unified Auditing Fills SYSTEM Tablespace
**Context**: Unified Auditing was enabled to capture all database activity for SOX compliance.
**Problem**: The SYSTEM tablespace grew by 50GB in 24 hours, filling the tablespace. The database crashed with "ORA-01653: unable to extend table". Queries stopped working.
**Root Cause**: The audit policy was configured as `AUDIT POLICY ALL_ACTIONS` which audited every SQL statement (SELECT, INSERT, UPDATE, DELETE). With 10K TPS, the audit trail grew at 50GB/day. The audit records were stored in the SYSTEM tablespace by default.
**Solution**: 1) Purged old audit records: `BEGIN DBMS_AUDIT_MGMT.CLEAN_AUDIT_TRAIL(cleanup_interval => 48); END;`. 2) Added 100GB datafile to SYSTEM tablespace. 3) Moved audit trail to a dedicated tablespace: `DBMS_AUDIT_MGMT.SET_AUDIT_TRAIL_LOCATION`. 4) Reduced audit scope: audited only DDL and failed logins instead of all SQL. 5) Implemented audit trail archiving and purging schedule.
**Lessons Learned**: Size audit tablespace based on workload volume. Move audit trail to a dedicated tablespace. Limit audit scope to relevant events. Implement audit archiving and purging. Monitor audit tablespace growth.
