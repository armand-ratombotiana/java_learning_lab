# Apache Kafka — Deep Interview Guide

## Table of Contents
1. [Kafka Fundamentals](#kafka-fundamentals)
2. [Producer Internals](#producer-internals)
3. [Consumer Internals](#consumer-internals)
4. [Exactly-Once Semantics](#exactly-once-semantics)
5. [Kafka Streams & KSQL](#kafka-streams--ksql)
6. [Java Code Examples](#java-code-examples)
7. [20+ Interview Questions](#20-interview-questions)

---

## Kafka Fundamentals

Apache Kafka is a distributed event streaming platform capable of handling trillions of events per day. It provides:
- **Publish/subscribe** messaging
- **Durable storage** with configurable retention
- **Stream processing** in real-time

### Core Concepts

| Concept | Description |
|---------|-------------|
| **Topic** | A category/feed name to which records are published |
| **Partition** | An ordered, immutable sequence of records within a topic |
| **Broker** | A Kafka server that stores data and serves clients |
| **Producer** | Publishes records to topics |
| **Consumer** | Reads records from topics |
| **Consumer Group** | A group of consumers that coordinate to consume partitions |
| **Offset** | A unique identifier for each record within a partition |
| **ZooKeeper / KRaft** | Coordination service for broker metadata |

### Partitioning Strategy

Kafka topics are divided into partitions for parallelism. Each partition is an ordered log. Records within a partition are assigned sequential IDs called offsets.

```
Topic "orders"
┌──────────┐  ┌──────────┐  ┌──────────┐
│Partition 0│  │Partition 1│  │Partition 2│
├──────────┤  ├──────────┤  ├──────────┤
│ offset 0 │  │ offset 0 │  │ offset 0 │
│ offset 1 │  │ offset 1 │  │ offset 1 │
│ offset 2 │  │ offset 2 │  │ offset 2 │
│ ...      │  │ ...      │  │ ...      │
└──────────┘  └──────────┘  └──────────┘
```

**Key-based partitioning**: When a key is provided, Kafka hashes the key to determine the target partition (using murmur2 hash). Without a key, a round-robin or sticky partitioner distributes records.

---

## Producer Internals

### Producer Architecture

```
Producer Application
┌─────────────────────────────────────────────┐
│  Record → Serializer → Partitioner          │
│       ↓                                     │
│  RecordAccumulator (in-memory buffer)       │
│       ↓                                     │
│  Sender Thread → Network Client → Broker    │
└─────────────────────────────────────────────┘
```

### Key Components

#### 1. Serializer
Converts the key and value from Java objects to bytes. Kafka ships with `StringSerializer`, `ByteArraySerializer`, `IntegerSerializer`, and you can implement custom serializers.

#### 2. Partitioner
Determines which partition a record goes to:
- **DefaultPartitioner**: If key is non-null, uses murmur2 hash of the key modulo number of partitions. If key is null, uses a sticky partitioner that batches records to the same partition for efficiency.
- **RoundRobinPartitioner**: Distributes evenly across all partitions.
- **CustomPartitioner**: User-defined partitioning logic.

#### 3. RecordAccumulator
An in-memory buffer that batches records before sending. Key configurations:
- `buffer.memory` — total memory available for buffering (default 32MB)
- `batch.size` — max bytes per batch (default 16KB)
- `linger.ms` — max time to wait before sending a batch (default 0)

When a batch reaches `batch.size` or `linger.ms` expires, the Sender thread picks it up.

#### 4. Sender Thread
Background thread that:
1. Reads batches from the accumulator
2. Sends produce requests to the leader broker for each partition
3. Processes responses (acks, errors)

### Acks Configuration

| `acks` | Guarantee | Description |
|--------|-----------|-------------|
| `0` | None | Fire-and-forget, fastest, may lose data |
| `1` | Leader | Leader writes to its log, no follower confirmation |
| `all` (`-1`) | Full | Leader waits for all in-sync replicas (ISR) |

### Compression

Kafka supports `gzip`, `snappy`, `lz4`, `zstd`. Compression happens at the producer side on entire batches. Benefits:
- Reduced network bandwidth
- Reduced storage on brokers
- Slightly higher CPU usage on producer and broker

### Important Producer Configurations

```properties
bootstrap.servers=localhost:9092,localhost:9093
acks=all
retries=2147483647
max.in.flight.requests.per.connection=5
delivery.timeout.ms=120000
request.timeout.ms=30000
enable.idempotence=true
compression.type=snappy
batch.size=32768
linger.ms=5
buffer.memory=67108864
```

---

## Consumer Internals

### Consumer Architecture

```
Consumer Application
┌───────────────────────────────────────┐
│  Fetcher → Deserializer → Records     │
│       ↓                               │
│  Consumer Poll Loop                    │
│       ↓                               │
│  Offset Commit (auto/manual)          │
└───────────────────────────────────────┘
```

### Consumer Groups and Rebalancing

A consumer group shares the load of reading from a topic. Each partition is assigned to exactly one consumer in the group.

```
Topic with 4 partitions
┌──P0──┐  ┌──P1──┐  ┌──P2──┐  ┌──P3──┐
   ↑           ↑           ↑           ↑
┌──C1──┐  ┌──C1──┐  ┌──C2──┐  ┌──C2──┐
├──────────────┤  ├──────────────┤
│ Consumer Group "g1"              │
└──────────────────────────────────┘
```

**Assignment Strategies**:
- **RangeAssignor**: Assigns partitions by topic range
- **RoundRobinAssignor**: Distributes partitions evenly
- **StickyAssignor**: Minimizes partition movement during rebalance
- **CooperativeStickyAssignor**: Incremental rebalancing (Kafka 2.4+)

### Rebalancing Protocols

#### Eager Rebalancing (Pre-2.4)
1. All consumers stop consuming
2. All consumers revoke their partitions
3. Group coordinator picks a leader
4. Leader computes new assignment
5. All consumers get new assignment
6. Consumption resumes

**Problem**: Stop-the-world — all consumers pause during rebalance.

#### Incremental Cooperative Rebalancing (2.4+)
1. Consumers continue consuming partitions they still own
2. Coordinator signals which partitions to revoke
3. Consumers revoke only those partitions
4. New assignment is sent
5. Consumers start consuming new partitions

### Offset Management

**Auto-commit** (`enable.auto.commit=true`): Commits offsets periodically (default every 5s via `auto.commit.interval.ms`). Risk of duplicates if processing fails between auto-commits.

**Manual commit** (`enable.auto.commit=false`):
- `commitSync()` — blocking, retries on failure
- `commitAsync()` — non-blocking, no retries

**Exactly-once semantics** via transactional API.

#### Offset Storage
Offsets are stored in the internal `__consumer_offsets` topic (compacted).

### Important Consumer Configurations

```properties
bootstrap.servers=localhost:9092,localhost:9093
group.id=order-processing-group
enable.auto.commit=false
auto.offset.reset=earliest
max.poll.records=500
max.poll.interval.ms=300000
session.timeout.ms=45000
heartbeat.interval.ms=3000
isolation.level=read_committed
```

---

## Exactly-Once Semantics

### Delivery Semantics

| Level | Description |
|-------|-------------|
| **At-most-once** | Records may be lost but never duplicated |
| **At-least-once** | Records may be duplicated but never lost |
| **Exactly-once** | Records are processed exactly once |

### Idempotent Producer

Introduced in Kafka 0.11. When `enable.idempotence=true`:
- Each producer gets a unique `producer.id` (PID)
- Each record gets a monotonically increasing sequence number
- Brokers deduplicate based on (PID, partition, sequence)
- Guarantees no duplicates even on retries

**Config**: `enable.idempotence=true`

### Transactions

Kafka Transactions extend idempotence to multi-partition, multi-topic atomic writes.

**Key Concepts**:
- **TransactionalProducer** — wraps produce calls in `beginTransaction()` / `commitTransaction()` / `abortTransaction()`
- **Transaction Coordinator** — broker-side component managing transaction state
- **Transaction Log** — internal topic `__transaction_state`
- **Transactional ID** — unique logical identifier for a producer, used for fencing

#### Transaction Flow

```
1. Producer.initTransactions()
2. Producer.beginTransaction()
3. Producer.send(record1)       → partition 0
4. Producer.send(record2)       → partition 1
5. Producer.sendOffsetsToTransaction()  → commit consumer offsets atomically
6. Producer.commitTransaction()
```

#### Read-Process-Write Pattern

```java
// Exactly-once read-process-write
try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
     KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

    producer.initTransactions();

    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        if (records.isEmpty()) continue;

        producer.beginTransaction();

        for (ConsumerRecord<String, String> record : records) {
            String processed = process(record.value());
            producer.send(new ProducerRecord<>("output-topic", processed));
        }

        producer.sendOffsetsToTransaction(
            getOffsets(records), consumer.groupMetadata()
        );
        producer.commitTransaction();
    }
}
```

---

## Kafka Streams & KSQL

### Kafka Streams

A lightweight stream processing library built on top of Kafka. No separate cluster required.

**Key Concepts**:
- **Stream** — an immutable, replayable sequence of facts (events)
- **Table** — a mutable, queryable view of the latest value per key (materialized view)
- **KStream** — record-by-record processing
- **KTable** — upsert semantics, stateful
- **GlobalKTable** — fully replicated across all instances
- **State Store** — local state (RocksDB or in-memory) backed by a changelog topic

#### Streams DSL Example

```java
StreamsBuilder builder = new StreamsBuilder();
KStream<String, Order> orders = builder.stream("orders",
    Consumed.with(Serdes.String(), orderSerde));

KTable<String, Long> orderCounts = orders
    .filter((key, order) -> order.amount() > 100)
    .groupByKey()
    .count(Materialized.as("high-value-orders"));

orderCounts.toStream().to("order-counts",
    Produced.with(Serdes.String(), Serdes.Long()));
```

**Windowing**:
- Tumbling windows — fixed size, non-overlapping
- Hopping windows — fixed size, overlapping
- Sliding windows — join-based
- Session windows — activity-based

### KSQL

A SQL-like interface for Kafka Streams.

```sql
CREATE STREAM orders (order_id VARCHAR, amount DOUBLE, user_id VARCHAR)
    WITH (kafka_topic='orders', value_format='json');

CREATE TABLE high_value_orders AS
    SELECT user_id, COUNT(*) AS order_count, SUM(amount) AS total
    FROM orders
    WHERE amount > 100
    GROUP BY user_id
    EMIT CHANGES;
```

---

## Java Code Examples

### 1. Custom Partitioner

```java
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

public class CustomerIdPartitioner implements Partitioner {

    private static final int CUSTOMER_PARTITIONS = 10;

    @Override
    public void configure(Map<String, ?> configs) {
        // Read custom config if needed
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

        if (keyBytes == null) {
            // Sticky partition for null keys
            return Utils.toPositive(Utils.murmur2(valueBytes)) % numPartitions;
        }

        String customerId = (String) key;
        // Extract customer region from ID prefix: "US-123" → US partitions
        String region = customerId.substring(0, 2);

        int regionHash = switch (region) {
            case "US" -> 0;
            case "EU" -> 1;
            case "AP" -> 2;
            default -> 3;
        };

        // Use region to select a range of partitions, then hash within that range
        int partitionsPerRegion = numPartitions / 4;
        int regionBase = regionHash * partitionsPerRegion;
        int hash = Utils.toPositive(Utils.murmur2(keyBytes)) % partitionsPerRegion;

        return regionBase + hash;
    }

    @Override
    public void close() {
        // Cleanup
    }
}
```

### 2. Consumer with Rebalance Listener

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatefulRebalanceConsumer {

    private static final Logger log = LoggerFactory.getLogger(StatefulRebalanceConsumer.class);

    private final ConcurrentHashMap<TopicPartition, OffsetState> stateStore = new ConcurrentHashMap<>();

    public void consume(String topic, String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(topic), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    log.info("Partitions revoked: {}", partitions);
                    // Save offsets before losing partitions
                    for (TopicPartition partition : partitions) {
                        OffsetState state = stateStore.get(partition);
                        if (state != null && state.offset > 0) {
                            consumer.commitSync(Map.of(partition,
                                new OffsetAndMetadata(state.offset)));
                            log.info("Committed offset {} for partition {}",
                                state.offset, partition.partition());
                        }
                    }
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    log.info("Partitions assigned: {}", partitions);
                    // Seek to stored offsets or beginning
                    for (TopicPartition partition : partitions) {
                        OffsetState state = stateStore.get(partition);
                        if (state != null) {
                            consumer.seek(partition, state.offset + 1);
                        } else {
                            consumer.seekToBeginning(List.of(partition));
                        }
                    }
                }
            });

            while (true) {
                ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Processing record: key={}, value={}, partition={}, offset={}",
                        record.key(), record.value(),
                        record.partition(), record.offset());

                    processRecord(record);

                    // Track offset in local state
                    stateStore.compute(
                        new TopicPartition(record.topic(), record.partition()),
                        (tp, existing) -> {
                            long currentOffset = record.offset();
                            return existing == null || currentOffset > existing.offset
                                ? new OffsetState(currentOffset, record.timestamp())
                                : existing;
                        }
                    );
                }

                // Commit offsets periodically
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.error("Commit failed for offsets: {}", offsets, exception);
                    }
                });
            }
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        // Simulate processing
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record OffsetState(long offset, long timestamp) {}
}
```

### 3. Exactly-Once Producer with Transactions

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ExactlyOnceProcessor {

    private static final Logger log = LoggerFactory.getLogger(ExactlyOnceProcessor.class);

    private final String sourceTopic;
    private final String sinkTopic;

    public ExactlyOnceProcessor(String sourceTopic, String sinkTopic) {
        this.sourceTopic = sourceTopic;
        this.sinkTopic = sinkTopic;
    }

    public void run(String transactionalId) {
        var consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "exactly-once-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        var producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        producerProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        try (var consumer = new KafkaConsumer<String, String>(consumerProps);
             var producer = new KafkaProducer<String, String>(producerProps)) {

            producer.initTransactions();
            consumer.subscribe(List.of(sourceTopic));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if (records.isEmpty()) continue;

                try {
                    producer.beginTransaction();
                    log.info("Starting transaction, processing {} records", records.count());

                    for (var record : records) {
                        String transformed = transform(record.value());
                        producer.send(new ProducerRecord<>(sinkTopic,
                            record.key(), transformed));
                    }

                    producer.sendOffsetsToTransaction(
                        getOffsets(records), consumer.groupMetadata()
                    );

                    producer.commitTransaction();
                    log.info("Transaction committed successfully");

                } catch (ProducerFencedException e) {
                    log.error("Producer fenced out", e);
                    throw new RuntimeException("Producer fenced", e);
                } catch (Exception e) {
                    log.error("Error in transaction, aborting", e);
                    producer.abortTransaction();
                }
            }
        }
    }

    private String transform(String input) {
        // Some business transformation
        return "[PROCESSED] " + input.toUpperCase();
    }

    private static Map<TopicPartition, OffsetAndMetadata>
    getOffsets(ConsumerRecords<String, String> records) {
        var offsets = new java.util.HashMap<TopicPartition, OffsetAndMetadata>();
        for (var partition : records.partitions()) {
            var partitionRecords = records.records(partition);
            long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
            offsets.put(partition, new OffsetAndMetadata(lastOffset + 1));
        }
        return offsets;
    }
}
```

### 4. Kafka Streams Application with State Store

```java
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class OrderAggregationStream {

    private static final Logger log = LoggerFactory.getLogger(OrderAggregationStream.class);

    public static void main(String[] args) {
        var props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-aggregation-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once_v2");

        var builder = new StreamsBuilder();

        // Source stream
        KStream<String, String> orders = builder.stream("orders",
            Consumed.with(Serdes.String(), Serdes.String()));

        // Parse and enrich
        KStream<String, Order> parsedOrders = orders.mapValues(OrderAggregationStream::parseOrder);

        // High-value order detection with windowed aggregation
        KTable<Windowed<String>, Long> highValueWindowed = parsedOrders
            .filter((key, order) -> order.amount() > 1000)
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
            .count(Materialized.as("high-value-windowed"));

        // Suppress and emit only final results per window
        highValueWindowed
            .toStream()
            .map((windowedKey, count) -> new KeyValue<>(
                windowedKey.key(),
                String.format("{\"key\":\"%s\",\"windowStart\":%d,\"windowEnd\":%d,\"count\":%d}",
                    windowedKey.key(),
                    windowedKey.window().start(),
                    windowedKey.window().end(),
                    count)))
            .to("high-value-counts", Produced.with(Serdes.String(), Serdes.String()));

        // Global aggregate per key
        KTable<String, Long> totalCounts = parsedOrders
            .groupByKey()
            .count(Materialized.as("total-counts"));

        totalCounts.toStream().to("order-totals",
            Produced.with(Serdes.String(), Serdes.Long()));

        var topology = builder.build();
        log.info("Topology: {}", topology.describe());

        var kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.setUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Uncaught exception in thread {}", thread, throwable);
        });

        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private static Order parseOrder(String value) {
        // Simplified parsing — in reality use JSON or Avro
        try {
            var parts = value.split(",");
            return new Order(parts[0].trim(), Double.parseDouble(parts[1].trim()));
        } catch (Exception e) {
            log.error("Failed to parse order: {}", value, e);
            return new Order("unknown", 0);
        }
    }

    record Order(String id, double amount) {}
}
```

### 5. Consumer with Cooperative Sticky Rebalance

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CooperativeRebalanceConsumer {

    private static final Logger log = LoggerFactory.getLogger(CooperativeRebalanceConsumer.class);

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Set<TopicPartition> pausedPartitions = new HashSet<>();

    public void consume(String topic, String groupId) {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
            "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");

        try (var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(List.of(topic), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsLost(Collection<TopicPartition> partitions) {
                    log.info("Partitions lost (cooperative): {}", partitions);
                    // Clean up state for lost partitions
                    for (var partition : partitions) {
                        pausedPartitions.remove(partition);
                    }
                }

                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    log.info("Partitions revoked: {}", partitions);
                    for (var partition : partitions) {
                        consumer.commitSync(Map.of(partition,
                            new OffsetAndMetadata(consumer.position(partition))));
                        pausedPartitions.remove(partition);
                    }
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    log.info("Partitions assigned: {}", partitions);
                    // No need to pause — cooperative rebalance allows continued consumption
                }
            });

            while (!closed.get()) {
                var records = consumer.poll(Duration.ofMillis(100));

                for (var record : records) {
                    log.info("Record: key={}, partition={}, offset={}",
                        record.key(), record.partition(), record.offset());
                    processRecord(record);
                }

                consumer.commitAsync();
            }
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        closed.set(true);
    }
}
```

### 6. Producer with Compression and Batching

```java
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class OptimizedProducer {

    private static final Logger log = LoggerFactory.getLogger(OptimizedProducer.class);

    private final KafkaProducer<String, byte[]> producer;
    private final AtomicLong sentCounter = new AtomicLong();
    private final AtomicLong failedCounter = new AtomicLong();

    public OptimizedProducer(String bootstrapServers) {
        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536);      // 64KB
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);           // 10ms
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 134217728); // 128MB
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576); // 1MB
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION_CONFIG, 5);

        this.producer = new KafkaProducer<>(props);
    }

    public Future<RecordMetadata> sendAsync(String topic, String key, byte[] value) {
        var record = new ProducerRecord<>(topic, key, value);
        return producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                long count = sentCounter.incrementAndGet();
                if (count % 1000 == 0) {
                    log.info("Sent {} records to topic={} partition={} offset={}",
                        count, metadata.topic(), metadata.partition(), metadata.offset());
                }
            } else {
                failedCounter.incrementAndGet();
                log.error("Failed to send record to topic={}", topic, exception);
            }
        });
    }

    public void sendSync(String topic, String key, byte[] value) {
        try {
            var metadata = producer.send(
                new ProducerRecord<>(topic, key, value)).get();
            log.debug("Sent to partition={} offset={}", metadata.partition(), metadata.offset());
        } catch (Exception e) {
            log.error("Sync send failed", e);
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }

    public long getSentCount() {
        return sentCounter.get();
    }

    public long getFailedCount() {
        return failedCounter.get();
    }
}
```

---

## 20+ Interview Questions

### Q1: How does Kafka achieve high throughput?
**Answer**: Kafka achieves high throughput through several mechanisms: **batching** — records are batched before sending, reducing network round trips; **sequential I/O** — Kafka writes to disk sequentially, which is much faster than random I/O; **page cache** — Kafka leverages the OS page cache rather than managing its own cache; **zero-copy** — consumers read directly from the page cache to the network socket without intermediate copies; **partitioning** — horizontal scaling across brokers; **compression** — batches are compressed reducing network and storage costs.

### Q2: Explain the difference between Kafka and traditional message queues (RabbitMQ, ActiveMQ).
**Answer**: Kafka is a distributed log (pull-based) while traditional queues are message brokers (push-based). Kafka provides: durable storage with configurable retention, replay capability, strong ordering within partitions, high throughput, and stream processing. Traditional queues provide: more sophisticated routing (exchanges, bindings), per-message acknowledgments, immediate deletion after consumption, and lower latency for individual messages.

### Q3: What happens during a consumer group rebalance?
**Answer**: A rebalance occurs when consumers join/leave the group or partitions change. In eager rebalancing (pre-2.4): all consumers stop → revoke partitions → coordinator elects a leader → leader computes new assignment → consumers receive assignment → consumption resumes. In cooperative rebalancing (2.4+): only affected partitions are revoked, minimizing downtime. Triggers include: new consumer joins, consumer failure (session timeout), consumer calls `unsubscribe()`, new partitions added to topic.

### Q4: How does the idempotent producer prevent duplicates?
**Answer**: Each idempotent producer gets a unique `producer.id` (PID) and assigns monotonically increasing sequence numbers to each record sent to a partition. The broker tracks the last 5 sequence numbers per (PID, partition). If it receives a duplicate sequence number, it returns success without writing (deduplication). This prevents duplicates from retries even if the previous attempt actually succeeded.

### Q5: Explain Kafka's exactly-once semantics end-to-end.
**Answer**: E2E exactly-once requires: (1) **Idempotent producer** — prevents duplicates within a producer session; (2) **Transactions** — atomic writes across multiple partitions/topics with commit/rollback; (3) **Consumer isolation level `read_committed`** — consumers only see committed transactions, ignoring aborted records; (4) **sendOffsetsToTransaction()** — atomically commits consumer offsets as part of the transaction. This ensures the read-process-write cycle is atomic: either the entire cycle succeeds or it's rolled back.

### Q6: What is the role of the __consumer_offsets topic?
**Answer**: It's an internal compacted topic that stores the committed offsets for each consumer group. Each partition of `__consumer_offsets` is assigned to a broker (based on group ID hash). The group coordinator is the broker that owns the partition for that group. Offsets are committed as messages to this topic; compacted retention ensures only the latest offset per (group, topic, partition) is kept.

### Q7: How does Kafka handle backpressure?
**Answer**: Kafka consumers use a pull model — they request records when ready. `max.poll.records` limits how many records per poll. `fetch.max.bytes` limits the response size. Consumers can pause specific partitions using `pause()` and resume with `resume()`. This gives consumers full control over consumption rate, naturally handling backpressure.

### Q8: Explain Kafka Streams exactly-once semantics.
**Answer**: Kafka Streams uses `processing.guarantee=exactly_once_v2` (Kafka 3.0+). This configures: idempotent producers, transactional producers with unique transactional IDs per task, and consumers with `read_committed` isolation. Streams tasks wrap both processing and offset commits in transactions. Task-level transactional IDs ensure fencing of zombie tasks.

### Q9: What are the trade-offs of increasing the number of partitions?
**Answer**: **Pros**: Higher parallelism (more consumers), higher throughput, faster data distribution. **Cons**: More files open on brokers (each partition = multiple segment files), higher leader election time, more memory for metadata, potential for rebalance latency, upper limit on total partitions per broker (~4000 recommended). **Best practice**: Start with projected throughput × replication factor, monitor and adjust.

### Q10: How does the Kafka consumer assign partitions?
**Answer**: Partition assignment is determined by the **group coordinator** (one broker per group). The coordinator handles: membership tracking via heartbeats, triggering rebalances when members change. The assignment strategy is configured via `partition.assignment.strategy`. The elected consumer leader computes the assignment. Available strategies: Range (default), RoundRobin, Sticky, CooperativeSticky.

### Q11: Describe the Kafka request pipeline.
**Answer**: (1) Producer sends ProduceRequest to partition leader. (2) Leader validates request, appends to its local log. (3) If `acks=all`, leader waits for all in-sync replicas to acknowledge. (4) Leader responds with success/error. For consumers: (1) Fetcher sends FetchRequest to leader. (2) Leader reads from local log or page cache. (3) Response is sent back with records starting from requested offset.

### Q12: What is a Kafka controller?
**Answer**: One broker in the cluster acts as the controller. Responsibilities: managing partition leader elections, handling broker failures (reassigning leaders), updating cluster metadata, creating/deleting topics. In KRaft mode (Kafka 3.x+), ZooKeeper is replaced by a controller quorum using the Raft consensus protocol.

### Q13: How does log compaction work?
**Answer**: Log compaction retains the latest record for each key and deletes older records for that key. It runs in the background — a cleaner thread reads the log from the compaction point, deduplicates by key, and writes a clean segment. Useful for keyed state stores and the `__consumer_offsets` topic. Configured via `cleanup.policy=compact`.

### Q14: Explain the difference between Kafka and Kinesis.
**Answer**: Kafka: self-hosted or Confluent Cloud, unlimited retention, larger ecosystem of tools, more complex operations. Kinesis: fully managed AWS service, maximum retention 365 days, limited to 1MB/s or 1000 records/s per shard, integrated with AWS ecosystem. Both provide ordered records within a partition/shard, horizontal scaling, and replay capability.

### Q15: How do you handle large messages in Kafka?
**Answer**: Options: (1) Increase `max.message.bytes` on broker and `max.request.size` on producer (not recommended beyond 10MB); (2) Store large payloads in external storage (S3, HDFS) and send only the reference in Kafka; (3) Chunk the message into multiple smaller records with a reassembly mechanism at the consumer.

### Q16: What is the role of linger.ms and batch.size?
**Answer**: `batch.size` (default 16KB) is the maximum bytes to accumulate per partition batch. `linger.ms` (default 0) is the maximum time to wait before sending a batch, even if it's not full. These control the batching trade-off: larger batches improve throughput (fewer requests, better compression) but add latency. For high throughput, set `linger.ms=5-10` and `batch.size=32-64KB`.

### Q17: How do you monitor Kafka health?
**Answer**: Key metrics: Under-replicated partitions, ISR shrinks, request handler idle ratio, produce/fetch request rates and latencies, consumer lag (difference between latest offset and consumer offset). Tools: Kafka's JMX metrics, Burrow (consumer lag), Cruise Control (cluster rebalancing), Prometheus + Grafana dashboards.

### Q18: Explain the Kafka Connect framework.
**Answer**: Kafka Connect is a framework for streaming data between Kafka and other systems. **Source connectors** import data into Kafka (e.g., JDBC, Debezium for CDC). **Sink connectors** export data from Kafka (e.g., S3 Sink, Elasticsearch). Connect runs in standalone or distributed mode. Key features: fault tolerance, exactly-once (with idempotent producer), REST API for management, schema support.

### Q19: What is the difference between KStream and KTable?
**Answer**: **KStream** represents a record stream — each record is a new independent fact. Analogous to a changelog. **KTable** represents a changelog that's materialized into a state — each new record with the same key updates/replaces the previous value. Analogous to a database table. Joining KStream-KTable yields a stream enriched with table lookups at event time.

### Q20: How do you handle schema evolution in Kafka?
**Answer**: Use a **Schema Registry** (Confluent Schema Registry, Apicurio). **Avro** with compatibility modes: BACKWARD (new schema can read old), FORWARD (old can read new), FULL (both), NONE (no checks). The producer sends the schema ID with each record; the consumer fetches schemas from the registry. Serializers/deserializers handle schema lookup and wire format automatically. **Protobuf** and **JSON Schema** are also supported.

### Q21: What is the cooperative sticky assignor and why is it important?
**Answer**: The CooperativeStickyAssignor performs incremental rebalancing — instead of stopping all consumers and revoking all partitions, only the partitions that need to move are revoked. Consumers continue processing unaffected partitions. This significantly reduces the "stop-the-world" impact of rebalances. It's the recommended assignor for Kafka 2.4+.

### Q22: How does Kafka's storage layer work at the filesystem level?
**Answer**: Each partition is a directory on disk containing segment files. Each segment file is typically 1GB (configurable). Active segment is being written to; older segments are read-only. Index files map offsets to file positions. The OS page cache caches recently accessed segments. `fsync` is controlled by `flush.messages` and `flush.ms`. Segments are cleaned up based on retention policy (time or size).

### Q23: Explain the difference between ZooKeeper-based and KRaft-based Kafka.
**Answer**: ZooKeeper mode: metadata stored in ZK, controller election via ZK, separate system to manage. KRaft mode (Kafka 3.x GA for metadata, 4.0 for everything): controller quorum using Raft, no external dependency on ZK, simpler operations, better scalability for large clusters (millions of partitions). Migration tools are available for ZK → KRaft.

### Q24: How do you guarantee message ordering in Kafka?
**Answer**: Ordering is guaranteed within a partition, not across partitions. To maintain ordering: send all related messages with the same key (so they go to the same partition), use a single partition for strict ordering (loses parallelism), set `max.in.flight.requests.per.connection=1` with idempotence off (or `5` with idempotence on). Kafka 3.0+ allows `5` in-flight requests with idempotence while preserving order.

### Q25: What are the failure scenarios and how does Kafka recover?
**Answer**: **Broker failure**: leaders for partitions on that broker are re-elected by the controller from ISR members. **Consumer failure**: group coordinator detects missed heartbeats → triggers rebalance → reassigns partitions. **Producer failure**: with idempotence, a new producer with the same transactional ID fences the old one. **Disk failure**: if a broker has failed disks, replicas on other brokers take over. **Network partition**: if a majority of brokers can't communicate, the partition's availability depends on whether the ISR overlaps both sides.
