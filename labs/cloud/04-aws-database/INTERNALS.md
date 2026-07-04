# AWS Database — Internals

## Aurora Storage Internals

### Log-Structured Storage
```
App ──► Writer ──► Redo Log ──► Storage Service
  │                              ├── 4/6 writes ACK for commit
  │                              ├── 6 copies across 3 AZs
  │                              └── Background: repair, rebalance, backup
  │
  └── Reader ◄── (reads from same storage, no separate copy)
```

- **Protection Group**: 6 copies across 3 AZs (2 copies per AZ)
- **Quorum**: Write requires 4/6 ACK; read requires 3/6
- **Storage Auto-Repair**: Corrupted pages replaced from good copies
- **Continuous Backup**: Backs up to S3 without performance impact

### Aurora Performance Advantage
- MySQL uses buffer pool + checkpoint + crash recovery
- Aurora uses buffer pool + redo log shipping to storage
- No double-write buffer, no purge, no crash recovery
- 5-10x MySQL performance on same hardware

## DynamoDB Internals

### Partition Architecture
```
Table = set of partitions
Each partition = SSD storage + CPU + memory (up to 10GB, 3000 RCU, 1000 WCU)

Partition split when:
  - Table exceeds 10GB per partition
  - Throughput exceeds 3000 RCU or 1000 WCU per partition

Data distribution:
  partition = hash(partition_key) mod N (number of partitions)
  All items with same partition key go to same partition
```

### DynamoDB Streams
- Records changes to DynamoDB table (CRUD operations)
- 24-hour retention
- Triggers Lambda for event-driven processing
- Ordered per partition key, best-effort ordering per shard
- Kinesis adapter for streaming to Redshift, OpenSearch, S3

## RDS Internals

### Multi-AZ Failover
```
Primary instance ──► Sync replication to standby
                        │
Failover scenario:     │
  1. Primary AZ outage │
  2. RDS detects loss  │
     of connectivity   │
  3. DNS CNAME updated │
     to standby IP     │
  4. Standby promoted  │
     as new primary    │
  5. New standby       │
     provisioned in    │
     another AZ        │
  Total: 60-120s outage│
```

### RDS Proxy
- Connection pooling between application and RDS
- Reduces connection overhead (Lambda → RDS connections)
- Handles IAM authentication transparently
- Connection multiplexing (up to 10,000 connections with minimal RDS connections)
