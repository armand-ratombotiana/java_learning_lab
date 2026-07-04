# AWS Storage — Internals

## S3 Internals

### Partition and Placement
- **Partition**: A group of storage servers responsible for a key range
- **Key hash**: md5(key) determines partition assignment
- **Front-end**: Routes requests, caches metadata in internal NoSQL (Shard Store)
- **Storage node**: Stores object bits on local disk (custom Linux + RAID)
- **Coordinator**: Ensures 3-way replication across AZs

### PUT Path Detail
```
Client → S3 Regional DNS → Front-end (load-balanced)
  → Front-end checks authz cache
  → Front-end assigns object to partition (hash key mod N)
  → Partition leader sends write to 3 storage nodes
  → Each node writes to 2 disks (RAID-1 mirror)
  → Cluster quorum: 2/3 ACK before responding
  → Response to client: 200 OK + ETag
```

### S3 Consistency Model
- **PUT new object**: Read-after-write consistency (200 OK → immediately visible)
- **PUT overwrite**: Eventually consistent (old version may serve for short time)
- **DELETE**: Eventually consistent (object may appear for short time)
- **LIST**: Eventually consistent (new objects may not appear immediately)
- **Single-key read**: Strongly consistent for PUT of new objects (since Dec 2020)

### Storage Class Implementation
- **Standard**: 3+ AZs, 3 replicas each → 9 replicas total
- **Standard-IA**: 3+ AZs, smaller minimum size (128KB), retrieval fee
- **One Zone-IA**: 1 AZ, 3 replicas within AZ
- **Glacier**: 3+ AZs, tape-like archival storage (backed by S3 but with retrieval time)
- **Deep Archive**: 3+ AZs, stored on optical media (robotic retrieval)

## EBS Internals

### Replication
- Each EBS volume replicated within the same AZ (not across AZs)
- Two replicas minimum: sync write requires both ACK
- gp3/io2: 99.999% durability within AZ
- For cross-AZ: manual snapshot → create volume in new AZ

### Nitro Block Storage
- Nitro instances access EBS through Nitro controller card (NVMe interface)
- No host CPU involvement in I/O path
- EBS bandwidth and IOPS are instance-type-specific (not shared)
- io2 Block Express: dedicated Nitro controller per volume

## EFS Internals

### Distributed File System
- EFS uses a distributed data plane with metadata and data servers
- Data stored as chunks (~16KB) across multiple storage servers
- Metadata: directory structure, permissions, file locks
- Performance modes:
  - **General Purpose** (default): <7000 files/sec operations
  - **Max I/O**: >7000 files/sec, higher latency
- Throughput modes:
  - **Bursting**: Throughput scales with file system size
  - **Provisioned**: Fixed throughput regardless of size
