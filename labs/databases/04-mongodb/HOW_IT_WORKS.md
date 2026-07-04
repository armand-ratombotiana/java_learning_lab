# How MongoDB Works

## Storage Engine (WiredTiger)

### Document Storage
- B-tree structure for collections and indexes
- Snapshot isolation with MVCC
- Optional block compression (snappy, zlib, zstd)
- Document-level concurrency

### Cache Management
- Internal cache for documents and indexes
- Eviction uses modified LRU (clock algorithm)
- Write-back caching with journal for durability

## Write Path
```
Application → MongoDB Driver → mongos/shard → WiredTiger → Journal → Data File
```
1. Document validated (BSON size, schema validation if configured)
2. Write recorded to journal (durability)
3. Document written to in-memory cache
4. Background eviction flushes to disk

## Read Path
```
Application → MongoDB Driver → mongos/shard → Cache Check
                                                    ↓
                                              Disk Read (cache miss)
                                                    ↓
                                              Return Document
```
- Index checked first for efficient document location
- Cache hit if document in memory
- Cache miss triggers disk read

## Aggregation Pipeline Execution
```
$match      → Uses indexes, early filter
$sort       → Uses indexes or blocks until complete
$group      → Accumulates documents in memory
$project    → Reshapes documents
$limit      → Stops processing after N documents
```

## Replication Protocol
1. Primary accepts all writes
2. Writes recorded in oplog (capped collection)
3. Secondaries read oplog and apply changes asynchronously
4. Heartbeat every 2 seconds detects primary failure
5. Automatic election of new primary via majority consensus

## Java Driver Connection
```java
// Connection string with options
String uri = "mongodb+srv://user:pass@cluster.mongodb.net/myapp"
    + "?retryWrites=true"
    + "&w=majority"
    + "&maxPoolSize=50"
    + "&serverSelectionTimeoutMS=5000";

MongoClient client = MongoClients.create(uri);
```
