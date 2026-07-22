# Production Scenarios: EBS Security Controls

## Scenario 1: VPD Policy Blocks All Users
**Context**: After applying a security patch, all EBS users received "ORA-28112: failed to execute policy function" when logging in.
**Problem**: No user could access any EBS form or OAF page. The application was completely down.
**Root Cause**: The patch updated `FND_MOBS` policy function, which applies the `ORG_ID` security predicate. The function `FND_SEC_MOBS_POLICY` was invalid due to a missing synonym. The VPD policy was enforced at the database level and could not be bypassed.
**Solution**: 1) Connected as SYS and temporarily disabled VPD policies: `EXEC DBMS_RLS.DROP_POLICY('APPS', 'FND_SEC_MOBS_POLICY')`. 2) Recompiled the invalid function `FND_SEC_MOBS_POLICY`. 3) Restored VPD policy with `DBMS_RLS.ADD_POLICY`. 4) Tested user access. 5) Implemented VPD policy validation script for post-patch verification.
**Lessons Learned**: Always test VPD-related patches in lower environments. Maintain SYS bypass access for emergencies. Document VPD policy disable/enable procedures. Implement automated VPD health checks.

## Scenario 2: Audit Logging Slowing Down Transactions
**Context**: EBS Financials transactions (AP invoice validation) were taking 10x longer than normal.
**Problem**: AP Invoice Validation went from 30 minutes to 5 hours. The database showed high `log file sync` waits.
**Root Cause**: A SOX compliance requirement enabled `FND_AUDIT` on `AP_INVOICES_ALL` and `AP_INVOICE_LINES_ALL` tables. The audit trigger was logging every column change for every row. With 50,000 invoices, the audit table insert per row was causing contention on the `AUDIT$` table.
**Solution**: 1) Identified the bottleneck: `SELECT * FROM V$SESSION_WAIT WHERE EVENT = 'log file sync'`. 2) Reduced audit granularity: log only before-and-after for key columns, not all columns. 3) Moved audit table to a separate tablespace on faster storage (SSD). 4) Implemented batch audit inserts (commit every 100 rows). 5) Indexed audit table by date for faster querying.
**Lessons Learned**: Audit selectively — only critical columns. Move audit tables to dedicated storage. Implement batch auditing for high-volume tables. Monitor audit insert performance. Balance compliance requirements with system performance.

## Scenario 3: Encryption Key Lost, Data Inaccessible
**Context**: A DBA rotated the TDE master encryption key but the old key was not backed up.
**Problem**: After a server crash and database recovery, the TDE-encrypted tablespace could not be opened. `ALTER TABLESPACE XX_SECURE OPEN` failed with "TDE master key not found". Three months of sensitive financial data was inaccessible.
**Root Cause**: The DBA used `ADMINISTER KEY MANAGEMENT SET KEY` without first backing up the previous key. The `$ORACLE_HOME/ssl_wallet` was not included in the RMAN backup. The keystore file (`ewallet.p12`) was lost in the crash.
**Solution**: 1) Searched for any copies of the keystore file on backup tapes. 2) Found a 6-month-old backup of `ewallet.p12` and restored it. 3) Used `ADMINISTER KEY MANAGEMENT IMPORT KEYS` to recover. 4) Restored tablespace with recovered keys. 5) Implemented automated keystore backup as part of RMAN backup strategy. 6) Added keystore file to daily backup manifest.
**Lessons Learned**: Backup keystore files with every key rotation. Include keystore in RMAN backup scripts. Document key rotation procedures. Implement auto-backup keystore configuration. Test keystore recovery procedures regularly.

## Scenario 4: Shared Accounts Enable Unauthorized Access
**Context**: Five users were sharing the same EBS account "AP_CLERK" for AP invoice processing.
**Problem**: An internal audit found $2M in fraudulent invoices processed. The shared account could not identify who processed the fraudulent transactions. The company could not support a forensic investigation.
**Root Cause**: The EBS account "AP_CLERK" was created as a shared generic account. Password was shared via email. No individual user accountability existed. The `FND_SIGNON_AUDIT` was disabled.
**Solution**: 1) Immediately disabled the shared AP_CLERK account. 2) Created individual user accounts for each of the 5 clerks. 3) Assigned unique responsibilities to each user. 4) Enabled Sign-On Audit: `FND_SIGNON_AUDIT_INSERT`. 5) Implemented mandatory periodic password changes. 6) Trained users on account security policies.
**Lessons Learned**: Never use shared accounts in EBS. Enforce individual user accountability. Enable sign-on audit logging. Implement SOD review for all user accounts. Conduct periodic user access reviews.
