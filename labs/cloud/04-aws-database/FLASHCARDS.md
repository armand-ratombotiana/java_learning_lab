# AWS Database - Flashcards

### Amazon RDS
Managed relational database service supporting MySQL, PostgreSQL, Oracle, SQL Server, and Aurora.

### Amazon Aurora
Cloud-native relational database compatible with MySQL and PostgreSQL with enterprise features.

### Multi-AZ Deployment
RDS feature for high availability with synchronous replication to standby instance.

### Read Replica
RDS feature for asynchronous replication to improve read performance.

### RDS Snapshot
Point-in-time backup of RDS instance stored in S3.

### Amazon DynamoDB
Fully managed NoSQL database with single-digit millisecond latency.

### DynamoDB Partition Key
Required key attribute that determines data distribution across partitions.

### DynamoDB Sort Key
Optional key enabling logical ordering of items within partition.

### GSI (Global Secondary Index)
Index that can have different partition/sort key from base table.

### LSI (Local Secondary Index)
Index with same partition key but different sort key as base table.

### DynamoDB DAX
DynamoDB Accelerator - in-memory cache providing microsecond latency.

### DynamoDB Stream
Feature capturing item-level changes in DynamoDB table.

### TTL (Time To Live)
DynamoDB feature for automatic item expiration after specified time.

### ElastiCache
Managed in-memory caching service supporting Redis and Memcached.

### Redis
ElastiCache engine supporting data types, persistence, and clustering.

### Memcached
ElastiCache engine providing simple, in-memory caching.

### ElastiCache Cluster Mode
Feature enabling horizontal scaling with sharding across nodes.

### DynamoDB On-Demand
Pricing mode allowing pay-per-request without capacity planning.

### Provisioned Capacity
RDS/DynamoDB capacity mode with pre-configured read/write capacity units.

### Aurora Serverless
Auto-scaling compute configuration for Aurora database.

### RDS Parameter Group
Configuration container for database engine settings.

### DynamoDB Transactions
Feature providing ACID operations across multiple items.

### Aurora Global Database
Aurora feature providing cross-region read scaling and disaster recovery.

### RDS Performance Insights
Database performance monitoring and visualization tool.

### DynamoDB Table Class
Storage pricing tier (Standard or Infrequent Access).

### ElastiCache Pub/Sub
Publish/Subscribe messaging pattern support in Redis.