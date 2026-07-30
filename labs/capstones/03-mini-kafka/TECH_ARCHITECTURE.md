# Technical Architecture: Distributed Messaging System (Mini Kafka)

## Architecture Overview

```
                    ┌─────────────────────────────┐
                    │      ZooKeeper / KRaft       │
                    │  (Metadata + Leader Election)│
                    └──────────┬──────────────────┘
                               │
      ┌────────────────────────┼────────────────────────┐
      │                        │                        │
┌─────▼──────┐          ┌─────▼──────┐          ┌─────▼──────┐
│  Broker 1   │          │  Broker 2   │          │  Broker 3   │
│ (Leader:    │◄────────►│ (Follower:  │◄────────►│ (Follower:  │
│  topicA-p0, │ Gossip   │  topicA-p0,│ Gossip   │  topicA-p0,│
│  topicB-p1) │          │  topicB-p0 │          │  topicA-p1)│
├─────────────┤          ├─────────────┤          ├─────────────┤
│ topicA-p0   │          │ topicA-p0   │          │ topicA-p0   │
│ topicB-p1   │          │ topicB-p0   │          │ topicA-p1   │
│ - Leader    │          │ - Follower  │          │ - Follower  │
│ - Writes    │          │ - Replicates│          │ - Replicates│
└─────────────┘          └─────────────┘          └─────────────┘
```

## Component Breakdown

### 1. Metadata Layer (KRaft / ZooKeeper)
- **Purpose**: Cluster metadata, topic configuration, partition leadership, consumer group offsets
- **Design**: KRaft (Kafka Raft) protocol replacing ZooKeeper for metadata consensus; Raft-based quorum for 3+ controllers
- **Metadata topics**: __cluster_metadata (KRaft), __consumer_offsets (offsets), __transaction_state (EOS)

### 2. Broker
- **Message storage**: Append-only log segments on filesystem; each partition = sequential log
- **Replication**: Leader handles all produce requests; followers replicate via FetchRequest from leader
- **Network**: Netty-based TCP server with 1 acceptor + N processor threads
- **Request pipeline**: Socket → Processor → Request Queue → API Handler → Log Append → Response Queue → Socket
- **Purgatory**: Delayed requests (acks=all waiting for replicas, delayed produce) held in purgatory until satisfied or timeout

### 3. Log Segment Structure
```
[Segment File: 00000000000000000000.log]
┌────────────────────────────────────────┐
│ Message 0:  offset=0,  key, value, ts │
│ Message 1:  offset=1,  key, value, ts │
│ Message 2:  offset=2,  key, value, ts │
│ ...                                    │
│ Message N-1: offset=N-1, key, value, ts│
└────────────────────────────────────────┘
[Index File: 00000000000000000000.index]
┌────────────────────────────────────────┐
│ Entry 0: offset=0,     position=0      │
│ Entry 1: offset=1000,  position=524288 │
│ Entry 2: offset=2000,  position=1048576│
│ ...                                    │
└────────────────────────────────────────┘
[Time Index: 00000000000000000000.timeindex]
┌────────────────────────────────────────┐
│ Entry 0: timestamp=T0, offset=0        │
│ Entry 1: timestamp=T1, offset=500      │
│ ...                                    │
└────────────────────────────────────────┘
```

- **Message format**: Size (4 bytes) + CRC (4) + Magic (1) + Attributes (1) + Timestamp (8) + Key Length (4) + Key + Value Length (4) + Value + Headers (variable)
- **Index**: Sparse index (every 4096 bytes); maps relative offset to file position
- **Segment roll**: 1GB or 7 days, whichever hits first

### 4. Replication Protocol
- **Leader**: Appends message locally, tracks highWatermark (last offset acknowledged by all ISR)
- **Follower**: Sends FetchRequest with current offset; leader responds with new messages
- **ISR (In-Sync Replicas)**: Replicas within replica.lag.time.max.ms (default 30s) of leader
- **Leader election**: Preferred leader wins; if unavailable, choose from ISR with most complete log

### 5. Consumer Group Protocol
- **JoinGroup**: Consumer sends JoinGroup request to group coordinator (broker hosting __consumer_offsets partition)
- **SyncGroup**: Leader consumer receives partition assignments; distributes to group members
- **Heartbeat**: Periodic heartbeats to coordinator; failure triggers rebalance
- **Sticky assignor**: Minimizes partition movement during rebalance; uses StickAssignor algorithm

## Data Flow

### Produce Request
```
1. Producer connects to any broker (metadata request → learns leader for target partition)
2. Producer sends ProduceRequest to partition leader
3. Leader validates, appends to local log
4. Leader waits for ISR acknowledgment (if acks=all)
5. Leader returns ProduceResponse with offset
   Total: 1 network round trip (optimized)
```

### Fetch Request
```
1. Consumer sends FetchRequest to partition leader (or any broker for follower fetch)
2. Leader reads from log segment or page cache
3. Leader sends data via zero-copy transfer (sendfile)
4. If no new data, request held in purgatory until timeout (500ms) or new data arrives
   Total: 1 network round trip (long poll)
```

## Deployment Topology

```
Cluster: k8s-kafka (3 brokers)

Broker Pod Layout:
  broker-0 (pod on node1): leader for p0, p5, p10
  broker-1 (pod on node2): leader for p1, p6, p11
  broker-2 (pod on node3): leader for p2, p7, p12

Storage:
  Each broker: 5TB NVMe (local SSD, not network storage)
  RAID-0 striping for throughput; replication provides durability

Networking:
  Inter-broker: 25 Gbps
  Client-broker: 10 Gbps
  Latency: < 0.5ms inter-broker
```

## Tech Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| Language | Java 21 | Runtime |
| Metadata | KRaft (Raft protocol) | Consensus |
| Storage | Local filesystem (ext4/XFS) | Log segments |
| Network | Netty + custom protocol | TCP server |
| Serialization | Custom binary format | Message wire format |
| Compression | Zstd / Snappy | Payload compression |
| Metrics | JMX + Prometheus exporter | Observability |
| Deployment | Kubernetes + StatefulSet | Orchestration |

## Configuration

```properties
# Broker
broker.id=0
log.dirs=/data/kafka/logs
num.network.threads=4
num.io.threads=8
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600

# Topics
num.partitions=20
default.replication.factor=3
min.insync.replicas=2

# Log retention
log.retention.hours=168
log.retention.bytes=1073741824000
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000

# Replication
replica.lag.time.max.ms=30000
replica.fetch.max.bytes=1048576
unclean.leader.election.enable=false

# Performance
compression.type=zstd
message.max.bytes=1048576
max.request.size=1048576
fetch.max.bytes=52428800
```

## Security

- **Authentication**: TLS mutual auth (mTLS) between brokers and clients; SASL/SCRAM for legacy clients
- **Authorization**: ACLs per topic (read, write, describe, create); super.users for admin access
- **Encryption**: TLS 1.3 for inter-broker and client-broker communication
- **Audit**: Producer and consumer request logs for compliance; immutable log of topic configuration changes
