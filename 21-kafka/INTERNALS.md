# Kafka Internals

## Log Structure

### Partition Log Architecture

```
Partition: topic-partition-0
├── 00000000000000000000.log          (data file)
├── 00000000000000000000.index        (offset index)
├── 00000000000000000000.timeindex    (timestamp index)
├── 00000000000000000000.txnindex     (transaction index)
├── 00000000000000000000.log.abstract (cleanup metadata)
└── leader-epoch-checkpoint           (leader epoch)
```

### Log Segment Files

```
┌─────────────────────────────────────────────────────────────┐
│                    Log Segment                               │
├─────────────────────────────────────────────────────────────┤
│  .log file:                                                 │
│  ┌─────────┬─────────┬─────────┬─────────┬─────────┐      │
│  │ Record  │ Record  │ Record  │ Record  │ Record  │      │
│  │ Batch 1 │ Batch 2 │ Batch 3 │ Batch 4 │ Batch 5 │      │
│  │ offset=0│ offset=5│ offset=10│offset=15│ offset=20│      │
│  └─────────┴─────────┴─────────┴─────────┴─────────┘      │
│      │                                                       │
│      └── Each batch contains:                               │
│          - Base offset                                      │
│          - Length                                          │
│          - Partition leader epoch                          │
│          - CRC                                             │
│          - Magic (current = 2)                             │
│          - Attributes (compression, etc.)                 │
│          - Last offset delta                                │
│          - Timestamp                                       │
│          - [Records...]                                    │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  .index file:                                               │
│  ┌──────────────────┬─────────────────────────────────┐   │
│  │ position (4B)    │ offset (4B)                      │   │
│  ├──────────────────┼─────────────────────────────────┤   │
│  │ 0                │ 0                                │   │
│  │ 104              │ 10                               │   │
│  │ 256              │ 20                               │   │
│  └──────────────────┴─────────────────────────────────┘   │
│  - Sparse index (not every record)                          │
│  - Enables O(log n) lookup                                  │
└─────────────────────────────────────────────────────────────┘
```

### Record Batch Structure

```
┌──────────────────────────────────────────────────────────┐
│ Base Offset: 0 (8 bytes)                                 │
├──────────────────────────────────────────────────────────┤
│ Length: 142 (4 bytes)                                    │
├──────────────────────────────────────────────────────────┤
│ Partition Leader Epoch: 5 (4 bytes)                      │
├──────────────────────────────────────────────────────────┤
│ CRC: 0x12345678 (4 bytes)                                 │
├──────────────────────────────────────────────────────────┤
│ Magic: 2 (1 byte)                                         │
├──────────────────────────────────────────────────────────┤
│ Attributes: 0 (1 byte)                                    │
│   - Compression: NONE (0)                                 │
│   - TimestampType: LOG_APPEND_TIME (1)                   │
│   - Transaction/IsControl: false                         │
├──────────────────────────────────────────────────────────┤
│ Last Offset Delta: 4 (1 byte)                            │
├──────────────────────────────────────────────────────────┤
│ First Timestamp: 1704067200000 (8 bytes)                  │
├──────────────────────────────────────────────────────────┤
│ Max Timestamp: 1704067200000 (8 bytes)                     │
├──────────────────────────────────────────────────────────┤
│ Producer ID: 1000 (8 bytes)                               │
├──────────────────────────────────────────────────────────┤
│ Producer Epoch: 1 (2 bytes)                               │
├──────────────────────────────────────────────────────────┤
│ Base Sequence: 0 (4 bytes)                               │
├──────────────────────────────────────────────────────────┤
│ Number of Records: 5 (4 bytes)                            │
├──────────────────────────────────────────────────────────┤
│  Records:                                                 │
│   ├─ Record 1: key=null, value="msg1"                    │
│   ├─ Record 2: key="k1", value="msg2"                    │
│   ├─ Record 3: key=null, value="msg3"                    │
│   ├─ Record 4: key="k2", value="msg4"                    │
│   └─ Record 5: key=null, value="msg5"                    │
└──────────────────────────────────────────────────────────┘
```

## Replication Protocol

### ISR (In-Sync Replicas) Management

```
Leader: Broker 1 (ISR)
ISR: [Broker 1, Broker 2, Broker 3]

┌──────────────────────────────────────────────────────────┐
│                     Leader Election                        │
├──────────────────────────────────────────────────────────┤
│  Broker 1 (Leader) fails                                 │
│                                                          │
│  1. Controller receives ZK notification                  │
│  2. Select new leader from ISR: Broker 2                 │
│  3. Send UpdateMetadata to all brokers                   │
│  4. New leader begins accepting writes                   │
│  5. Broker 1 comes back → joins ISR                      │
└──────────────────────────────────────────────────────────┘
```

### ISR Conditions

```
ISR = {brokers where:
  - Has caught up to min.insync.replicas
  - Last fetch time < replica.lag.time.max.ms
  - Current leader's high watermark
}

Replica lag calculation:
  lag = leader.logEndOffset - follower.logEndOffset
  isOutOfSync = lag > replica.lag.max.messages
              || timeSinceLastFetch > replica.lag.time.max.ms
```

### High Watermark (HW)

```
┌────────────────────────────────────────────────────────────┐
│                   Replicated Log State                     │
├────────────────────────────────────────────────────────────┤
│  Broker 0 (Leader)                                        │
│  HW = 12, LEO = 15                                        │
│  ┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐     │
│  │ 0-4│ 5-8│ 9-10│ 11-12│ 13  │ 14  │ 15  │    │    │     │
│  └────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘     │
│  Committed  Uncommitted                                    │
├────────────────────────────────────────────────────────────┤
│  Broker 1 (Follower)                                      │
│  HW = 12                                                  │
│  ┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐     │
│  │ 0-4│ 5-8│ 9-10│ 11-12│    │    │    │    │    │     │
│  └────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘     │
│  Replicated (committed)                                    │
├────────────────────────────────────────────────────────────┤
│  Broker 2 (Follower)                                      │
│  HW = 12, LEO = 12                                        │
│  Same as Broker 1                                          │
└────────────────────────────────────────────────────────────┘
```

### Fetch Protocol

```java
// Follower fetch request to leader
FetchRequest:
  - max_wait_ms: 500
  - min_bytes: 1
  - max_bytes: 10485760
  - isolation_level: read_committed (for transactions)
  - topics: [{topic: "orders", partitions: [{partition: 0, current_leader_epoch: 5, fetch_offset: 12}]}]

// Leader response
FetchResponse:
  - throttle_time_ms: 0
  - topics: [{
      topic: "orders",
      partitions: [{
        partition: 0,
        error_code: 0,
        high_watermark: 12,
        log_start_offset: 0,
        records: [ ... batch of records ... ]
      }]
    }]
```

### Producer ACKs Mechanism

```
┌─────────────────────────────────────────────────────────────┐
│                   Acknowledgment Levels                     │
├─────────────────────────────────────────────────────────────┤
│  acks=0:                                                   │
│    Producer sends, doesn't wait                            │
│    Fastest, no guarantee                                   │
│                                                          │
│  acks=1 (default):                                         │
│    Wait for leader ACK                                    │
│    Guarantees leader has record                           │
│    Lost if leader fails before replication                │
│                                                          │
│  acks=all (-1):                                            │
│    Wait for all ISRs                                      │
│    Slowest, strongest guarantee                           │
│    min.insync.replicas controls minimum                   │
└─────────────────────────────────────────────────────────────┘
```

## Controller Internals

### Controller Election

```
1. Startup: All brokers try to create /controller znode
2. Winner becomes controller (ZooKeeper ephemeral node)
3. Loser gets notification, becomes follower

Controller ZooKeeper path:
  /kafka/controller
    → {"version":1,"brokerid":1,"timestamp":"..."}
```

### Controller Responsibilities

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Functions                   │
├─────────────────────────────────────────────────────────────┤
│  1. Leader election for partitions                         │
│  2. Partition reassignment                                 │
│  3. ISR management                                         │
│  4. Topic creation/deletion                                │
│  5. Broker join/leave handling                            │
│  6. Preferred replica election                             │
│  7. Topic config changes                                   │
└─────────────────────────────────────────────────────────────┘
```

### Controller Event Processing

```
KafkaController.onBrokerChange:
  1. Receive broker change event
  2. Update live broker set
  3. Trigger partition leader election if needed
  
  // Partition state machine
  PartitionStateMachine:
    - NewLeaderAndIsr
    - UpdateMetadata
    - LeaderAndIsrResponse
```

## Transaction Protocol

### Transaction Coordinator

```
┌─────────────────────────────────────────────────────────────┐
│                   Transaction Lifecycle                      │
├─────────────────────────────────────────────────────────────┤
│  1. Producer InitTransactions()                            │
│     → Register with coordinator                            │
│                                                          │
│  2. BeginTransaction()                                     │
│     → Coordinator registers PID                           │
│                                                          │
│  3. SendOffsets() / Send()                                │
│     → Mark as part of transaction                         │
│                                                          │
│  4. CommitTransaction()                                   │
│     → Write COMMIT marker to __transaction_state topic    │
│     → Update partition HW                                  │
│                                                          │
│  5. AbortTransaction()                                    │
│     → Write ABORT marker                                  │
└─────────────────────────────────────────────────────────────┘
```

### Transaction Log Topic

```
Internal topic: __transaction_state
  Partitions: transaction.state.num.partitions (default: 50)

Record format:
  Key: PID (producer ID)
  Value: {
    epoch: producer epoch,
    transaction_timeout_ms: 300000,
    status: [ongoing|prepare_commit|prepared|complete],
    partitions: [topic-0, topic-1]
  }
```

### Idempotent Producer

```
Requirements for exactly-once:
  - producer.id + baseSequence uniqueness
  - Transaction coordinator tracks in-flight batches
  - On retry, same sequence number used
  
Transaction marker (control batch):
  ┌──────────────────────────────────────┐
  │  CONTROL | 0xFFFF (commit/abort)     │
  │  PID + Epoch                        │
  │  Transaction ID                      │
  └──────────────────────────────────────┘
```

## Storage Internals

### Log Compaction

```
┌─────────────────────────────────────────────────────────────┐
│                   Log Compaction Process                     │
├─────────────────────────────────────────────────────────────┤
│  Before Compaction:                                         │
│  ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┐        │
│  │ K=V1│ K=V2│ K=V3│ K=V1│ K=V4│ K=V2│ K=V5│ K=V3│        │
│  │  0  │  1  │  2  │  3  │  4  │  5  │  6  │  7  │        │
│  └─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┘        │
│  key1: [V1@0, V2@1, V1@3, V2@5]                           │
│  key2: [V3@2, V4@4, V5@6]                                  │
│                                                          │
│  After Compaction (delete tombstone retained):           │
│  ┌─────┬─────┬─────┬─────┬─────┐                         │
│  │ K=V2│ K=V4│ K=V2│ K=V5│ DEL │  ← Tombstone for K=V3 │
│  │  3  │  4  │  5  │  6  │  7  │                        │
│  └─────┴─────┴─────┴─────┴─────┘                         │
│  key1: [V2@5] (V1@3 removed)                              │
│  key2: [V4@4] (V3 deleted)                               │
└─────────────────────────────────────────────────────────────┘
```

### Retention Policies

```
log.retention.hours: 168 (7 days)
log.retention.bytes: -1 (unlimited)
log.retention.check.interval.ms: 300000

Cleanup policies:
  - delete: Remove old segments
  - compact: Keep latest value per key  
  - delete + compact: Both
```

## Network Architecture

### Request/Response Flow

```
┌────────────────────────────────────────────────────────────┐
│                     Network Layer                            │
├────────────────────────────────────────────────────────────┤
│  Client ──[ProduceRequest]──> Broker                        │
│                                         │                   │
│                    ┌────────────────────┼────────────┐     │
│                    │ Request Channel   │            │     │
│                    │ (KafkaRequestPool) │            │     │
│                    └────────────────────┼────────────┘     │
│                                         │                   │
│  Client <──[ProduceResponse]──┤  Processor Thread         │
│                                 └─────────────────────────┘
```

### Selector Implementation

```
NIO Selector:
  - One selector per broker
  - Handles all connections
  - Multiple threads (kafka.network:type=SocketServer,...) 

Acceptor threads:
  - Accept new connections
  - Round-robin to processor threads

Processor threads (default: 3):
  - Read requests from clients
  - Write responses to clients
  - Handle protocol parsing
```

### Zero-Copy Transfer

```
Traditional:                          Zero-Copy:
┌─────────┐    ┌─────────┐   ┌───┐   ┌─────────┐    ┌───┐
│  Disk   │───▶│  App    │──▶│ NIC│   │  Disk   │───▶│NIC│
│ Kernel  │    │  Buffer │   └───┘   │ Kernel  │    └───┘
└─────────┘    └─────────┘           └─────────┘

4 copies:                           2 copies:
1. disk → kernel                   1. disk → kernel
2. kernel → app                    2. kernel → NIC (sendfile)
3. app → kernel
4. kernel → NIC

FileChannel.transferTo():
  Uses sendfile() system call
  Eliminating user-space copies
```