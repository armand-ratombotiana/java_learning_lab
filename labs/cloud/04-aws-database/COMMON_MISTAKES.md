# Common Mistakes — AWS Database

## RDS Mistakes

### 1. Publicly Accessible RDS
**Risk**: RDS instance reachable from internet. Brute-force attacks.
**Fix**: Set `PubliclyAccessible = false`. Access only from VPC via security group.

### 2. No Connection Pooling (Especially Lambda)
**Mistake**: Lambda creates new DB connection per invocation.
**Effect**: "Too many connections" error, exhausted RDS connections.
**Fix**: Use RDS Proxy (connection pooling for Lambda) or static connection pool.

### 3. Inadequate Backup Retention
**Mistake**: Default 7-day backup retention for production.
**Risk**: Can't recover data from 8+ days ago.
**Fix**: Set backup retention to 35 days for production databases.

### 4. Wrong Instance Class for Workload
**Mistake**: Using burstable (db.t3) for production with constant high load.
**Effect**: CPU credit exhaustion → throttled performance.
**Fix**: Use dedicated (db.r5/db.m6i) for production workloads.

## DynamoDB Mistakes

### 1. Hot Partition Due to Bad Key Design
**Mistake**: Using sequential order IDs as partition keys.
**Effect**: All writes go to one partition (throttling).
**Fix**: Use random/ULID keys or add shard suffix: `orderId + "#" + random(1-100)`.

### 2. Over-provisioning RCU/WCU
**Mistake**: Provisioning 10000 RCU for a table used only 10 reads/sec.
**Cost**: $700/month for unused capacity.
**Fix**: Use On-Demand mode for variable workloads; auto-scaling for predictable.

### 3. Not Using Global Secondary Indexes Properly
**Mistake**: Creating GSI with same partition key as table (no access pattern benefit).
**Fix**: Design GSIs based on actual query patterns (inverted index, sparse indexes).

### 4. Item Size Exceeds 400KB Limit
**Mistake**: Storing large payloads (images, logs) as DynamoDB attributes.
**Effect**: PutItem fails with ValidationException.
**Fix**: Store large payloads in S3, reference by pointer in DynamoDB.

## ElastiCache Mistakes

### 1. Not Setting TTL on Cache Entries
**Effect**: Stale data served indefinitely; memory fills with unused data.
**Fix**: Always set TTL: `jedis.setex(key, 3600, value)`.

### 2. Insufficient Node Size
**Mistake**: Single cache.t2.micro for production cache.
**Effect**: Out of memory, evictions, performance degradation.
**Fix**: Size for peak data + 25% overhead + replica memory.

### 3. No Failover Testing
**Risk**: Redis primary fails → manual intervention, downtime.
**Fix**: Enable Multi-AZ with automatic failover; test failover regularly.
