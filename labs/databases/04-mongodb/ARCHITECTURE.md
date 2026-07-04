# Architecture: MongoDB

## Single Node → Replica Set → Sharded Cluster

### Single Node
```
App → mongod
```
Simple, no redundancy. Development/testing only.

### Replica Set (Production Minimum)
```
App → mongos/router (or direct to primary)
       │
  ┌────┴────┐
  │ PRIMARY  │  ← all writes
  ├──────────┤
  │ SECONDARY│  ← reads (optional)
  ├──────────┤
  │ SECONDARY│  ← failover candidate
  └─────────┘
```

### Sharded Cluster (Scale-Out)
```
App
 │
 ┌┴┐ ┌┴┐ ┌┴┐       ← Application servers
 │ │ │ │ │ │
 └┬┘ └┬┘ └┬┘
  │    │    │
 ┌┴────┴────┴┐
 │  mongos   │  ← Routers (stateless, can be many)
 └────┬──────┘
      │
 ┌────┴──────┐
 │  Config   │  ← Metadata (replica set, 3 members)
 │  Servers  │
 └────┬──────┘
      │
 ┌────┴────┴────┐
 │       │       │
Shard1  Shard2  Shard3  ← Each shard = replica set
```

## Application Integration Patterns

### Direct Driver (Spring Boot)
```
Controller → Service → Repository → MongoClient → Replica Set
```

### Spring Data MongoDB
```
Controller → Service → MongoRepository → MongoTemplate → MongoClient
```

### Reactive Stack
```
Controller (WebFlux) → Service → ReactiveMongoRepository → MongoClient (RxJava)
```

## Deployment Models

### Standalone (dev)
- Single mongod
- No replication, no high availability

### Replica Set (prod small-medium)
- 3 data-bearing members + optional arbiter
- Automatic failover < 30s

### Sharded Cluster (prod large)
- 2+ shards, each a replica set
- 3 config servers
- 1+ mongos routers
- Balancer for automatic data distribution

### Atlas (fully managed)
- Automated backups and scaling
- Global clusters for multi-region
- Serverless instances for variable workloads
