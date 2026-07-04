# Refactoring — AWS Database

## 1. From Self-Managed MySQL to RDS

### Before (Self-Managed on EC2)
```
- Install MySQL, configure my.cnf
- Set up replication script (cron-based mysqldump)
- Monitor disk space, rotate logs
- Manual failover to replica
- Security patches applied via SSH
```

### After (RDS)
```
- Create via CLI/Console: aws rds create-db-instance
- Automated backups, point-in-time recovery
- Multi-AZ failover (< 60s automatic)
- Automatic minor version upgrades
- CloudWatch monitoring included
- RDS Proxy for connection pooling

Migration: mysqldump → RDS import, or DMS (Database Migration Service)
```

## 2. From RDS (MySQL) to DynamoDB for Session Store

### Before (RDS)
```sql
CREATE TABLE sessions (
    session_id VARCHAR(128) PRIMARY KEY,
    data TEXT,
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);
SELECT data FROM sessions WHERE session_id = ?;
DELETE FROM sessions WHERE expires_at < NOW();
```

### After (DynamoDB)
```java
// DynamoDB session store — auto-expire with TTL
PutItemRequest request = PutItemRequest.builder()
    .tableName("Sessions")
    .item(Map.of(
        "sessionId", AttributeValue.fromS(sessionId),
        "data", AttributeValue.fromS(sessionData),
        "ttl", AttributeValue.fromN(String.valueOf(expireTime))
    ))
    .build();

// TTL: DynamoDB auto-deletes expired items (no purge script)
// Enable: AWS Console → DynamoDB → TTL → ttl attribute
```

## 3. From RDS Read Replicas to ElastiCache

### Before
```
Primary DB: writes + reads
Read Replica: read queries (adds DB load, replica lag)
```

### After
```
ElastiCache Redis: sub-ms reads, zero DB load for cached queries
Primary DB: writes only

Cache-aside: check Redis first → miss → query DB → populate Redis
```

```java
// Before: Direct DB query every time
List<Product> products = productRepository.findByCategory("electronics");

// After: Cache-first with Redis
String cacheKey = "category:electronics";
String cached = redisTemplate.opsForValue().get(cacheKey);
if (cached != null) {
    return deserialize(cached);
}
List<Product> products = productRepository.findByCategory("electronics");
redisTemplate.opsForValue().set(cacheKey, serialize(products), 1, TimeUnit.HOURS);
return products;
```
