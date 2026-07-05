# Architecture: Event Streaming

## High-Level Architecture
```
[Producers]                  [Consumers]
    |                            |
    |  ProduceRequest          FetchRequest
    v                            ^
+--------+                    +--------+
| Broker | <-- replication -> | Broker |
| (Leader|                    | (Follower)
|  P0)   |                    |  P0    |
+--------+                    +--------+
    |                              |
[Log Segments]              [Log Segments]
    |                              |
[Disk]                       [Disk]

[Controller] -- manages metadata, leader election
[Consumer Coordinator] -- manages consumer groups
```

## Technology Stack
- **Language**: Java 17
- **Networking**: Netty 4.x (NIO)
- **Build**: Maven
- **Storage**: Local filesystem (log segments)
- **Serialization**: Custom binary protocol (similar to Kafka wire protocol)
- **Metadata**: ZooKeeper or embedded Raft consensus (KRaft-style)
- **Containerization**: Docker + docker-compose
- **Monitoring**: Micrometer + Prometheus + Grafana (consumer lag dashboards)

## Key Configuration
- `num.partitions`: 3 default
- `replication.factor`: 3
- `min.insync.replicas`: 2
- `log.retention.hours`: 168 (7 days)
- `log.segment.bytes`: 1073741824 (1 GB)
- `auto.create.topics.enable`: false
