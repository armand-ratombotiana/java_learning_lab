# Production Scenarios: APEX SQL Workshop

## Scenario 1: Data Workshop Import Corrupts Existing Records
**Context**: A manufacturing company used Data Workshop to import daily inventory updates from suppliers.
**Problem**: The import silently updated existing records with NULL values for all non-mapped columns. Inventory levels appeared as NULL across thousands of products, causing downstream supply chain systems to fail.
**Root Cause**: The CSV file contained only three columns (SKU, quantity, date), but Data Workshop "Update" mode was selected with "Replace all values" enabled. Columns not present in the CSV were set to NULL rather than preserved.
**Solution**: 1) Restored inventory data from the previous day's RMAN backup. 2) Changed the import process to use "Update only mapped columns" option. 3) Implemented a PL/SQL wrapper using `APEX_DATA_PARSER` in a process that validates all columns before update. 4) Added pre-import validation to reject CSVs with missing critical columns.
**Lessons Learned**: Always use "Update only mapped columns" for partial CSV imports. Never use "Replace all values" for production imports. Add pre-import validation and always take a backup before bulk data operations.

## Scenario 2: Long-Running Query Blocks SQL Workshop
**Context**: A financial analyst ran a query joining 6 tables without filters in SQL Workshop during peak hours.
**Problem**: The query consumed 100% CPU across 8 RAC nodes for 45 minutes. Other users could not log in. The database session could not be killed immediately.
**Root Cause**: The query performed a Cartesian join between two large fact tables (50M and 200M rows) without any WHERE clause. The query returned 10^16 rows theoretically. Oracle's sort area was exhausted, leading to massive temporary tablespace usage.
**Solution**: 1) Identified the blocking session via `V$SESSION` and `V$SQL`. 2) Used `ALTER SYSTEM KILL SESSION` with `IMMEDIATE`. 3) Set a resource manager plan to limit SQL Workshop sessions to 30 seconds of CPU. 4) Implemented APEX page-level query timeout using `ORA$INTERCEPT` or `DBMS_SQL` timeout. 5) Created a read-only materialized view for the analyst's common queries.
**Lessons Learned**: Implement resource limits for SQL Workshop users. Use Database Resource Manager to prevent runaway queries. Train users on query best practices and encourage use of Query Builder with automatic filters.

## Scenario 3: Accidental Production Schema Drop
**Context**: A junior DBA was using Object Browser to clean up test tables but was connected to the PROD workspace.
**Problem**: They executed `DROP TABLE CUSTOMERS_ARCHIVE` but the Object Browser was pointed to the live CUSTOMERS table (similar name, different schema). 6 years of customer data was lost.
**Root Cause**: The workspace had access to multiple schemas. The Object Browser listed all accessible tables without clear schema labeling. The DBA selected from a dropdown that defaulted to the current user schema, which was the PROD schema.
**Solution**: 1) Restored from RMAN backup (lost 4 hours of transactions). 2) Implemented Flashback Drop (`FLASHBACK TABLE CUSTOMERS TO BEFORE DROP`). 3) Configured Fine-Grained Access Control to restrict SQL Workshop to read-only in PROD. 4) Implemented a "recycle bin" policy with `RECYCLEBIN = ON`. 5) Added workspace-level restriction: SQL Workshop disabled in PROD workspace.
**Lessons Learned**: Disable SQL Workshop in production workspaces. Use read-only accounts for production query access. Enable recycle bin in production. Implement separation of duties between DEV and PROD workspaces.

## Scenario 4: Exposed Database Credentials in Query History
**Context**: A developer pasted a connection string containing a password into the SQL Commands editor to test a database link.
**Problem**: The SQL Commands history (stored in `APEX_WORKSPACE_ACTIVITY_LOG`) contained the plaintext password. An auditor reviewing logs discovered this, triggering a compliance violation.
**Root Cause**: SQL Workshop retains a history of all executed commands. The workspace activity log captures SQL text without masking sensitive data. The developer used `CREATE DATABASE LINK ... IDENTIFIED BY password` in a direct SQL statement.
**Solution**: 1) Rotated all affected database credentials. 2) Purged sensitive entries from `APEX_WORKSPACE_ACTIVITY_LOG`. 3) Implemented `DBMS_ASSERT` to detect and block credential patterns in SQL text. 4) Added workspace ACL to prevent SQL Workshop from executing DDL. 5) Trained developers to use credential management tools instead of inline passwords.
**Lessons Learned**: Never execute DDL containing credentials in SQL Workshop. Mask sensitive SQL in application logs. Restrict SQL Workshop to SELECT-only in non-development environments. Implement automated credential scanning in log pipelines.
