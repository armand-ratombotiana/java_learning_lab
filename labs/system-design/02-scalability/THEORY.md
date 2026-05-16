# Scalability - THEORY

## Table of Contents
1. [Introduction to Scalability](#intro)
2. [Vertical Scaling](#vertical)
3. [Horizontal Scaling](#horizontal)
4. [Load Balancing](#load-balancing)
5. [Caching Strategies](#caching)
6. [Database Scaling](#database-scaling)
7. [Auto-Scaling](#auto-scaling)

---

## 1. Introduction to Scalability <a name="intro"></a>

### What is Scalability?

Scalability is the ability of a system to handle increased load by adding resources. It's a critical non-functional requirement that determines how well your application grows with demand.

### Types of Scalability

1. **Scalability UP (Vertical)**: Add more power to existing machines
2. **Scalability OUT (Horizontal)**: Add more machines to the system
3. **Scalability DOWN**: Reduce resources when load decreases

### Key Metrics

- **Throughput**: Requests processed per second (RPS)
- **Latency**: Time to process a single request
- **Concurrent Users**: Number of users actively using the system
- **Data Size**: Amount of data the system can store and process

---

## 2. Vertical Scaling <a name="vertical"></a>

### Overview

Vertical scaling (scaling up) involves adding more resources to a single machine - more CPU, RAM, storage, or network bandwidth.

### When to Use Vertical Scaling

- Single-node applications that can't be easily distributed
- Databases where distribution is complex
- Legacy systems not designed for horizontal scaling
- Short-term solutions while implementing horizontal scaling

### Advantages

- Simple to implement - no code changes needed
- No distributed system complexity
- Works with any application
- Single point of management

### Disadvantages

- Hardware limits - eventually you hit maximum
- Expensive at high-end hardware
- Single point of failure
- Hardware upgrades require downtime

### Vertical Scaling Examples

| Resource | Small | Medium | Large |
|----------|-------|--------|-------|
| CPU | 4 cores | 16 cores | 64+ cores |
| RAM | 8 GB | 64 GB | 256+ GB |
| Storage | 256 GB SSD | 2 TB NVMe | 10+ TB SSD |
| Cost/Month | $100 | $500 | $3000+ |

---

## 3. Horizontal Scaling <a name="horizontal"></a>

### Overview

Horizontal scaling (scaling out) involves adding more machines to the system to handle increased load.

### Key Principles

1. **Stateless Services**: Services don't store user state locally
2. **Shared Nothing Architecture**: Each node is independent
3. **Data Partitioning**: Distribute data across nodes
4. **Request Distribution**: Use load balancers

### Implementation Strategies

- **Replication**: Copy data to multiple nodes
- **Sharding**: Partition data across nodes
- **Microservices**: Split by functional domain
- **Geographic Distribution**: Deploy across regions

### Advantages

- Essentially unlimited scaling potential
- Better fault tolerance
- Cost-effective (commodity hardware)
- No single point of failure

### Disadvantages

- More complex to manage
- Network overhead between nodes
- Data consistency challenges
- Operational complexity

---

## 4. Load Balancing <a name="load-balancing"></a>

### What is Load Balancing?

Load balancing distributes incoming network traffic across multiple servers to ensure no single server bears too much load.

### Load Balancing Algorithms

#### Round Robin
- Requests distributed sequentially
- Simple but doesn't consider server load
- Best for: Equal-capability servers

#### Least Connections
- Sends request to server with fewest active connections
- Better for: Variable request sizes
- Best for: Long-lived connections

#### Weighted Round Robin
- Assign weights based on server capacity
- Higher weight = more requests
- Best for: Heterogeneous server pools

#### IP Hash
- Hash source IP to determine server
- Ensures same client goes to same server
- Best for: Session-based applications

#### Least Response Time
- Send to server with lowest average response time
- Requires health monitoring
- Best for: Real-time applications

### Load Balancer Types

#### Layer 4 (Transport Layer)
- Makes routing decisions based on IP and port
- Faster, less feature-rich
- Examples: HAProxy, AWS NLB

#### Layer 7 (Application Layer)
- Makes decisions based on HTTP content
- Can route based on URL, headers, cookies
- More flexible but slightly slower
- Examples: NGINX, AWS ALB

### Health Checks

Load balancers monitor server health:
- **TCP Health Check**: Port open/closed
- **HTTP Health Check**: Endpoint returns 200
- **HTTPS Health Check**: Valid certificate
- **Custom Health Check**: Application-specific

---

## 5. Caching Strategies <a name="caching"></a>

### Why Cache?

Caching stores frequently accessed data in fast storage to reduce latency and database load.

### Cache Placement

1. **Client-Side Cache**: Browser, mobile app
2. **CDN Cache**: Edge locations
3. **Application Cache**: In-memory (Redis, Memcached)
4. **Database Query Cache**: Query results
5. **Object Cache**: Serialized objects

### Cache Strategies

#### Cache-Aside (Lazy Loading)
```
1. Request comes in
2. Check cache
3. If miss, get from database
4. Store in cache
5. Return data
```

#### Write-Through
```
1. Write to cache
2. Also write to database
3. Return success
```

#### Write-Behind
```
1. Write to cache
2. Return success
3. Async write to database
```

#### Refresh-Ahead
```
1. Check if data will expire soon
2. If so, refresh proactively
3. Serve stale data while refreshing
```

### Cache Invalidation

The hardest problem in computer science:
- **TTL-based**: Time-to-live expiration
- **Event-based**: Invalidate on data change
- **Version-based**: Use version keys
- **Write-invalidate**: Clear on writes

---

## 6. Database Scaling <a name="database-scaling"></a>

### Read Replicas

Create copies of the database for read operations:

```
┌─────────────┐
│  Primary DB │ (Writes + Reads)
└──────┬──────┘
       │
   ┌───┴───┐
   ▼       ▼
┌─────┐ ┌─────┐
│Replica│ │Replica│
│ (Read)│ │ (Read)│
└─────┘ └─────┘
```

### Advantages
- Scale read capacity horizontally
- Improve read performance
- Better read availability
- Cost-effective for read-heavy workloads

### Disadvantages
- Replication lag
- Eventual consistency
- Complexity in routing
- Additional infrastructure cost

### Database Sharding

Split data across multiple databases:

#### Sharding Strategies

**By Range (Temporal)**
- Orders by date range
- Users by ID range
- Good for: Range queries

**By Hash (Random)**
- Hash key to determine shard
- Good for: Even distribution
- Bad for: Range queries

**By Directory (Lookup)**
- Shard lookup table
- Most flexible
- Additional lookup overhead

### CQRS for Scaling

Use different databases for reads and writes:

- Write database: Normalized, optimized for transactions
- Read database: Denormalized, optimized for queries

---

## 7. Auto-Scaling <a name="auto-scaling"></a>

### What is Auto-Scaling?

Automatically adjust compute capacity based on demand.

### Scaling Metrics

**Infrastructure Metrics**
- CPU utilization
- Memory usage
- Network I/O

**Application Metrics**
- Request count
- Response time
- Queue depth

**Business Metrics**
- Orders per minute
- Active users
- Transaction volume

### Scaling Policies

#### Metric-Based
```
IF average CPU > 70% FOR 5 minutes
THEN add 2 instances
MAX 10 instances
```

#### Scheduled
```
At 9 AM weekdays: scale to 5 instances
At 6 PM weekdays: scale to 2 instances
At midnight: scale to 1 instance
```

#### Predictive
```
Machine learning predicts load
Pre-scale accordingly
```

### Scaling Considerations

1. **Warm-up Time**: New instances take time to be ready
2. **Cooldown Period**: Wait before scaling again
3. **Minimum Instances**: Always keep some running
4. **Maximum Cap**: Don't overspend
5. **Cost vs. Performance**: Balance optimization goals

---

## Scalability Patterns Summary

| Pattern | Use Case | Complexity |
|---------|----------|------------|
| Vertical Scaling | Quick fix, simple apps | Low |
| Horizontal Scaling | Production systems | Medium |
| Load Balancing | Any distributed system | Medium |
| Caching | Read-heavy workloads | Low-Medium |
| Read Replicas | Read-heavy databases | Medium |
| Database Sharding | Large datasets | High |
| Auto-Scaling | Variable workloads | Medium-High |

---

## Decision Framework

### When to Scale Vertically

- Quick solution needed
- Legacy monolithic application
- Database that can't be distributed
- Development/testing environment

### When to Scale Horizontally

- Production system with growth potential
- Microservices architecture
- Need high availability
- Cost-effective scaling needed

### When to Add Caching

- Same data requested repeatedly
- Expensive database queries
- Static or semi-static data
- Response latency is critical

### When to Use Read Replicas

- Read-heavy workload (>80% reads)
- Need to reduce primary DB load
- Geographic distribution needed
- Reporting/analytics queries

### When to Shard

- Single DB can't handle write load
- Data too large for single node
- Need geographic data isolation
- Performance degradation at scale