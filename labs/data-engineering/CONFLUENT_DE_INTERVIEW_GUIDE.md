# Confluent / Kafka Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): Streaming experience, Kafka knowledge
- Technical Phone (45-60 min): Kafka fundamentals, Streams, Connect
- System Design (60 min): Real-time pipelines, multi-region, CDC
- Distributed Systems Deep Dive (60 min): Replication, controller, performance
- Behavioral + Bar Raiser (45 min): Production incidents, open-source

## Key Topics

### Kafka Architecture
- Topic, partition, offset, consumer group fundamentals
- Leader/follower replicas, ISR (In-Sync Replicas)
- Producer: acks=0/1/all, idempotent, transactional
- Consumer: poll loop, commit strategies (auto, manual, async)
- Consumer rebalancing: eager vs cooperative (sticky)

### Performance
- Zero-copy: `sendfile` bypasses application buffer
- Batching: batch.size, linger.ms, compression (gzip, snappy, lz4, zstd)
- Partition count: throughput vs latency trade-off
- Broker: num.network.threads, num.io.threads, log segment sizing

### Kafka Streams
- KStream vs KTable vs GlobalKTable
- State stores: RocksDB (default), in-memory, persistent
- Exactly-once semantics: processing.guarantee=exactly_once_v2
- Interactive queries: query state stores via RPC
- Topology: source processors → stream processors → sink processors

### ksqlDB
- Pull vs push queries
- Persistent vs transient queries
- Materialized views for real-time dashboards
- Windowed aggregations (hopping, tumbling, session)

### Confluent Cloud Features
- Cluster Linking: multi-region async replication
- Schema Linking: sync schemas across clusters
- Flink on Confluent Cloud: serverless Flink
- Tiered Storage: move older data to S3/Blob
- Kafka Connect: 200+ connectors, SMTs

### Replication Protocol
- Controller: leader election via KRaft (Raft consensus)
- unclean.leader.election.enable: allow out-of-sync replica
- min.insync.replicas: minimum ISR for write acceptance
- Replica fetcher: replicas fetch from leader asynchronously

## Sample Questions
1. "Design a real-time fraud detection pipeline with Kafka Streams"
2. "How does Kafka achieve high throughput with sequential I/O?"
3. "Design a multi-region active-active Kafka deployment"
4. "Explain the Kafka replication protocol in detail"
5. "Design a CDC pipeline from Postgres to Kafka with Debezium"

## Resources
- Confluent Developer: free courses and certification
- Kafka: The Definitive Guide (Narkhede, Shapira, Palino)
- Confluent blog: engineering posts, KIPs
