$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\06-kafka-streaming"

$files = @{}

$files["README.md"] = @"
# Kafka Streaming

## Overview
Kafka Streams is a client library for building stream processing applications that run on top of Apache Kafka, providing exactly-once semantics, stateful processing, and real-time data pipelines.

## Key Concepts
- **KStream**: Record stream, each record is independent
- **KTable**: Changelog stream, each record is an update
- **GlobalKTable**: Fully replicated table for joins
- **State Store**: Local state for stateful operations
- **Exactly-Once**: Guaranteed processing semantics

## Java Example
```java
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class WordCountApp {
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("input-topic");

        KTable<String, Long> wordCounts = textLines
            .flatMapValues(line -> Arrays.asList(line.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count(Materialized.as("counts-store"));

        wordCounts.toStream().to("output-topic", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfig());
        streams.start();
    }

    private static Properties getStreamsConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        return props;
    }
}
```
"@

$files["THEORY.md"] = @"
# Kafka Streaming Theory

## Streams Architecture
Kafka Streams is a lightweight library (not a cluster) that embeds directly into Java applications.

## Stream-Table Duality
- **KStream**: Append-only log of facts
- **KTable**: Update log (changelog) representing current state
- Either can be derived from the other

## Processing Guarantees
- **At-most-once**: No retries, may lose data
- **At-least-once**: Retry on failure, may duplicate
- **Exactly-once**: Transactional producers + idempotent writes

## State Stores
- **RocksDB**: Embedded key-value store (default)
- **InMemory**: Pure in-memory store
- **Timestamped**: Stores with timestamp per record

## Windowing
- **Tumbling**: Fixed non-overlapping windows
- **Hopping**: Fixed overlapping windows
- **Session**: Activity-based windows
- **Sliding**: Time-based join windows
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Kafka Streaming Exists

## The Problem
Before Kafka Streams, Kafka consumers had to use external frameworks (Spark, Flink, Storm) for stream processing, adding complexity in deployment, monitoring, and operations.

## Root Cause
- Need for a lightweight, embeddable stream processing library
- Tight Kafka integration for exactly-once semantics
- No separate cluster required
- Stateful processing with RocksDB

## Kafka Streams Solution
- **No separate cluster** - runs in your application JVM
- **Exactly-once** - native Kafka transactions
- **Exactly-once** with Kafka's own protocol
- **Interactive queries** - query state stores directly
- **Exactly-once** semantics built on Kafka transactions

## Java Integration
Kafka Streams is pure Java library, easily integrated with Spring Boot:
```java
@Bean
public KStream<String, Order> orderStream(StreamsBuilder builder) {
    return builder.stream("orders", Consumed.with(Serdes.String(), orderSerde));
}
```
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Kafka Streaming Matters

## Business Impact
- **Real-time Processing**: Millisecond latency within same JVM
- **Operational Simplicity**: No separate processing cluster
- **Stateful Processing**: Exactly-once state with RocksDB
- **Interactive Queries**: Query streaming state via REST APIs

## Key Advantages
- Deploy as a standard Java application
- Auto-scaling with Kafka partition rebalancing
- Fault-tolerant state via RocksDB + changelog topics
- GlobalKTables for reference data joins

## Performance
- Millions of events/second per application
- Sub-millisecond processing latency
- Linear scalability with partitions
- Exactly-once with < 5% overhead
"@

$files["HISTORY.md"] = @"
# History of Kafka Streaming

## Timeline
- **2011**: Apache Kafka created at LinkedIn
- **2014**: Kafka 0.8 with replication
- **2016**: Kafka 0.10 introduces Kafka Streams
- **2017**: Kafka 0.11 with exactly-once semantics
- **2018**: Kafka 2.0 with improved streams
- **2020**: Kafka 2.5 with exactly-once v2
- **2022**: Kafka 3.0 with KRaft mode
- **2024**: Kafka 3.7 with improved stream processing

## Key Milestones
1. 2016: First stream processing library embedded in Kafka client
2. 2017: Exactly-once semantics for Kafka Streams
3. 2018: Kafka Streams surpasses Spark Streaming usage for Kafka-native processing
4. 2020: Exactly-once V2 reduces overhead
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Kafka Streaming

## 1. The Whiteboard
- **KStream** = Dry-erase marker (writes facts, permanent)
- **KTable** = Pencil (writes current state, can erase/update)
- **State Store** = Sticky notes (local state for fast access)

## 2. The Ledger
- **KStream** = Journal entries (every transaction recorded)
- **KTable** = Account balance (current state derived from journal)
- **State store** = Running tally maintained by accountant

## 3. The Messaging App
- **KStream** = Chat messages flowing by (each message is independent)
- **KTable** = Contact list (latest info about each person)
- **State store** = Local cache of contacts on your phone
"@

$files["HOW_IT_WORKS.md"] = @"
# How Kafka Streaming Works

## Core Processing Model
```java
public class OrderProcessingTopology {

    public void buildTopology(StreamsBuilder builder) {
        // Input streams
        KStream<String, Order> orders = builder
            .stream("orders", Consumed.with(Serdes.String(), orderSerde));

        KTable<String, Customer> customers = builder
            .table("customers", Consumed.with(Serdes.String(), customerSerde));

        // Enrich orders with customer data
        KStream<String, EnrichedOrder> enriched = orders.join(
            customers,
            (order, customer) -> new EnrichedOrder(order, customer),
            Joined.with(Serdes.String(), orderSerde, customerSerde));

        // Aggregate by region
        KTable<String, Long> regionalCounts = enriched
            .groupBy((key, order) -> order.getRegion(),
                     Grouped.with(Serdes.String(), enrichedOrderSerde))
            .count(Materialized.as("regional-counts"));

        // Filter for alerts
        KStream<String, Alert> alerts = enriched
            .filter((key, order) -> order.getAmount() > 10000)
            .mapValues(order -> new Alert("High value order: " + order.getId()));

        // Output
        enriched.to("enriched-orders", Produced.with(Serdes.String(), enrichedOrderSerde));
        alerts.to("alerts", Produced.with(Serdes.String(), alertSerde));
    }
}
```

## Stateful Transformations
```java
public class FraudDetectionProcessor implements Processor<String, Transaction, String, Alert> {
    private KeyValueStore<String, Double> lastAmountStore;

    @Override
    public void init(ProcessorContext context) {
        lastAmountStore = context.getStateStore("transaction-store");
    }

    @Override
    public void process(String key, Transaction transaction) {
        Double lastAmount = lastAmountStore.get(key);
        if (lastAmount != null &&
            transaction.getAmount() > 1000 &&
            lastAmount > 1000 &&
            transaction.getTimestamp() - lastTimestamp < 60000) {
            context.forward(key, new Alert("Suspicious transaction pattern"));
        }
        lastAmountStore.put(key, transaction.getAmount());
    }
}
```
"@

$files["INTERNALS.md"] = @"
# Kafka Streaming Internals

## Task Architecture
```java
/*
Application Thread 1          Application Thread 2
+---------------------+       +---------------------+
| Task 0 (p0)         |       | Task 2 (p2)         |
| Task 1 (p1)         |       | Task 3 (p3)         |
+---------------------+       +---------------------+
| Consumer + Producer |       | Consumer + Producer |
+---------------------+       +---------------------+

Partitions: p0, p1, p2, p3
Tasks = Partitions / Threads
*/
```

## State Management
```java
// RocksDB state store
// Key-value data stored on local disk
// Changelog topic for fault tolerance
// Backup and restore via Kafka topics

Properties props = new Properties();
props.put(StreamsConfig.STATE_DIR_CONFIG, "/data/kafka-streams");
props.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG,
    CustomRocksDBConfig.class.getName());
```

## Consumer/Producer Integration
```java
// Each task has its own consumer and producer
// Records consumed -> processed -> produced
// Offsets committed with transactions (exactly-once)
props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
props.put(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, 1000);
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Kafka Streaming

## Throughput
```
Records/Sec = BatchSize / ProcessingTime
Effective Throughput = Min(ConsumeRate, ProcessRate, ProduceRate)

Optimal BatchSize = TargetLatency * Throughput
MaxThroughput = Partitions * PerPartitionThroughput
```

## State Size
```
StateSize = Keys * ValueSize * (1 + Overhead)
ChangelogTopicSize = StateSize * UpdateRate * RetentionTime

RocksDB WriteBuffer = Memtable Size
RocksDB ReadCache = BlockCache Size
```

## Window Calculations
```
Tumbling Windows = Duration / WindowSize * Partitions
Hopping Windows = Duration / Advance * Partitions
StatePerWindow = KeysPerWindow * ValueSize * Overhead

SessionWindows = Sessions * AvgSessionDuration * Partitions
```

## Exactly-Once Overhead
```
Normal Latency = ConsumerPoll + Process + Produce + Commit
ExactlyOnceLatency = NormalLatency + TransactionCoordinator + CommitLog

EOS Overhead = 1-5% additional latency
TransactionTimeout = 2x MaxProcessingTime
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Kafka Streaming

## Topology Visualization
```
Source: orders (KStream)
  |
  v
Join with customers (KTable)
  |
  v
EnrichedOrder (KStream)
  |
  +-----> GroupBy region
  |         |
  |         v
  |       Count (KTable)
  |         |
  |         v
  |       Sink: regional-counts
  |
  +-----> Filter: amount > 10000
  |         |
  |         v
  |       MapValue -> Alert
  |         |
  |         v
  |       Sink: alerts
  |
  +-----> Sink: enriched-orders (to output)
```

## Stream-Table Duality
```
KStream (Append Log):        KTable (Changelog):
key=1,val=A                  key=1 -> A
key=2,val=B                  key=2 -> B
key=1,val=C                  key=1 -> C (overwrites A)
key=1,val=D                  key=1 -> D (overwrites C)

Final:
KStream: 4 records
KTable: {1=D, 2=B}
```

## State Store Architecture
```
Application Instance 1          Application Instance 2
+------------------------+      +------------------------+
| State Store (p0)       |      | State Store (p2)       |
|   cust_1 -> {...}     |      |   cust_3 -> {...}      |
|   cust_2 -> {...}     |      |   cust_4 -> {...}      |
| Changelog Topic p0    |      | Changelog Topic p2     |
+------------------------+      +------------------------+
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Kafka Streams

## Clickstream Analysis
```java
@Configuration
@EnableKafkaStreams
public class ClickstreamPipeline {

    @Bean
    public KStream<String, ClickEvent> clickStream(StreamsBuilder builder) {
        return builder.stream("clicks",
            Consumed.with(Serdes.String(), clickEventSerde));
    }

    @Bean
    public KTable<String, Long> pageViews(KStream<String, ClickEvent> clicks) {
        return clicks
            .filter((key, click) -> click.getEventType() == EventType.PAGE_VIEW)
            .groupBy((key, click) -> click.getUrl(),
                     Grouped.with(Serdes.String(), clickEventSerde))
            .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>
                as("page-view-counts")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long()));
    }

    @Bean
    public KStream<String, UserSession> userSessions(KStream<String, ClickEvent> clicks) {
        return clicks
            .groupByKey(Grouped.with(Serdes.String(), clickEventSerde))
            .windowedBy(SessionWindows.with(Duration.ofMinutes(30)))
            .aggregate(
                UserSession::new,
                (key, click, session) -> session.addEvent(click),
                (key, s1, s2) -> s1.merge(s2),
                Materialized.with(Serdes.String(), userSessionSerde))
            .toStream()
            .map((windowedKey, session) ->
                KeyValue.pair(windowedKey.key(), session));
    }

    @Bean
    public KStream<String, AnomalyAlert> anomalyDetection(
            KStream<String, ClickEvent> clicks) {
        return clicks
            .groupByKey(Grouped.with(Serdes.String(), clickEventSerde))
            .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
            .count(Materialized.as("click-rate-store"))
            .toStream()
            .filter((key, count) -> count > 100)
            .map((key, count) ->
                KeyValue.pair(key.key(),
                    new AnomalyAlert(key.key(), count,
                        "High click rate: " + count + " in 5 min")));
    }

    @Bean
    public InteractiveQueryService interactiveQuery(
            KafkaStreams streams, StreamsBuilder builder) {
        return new InteractiveQueryService(streams);
    }
}
```

## Interactive Queries
```java
@RestController
@RequestMapping("/api/kafka-state")
public class StateQueryController {
    private final KafkaStreams streams;

    @GetMapping("/count/{url}")
    public ResponseEntity<Long> getPageViewCount(@PathVariable String url) {
        ReadOnlyKeyValueStore<String, Long> store =
            streams.store(StoreQueryParameters.fromNameAndType(
                "page-view-counts", QueryableStoreTypes.keyValueStore()));
        Long count = store.get(url);
        return ResponseEntity.ok(count != null ? count : 0L);
    }

    @GetMapping("/instances")
    public ResponseEntity<Collection<StreamsMetadata>> getInstances() {
        return ResponseEntity.ok(
            streams.metadataForAllStreamsClients());
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Kafka Streams Development

## Step 1: Add Dependencies
```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>3.7.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Step 2: Configure
```java
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
    @Bean
    public KafkaStreamsConfiguration kStreamsConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, customSerde().getClass());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        return new KafkaStreamsConfiguration(props);
    }
}
```

## Step 3: Build Topology
```java
@Bean
public KStream<String, Order> processOrders(StreamsBuilder builder) {
    KStream<String, Order> orders = builder.stream("orders");
    KTable<String, Product> products = builder.table("products");

    orders.join(products, (order, product) -> {
        order.setProductName(product.getName());
        order.setCategory(product.getCategory());
        return order;
    }, Joined.with(Serdes.String(), orderSerde(), productSerde()))
        .to("enriched-orders");
    return orders;
}
```

## Step 4: Start Application
```java
@SpringBootApplication
public class KafkaStreamsApp {
    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsApp.class, args);
    }
}
```

## Step 5: Monitor
```bash
# Check consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group my-app --describe

# View state stores
kafka-topics --bootstrap-server localhost:9092 --list | grep my-app
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Kafka Streams Mistakes

## 1. Wrong Serde Configuration
```java
// WRONG - no serde specified
KTable<String, Product> products = builder.table("products");

// RIGHT
KTable<String, Product> products = builder.table(
    "products", Consumed.with(Serdes.String(), productSerde()));
```

## 2. State Store Naming Conflicts
```java
// WRONG - duplicate store names
stream1.groupByKey().count(Materialized.as("my-store"));
stream2.groupByKey().count(Materialized.as("my-store"));

// RIGHT - unique names
stream1.groupByKey().count(Materialized.as("store-1"));
stream2.groupByKey().count(Materialized.as("store-2"));
```

## 3. Not Handling Tombstone Records
```java
// WRONG - null values cause NPE
KTable<String, Customer> table = builder.table("customers");
// null value = tombstone (delete)

// RIGHT - handle nulls
table.filter((key, value) -> value != null);
```

## 4. Ignoring Rebalance
```java
// WRONG - no rebalance listener
KafkaStreams streams = new KafkaStreams(topology, config);
streams.start();

// RIGHT - handle rebalance
streams.setStateListener((newState, oldState) -> {
    if (newState == KafkaStreams.State.REBALANCING) {
        // Pause external operations
    }
});
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Kafka Streams

## Common Issues

### Lag Building Up
```bash
# Check consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group app-id --describe
# LAG column tells you how far behind
```

### State Store Corruption
```java
// Reset state stores by changing application ID
// Or clean up local state:
props.put(StreamsConfig.STATE_CLEANUP_DELAY_MS_CONFIG, 10000);
```

### Serialization Errors
```java
// Add error handlers
props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
    LogAndContinueExceptionHandler.class.getName());
props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG,
    LogAndFailExceptionHandler.class.getName());
```

### Topology Issues
```java
// Print topology description
System.out.println(builder.build().describe());
// Shows: Sources, Processors, Stores, Sinks
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Kafka Streams Applications

## Before: Single Large Topology
```java
builder.stream("input")
    .mapValues(v -> { /* complex logic #1 */ })
    .filter((k, v) -> { /* complex logic #2 */ })
    .groupByKey()
    .count()
    .toStream()
    .to("output");
```

## After: Modular Topologies
```java
// Split into sub-topologies
// Topology 1: Processing
KStream<String, Processed> processed = processInput(builder.stream("input"));

// Topology 2: Aggregation
KTable<String, Long> counts = aggregate(processed);

// Topology 3: Output
outputResults(counts);
```

## Before: Hardcoded Topics
```java
builder.stream("orders");
```

## After: Configuration
```java
@Value("${kafka.topics.input}")
private String inputTopic;
@Value("${kafka.topics.output}")
private String outputTopic;
```
"@

$files["PERFORMANCE.md"] = @"
# Kafka Streams Performance

## Tuning Parameters
```java
// Performance tuning
props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
props.put(StreamsConfig.MAX_TASK_IDLE_MS_CONFIG, 500);
props.put(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, 1000);
```

## RocksDB Tuning
```java
props.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG,
    (RocksDBConfigSetter) (db, config) -> {
        db.setOptions(options -> {
            options.setIncreaseParallelism(4);
            options.setMaxBackgroundJobs(4);
        });
        db.tableFilterOptions(options -> {
            options.setBlockCacheSize(100 * 1024 * 1024L);
            options.setCacheIndexAndFilterBlocks(true);
        });
    }.getClass());
```

## Record Cache
```java
// Disable cache for lower latency
props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
// Or increase for higher throughput
props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 50 * 1024 * 1024L);
```
"@

$files["SECURITY.md"] = @"
# Kafka Streams Security

## Authentication
```java
props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-512");
props.put(SaslConfigs.SASL_JAAS_CONFIG,
    "org.apache.kafka.common.security.scram.ScramLoginModule required " +
    "username=\"streams-user\" password=\"streams-pass\";");
```

## TLS/SSL
```java
props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/path/to/truststore.jks");
props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/path/to/keystore.jks");
props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "password");
```

## ACLs
```bash
# Grant permissions for the application
kafka-acls --authorizer-properties zookeeper.connect=localhost:2181 \
  --add --allow-principal User:streams-app \
  --operation read --topic source-topic \
  --operation write --topic sink-topic \
  --operation write --topic my-app-*-changelog
```
"@

$files["ARCHITECTURE.md"] = @"
# Kafka Streams Architecture

## Application Architecture
```
+------------------------------+
|     Kafka Streams App        |
|  +------------------------+  |
|  | Topology               |  |
|  | +--------+ +---------+ |  |
|  | | Source | | Process | |  |
|  | | Topic  | | or      | |  |
|  | | Stream | | Sink    | |  |
|  | +--------+ +---------+ |  |
|  +------------------------+  |
|  | State Stores (RocksDB) |  |
|  +------------------------+  |
+------------------------------+
         |            |
    +----+            +----+
    v                      v
Kafka Topic            Kafka Topic
```

## Spring Boot Integration
```java
@SpringBootApplication
@EnableKafkaStreams
public class KafkaStreamsApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsApplication.class, args);
    }
}
```
"@

$files["EXERCISES.md"] = @"
# Kafka Streams Exercises

## Exercise 1: Word Count
Implement a word count application with Kafka Streams.

## Exercise 2: Stream-Table Join
Join an order stream with a customer table to create enriched orders.

## Exercise 3: Session Window
Implement session window aggregation for user clickstream data.

## Exercise 4: Stateful Processor
Build a custom processor that detects rapid-fire events (10 events in 5 seconds).

## Exercise 5: Interactive Query
Create a REST API that queries state stores for real-time dashboard data.
"@

$files["QUIZ.md"] = @"
# Kafka Streams Quiz

## Question 1
What is the difference between KStream and KTable?
- A) KStream is faster
- B) KStream is append-only, KTable is update
- C) KTable is faster
- D) There is no difference

## Question 2
What is the default state store in Kafka Streams?
- A) InMemory store
- B) RocksDB store
- C) MySQL store
- D) Redis store

## Question 3
What configuration enables exactly-once semantics?
- A) exactly.once=true
- B) exactly.once
- C) PROCESSING_GUARANTEE_CONFIG = EXACTLY_ONCE_V2
- D) enable.idempotence=true

## Answer Key
1: B, 2: B, 3: C
"@

$files["FLASHCARDS.md"] = @"
# Kafka Streams Flashcards

## Card 1
**Front**: What is a KStream?
**Back**: An append-only stream of records where each record is an independent fact.

## Card 2
**Front**: What is a KTable?
**Back**: A changelog stream where each record represents an update/upsert to a key.

## Card 3
**Front**: What is the stream-table duality?
**Back**: A stream can be viewed as a table (current state), and a table can be viewed as a stream (changelog).

## Card 4
**Front**: What is a state store in Kafka Streams?
**Back**: Local embedded storage (RocksDB) for stateful operations like aggregations and joins.

## Card 5
**Front**: What is interactive queries?
**Back**: Ability to query Kafka Streams' state stores directly via API, enabling read-your-writes consistency.
"@

$files["INTERVIEW.md"] = @"
# Kafka Streams Interview Questions

## Beginner
**Q**: What is Kafka Streams and how is it different from Kafka Consumer?
**A**: Kafka Streams is a stream processing library built on top of Kafka's consumer/producer APIs, adding stateful processing, exactly-once semantics, and a declarative DSL.

## Intermediate
**Q**: How does exactly-once work in Kafka Streams?
**A**: Uses Kafka's transactional API. Producers write atomically to output topics and consumer offsets are committed as part of the same transaction.

## Advanced
**Q**: How would you handle state store recovery in Kafka Streams?
**A**: State stores are backed by changelog topics. On restart or rebalance, the stores are rebuilt from these topics. RocksDB provides fast local recovery with the changelog as the source of truth.
"@

$files["REFLECTION.md"] = @"
# Kafka Streams Reflection

## Key Learnings
- Kafka Streams provides the tightest Kafka integration for stream processing
- Stream-table duality is a powerful mental model
- Stateful processing with RocksDB enables complex streaming applications
- Interactive queries bridge streaming and serving

## Questions to Explore
1. When should you use Kafka Streams vs Flink vs Spark Streaming?
2. How do you manage state store size in long-running applications?
3. What are the best practices for Kafka Streams deployment and monitoring?
"@

$files["REFERENCES.md"] = @"
# Kafka Streams References

## Books
- "Kafka: The Definitive Guide" by Neha Narkhede, Gwen Shapira, Todd Palino
- "Stream Processing with Kafka Streams" by Mitch Seymour
- "Kafka Streams in Action" by Bill Bejeck

## Documentation
- Apache Kafka: https://kafka.apache.org/documentation/streams/
- Confluent: https://docs.confluent.io/platform/current/streams/

## Guides
- Kafka Streams Developer Guide
- Exactly-once Semantics in Kafka Streams
- Interactive Queries Documentation
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 06 complete"
