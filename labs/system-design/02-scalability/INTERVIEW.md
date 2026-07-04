# Scalability - INTERVIEW

## Common Interview Questions

### Q1: Design a URL shortener (like bit.ly).
**Answer**: Write-heavy at creation, read-heavy on redirect. Use consistent hashing for DB sharding by short code. Cache popular URLs in Redis. Pre-generate codes to handle write bursts.

### Q2: How do you handle database scaling?
**Answer**: Three phases: (1) Index optimization + connection pooling, (2) Read replicas + caching, (3) Sharding by customer/region ID. Each phase adds complexity, so start later.

### Q3: Explain CAP theorem's impact on scaling.
**Answer**: In a distributed DB, you choose 2 of 3: Consistency, Availability, Partition Tolerance. For scalability, P is mandatory (servers will fail). Choose A (AP systems like DynamoDB, Cassandra) for high availability or C (CP systems like HBase, Spanner) for strong consistency.

### Q4: How does consistent hashing work?
**Answer**: Keys and servers map to a hash ring. Each key is stored on the nearest clockwise server. Adding/removing a server only affects O(1/N) keys, not all keys. Used by DynamoDB, Cassandra, Redis Cluster.

### Q5: What's the difference between vertical and horizontal scaling?
**Answer**: Vertical = bigger machine (faster CPU, more RAM). Horizontal = more machines (distributed). Vertical is simpler but has hard limits. Horizontal scales infinitely but adds complexity.

### Q6: How do you prevent cache stampede?
**Answer**: (1) Locking around cache miss computation, (2) Early recompute (refresh before TTL expires), (3) Probabilistic early expiration, (4) Pre-warm cache during deployment.

## System Design Problem: Design a Ticket Booking System

### Requirements
- Handle 100K concurrent users during ticket launch
- Prevent overselling
- Fair queuing

### Proposed Solution
- **Queue**: Pre-queue users before sale starts (random assignment)
- **Request Queuing**: AWS SQS or Redis queue for purchase requests
- **Optimistic Locking**: `UPDATE tickets SET version = version + 1 WHERE version = :v`
- **Auto-scaling**: Scale up 10 minutes before sale, scale down after
- **CDN**: Serve static countdown pages from edge
