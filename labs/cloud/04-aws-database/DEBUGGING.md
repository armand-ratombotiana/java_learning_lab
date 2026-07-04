# Debugging — AWS Database

## RDS Debugging

### Connection Timeout
```
Symptom: "Cannot connect to MySQL server" or "Connection timed out"
Check:
  1. Security group: allow port 3306 from app SG (not 0.0.0.0/0)
  2. Subnet group: RDS in same VPC as app servers
  3. PubliclyAccessible = false (correct for internal apps)
  4. NACL: allow inbound/outbound port 3306

Test:
  mysql -h myapp.xxx.us-east-1.rds.amazonaws.com -u app_admin -p
```

### High CPU
```
Symptom: DB instance CPU consistently > 80%
Check:
  1. SHOW FULL PROCESSLIST; (find slow queries)
  2. EXPLAIN SELECT... (missing indexes?)
  3. Performance Insights (top SQL by load)
  4. Slow query log (enable in parameter group)

Fix:
  - Add indexes for slow queries
  - Optimize JOINs, avoid SELECT *
  - Increase instance size (scale up)
  - Add read replicas (scale read)
```

## DynamoDB Debugging

### Throttling (ProvisionedThroughputExceededException)
```
Symptom: "ProvisionedThroughputExceededException" in logs
Check:
  - CloudWatch: ConsumedWriteCapacityUnits vs ProvisionedWriteCapacityUnits
  - Look for hot partition: throttled vs not throttled

Fix:
  - Increase provisioned capacity
  - Switch to On-Demand
  - Fix hot key: add write sharding
  - Implement exponential backoff in Java SDK (built-in)
```

### Expensive Scans
```
Symptom: Queries returning "ConsumedCapacity" high
Check:
  - Are you using Scan instead of Query? (Scan reads entire table)
  - Do you have a GSI for the filter pattern?
  - FilterExpression is applied after scan (same RCU cost)

Fix:
  - Always use Query with KeyConditionExpression
  - Create GSI for alternate access patterns
  - Use FilterExpression but know it doesn't reduce RCU
```

## ElastiCache Debugging

### High Evictions
```
Symptom: evictions > 0 in CloudWatch
Check:
  - CurItems vs MaxMemory
  - TTL on cache entries (are you setting TTL?)
  - Volatile-lru vs allkeys-lru eviction policy

Fix:
  - Increase node size
  - Set shorter TTLs
  - Configure maxmemory-policy to allkeys-lru
```

### Cache Miss Spike
```
Symptom: Cache miss rate jumps → database load spikes
Check:
  - Cluster restarted? (all keys lost)
  - Key expiration aligned? (many keys expire at same time)
  - Application code change? (key format changed)

Fix:
  - Add jitter to TTL: TTL + random(0, 300)
  - Warm cache after restart
  - Monitor CacheMisses metric in CloudWatch
```
