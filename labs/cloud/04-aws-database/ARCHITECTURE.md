# Architecture — AWS Database

## E-Commerce Platform Database Design

```
┌─────────────────────────────────────────────────────────────┐
│                    E-Commerce Platform                        │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  SQL (RDS/Aurora — Product Catalog)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Products     │  │ Categories   │  │ Inventory    │      │
│  │ (ACID, joins)│  │ (tree/hierarchy) │ (stock mgmt)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  NoSQL (DynamoDB — Session, Cart, Orders)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Sessions     │  │ Cart Items   │  │ Orders       │      │
│  │ (expiring TTL)│  │ (user key)   │  │ (order key)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  Cache (ElastiCache Redis)                                    │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │ Product Cache │  │ Session Store│                         │
│  │ (5 min TTL)   │  │ (30 min TTL) │                         │
│  └──────────────┘  └──────────────┘                         │
│                                                               │
│  Warehouse (Redshift — Analytics)                             │
│  ┌────────────────────────────────────────┐                 │
│  │ Sales Aggregates, User Analytics,       │                 │
│  │ Inventory Forecasting                   │                 │
│  └────────────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## Multi-Region Active-Active with DynamoDB Global Tables

```
┌─────────────────────┐          ┌─────────────────────┐
│    us-east-1         │          │    eu-west-1         │
│                      │          │                      │
│  ┌──────────────┐   │          │  ┌──────────────┐   │
│  │ DynamoDB     │   │  async   │  │ DynamoDB     │   │
│  │ Table        │◄──┼─────────┼──►│ Table        │   │
│  │ (Orders)     │   │          │  │ (Orders)     │   │
│  └──────────────┘   │          │  └──────────────┘   │
│                      │          │                      │
│  ┌──────────────┐   │          │  ┌──────────────┐   │
│  │ Lambda App   │   │          │  │ Lambda App   │   │
│  │ us-east-1a   │   │          │  │ eu-west-1a   │   │
│  └──────────────┘   │          │  └──────────────┘   │
└─────────────────────┘          └─────────────────────┘

Conflict resolution: "last writer wins" (timestamp-based)
```

## Caching Strategy Pattern

```
Request flow:
  1. Parsing service checks Redis for product:{id}
  2. HIT → return JSON directly (sub-ms)
  3. MISS → query RDS Products table
  4. Store in Redis with 5 minute TTL
  5. Return JSON

Invalidation:
  1. Admin updates product → update RDS
  2. Publish "product:updated:{id}" to SNS
  3. Cache service subscribes, deletes Redis key
  4. Next request: cache miss → fresh data

Result: RDS handles ~5% of reads; Redis handles 95%
```
