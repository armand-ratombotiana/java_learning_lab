# Mock Interview: Apache Flink (10-apache-flink)

## Scenario: Real-time order enrichment pipeline
Your e-commerce company needs a real-time pipeline that enriches orders with customer and product data from a PostgreSQL database and caches the lookups.

### Time: 45 minutes | Difficulty: Medium-Hard

---

## Part 1: Stream Enrichment (15 min)

**Architecture:**
```
Kafka Topic: "orders" (raw) → Flink Job → Kafka Topic: "enriched_orders"
                                    ↑
                    PostgreSQL (customers, products)
                    ↓
        Async IO (JDBC Connector) with cache
```

**Two approaches for enrichment:**

**Approach 1: Async IO with JDBC (recommended)**
```java
// Async function for non-blocking lookups
public class CustomerEnrichment extends RichAsyncFunction<Order, EnrichedOrder> {
    private transient Cache<String, Customer> customerCache;
    private transient JDBCClient jdbcClient;

    @Override
    public void open(Configuration params) {
        // Initialize cache with TTL
        customerCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();
        jdbcClient = JDBCClient.builder()
            .setDBUrl("jdbc:postgresql://host/customers")
            .setUsername("user").setPassword("pass")
            .setMaxPoolSize(100)
            .build();
    }

    @Override
    public void asyncInvoke(Order order, ResultFuture<EnrichedOrder> resultFuture) {
        // Check cache first
        Customer cached = customerCache.getIfPresent(order.getCustomerId());
        if (cached != null) {
            resultFuture.complete(new EnrichedOrder(order, cached, null));
            return;
        }
        // Async DB lookup
        jdbcClient.query(
            "SELECT * FROM customers WHERE id = ?",
            order.getCustomerId()
        ).thenAccept(customer -> {
            if (customer != null) customerCache.put(order.getCustomerId(), customer);
            resultFuture.complete(new EnrichedOrder(order, customer, null));
        }).exceptionally(e -> {
            // Handle timeout or error
            resultFuture.complete(new EnrichedOrder(order, null, e.getMessage()));
        });
    }
}
```

**Approach 2: Broadcast state (for smaller dimension tables)**
```java
// Broadcast product catalog (changes infrequently)
MapStateDescriptor<String, Product> productState =
    new MapStateDescriptor<>("products", Types.STRING, Types.POJO(Product.class));

BroadcastStream<Product> productBroadcast = env
    .addSource(new ProductChangesSource())
    .broadcast(productState);

DataStream<EnrichedOrder> enriched = orders
    .connect(productBroadcast)
    .process(new BroadcastJoinFunction());
```

**Trade-offs:**
| Approach | Latency | Freshness | Database Load | Complexity |
|----------|---------|-----------|---------------|------------|
| Async IO + Cache | Low (cache hit) | Configurable TTL | Moderate | Medium |
| Broadcast State | Lowest | Near-real-time | Low (only on change) | Higher |
| Table API/SQL | Moderate | Depends on refresh | Low | Lower |

---

## Part 2: State Management (10 min)

**Types of state needed:**

```java
public class OrderEnrichmentProcess extends KeyedProcessFunction<String, Order, EnrichedOrder> {
    // Per-key state: order counts per customer (last 1 hour)
    private ValueState<CustomerOrderStats> statsState;
    private MapState<String, Order> pendingOrders;  // Orders awaiting enrichment

    @Override
    public void open(Configuration params) {
        ValueStateDescriptor<CustomerOrderStats> statsDesc =
            new ValueStateDescriptor<>("customerStats", CustomerOrderStats.class);
        statsState = getRuntimeContext().getState(statsDesc);

        MapStateDescriptor<String, Order> pendingDesc =
            new MapStateDescriptor<>("pendingOrders", Types.STRING, Types.POJO(Order.class));
        pendingOrders = getRuntimeContext().getMapState(pendingDesc);
    }
}
```

**Handling stale lookups:**
1. **Time-to-Live (TTL):** Cache entries expire after configurable duration
2. **Change tracking:** Subscribe to PostgreSQL WAL for real-time changes
3. **Age-based flag:** Mark enrichment as "aged" if lookup > threshold old
4. **Periodic refresh:** Re-enrich stale records during off-peak

**Keyed vs Operator state:**
| Aspect | Keyed State | Operator State |
|--------|------------|----------------|
| Scope | Per key (partitioned by key) | Per operator parallel subtask |
| Access | Only within keyed context (keyBy) | Any operator |
| Examples | ValueState, ListState, MapState | ListState, UnionListState, BroadcastState |
| Rescaling | Redistributed by key | Manually handled (re-distribute on rescale) |
| Use case | Per-user stats, session aggregation | Kafka offsets, file processing position |

---

## Part 3: Windowing & Output (10 min)

**Emit enriched orders at order time (event-driven):**
```java
// Emit immediately when enrichment completes
DataStream<EnrichedOrder> enriched = orders
    .keyBy(order -> order.orderId)
    .process(new KeyedEnrichmentProcess());

// Side output for late enrichments
OutputTag<EnrichedOrder> lateEnrichmentTag = new OutputTag<EnrichedOrder>("late-enrichment") {};
```

**Output to multiple sinks:**
```java
// Kafka sink (real-time)
enriched.addSink(KafkaSink.<EnrichedOrder>builder()
    .setBootstrapServers("kafka:9092")
    .setRecordSerializer(new EnrichedOrderSerializer())
    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
    .build());

// S3 sink (for batch reprocessing, archival)
enriched.addSink(FileSink.forRowFormat(
    new Path("s3://data-lake/enriched-orders/"),
    new SimpleStringEncoder<EnrichedOrder>("UTF-8"))
    .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy-MM-dd/HH"))
    .withRollingPolicy(OnCheckpointRollingPolicy.build())
    .build()
);

// Side output (late enrichments, errors)
enriched.getSideOutput(lateEnrichmentTag).addSink(lateOrdersSink);
enriched.getSideOutput(errorTag).addSink(errorSink);
```

---

## Part 4: Fault Tolerance (10 min)

**Checkpointing strategy:**
```java
// Configurable checkpointing
env.enableCheckpointing(
    checkpointIntervalMs,
    CheckpointingMode.EXACTLY_ONCE
);
env.getCheckpointConfig().setCheckpointStorage("s3://flink-checkpoints/");
env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
env.getCheckpointConfig().setCheckpointTimeout(60000);
env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
```

**Recovery without reprocessing:**
1. Flink job fails (e.g., Kafka cluster unavailable for 2 minutes)
2. JobManager detects failure, restarts job from checkpoint
3. Source (Kafka): resets to last committed offset (before checkpoint)
4. Async IO: cache is rebuilt (cold start, slower initially)
5. State: fully restored from checkpoint (including pending orders, stats)

**Savepoint for planned upgrades:**
```bash
# Take savepoint before deployment
flink savepoint <jobId> s3://flink-savepoints/

# Restart with new code, same state
flink run -s s3://flink-savepoints/savepoint-xxx job.jar
```

**Ensuring exactly-once end-to-end:**
- Source: Kafka with checkpointed offsets
- State: Checkpointed state backends (RocksDB)
- Sink: Idempotent Kafka producer (enable.idempotence=true)
- S3 sink: OnCheckpointRollingPolicy (files committed on checkpoint)
- Two-phase commit: Kafka transactions for transactional sinks

---

## Follow-up Questions

**Stream-stream join (orders + payments):**
```java
DataStream<Order> orders = env.fromSource(orderSource, ...);
DataStream<Payment> payments = env.fromSource(paymentSource, ...);

orders.join(payments)
    .where(order -> order.orderId)
    .equalTo(payment -> payment.orderId)
    .window(TumblingEventTimeWindows.of(Duration.ofMinutes(5)))
    .apply((order, payment) -> new FullfilledOrder(order, payment));
```

**Schema evolution in Flink:**
- Use Avro with Schema Registry (forward/backward compatibility)
- Configure `allow.forward.evolution`, `allow.backward.evolution`
- Handle missing fields with defaults (avoid crashes on schema change)
- Test schema evolution in staging before production
- Monitor Schema Registry version changes in CI/CD

**Async IO for enrichment:**
- `AsyncDataStream.unorderedWait()` vs `orderedWait()`: unordered for higher throughput
- Capacity: max concurrent async requests per operator
- Timeout: fail or skip on timeout depending on business requirement
- Cache sizing: based on unique keys per time window (e.g., unique customers per hour)

