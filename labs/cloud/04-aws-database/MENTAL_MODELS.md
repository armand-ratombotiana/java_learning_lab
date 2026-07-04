# Mental Models for AWS Database

## 1. The Database Spectrum

```
Relational ────────────────────────────── NoSQL
    │                    │                        │
   RDS                 Aurora               DynamoDB
  (Traditional)    (Cloud-optimized)        (Key-value)
    │                    │                        │
  ACID joins       5x performance         Auto-scaling
  ORM-friendly     Storage-compute        Single-digit ms
  Manual scaling    decoupled             Unlimited scale

  Use cases:
  RDS:    Existing apps, complex queries, joins
  Aurora: High throughput relational, global
  DynamoDB: Session store, leaderboards, IoT, cart
```

## 2. The Cache-aside Pattern
```
1. App checks cache (ElastiCache) first
2. Cache hit → return data (sub-ms)
3. Cache miss → query database (RDS/DynamoDB)
4. Store result in cache with TTL
5. Return data

           ┌──────────┐
           │  Client  │
           └────┬─────┘
                │
          ┌─────▼──────┐
          │  Check      │
          │  Cache      │
          └──┬──────┬───┘
        hit │      │ miss
            │      │
     ┌──────▼┐  ┌──▼──────┐
     │Return │  │  Query   │
     │Cache  │  │Database  │
     │Data   │  └──┬───────┘
     └───────┘     │
                   │
            ┌──────▼─────┐
            │ Update      │
            │ Cache + TTL │
            └──────┬──────┘
                   │
            ┌──────▼─────┐
            │   Return   │
            └────────────┘
```

## 3. The Read Capacity Unit (RCU) and Write Capacity Unit (WCU)

```
DynamoDB capacity = discrete units you pre-purchase

1 RCU = 1 strongly consistent read/sec for 4KB item
       = 2 eventually consistent reads/sec for 4KB item
1 WCU = 1 write/sec for 1KB item

Think of it like a toll booth:
  - RCU/WCU = number of toll lanes
  - Throttling = traffic jam when all lanes full
  - Auto-scaling = dynamic toll lane addition
```

## 4. The Multi-AZ Database

```
Primary (writer) ──► us-east-1a
    │ Synchronous replication
Standby (reader) ──► us-east-1b
    │ Can also serve reads (Aurora up to 15 read replicas)
Read Replica ──► us-east-1c

Failover: DNS CNAME automatically points to new primary
  RDS: ~60-120s failover
  Aurora: ~30s failover (no data loss)
```
