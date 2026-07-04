# Interview Questions — AWS Database

## Beginner

**Q1**: What's the difference between RDS and DynamoDB?

**Q2**: What is Multi-AZ RDS and why would you use it?

**Q3**: When would you use ElastiCache Redis?

## Intermediate

**Q4**: Explain DynamoDB's partition key design and its impact on performance.

**Q5**: How does Aurora differ from standard MySQL in terms of storage architecture?

**Q6**: What's the difference between RDS Read Replicas and Multi-AZ?

**Q7**: How do you handle database connections in a Lambda function?

## Advanced

**Q8**: Design a multi-region database strategy for a global Java application.

**Q9**: Your DynamoDB table is throttling despite low overall throughput. What's happening?

**Q10**: How does Aurora achieve 5x MySQL performance while maintaining compatibility?

**Q11**: Design a strategy for migrating a 2TB MySQL database to Aurora with minimal downtime.

## Sample Answers

**A1**: RDS is relational (SQL, schemas, joins, ACID). DynamoDB is NoSQL (key-value, schemaless, auto-scaling, single-digit ms latency). Choose RDS for complex queries and relationships; DynamoDB for high-scale, low-latency, simple access patterns.

**A2**: Multi-AZ synchronously replicates to a standby instance in another AZ. On failure, DNS automatically fails over to the standby (~60-120s). Provides high availability, not read scaling (standby doesn't serve reads).

**A3**: ElastiCache Redis provides sub-millisecond data access. Use for: session stores, cache-aside patterns, real-time leaderboards, rate limiting, message queues (Pub/Sub), and distributed locks.

**A4**: DynamoDB partitions data by partition key hash. Poor key design (e.g., sequential IDs) creates "hot partitions" where one partition handles most traffic. Use write sharding (e.g., random suffix) or choose high-cardinality keys to distribute evenly.

**A5**: Aurora separates compute and storage. Compute is stateless (EC2 instances); storage is a distributed 6-copy volume across 3 AZs. Only redo logs are sent to storage (not full pages). This reduces I/O, enables fast crash recovery, and allows up to 15 read replicas sharing the same storage.

## Key Topics for AWS Database Exam
- RDS backup, restore, and point-in-time recovery
- DynamoDB capacity modes (provisioned vs on-demand)
- DynamoDB secondary indexes (GSI, LSI)
- Aurora Serverless v2 scaling
- ElastiCache Redis cluster modes
- Database migration strategies (DMS, native tools)
- Database security (encryption, IAM auth, VPC)
