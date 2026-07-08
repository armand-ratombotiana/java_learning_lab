# Internals: Database Replication

## Internal Architecture

### Replication Manager Internals
The ReplicationManager maintains node topology and coordinates data replication between leader and followers.

**Data Structures:**
- `Map<String, ReplicaNode>`: All nodes indexed by ID
- `ReplicationStrategy`: Configured strategy (SYNC/ASYNC/QUORUM)
- Health check scheduler running at fixed interval

### Leader Election
When a leader fails:
1. Health checker detects missed heartbeats (>15s threshold)
2. Healthy followers are identified
3. First healthy follower is promoted to leader
4. Old leader is demoted if it recovers

### Replication Protocol
**Synchronous:** Leader waits for ALL followers to acknowledge before returning success.
**Quorum:** Leader waits for majority (N/2 + 1) of followers.
**Asynchronous:** Leader returns immediately after local write.

### Conflict Resolution
**Last Write Wins:** Uses version counter to pick the highest version.
**Timestamp Based:** Uses wall clock time (requires clock synchronization).
**CRDT Merge:** Merges values using set union for conflict-free resolution.

### WAL (Write-Ahead Log)
Each node maintains a WAL for crash recovery. Replication lag is tracked per follower to detect slow replicas.

### Monitoring
- Replication lag per follower (ms)
- Node health status
- Leader/follower topology
- Conflict count by key
- Throughput per replication stream
