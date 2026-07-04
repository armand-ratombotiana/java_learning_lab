# Replication: Internals

## MySQL Replication

### Binary Log (binlog)
- Records all changes in order
- Statement-based or row-based format
- Followers read binlog and apply

### Replication Threads
- **IO thread**: Reads binlog from leader
- **SQL thread**: Applies relay log to local database

### GTID (Global Transaction ID)
- Unique ID per transaction across entire cluster
- Makes failover and positioning easier

## Cassandra Replication

### Hinted Handoff
- If replica is down, coordinator stores hint
- Hint delivered when replica comes back
- Prevents data loss during temporary failures

### Read Repair
- On read, compare all replicas
- If inconsistent, repair stale replicas
- Can be synchronous or asynchronous

### Anti-Entropy (Merkle Trees)
- Periodic comparison of replica data
- Merkle tree exchange for efficient diff
- Stream differences between nodes
