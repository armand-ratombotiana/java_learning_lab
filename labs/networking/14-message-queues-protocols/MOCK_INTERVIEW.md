# Message Queues & Protocols — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is a message queue? What problems does it solve in distributed systems?

**Expected coverage**: Asynchronous communication between services, decouples producers and consumers, provides buffering (handles traffic spikes), reliable delivery (persistence, retries), load leveling, ensures at-least-once/exactly-once semantics, enables event-driven architectures, fault tolerance (consumer down → messages queued). Common patterns: pub/sub, work queues, dead letter queues.

**Q2**: Explain the difference between message brokers and event streaming platforms (RabbitMQ vs Kafka).

**Expected coverage**: Message broker (RabbitMQ): push-based, smart broker/dumb consumer, AMQP, routing (direct, topic, fanout, headers), message acknowledgment, ideal for task distribution, complex routing, transactional messaging. Event streaming (Kafka): pull-based, dumb broker/smart consumer, partitioned log, high throughput (100K msg/s per partition), log compaction, replayability, ideal for event sourcing, audit logs, real-time stream processing. Not mutually exclusive — often used together.

**Q3**: What are the main messaging patterns (pub/sub, work queue, request/reply)?

**Expected coverage**: Pub/Sub: one producer, many consumers (each gets every message). Work Queue (Competing Consumers): one queue, multiple consumers share load, each message processed once. Request/Reply: temporary reply queue, correlation ID for matching responses. Fanout: exchange sends to all bound queues. Direct: routing key match. Topic: pattern-matched routing. Dead Letter Queue: unconsumable messages go here for analysis.

## Intermediate (3 questions)

**Q4**: Compare AMQP, MQTT, and Kafka protocol for IoT data collection.

**Expected coverage**: AMQP (feature-rich, enterprise routing, persistent, higher overhead, best for complex workflows), MQTT (lightweight, 2-byte header, QoS levels 0/1/2, last will/testament, ideal for constrained IoT devices, publish-subscribe with topics), Kafka (high throughput, durable log, best for IoT data lakes/streaming analytics, higher resource requirements). For edge devices: MQTT to gateway, gateway publishes to Kafka for backend.

**Q5**: How do you guarantee message delivery in a distributed messaging system? Explain at-least-once, at-most-once, exactly-once.

**Expected coverage**: At-most-once (no retries, no ACK, lowest overhead, loss possible, best for telemetry), at-least-once (ACK required, retries on failure, duplicates possible, best for task processing), exactly-once (transactional, dedup + idempotency, highest overhead, best for financial transactions). Kafka: exactly-once via idempotent producer + transactional API + consumer offset management. MQTT QoS 2 delivers exactly-once via 4-step handshake.

**Q6**: What is Kafka partitioning? How do you choose the right partition count?

**Expected coverage**: Topic split into partitions (ordered, immutable log), each partition on different broker (parallelism), messages in same partition have same key → same consumer group member. Partition count: at least max(consumers), at most broker capacity (file descriptors, disk, memory). Rules: key-based partitioning (partition = hash(key) % N), round-robin (null key). Rebalance triggers when consumer joins/leaves group (EagerSticky vs CooperativeSticky assignors).

## Advanced (2 questions)

**Q7**: Design a messaging system for real-time fraud detection handling 500K transactions/second.

**Expected coverage**: Kafka for high-throughput ingestion (100 partitions, 10 brokers, replication factor 3), stream processing (Kafka Streams/Flink for windowed fraud detection: count per user per minute, anomaly detection), low-latency path (alert fired via Redis pub/sub for immediate action), stateful processing (user state store in RocksDB for pattern detection), exactly-once semantics for critical transactions, idle consumer detection and alerting. Kafka as backbone, Flink for stateful processing.

**Q8**: Your message broker is experiencing backpressure. Queues are growing. Consumers can't keep up. Walk through diagnosis and resolution.

**Expected coverage**: Diagnosis: monitor queue depth (RabbitMQ: management UI queue length, Kafka: consumer lag via kafka-consumer-groups), identify slow consumer (CPU bottleneck, DB contention, external API call latency), check consumer parallelism (too few consumers vs partition count). Resolution: scale horizontally (add more consumers), batch processing (increase fetch.min.bytes/max.poll.records in Kafka), optimize consumer processing (asynchronous non-blocking), increase partitions (needs new topic or repartitioning), backpressure signal to producers (Kafka: producer acks=all, RabbitMQ: publisher confirms + flow control).
