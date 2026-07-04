$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\05-apache-flink"

$files = @{}

$files["README.md"] = @"
# Apache Flink

## Overview
Apache Flink is a distributed stream processing framework for real-time data processing with exactly-once semantics, event-time processing, and state management.

## Key Concepts
- **DataStream**: Continuous stream of events
- **Event Time**: Time when events actually occurred
- **Watermarks**: Signals tracking progress in event time
- **State**: Managed, fault-tolerant state for operators
- **Checkpointing**: Distributed snapshots for fault recovery

## Java Example
```java
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;

public class FlinkJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);

        DataStream<Event> stream = env
            .addSource(new FlinkKafkaConsumer<>("events", new EventDeserializer(), props))
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                    .withTimestampAssigner((event, timestamp) -> event.getTimestamp()));

        stream.keyBy(Event::getCategory)
            .window(TumblingEventTimeWindows.of(Time.minutes(5)))
            .aggregate(new AverageAggregate())
            .print();

        env.execute("Flink Streaming Job");
    }
}
```
"@

$files["THEORY.md"] = @"
# Apache Flink Theory

## Stream Processing Models
- **Batch**: Bounded data processed as a stream
- **Streaming**: Unbounded data processed continuously
- **Event-driven**: Applications react to incoming events

## Time Semantics
- **Event Time**: When the event occurred (source timestamp)
- **Processing Time**: When the event is processed
- **Ingestion Time**: When event enters Flink

## State Management
- **Keyed State**: Scoped to key in keyed streams
- **Operator State**: Scoped to operator parallelism
- **Managed State**: Automatically managed by Flink
- **Raw State**: Custom state implementation

## Exactly-Once Semantics
- **Idempotent Sinks**: Duplicate writes are safe
- **Transactional Sinks**: Two-phase commit protocol
- **Flink exactly-once**: Checkpoint + transactional sink

## Windows
- **Tumbling**: Fixed-size, non-overlapping
- **Sliding**: Fixed-size, overlapping
- **Session**: Gaps between events define windows
- **Global**: Single window for all records
- **Count-based**: Window by record count
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Apache Flink Exists

## The Problem
Stream processing engines before Flink had limitations: Spark Streaming used micro-batching (not true streaming), Storm lacked exactly-once semantics, and Samza relied on Kafka's log-based processing.

## Root Cause
- Need for true event-by-event processing
- Requirements for exactly-once state consistency
- Demand for event-time processing with late data handling
- Complex event processing (CEP) patterns

## Flink's Solution
- True streaming architecture (not micro-batches)
- Distributed snapshotting for exactly-once
- Event-time processing with watermarks
- Managed state with automatic recovery

## Java Integration
```java
// Flink's Java API provides type-safe stream processing
DataStream<Event> stream = env.fromElements(event1, event2);
DataStream<Output> processed = stream
    .keyBy(Event::getId)
    .process(new KeyedProcessFunction<>() {
        // Access to managed state, timers, etc.
    });
```
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Apache Flink Matters

## Business Impact
- **Real-time Analytics**: Sub-second latency on streaming data
- **Fraud Detection**: Event-time correlations across streams
- **IoT Processing**: Billions of sensor events per day
- **Financial Trading**: CEP patterns on market data

## Key Differentiators
- True streaming (not micro-batching)
- Superior state management with exactly-once
- Event-time handling with watermarks
- Savepoints for application upgrades

## Performance
- Millions of events per second per node
- Sub-millisecond latency
- Exactly-once state consistency
- 1000+ operator topologies
"@

$files["HISTORY.md"] = @"
# History of Apache Flink

## Timeline
- **2009**: Stratosphere project started at TU Berlin
- **2014**: Entered Apache Incubator as Flink
- **2015**: Become top-level Apache project
- **2016**: Flink 1.0 released with data stream API
- **2017**: Flink 1.3 with SQL support
- **2019**: Flink 1.9 with Hive integration
- **2021**: Flink 1.13 with unified batch/streaming
- **2024**: Flink 2.0 preview with major improvements

## Key Achievements
- 2015: First to achieve exactly-once Kafka integration
- 2018: Ververica (backed by Alibaba) acquired
- 2019: Flink becomes Alibaba's primary streaming engine
- 2022: Flink surpasses Spark Streaming in adoption for real-time
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Flink

## 1. The Factory Conveyor Belt
- **Source** = Products enter belt
- **Operators** = Workstations along belt
- **State** = Tools/materials at each station
- **Checkpoints** = Photos of assembly line state
- **Watermarks** = Clock showing time progress

## 2. The Theme Park Queue
- **Rides** = Operators
- **Queue lines** = Buffers
- **Fast passes** = Watermarks
- **Photo snapshots** = Checkpoints
- **Ride groups** = Keyed streams

## 3. The Newspaper Production
- **Events** = News stories arriving
- **Event time** = When story happened
- **Processing time** = When story is printed
- **Watermark** = "We've received all stories up to 5pm"
- **Late data** = Tomorrow's corrections page
"@

$files["HOW_IT_WORKS.md"] = @"
# How Apache Flink Works

## Streaming Topology
```java
public class StreamTopology {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Source
        DataStream<Transaction> transactions = env
            .addSource(kafkaSource)
            .name("Kafka Source");

        // Transformations
        DataStream<EnrichedTransaction> enriched = transactions
            .keyBy(Transaction::getUserId)
            .process(new EnrichmentFunction())
            .name("Enrichment");

        // Windowed Aggregation
        DataStream<AggregateResult> aggregated = enriched
            .keyBy(Transaction::getCategory)
            .window(TumblingEventTimeWindows.of(Time.minutes(5)))
            .aggregate(new RevenueAggregator())
            .name("5min Revenue");

        // Sink
        aggregated.addSink(jdbcSink).name("JDBC Sink");

        env.execute("Transaction Processing Pipeline");
    }
}
```

## Watermark Propagation
```java
// Watermark strategy
DataStream<Event> withTimestamps = stream
    .assignTimestampsAndWatermarks(
        WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(10))
            .withTimestampAssigner((event, ts) -> event.getEventTime()));
```

## Checkpointing Mechanism
```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.enableCheckpointing(10000); // 10 seconds
env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
env.getCheckpointConfig().setMinPauseBetweenCheckpoints(5000);
env.getCheckpointConfig().setCheckpointTimeout(60000);
env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
```
"@

$files["INTERNALS.md"] = @"
# Apache Flink Internals

## Job Execution Architecture
```java
/*
+------------------+     +-------------------+     +------------------+
| JobManager       |     | TaskManager 1     |     | TaskManager 2    |
| +--------------+ |     | +---------------+ |     | +---------------+ |
| | Scheduler    | |     | | Task Slot 1   | |     | | Task Slot 1   | |
| | Checkpoint   | |<--->| | Task Slot 2   | |     | | Task Slot 2   | |
| | Coordinator  | |     | +---------------+ |     | +---------------+ |
| +--------------+ |     +-------------------+     +------------------+
+------------------+
*/
```

## Task Execution
```java
// Operator chaining for performance
env.disableOperatorChaining(); // Disable for debugging
stream.map(new MyMapper()).name("Map1")
      .map(new MyMapper2()).name("Map2")
      .startNewChain() // Start new chain
      .map(new MyMapper3()).name("Map3");
```

## State Backends
```java
// MemoryStateBackend (dev only)
env.setStateBackend(new MemoryStateBackend());

// FsStateBackend (production, state in filesystem)
env.setStateBackend(new FsStateBackend("hdfs://namenode:40010/flink/checkpoints"));

// RocksDBStateBackend (large state, disk-based)
env.setStateBackend(new RocksDBStateBackend("hdfs://namenode:40010/flink/checkpoints"));
```

## Network Stack
```java
// Credit-based flow control
env.setBufferTimeout(100); // Default 100ms
// TaskManager network memory
// config set taskmanager.network.memory.min=64mb
// config set taskmanager.network.memory.max=1gb
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Flink

## Parallelism Calculation
```
Throughput = EventCount / Time
ProcessingTime = EventsProcessed / Parallelism
NetworkLatency = ShuffleSize / Bandwidth

Optimal Parallelism = min(SourcePartitions, TargetCores)
MaxParallelism = 32768 (default, can increase)
```

## Checkpoint Cost
```
CheckpointDuration = StateSize / SnapshotBandwidth
AlignTime = MaxInFlightBytes / NetworkBandwidth
TotalCheckpointTime = CheckpointDuration + AlignTime
Goal: < 1% of processing time
```

## Watermark Delay
```
Watermark = Min(EventTimes in Buffer) - MaxOutOfOrderness
EventTimeProgress = Watermark / WallClock
LateEventFraction = LateEvents / TotalEvents
LateEventHandlingLatency = SideOutputBufferTime
```

## Window Calculations
```
Tumbling = Floor(EventTime / WindowSize) * WindowSize
Sliding = Floor(EventTime / Slide) * Slide
WindowCount = WindowSize / Slide
SessionGapThreshold = InactivityGap

StateSize = Keys * Window * WindowStatePerKey
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Apache Flink

## Streaming Dataflow
```
Source: Kafka
  |
  v
Map: Parse JSON
  |
  v
KeyBy: customer_id
  |
  v
Window: Tumbling 5min
  |
  v
Aggregate: SUM(amount)
  |
  v
Sink: PostgreSQL
```

## Checkpointing
```
Time ----> t0 ----- t1 ----- t2 ----- t3 ----- t4
           |        |        |        |
           v        v        v        v
         +------+ +------+ +------+ +------+
         | Chk  | | Proc | | Chk  | | Proc |
         | 1    | |      | | 2    | |      |
         +------+ +------+ +------+ +------+
                    | Failure |
                    v
         Restore from Checkpoint 1
         +------+ +------+
         | Chk  | | Proc |
         | 1r   | | replay|
         +------+ +------+
```

## Watermark Progress
```
Event Time -->
  10:00   10:01   10:02   10:03   10:04   10:05   10:06
  |       |       |       |       |       |       |
  e1      e2      e3      e4      e5      e6
                      |
                    W=10:02  (max out-of-order = 30s)
                            W=10:03
                                    W=10:04
                                            W=10:05
                                                    W=10:06
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Apache Flink

## Complex Event Processing
```java
public class FraudDetector extends KeyedProcessFunction<String, Transaction, Alert> {
    private final transient ValueState<Double> lastAmountState;
    private final transient ValueState<Long> lastTimestampState;
    private static final double THRESHOLD = 1000.0;
    private static final long TIME_WINDOW_MS = 60000;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Double> amountDesc =
            new ValueStateDescriptor<>("lastAmount", Types.DOUBLE);
        lastAmountState = getRuntimeContext().getState(amountDesc);

        ValueStateDescriptor<Long> timeDesc =
            new ValueStateDescriptor<>("lastTimestamp", Types.LONG);
        lastTimestampState = getRuntimeContext().getState(timeDesc);
    }

    @Override
    public void processElement(Transaction transaction, Context ctx, Collector<Alert> out) {
        Double lastAmount = lastAmountState.value();
        Long lastTimestamp = lastTimestampState.value();

        if (lastAmount != null && lastTimestamp != null) {
            long timeDiff = transaction.getTimestamp() - lastTimestamp;
            if (timeDiff < TIME_WINDOW_MS &&
                transaction.getAmount() > THRESHOLD &&
                lastAmount > THRESHOLD) {
                out.collect(new Alert("Suspicious activity: multiple large transactions",
                    transaction.getAccountId()));
            }
        }

        lastAmountState.update(transaction.getAmount());
        lastTimestampState.update(transaction.getTimestamp());

        // Register timer to clear state after 5 minutes
        ctx.timerService().registerEventTimeTimer(
            transaction.getTimestamp() + 300000);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Alert> out) {
        lastAmountState.clear();
        lastTimestampState.clear();
    }
}
```

## Custom Window Aggregate
```java
public class AverageAggregate
    implements AggregateFunction<Transaction, AverageAccumulator, Double> {

    @Override
    public AverageAccumulator createAccumulator() {
        return new AverageAccumulator();
    }

    @Override
    public AverageAccumulator add(Transaction value, AverageAccumulator acc) {
        acc.sum += value.getAmount();
        acc.count++;
        return acc;
    }

    @Override
    public Double getResult(AverageAccumulator acc) {
        return acc.count == 0 ? 0.0 : acc.sum / acc.count;
    }

    @Override
    public AverageAccumulator merge(AverageAccumulator a, AverageAccumulator b) {
        a.sum += b.sum;
        a.count += b.count;
        return a;
    }

    public static class AverageAccumulator {
        double sum = 0.0;
        long count = 0;
    }
}
```

## Side Output for Late Data
```java
public class LateDataHandler {
    private static final OutputTag<Transaction> lateOutputTag =
        new OutputTag<Transaction>("late-data") {};

    public DataStream<Transaction> processMainStream(DataStream<Transaction> stream) {
        SingleOutputStreamOperator<Transaction> processed = stream
            .keyBy(Transaction::getCategory)
            .window(TumblingEventTimeWindows.of(Time.hours(1)))
            .sideOutputLateData(lateOutputTag)
            .aggregate(new MyAggregate());

        // Handle late data separately
        DataStream<Transaction> lateStream = processed.getSideOutput(lateOutputTag);
        lateStream.addSink(new LateDataSink());

        return processed;
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Flink Development

## Step 1: Setup Project
```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-java</artifactId>
    <version>1.18.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-streaming-java</artifactId>
    <version>1.18.0</version>
</dependency>
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-connector-kafka</artifactId>
    <version>3.0.0</version>
</dependency>
```

## Step 2: Create Environment
```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.setParallelism(4);
env.enableCheckpointing(10000);
```

## Step 3: Add Source
```java
Properties kafkaProps = new Properties();
kafkaProps.setProperty("bootstrap.servers", "localhost:9092");
kafkaProps.setProperty("group.id", "flink-consumer");

DataStream<String> raw = env.addSource(
    new FlinkKafkaConsumer<>("input-topic",
        new SimpleStringSchema(), kafkaProps));
```

## Step 4: Transform
```java
DataStream<Event> events = raw
    .map(new JsonDeserializer())
    .assignTimestampsAndWatermarks(...)
    .keyBy(Event::getKey)
    .window(TumblingEventTimeWindows.of(Time.minutes(5)))
    .aggregate(new CountAggregate());
```

## Step 5: Add Sink
```java
events.addSink(new FlinkKafkaProducer<>("output-topic",
    new EventSerializer(), kafkaProps));
```

## Step 6: Submit
```bash
./bin/flink run -m yarn-cluster -p 10 \
  -c com.example.FlinkJob \
  my-job.jar
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Flink Mistakes

## 1. Wrong Time Characteristic
```java
// WRONG - using processing time for event-time logic
env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

// RIGHT
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
```

## 2. No Watermark Strategy
```java
// WRONG - no watermarks assigned
stream.keyBy(...).window(TumblingEventTimeWindows.of(Time.hours(1)));

// RIGHT
stream.assignTimestampsAndWatermarks(WatermarkStrategy
    .<Event>forBoundedOutOfOrderness(Duration.ofSeconds(10))
    .withTimestampAssigner((e, ts) -> e.getTimestamp()));
```

## 3. Ignoring State Backend
```java
// WRONG - default is JVM heap (can OOM)
// RIGHT - use RocksDB for large state
env.setStateBackend(new RocksDBStateBackend("hdfs://checkpoints/"));
```

## 4. Missing Checkpoint Config
```java
// WRONG - no checkpointing configured
env.enableCheckpointing(0);

// RIGHT
env.enableCheckpointing(5000);
env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Apache Flink

## Common Issues

### Backpressure
```java
// Monitor in Flink Web UI
// Tasks with high backpressure show in red
// Check: Source -> Operator -> Sink backpressure chain

// Fix: Increase parallelism, optimize operators
stream.setParallelism(10);
```

### Watermark Not Advancing
```java
// Check if watermarks are progressing
// Fix: Ensure timestamps are monotonic
// Verify source is providing events with valid timestamps
env.getConfig().setAutoWatermarkInterval(200); // 200ms
```

### Checkpoint Failures
```java
// Check Flink logs for checkpoint failures
env.getCheckpointConfig().setTolerableCheckpointFailureNumber(5);
// Increase timeout if state is large
env.getCheckpointConfig().setCheckpointTimeout(120000);
```

### Serialization Issues
```java
// Register custom types with Kryo
env.getConfig().registerKryoType(MyCustomType.class);
env.getConfig().registerPojoType(MyPojo.class);
env.getConfig().disableGenericTypes(); // Fail fast on unregistered types
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Flink Applications

## Before: Monolithic ProcessFunction
```java
stream.process(new MyComplexProcessFunction() {
    // 500 lines with extraction, transformation, enrichment, aggregation
});
```

## After: Modular Operators
```java
// Separate concerns
DataStream<Raw> rawSource = env.addSource(source);
DataStream<Parsed> parsed = rawSource.map(new Parser());
DataStream<Enriched> enriched = parsed.keyBy(...).process(new Enricher());
DataStream<Output> result = enriched.keyBy(...).window(...).aggregate(new Aggregator());
```

## Before: Hardcoded Config
```java
stream.window(TumblingProcessingTimeWindows.of(Time.minutes(5)));
```

## After: Parameterized
```java
@Value("${window.size.minutes:5}")
private int windowSize;
stream.window(TumblingProcessingTimeWindows.of(Time.of(windowSize, TimeUnit.MINUTES)));
```
"@

$files["PERFORMANCE.md"] = @"
# Flink Performance Optimization

## Tuning Parameters
```java
// Buffer timeout (lower = lower latency, higher CPU)
env.setBufferTimeout(50); // Default 100ms

// Network buffers
// flink-conf.yaml
// taskmanager.network.memory.min: 128mb
// taskmanager.network.memory.max: 1gb
// taskmanager.network.numberOfBuffers: 2048
```

## Operator Chaining
```java
// Control chaining for debugging vs production
env.disableOperatorChaining(); // Debug
// Or chain manually
stream.map(new Op1()).name("op1")
      .map(new Op2()).name("op2");
```

## State Tuning
```java
// RocksDB tuning
RocksDBStateBackend backend = new RocksDBStateBackend(checkpointUri, true);
backend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH);
env.setStateBackend(backend);

// Incremental checkpoints
env.getCheckpointConfig().enableUnalignedCheckpoints();
```

## Memory Configuration
```bash
# Managed memory for RocksDB
taskmanager.memory.managed.size: 512mb
# Heap memory
taskmanager.memory.task.heap.size: 2g
# Network buffer memory
taskmanager.memory.network.max: 1g
```
"@

$files["SECURITY.md"] = @"
# Apache Flink Security

## Authentication
```java
// Kerberos authentication
env.getConfig().setGlobalJobParameters(
    new Configuration() {{
        setString("security.kerberos.login.keytab", "/path/to/keytab");
        setString("security.kerberos.login.principal", "flink@REALM");
    }});
```

## SSL Configuration
```java
// Enable SSL for network communication
// flink-conf.yaml
security.ssl.enabled: true
security.ssl.keystore: /path/to/keystore
security.ssl.truststore: /path/to/truststore
```

## Data Encryption
```java
// Encrypt sensitive fields
DataStream<Event> safe = stream.map(event -> {
    event.setSsn(encrypt(event.getSsn()));
    event.setEmail(hash(event.getEmail()));
    return event;
});
```
"@

$files["ARCHITECTURE.md"] = @"
# Apache Flink Architecture

## System Architecture
```
+---------------------------+
|        Flink Client       |
+---------------------------+
          |
          | Submit Job
          v
+---------------------------+
|       JobManager          |
| +----------------------+  |
| | Resource Manager     |  |
| | Dispatcher           |  |
| | JobMaster per Job    |  |
| +----------------------+  |
+---------------------------+
          |
   +------+------+
   |             |
+--v---+   +---v--+
|Task  |   |Task  |
|Mgr 1 |   |Mgr 2 |
+------+   +------+
| Slot1|   | Slot1|
| Slot2|   | Slot2|
+------+   +------+
```

## Spring Boot Integration
```java
@SpringBootApplication
public class FlinkApplication {
    @Bean
    public StreamExecutionEnvironment flinkEnv() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        return env;
    }
}
```
"@

$files["EXERCISES.md"] = @"
# Apache Flink Exercises

## Exercise 1: Word Count (Streaming)
Implement real-time word count reading from a socket or Kafka source.

## Exercise 2: Event-Time Aggregation
Process a stream of transactions, computing 5-minute tumbling window averages by category.

## Exercise 3: Fraud Detection
Use Flink's CEP library to detect patterns like three failed logins in 5 minutes.

## Exercise 4: Stateful Enrichment
Enrich a stream of order events with customer data from a side input/file.

## Exercise 5: Custom Operator
Implement a custom ProcessFunction with timers that alerts if no heartbeats received for 30 seconds.
"@

$files["QUIZ.md"] = @"
# Apache Flink Quiz

## Question 1
What is a watermark in Flink?
- A) A measure of data quality
- B) A signal of event time progress
- C) A type of window
- D) A state backend

## Question 2
Which state backend is recommended for large state?
- A) MemoryStateBackend
- B) FsStateBackend
- C) RocksDBStateBackend
- D) HeapStateBackend

## Question 3
What does checkpointing provide?
- A) Better performance
- B) Exactly-once fault tolerance
- C) Data compression
- D) Schema evolution

## Answer Key
1: B, 2: C, 3: B
"@

$files["FLASHCARDS.md"] = @"
# Apache Flink Flashcards

## Card 1
**Front**: What is event time in Flink?
**Back**: The time when an event actually occurred, embedded in the event data itself.

## Card 2
**Front**: What is a watermark?
**Back**: A signal that indicates no events with timestamp less than the watermark value will arrive.

## Card 3
**Front**: What is managed state in Flink?
**Back**: State automatically managed by Flink, with checkpointing and recovery support.

## Card 4
**Front**: What are the three types of windows in Flink?
**Back**: Tumbling (fixed non-overlapping), Sliding (fixed overlapping), Session (based on inactivity gaps).

## Card 5
**Front**: What is the difference between savepoints and checkpoints?
**Back**: Savepoints are manually triggered for planned upgrades; checkpoints are automatic for fault tolerance.
"@

$files["INTERVIEW.md"] = @"
# Apache Flink Interview Questions

## Beginner
**Q**: What is the difference between Flink and Spark Streaming?
**A**: Flink is true streaming (processes each event as it arrives), while Spark Streaming uses micro-batching (processes events in small batches). Flink has superior event-time handling and state management.

## Intermediate
**Q**: How does exactly-once work in Flink?
**A**: Flink uses distributed snapshots (checkpoints) aligned with barriers in the data stream. Combined with idempotent or transactional sinks, this provides exactly-once guarantees.

## Advanced
**Q**: How would you handle out-of-order events with large latencies?
**A**: Use allowed lateness with side outputs for very late events. Configure watermarks with generous bounded-out-of-orderness. Use a two-stage processing model where initial results are produced and corrected as late data arrives.
"@

$files["REFLECTION.md"] = @"
# Apache Flink Reflection

## Key Learnings
- True streaming outperforms micro-batching for real-time use cases
- Event-time processing is essential for correct results
- Managed state simplifies complex stream processing
- Watermarks are the key mechanism for handling unordered data

## Questions to Explore
1. When should you choose Flink over Kafka Streams?
2. How does Flink SQL compare to Spark SQL for streaming?
3. What are the operational challenges of running Flink at scale?
"@

$files["REFERENCES.md"] = @"
# Apache Flink References

## Books
- "Stream Processing with Apache Flink" by Fabian Hueske and Vasiliki Kalavri
- "Learning Apache Flink" by Tanmay Deshpande
- "Apache Flink in Action" by Ellen Friedman and Kostas Tzoumas

## Documentation
- Apache Flink: https://flink.apache.org
- Ververica: https://www.ververica.com

## Papers
- "Apache Flink: Stream and Batch Processing in a Single Engine" (Carbone et al., 2015)
- "Lightweight Asynchronous Snapshots for Distributed Dataflows" (Carbone et al., 2015)
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 05 complete"
