# Mock Interview — AWS Database

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Associate/Professional

## Warm-Up (5 min)

Q1: Compare RDS, DynamoDB, and ElastiCache. When would you choose each?

Q2: What is the difference between RDS Multi-AZ and RDS Read Replicas?

## Technical Questions (20 min)

### Question 1: Database Migration (10 min)
Your company runs a Java application with a self-managed PostgreSQL database on EC2 (500GB, 10K TPS). You need to migrate to a managed AWS database service with minimal downtime.

**Design the migration**: Which service would you choose (RDS, Aurora, DynamoDB)? What migration approach (DMS, dump/restore)? How do you achieve minimal downtime?

### Question 2: DynamoDB Table Design (10 min)
Design a DynamoDB table for a social media application where users can follow each other and see a feed of recent posts from followed users. Requirements:
- 10M users, each follows 100 users on average
- Users view feed: recent 20 posts from followed users
- Write-heavy: users post 1M new posts/day

**Design**: Primary key, sort key, GSIs, access patterns.

## Behavioral Question (10 min)

**Question**: Tell me about a time you had to optimize a slow database query or resolve a database performance issue in production.

## System Design Whiteboard (10 min)

**Problem**: Design a global e-commerce platform database layer:
- Product catalog (read-heavy, 50K products)
- Shopping cart (high-write during sales)
- Orders (transactional, ACID required)
- Session state (cache, high throughput)

Choose the right AWS database service for each and explain why.

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| RDS/Aurora | Knows Multi-AZ, replicas, performance insights | Basic understanding | Can't explain replication |
| DynamoDB | Well-designed schema, access patterns, hot keys | Can design basic table | No GSI/LCI awareness |
| Migration | DMS, CDC, cutover strategy | Basic migration plan | Downtime not considered |
| Caching | Redis vs Memcached, DAX, cache strategies | Knows ElastiCache | No caching strategy |

## Sample Solution Outline

### Database Migration
- Use AWS DMS with ongoing replication (CDC) for minimal downtime
- Source: PostgreSQL on EC2; Target: Aurora PostgreSQL
- Steps: (1) Full load with DMS; (2) Ongoing replication from WAL; (3) Cutover: stop app, apply final changes, point app to Aurora; (4) Test and monitor
- Alternative: pglogical extension for logical replication

### DynamoDB Feed Table
- Table: `Posts` PK: `userId`, SK: `postTimestamp`
- GSI: `followers` — for feed generation, inverse access pattern
- Better approach: Materialized feed using DynamoDB Streams + Lambda
- `Feed` table: PK: `userId`, SK: `postTimestamp`, Store post content
- Fan-out on write: when user posts, update all follower feeds (limit to 1000)
- For high-profile users: on-read fan-out (generate feed on demand)

### E-Commerce Database Design
- Product Catalog: DynamoDB (read-heavy, flexible schema)
- Shopping Cart: DynamoDB + DAX (high throughput, eventually consistent OK)
- Orders: RDS Aurora (ACID required for transactions)
- Session State: ElastiCache Redis (in-memory, TTL-based, high throughput)
