# Interview: MongoDB

## Beginner

**Q**: What is MongoDB and what makes it different from SQL databases?
**A**: MongoDB is a NoSQL document database. Instead of tables and rows, it uses collections of JSON-like documents. Documents can have nested structures, arrays, and varying fields between documents. No fixed schema means faster iteration and direct mapping to application objects.

**Q**: How do you model a 1:N relationship in MongoDB?
**A**: Two main approaches: embedding (store related data as subdocuments within the parent, good for small bounded relationships accessed together) and referencing (store child document IDs in an array or parent ID in child documents, good for unbounded relationships or independent access). Choose based on access patterns.

## Intermediate

**Q**: Explain the aggregation pipeline and when you'd use it.
**A**: The aggregation pipeline is a framework for data processing. Documents pass through stages like `$match` (filter), `$group` (aggregate), `$sort` (order), `$project` (reshape), and `$lookup` (join). Use it for reporting, data transformation, complex filtering, and computed aggregations that are too complex for `find()`.

**Q**: How does MongoDB handle ACID transactions?
**A**: Since 4.0, MongoDB supports multi-document ACID transactions across collections. Since 4.2, transactions span sharded clusters. Use `startTransaction()`, `commitTransaction()`, `abortTransaction()`. Default transaction timeout is 60 seconds. Use `w: majority` and `readConcern: snapshot` for full ACID guarantees.

**Q**: How do you choose a shard key?
**A**: Ideal shard key has: high cardinality (many unique values), low frequency (few documents per value), and non-monotonic change pattern (avoids hot shards). Examples: hashed user ID, composite key of region + timestamp. Avoid: monotonically increasing keys (`_id`, timestamps without hashing).

## Advanced

**Q**: Describe WiredTiger's snapshot isolation and conflict resolution.
**A**: WiredTiger uses MVCC with snapshot isolation. Each transaction gets a snapshot of committed state at start time. Writes create new versions; reads see the snapshot version. On write conflict (two transactions modifying the same document), the second transaction receives a `WriteConflict` error and must retry. This is transparent in transactions but must be handled in application code for retryable writes.

**Q**: Design a multi-tenant SaaS application on MongoDB.
**A**: Options: (1) Database per tenant – best isolation, complex backup/restore, (2) Collection per tenant – moderate isolation, shared resources, (3) Threaded tenant pattern (single collection with tenantId field) – simplest operations, proper indexes on tenantId, potential hot spots. For most SaaS apps, a shared collection with tenantId index + RYOW (Read Your Own Write) consistency is recommended. Use zones/tag-aware sharding for geographic distribution.

**Q**: How would you debug a sudden performance degradation in a MongoDB production cluster?
**A**: 1) Check `currentOp()` for long-running queries. 2) Review slow queries in `system.profile`. 3) Check index usage with `$indexStats`. 4) Examine `serverStatus()` for page faults, lock percentage. 5) Review WiredTiger cache pressure. 6) Check replication lag. 7) Monitor network latency. 8) Look for collection scans in `log` level.
