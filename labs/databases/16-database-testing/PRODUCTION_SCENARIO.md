# Production Scenarios: Database Testing (Oracle Focus)

## Scenario 1: SPA Finds 20% Queries Regressed After Upgrade
**Context**: An Oracle database was being upgraded from 19c to 23c.
**Problem**: SQL Performance Analyzer (SPA) reported that 20% of the SQL workload had regressed after the upgrade. The worst query went from 50ms to 5000ms.
**Root Cause**: The optimizer in 23c changed cardinality estimation for some predicates. The worst regression was a query using `WHERE status = 'ACTIVE'` — the new optimizer estimated 30% selectivity instead of 0.1%, causing a table scan instead of index scan.
**Solution**: 1) Used SPA to identify the 20% regressed SQLs. 2) Fixed each regressed SQL: added SQL Plan Baseline for the 19c plan. 3) For the worst query, used `DBMS_SPM.LOAD_PLANS_FROM_SQLSET` to import the pre-upgrade plan. 4) Evolved the baselines and verified performance. 5) Re-ran SPA to confirm all regressions were resolved.
**Lessons Learned**: Always run SPA before and after database upgrades. Fix regressions with SPM baselines before production cutover. Plan for 2-4 weeks of SQL tuning after major upgrades. Keep pre-upgrade SQL tuning set for comparison.

## Scenario 2: Database Replay Shows 50% Drop in Throughput
**Context**: A system change (storage migration) was tested using Oracle Database Replay.
**Problem**: Database Replay in a test environment showed 50% throughput reduction compared to production. The change was blocked from production deployment.
**Root Cause**: The storage migration moved Oracle datafiles from high-performance SSD to lower-cost HDD storage. The replay workload captured from production showed high `db file sequential read` waits on the HDD storage. Disk latency was 10ms vs 1ms on SSD.
**Solution**: 1) Identified root cause via AWR comparison between captured and replay runs. 2) Migrated datafiles back to SSD storage. 3) Re-ran Database Replay to verify throughput was restored. 4) For future storage migrations, used automatic storage management (ASM) with SSD tiering. 5) Included Database Replay in all infrastructure change processes.
**Lessons Learned**: Use Database Replay to test infrastructure changes before production. Compare AWR between captured and replay workloads. Test storage changes with production-like I/O. Implement SSD storage for Oracle datafiles.

## Scenario 3: PL/SQL Unit Tests Failing in Production
**Context**: PL/SQL unit tests using utPLSQL passed in development and testing but failed in production.
**Problem**: A function `calculate_tax(amount, region)` returned wrong values for specific regions in production. Tests had passed because test data did not include those regions.
**Root Cause**: The test data in DEV only included regions A, B, C. Production had regions A through Z. Region Z had a special tax rule that was not handled in the function. The test coverage was poor because it did not test all possible regions.
**Solution**: 1) Fixed the `calculate_tax` function to handle region Z. 2) Copied production data subset to DEV for comprehensive testing. 3) Added test cases for all possible regions using production-distilled data. 4) Implemented code coverage analysis: `utPLSQL.coverage.start()`. 5) Added data profiling: compare data distributions between environments.
**Lessons Learned**: Use production-like data for testing. Measure code coverage in PL/SQL tests. Test with all possible input combinations. Implement data profiling to identify environment differences.

## Scenario 4: Load Test Causes ORA-1555 — Undo Exhaustion
**Context**: A load test was run using Swingbench against an Oracle database.
**Problem**: The load test hit 50% of target TPS before failing with `ORA-01555: snapshot too old`. The load test could not reach the target throughput.
**Root Cause**: The undo tablespace was sized for normal OLTP (4GB). The load test generated 10x the normal transaction rate without adjusting undo sizing. The undo retention of 900 seconds required more undo space than available. Queries were seeing snapshots too old.
**Solution**: 1) Increased undo tablespace to 32GB for the load test. 2) Set `UNDO_RETENTION` to 1800 seconds. 3) Added `GUARANTEE` retention to prevent overwriting. 4) Re-ran the load test successfully reaching target TPS. 5) Documented the undo sizing for production: calculated undo size = `(undo_retention × undo_blocks_per_sec × block_size)`.
**Lessons Learned**: Size undo tablespace for peak load, not average. Test undo sizing during load tests. Monitor `V$UNDOSTAT` for undo pressure during testing. Document undo sizing calculations for production capacity planning.
