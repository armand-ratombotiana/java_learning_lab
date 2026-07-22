# Production Scenarios: EBS Architecture

## Scenario 1: EBS Application Tier Services Crash After Patch
**Context**: A retail company applied a quarterly EBS patch bundle to their R12.2 production instance.
**Problem**: After the patch, OHS and Forms services crashed on startup. The application was down for 6 hours. `opmnctl` status showed all managed processes in DOWN state.
**Root Cause**: The patch updated `opmn.xml` with incorrect port configurations. The patch also changed `formsweb.cfg` with an incompatible JVM parameter. The `$INST_TOP` configuration was corrupted.
**Solution**: 1) Restored `opmn.xml` and `formsweb.cfg` from the pre-patch backup in `$INST_TOP`. 2) Rolled back the patch using ADOP `abort` phase. 3) Applied the patch in a maintenance window after validating against test environment. 4) Implemented pre-patch config backup automation. 5) Created a health check script that validates OHS and Forms startup before declaring patch success.
**Lessons Learned**: Always backup configuration files before patching. Test patches in a clone environment first. Automate configuration validation after patching. Maintain rollback procedures for every patch cycle.

## Scenario 2: Concurrent Manager Requests Not Processing
**Context**: A financial company's EBS period-end closing was blocked because concurrent requests were not processing.
**Problem**: All concurrent requests showed status "Pending" and never transitioned to "Running" or "Completed". The period-end close could not proceed.
**Root Cause**: The Internal Concurrent Manager was not running. `FND_CONCURRENT_REQUESTS` had no manager assigned to process requests. The concurrent manager log showed `IPC refused connection` errors. The CM port was blocked by a firewall rule added during a recent security audit.
**Solution**: 1) Verified ICM status: `SELECT * FROM FND_CONCURRENT_PROCESSES WHERE QUEUE_APPLICATION_ID = 0`. 2) Restarted ICM via System Administrator responsibility. 3) Checked `FNDLIBR` process on the application tier. 4) Identified that the CM port range (15000-15010) was blocked by firewall. 5) Updated firewall rules to allow ICM communication. 6) Implemented concurrent manager availability monitoring with alerting.
**Lessons Learned**: Monitor concurrent manager health proactively. Document CM port requirements for firewall teams. Test after security audits. Implement automated recovery for ICM failures.

## Scenario 3: Database Corruption After Failed ADOP Cutover
**Context**: A manufacturing company was applying an EBS patch using ADOP during a maintenance window.
**Problem**: The ADOP `cutover` phase failed mid-way, leaving the database in an inconsistent state. The application could not start in either old or new edition. The database was partially upgraded with some objects in the new edition and some still in the old.
**Root Cause**: The ADOP cutover process had a network timeout while updating `FND_EDITION_OBJECTS`. Half of the editioned objects were switched. The cutover did not complete, and `ADOP phase=cutover` could not be re-run without manual cleanup.
**Solution**: 1) Identified the inconsistent objects: `SELECT * FROM DBA_OBJECTS WHERE EDITION_NAME = 'ORA$BASE' AND STATUS = 'INVALID'`. 2) Manually recompiled invalid objects using `UTL_RECOMP`. 3) Re-ran ADOP cutover after verifying object counts matched between editions. 4) Applied patch to a fresh clone and validated, then re-deployed. 5) Implemented ADOP phase monitoring with checkpoint tracking.
**Lessons Learned**: Always monitor ADOP phases with checkpoints. Keep pre-patch backups available. Test ADOP cutover in lower environments first. Implement timeout handling for long-running ADOP operations. Document manual recovery procedures for ADOP failures.

## Scenario 4: EBS Port Exposed to Internet
**Context**: An audit revealed that EBS Forms port 8000 was accessible from the internet.
**Problem**: The Forms listener port (8000) was opened in the firewall for "remote vendor access" and never closed. An attacker could attempt to connect directly to the Forms server, bypassing OHS web tier security.
**Root Cause**: A temporary firewall rule for a vendor support engagement was documented as "remove after 30 days" but was not removed after 6 months. The security team was not notified when the engagement ended.
**Solution**: 1) Immediately closed port 8000 in the firewall. 2) Changed Forms servlet port from default 8000 to a non-standard port. 3) Implemented VPN requirement for all remote EBS access. 4) Implemented network segmentation: EBS is now only accessible from internal networks and VPN. 5) Automated firewall rule review with expiration enforcement.
**Lessons Learned**: Restrict EBS application ports to internal networks only. Implement automated firewall rule review and expiration. Document and audit all temporary access rules. Never expose Forms port directly — use OHS web tier with SSL.
