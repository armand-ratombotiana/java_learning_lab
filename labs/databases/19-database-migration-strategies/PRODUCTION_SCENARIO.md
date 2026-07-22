# Production Scenarios: Database Migration Strategies (Oracle Focus)

## Scenario 1: Character Set Conversion Corrupts Data
**Context**: An Oracle database with `WE8MSWIN1252` character set was being migrated to PostgreSQL with `UTF8`.
**Problem**: After migration, 10% of records showed corrupted characters. Accented characters (é, ü, ñ) appeared as garbage (¿¿¿). Reports that included these fields had errors.
**Root Cause**: The Oracle database stored some characters in Windows-1252 encoding that do not have direct UTF-8 equivalents. Specifically, the "smart quotes" (`\x92` and `\x93`) and some Windows-specific typographic characters were not converted correctly during the migration.
**Solution**: 1) Identified the problem characters using `ASCIISTR()` in Oracle. 2) Cleansed the source data before migration: replaced problematic characters with UTF-8 equivalents. 3) Used a custom character mapping file with AWS DMS. 4) Added data validation step: compare character length and checksums post-migration. 5) Implemented pre-migration character set audit report.
**Lessons Learned**: Test character set conversion with production data samples before full migration. Use Unicode as target encoding. Implement pre-migration data quality checks. Validate data integrity post-migration with character-level comparison.

## Scenario 2: GoldenGate Migration Lag Creates Cutover Difficulty
**Context**: An Oracle to PostgreSQL migration using GoldenGate for real-time CDC.
**Problem**: At the planned cutover time, GoldenGate lag was still 30 minutes. The business could not decide whether to wait or proceed. Waiting meant extending the maintenance window. Proceeding meant 30 minutes of data loss.
**Root Cause**: The target PostgreSQL had insufficient write throughput during the final catch-up phase. The Replicat was applying at 2K TPS while the source was generating 10K TPS. The lag was not reducing.
**Solution**: 1) Paused the source application to stop new transactions. 2) Let GoldenGate catch up (took 10 minutes). 3) Verified lag was zero: `INFO REPLICAT *`. 4) Performed cutover: stopped GoldenGate. 5) For future: implemented a two-phase cutover: stop writes, wait for zero lag, validate, cut over. 6) Increased target write throughput for migration.
**Lessons Learned**: Plan for target catch-up capacity during migration. Allow extra maintenance window time for lag reduction. Implement zero-lag verification before cutover. Monitor GoldenGate lag in real-time during cutover. Have rollback plan if lag exceeds acceptable threshold.

## Scenario 3: Schema Conversion Fails on Complex PL/SQL
**Context**: AWS Schema Conversion Tool (SCT) was used to convert Oracle PL/SQL packages to PostgreSQL.
**Problem**: 30% of PL/SQL packages failed conversion. Complex features like `PIPELINED` functions, `COLLECT` operations, and `MERGE` statements were not supported by the converter. Manual rewriting was needed.
**Root Cause**: SCT cannot convert Oracle-specific features: pipelined table functions, `BULK COLLECT` into PL/SQL collections, `CONNECT BY` hierarchical queries, and `MODEL` clause. These require manual rewriting to PostgreSQL equivalents.
**Solution**: 1) Identified all unconvertable packages from SCT report. 2) Rewrote pipelined functions as PostgreSQL `RETURNS TABLE` functions. 3) Replaced `CONNECT BY` with recursive CTEs. 4) Replaced `MERGE` with `INSERT ... ON CONFLICT`. 5) Replaced `BULK COLLECT` with `ARRAY_AGG`. 6) Implemented automated testing for each converted package.
**Lessons Learned**: Plan for 20-40% manual rewrite of complex PL/SQL. Use SCT for initial conversion and manual fix for complex cases. Allocate 2-3x more time for PL/SQL conversion than initial estimate. Test each converted package with production-like data.

## Scenario 4: Migration Rollback with Inconsistent Data
**Context**: A phased migration kept both Oracle and PostgreSQL active during the cutover phase.
**Problem**: When rollback was triggered (due to performance issues), data written to both databases during the dual-write phase was inconsistent. Oracle had 1000 records that PostgreSQL did not, and vice versa.
**Root Cause**: The dual-write application code wrote to Oracle first, then PostgreSQL. If the PostgreSQL write failed, the application did not roll back the Oracle write. During dual-write, some transactions completed on one side only.
**Solution**: 1) Wrote a reconciliation script to compare Oracle and PostgreSQL records. 2) Identified 2000 inconsistent records across both systems. 3) Resolved each inconsistency: applied missing writes from source to target. 4) Fixed the dual-write logic: use distributed transaction or compensate with retry+audit. 5) Implemented automated reconciliation as a scheduled job during dual-write phase.
**Lessons Learned**: Use idempotent writes in dual-write mode. Implement automated reconciliation during migration. Use transactional outbox pattern instead of dual-write. Test rollback procedures with data consistency validation.
