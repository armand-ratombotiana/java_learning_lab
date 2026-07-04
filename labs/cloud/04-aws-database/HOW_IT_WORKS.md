# How AWS Database Works

## RDS Provisioning Flow

1. **DB Instance creation**: Select engine (MySQL, PostgreSQL, Oracle, SQL Server, MariaDB)
2. **Instance class**: Choose CPU/memory (db.t3.micro, db.r5.large)
3. **Storage**: gp3, io2, or Aurora (auto-scaling)
4. **VPC**: Placement in VPC with security group restrictions
5. **Multi-AZ**: Replication to standby in another AZ (synchronous)
6. **Backup**: Automated backups enabled by default (35-day retention)
7. **Endpoint**: DNS endpoint provided (no IP changes on failover)

```java
// RDS connection with HikariCP connection pool
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://myapp.xxxxx.us-east-1.rds.amazonaws.com:3306/mydb");
config.setUsername("app_user");
config.setPassword(System.getenv("DB_PASSWORD"));
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(3000);

DataSource ds = new HikariDataSource(config);
```

## DynamoDB Request Flow

1. **Request arrives** at DynamoDB endpoint
2. **Request router** identifies the correct partition by hashing the partition key
3. **Partition** processes the request (read from leader or replica)
4. **Consistency**: Strongly consistent reads from leader; eventually consistent from any replica
5. **Throttling**: If provisioned capacity exceeded, returns ProvisionedThroughputExceededException

```java
// DynamoDB putItem
DynamoDbClient dynamo = DynamoDbClient.create();

PutItemRequest request = PutItemRequest.builder()
    .tableName("Orders")
    .item(Map.of(
        "orderId", AttributeValue.fromS("ORD-12345"),
        "customerId", AttributeValue.fromS("CUST-567"),
        "amount", AttributeValue.fromN("99.95"),
        "status", AttributeValue.fromS("CONFIRMED")
    ))
    .build();
dynamo.putItem(request);
```

## Aurora Storage Model

- **Compute**: Stateless query processing layer (EC2-based)
- **Storage**: Distributed, auto-healing, multi-AZ storage volume (separate from compute)
- **Writes**: Only log records (redo logs) sent to storage — 6 copies across 3 AZs
- **Crash recovery**: Storage layer applies redo logs — no checkpoint needed (always consistent)
- **Scale**: Up to 128TB, auto-scales in 10GB increments

## ElastiCache Redis

1. **Cluster mode**: Data sharded across nodes (up to 500 nodes, 340TB)
2. **Replication**: Primary node + up to 5 read replicas per shard
3. **Persistence**: RDB snapshots to S3 and/or AOF log
4. **Failover**: Automatic replica promotion if primary fails

```java
// Jedis client for ElastiCache Redis
try (Jedis jedis = new Jedis("my-cache.xxxxx.ng.0001.use1.cache.amazonaws.com", 6379)) {
    // Set with 1 hour TTL
    jedis.setex("user:session:" + sessionId, 3600, jsonData);
    
    // Get
    String cached = jedis.get("user:session:" + sessionId);
}
```
