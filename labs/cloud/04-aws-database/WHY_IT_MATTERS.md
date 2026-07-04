# Why AWS Database Matters

## Business Impact
- **Zero-setup replication**: Multi-AZ RDS handles failover automatically (<60s)
- **Auto-scaling storage**: RDS Aurora grows up to 128TB without resizing
- **Serverless options**: Aurora Serverless v2 scales to zero when idle (60%+ savings)
- **Managed patching**: AWS handles security updates, minor version upgrades

## Technical Impact
- **DynamoDB latency**: Single-digit millisecond reads at any scale
- **Aurora throughput**: 5x MySQL, 3x PostgreSQL — 200K+ reads/sec
- **ElastiCache**: Sub-millisecond cache hits for JAva session data
- **Redshift Concurrency Scaling**: Handles 1000s of concurrent queries

## For Java Developers
- Spring Data JPA + RDS/Aurora = minimal code changes from local MySQL
- Spring Data DynamoDB = annotation-driven NoSQL access
- Jedis client + ElastiCache = drop-in Redis session caching
- HikariCP connection pooling for RDS — best-practice Java configuration
