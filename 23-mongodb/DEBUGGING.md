# Debugging MongoDB Query Performance Issues

## Common Failure Scenarios

### Slow Query Execution

Queries take excessive time to complete. Response times exceed SLAs, and application timeouts occur. The query returns but users experience delays. This indicates inefficient query patterns or inadequate indexing.

Full collection scans cause slow performance. When queries cannot use indexes, MongoDB examines every document. Explain plan shows "COLLSCAN" instead of "IXSCAN". Review the query shape and ensure indexed fields are used in filters and sorts.

Missing indexes on filter fields cause slow queries. Add indexes for frequently queried fields. Compound indexes help when queries filter on multiple fields. Ensure index selectivity—indexes on low-cardinality fields provide minimal benefit.

### Index Not Used

Indexes exist but queries don't use them. Explain plans show collection scans despite available indexes. This happens with incorrect index field order, incompatible query patterns, or statistics being stale.

Compound index field order matters. The index must match query filter order for equality fields, followed by sort fields. A query filtering on the second field of a compound index cannot use the index effectively.

Query pattern doesn't match the index. Using `$exists`, `$type`, or negation operators can prevent index usage. $or queries use each branch's index but may be slower than a covered approach.

### Stack Trace Examples

**Cursor timeout:**
```
com.mongodb.MongoExecutionTimeoutException: Query exceeded time limit. MS=5000
    at com.mongodb.operation.QueryOperation.execute(QueryOperation.java:78)
```

**Missing index:**
```
Query failed with error code 2 and error message 'PlanExecutor killed due to memory pressure'
    at com.mongodb.driver.internal.operation.ExplainOperation.execute(ExplainOperation.java:123)
```

**Write concern timeout:**
```
com.mongodb.MongoWriteConcernException: Write concern error: timeout
    at com.mongodb.driver.internal.MongoCollectionImpl.executeBulkWriteOperation(MongoCollectionImpl.java:567)
```

## Debugging Techniques

### Analyzing Query Execution Plans

Use `explain(true)` to see detailed execution plans. Look for COLLSCAN vs IXSCAN, number of documents examined vs returned, and sort type. A good plan examines few documents relative to results returned.

Review the "winning plan" section. Check if indexes are used for both filtering and sorting. Look for in-memory sort operations indicating missing sort indexes.

Monitor `nscanned` vs `nreturned`. Large ratio of scanned to returned documents indicates inefficient access patterns. Create covering indexes to eliminate document fetching.

### Index Performance Analysis

Use `db.currentOp()` to see currently running operations. Look for slow queries blocking others. Use `db.system.profile.find()` to query the profiler output.

Check index statistics with `db.collection.getIndexes()` and `db.collection.stats()`. Look for indexes with high `accesses` but low `hits`. Rebuild fragmented indexes periodically.

Use compound indexes to support multiple query patterns efficiently. Place equality fields first, sort fields second. Consider covering indexes for read-heavy queries.

## Best Practices

Create indexes matching actual query patterns. Use partial indexes for filtered collections. Use wildcard indexes for dynamic fields when appropriate.

Monitor slow query log and create indexes for recurring slow queries. Set appropriate query timeouts in application code. Use projection to limit fields returned.

Review cardinality before creating indexes. Low-cardinality fields benefit less from indexing. Consider compound indexes over multiple single-field indexes.