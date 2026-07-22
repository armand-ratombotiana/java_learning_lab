# Production Scenarios: Multi-Model Databases (Oracle Focus)

## Scenario 1: Oracle JSON Query Performing Full Scan
**Context**: A document store using Oracle JSON data type with SODA for a product catalog.
**Problem**: A query filtering on `color = 'red'` in a JSON document performed a full table scan on 10M documents. Query time was 30 seconds.
**Root Cause**: The JSON column had no JSON index on the `color` property. Oracle created a virtual column but no index. The query used `JSON_VALUE(doc, '$.color') = 'red'` which was not indexable without a JSON index or function-based index.
**Solution**: 1) Created a JSON index: `CREATE INDEX idx_json_color ON products (JSON_VALUE(doc, '$.color' RETURNING VARCHAR2(100)))`. 2) Or created a JSON search index: `CREATE SEARCH INDEX idx_json_search ON products (doc) FOR JSON`. 3) For equality queries, used JSON data guide to create virtual columns automatically. 4) Verified index usage via `DBMS_XPLAN.DISPLAY`. 5) Rewrote query to use `JSON_EXISTS(doc, '$.color?(@ == "red")')` for better performance with search index.
**Lessons Learned**: Always create JSON indexes for filtered queries. Use `JSON_VALUE` function-based index for equality filters. Use JSON search index for ad-hoc queries. Monitor execution plans for JSON queries.

## Scenario 2: SODA Document Store Growing Unbounded
**Context**: An application used Oracle SODA to store audit logs as JSON documents.
**Problem**: The table grew to 500GB, consuming all available tablespace. Purge queries taking hours. Old documents were never deleted.
**Root Cause**: The SODA collection had no retention policy. Every API call created a new document. The application generated 100K documents per day. No archiving or cleanup process existed.
**Solution**: 1) Created a retention policy: delete documents older than 90 days. 2) Implemented partition-by-month strategy: `CREATE TABLE audit_log PARTITION BY RANGE (created_on)`. 3) Used partition drop for monthly cleanup: `ALTER TABLE audit_log DROP PARTITION p_202401`. 4) Created a scheduled job using `DBMS_SCHEDULER` to archive old partitions to a separate archive schema. 5) Added monitoring for tablespace usage with alerting at 70%.
**Lessons Learned**: Always define retention policies for document stores. Use partitioning for time-series data. Automate archive/cleanup processes. Monitor storage growth for SODA collections.

## Scenario 3: Graph Query Cycling
**Context**: An organizational hierarchy query using Oracle's CONNECT BY for graph traversal.
**Problem**: After a data migration, the hierarchy query started returning billions of rows. Eventually it failed with `ORA-01436: CONNECT BY loop in user data`.
**Root Cause**: The migration created a circular reference: Employee A reported to B, B reported to C, C reported to A. The `CONNECT BY NOCYCLE` clause was not used. Oracle detected the loop at runtime after consuming significant resources.
**Solution**: 1) Modified query to use `CONNECT BY NOCYCLE PRIOR employee_id = manager_id`. 2) Added `CONNECT_BY_ISCYCLE` to identify cycle-causing rows. 3) Fixed data: updated C's manager_id to NULL. 4) Added database constraint to prevent circular references using a trigger. 5) Added pre-import validation for hierarchical data.
**Lessons Learned**: Always use NOCYCLE in production CONNECT BY queries. Validate hierarchical data imports. Add constraints to prevent circular references. Monitor `CONNECT_BY_ISCYCLE` in audit reports.

## Scenario 4: Oracle Text Index Corruption
**Context**: An e-commerce site uses Oracle Text for product search.
**Problem**: Search queries for "wireless mouse" returned zero results even though "wireless mouse" products existed. Search was broken for 4 hours.
**Root Cause**: The Oracle Text index (`ctxsys.context`) on the product description column became corrupt after a bulk data load that failed mid-way. The index `SYNC` process did not complete. The `DR$...$I` table was in an inconsistent state.
**Solution**: 1) Identified the corrupted index: `SELECT * FROM CTX_USER_INDEXES WHERE STATUS = 'FAILED'`. 2) Rebuilt the index: `ALTER INDEX prod_desc_idx REBUILD PARAMETERS('sync memory 500M')`. 3) Manually synchronized: `EXEC CTX_DDL.SYNC_INDEX('prod_desc_idx')`. 4) Optimized the index: `ALTER INDEX prod_desc_idx REBUILD PARAMETERS('optimize full')`. 5) Implemented index health monitoring with automated rebuild on failure detection.
**Lessons Learned**: Monitor Oracle Text index status regularly. Rebuild indexes after bulk data loads. Implement automated index health checks. Have index rebuild procedures in runbook.
