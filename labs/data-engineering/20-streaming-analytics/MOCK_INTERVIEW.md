# Mock Interview: Streaming Analytics (20-streaming-analytics)

## Scenario: Real-time dashboard for business metrics
The CEO wants a real-time dashboard showing revenue, user sessions, and product performance. Data sources: clickstream (100K events/sec), orders (10K/sec), payments (5K/sec).

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Pipeline Architecture (15 min)

**End-to-end streaming analytics pipeline:**

```
Sources:
├── Clickstream (website + mobile) → Kafka topic: "clickstream"
├── Orders (order service) → Kafka topic: "orders"
└── Payments (payment service) → Kafka topic: "payments"

Stream Processing (Flink):
├── orders + payments join → enriched orders
├── clickstream sessionization → user sessions (30-min window)
├── per-minute revenue aggregation (tumbling window, 1 min)
├── per-minute user session metrics
└── per-minute product performance

Serving Layer:
├── Real-time aggregates → Redis (key-value, < 5ms reads)
├── Recent raw events → Elasticsearch (search + aggregation)
├── Materialized views → PostgreSQL/ClickHouse (for historical queries)
└── Latest metrics → WebSocket server (push to dashboard)

Dashboard:
├── CEO Dashboard (Grafana / Metabase / Custom React)
├── Refresh: 5 seconds (WebSocket push)
├── Metrics: Revenue (last 5/30/60 min), Active Users, Top Products
└── Alerts: Revenue drop > 10% from hourly average
```

**Flink pipeline code:**
```java
// 1. Per-minute revenue aggregation
DataStream<Order> orders = env.fromSource(kafkaSource("orders"));
DataStream<Payment> payments = env.fromSource(kafkaSource("payments"));

DataStream<EnrichedOrder> enriched = orders
    .keyBy(order -> order.orderId)
    .connect(payments.keyBy(payment -> payment.orderId))
    .process(new OrderPaymentJoin());

DataStream<MinuteRevenue> minuteRevenue = enriched
    .keyBy(order -> order.productCategory)
    .window(TumblingEventTimeWindows.of(Duration.ofMinutes(1)))
    .aggregate(new RevenueAggregator())
    .map(revenue -> {
        // Write to Redis for dashboard
        redisClient.hset("revenue:minute",
            String.valueOf(revenue.minuteTimestamp),
            String.valueOf(revenue.totalRevenue));
        redisClient.expire("revenue:minute", 7200); // 2 hour TTL
        return revenue;
    });

// 2. Write to Elasticsearch for historical queries
minuteRevenue.addSink(ElasticsearchSink.builder()
    .setBulkFlushMaxActions(100)
    .setHosts(new HttpHost("elasticsearch", 9200, "http"))
    .build());

// 3. Write to ClickHouse for long-term analytics
minuteRevenue.addSink(ClickHouseSink.builder()
    .setJdbcUrl("jdbc:clickhouse://clickhouse:8123/default")
    .setTable("minute_revenue")
    .build());
```

---

## Part 2: Serving Layer (10 min)

**Comparison of serving options:**

| Option | Latency | Query Type | Freshness | Scaling | Best For |
|--------|---------|------------|-----------|---------|----------|
| **Redis** | < 1ms | Point lookup | Latest value only | Sharding | Dashboard metrics, feature serving |
| **Elasticsearch** | 10-50ms | Search, aggregation | Near-real-time | Horizontal | Log analytics, search, drill-down |
| **ClickHouse** | 10-100ms | SQL analytics | Near-real-time | Sharding + replication | Historical + real-time analytics |
| **Pinot/Druid** | 10-50ms | OLAP queries | Real-time | Native | High cardinality, large-scale |
| **Kafka KTable** | < 5ms | Key lookup | Real-time | Partition-based | Materialized views in Kafka |
| **PostgreSQL** | 1-10ms | SQL | Depends on refresh | Read replicas | Moderate volume, need SQL |

**Serving architecture for CEO dashboard:**
```
Live metrics (5 second refresh):
├── Kafka → Flink → Redis (latest minute aggregates)
├── WebSocket server (reads Redis, pushes to dashboard)
└── Dashboard subscribes to WebSocket

Historical + drill-down (user interaction):
├── Dashboard queries ClickHouse (last 7 days)
├── For drill-down: queries Elasticsearch (raw events, filtered)
└── For scheduled reports: ClickHouse pre-aggregated tables

Alerts:
├── Flink CEP (Complex Event Processing)
├── Detects: revenue drop below threshold, error rate spike
└── Pushes alert to Redis → WebSocket → Dashboard notification
```

**WebSocket server (Python/Node.js):**
```python
import asyncio
import websockets
import redis.asyncio as aioredis
import json

class MetricServer:
    def __init__(self):
        self.redis = aioredis.from_url("redis://redis:6379")
        self.clients = set()

    async def broadcast_metrics(self):
        """Push latest metrics every 5 seconds"""
        while True:
            # Read latest metrics from Redis
            revenue = await self.redis.hgetall("revenue:minute")
            sessions = await self.redis.hgetall("sessions:minute")
            products = await self.redis.hgetall("products:top10")

            metrics = {
                "revenue": revenue,
                "activeUsers": sessions,
                "topProducts": products,
                "timestamp": datetime.now().isoformat()
            }

            # Push to all connected clients
            if self.clients:
                message = json.dumps(metrics)
                await asyncio.gather(
                    *[client.send(message) for client in self.clients],
                    return_exceptions=True
                )

            await asyncio.sleep(5)

    async def handler(self, websocket):
        self.clients.add(websocket)
        try:
            async for message in websocket:
                # Handle client commands (subscribe to metrics, etc.)
                pass
        finally:
            self.clients.remove(websocket)
```

---

## Part 3: Consistency & Late Data (10 min)

**Reconciliation strategy: Real-time vs Batch:**

```
Real-time (Flink): per-minute window, emitted every minute
Batch (Spark): hourly full recomputation, more accurate (no late data issues)

Reconciliation:
1. Batch job runs on hour boundary (e.g., 1:05 PM for 12:00-1:00 PM)
2. Batch produces "correct" hourly aggregates
3. Compare: batch_metric - sum_of_real_time_minutes = drift
4. Drift reasons: late data, out-of-order events, window boundary approximations

Correction strategy:
├── For current hour dashboard: show real-time + last hour batch correction
├── For closed hours: show batch value (authoritative)
└── Visual indicator: show "Real-time (preliminary)" vs "Verified (batch)"
```

**Handling late-arriving orders:**

```java
// Allow late data with allowedLateness
DataStream<MinuteRevenue> minuteRevenue = orders
    .keyBy(order -> order.productCategory)
    .window(TumblingEventTimeWindows.of(Duration.ofMinutes(1)))
    .allowedLateness(Duration.ofMinutes(5))  // Accept orders up to 5 min late
    .sideOutputLateData(lateTag)             // Orders later than 5 min → side output
    .aggregate(new RevenueAggregator());

// Late data side output → daily batch recomputation
DataStream<Order> veryLateOrders = minuteRevenue.getSideOutput(lateTag);
veryLateOrders.addSink(new BatchRecomputeSink());

// Dashboard correction: show "estimated" until batch confirms
// Redis: store both real-time and batch-corrected values
// Dashboard: show "est $1.2M revenue" (real-time) and "$1.18M verified" (batch)
```

**Late data treatment by window:**
| Late Arrival | Treatment | Dashboard Display |
|-------------|-----------|-------------------|
| < 1 minute | Included in window | Standard real-time |
| 1-5 minutes | Allowed lateness (trigger fires) | Metrics updated with correction |
| 5-60 minutes | Side output → micro-batch | Include in next dashboard refresh |
| > 60 minutes | Batch reconciliation | Fixed in next hourly batch |

---

## Part 4: Scaling (10 min)

**Scaling the pipeline for 10x growth (1M events/sec):**

| Component | Current | 10x Growth | Scaling Strategy |
|-----------|---------|------------|------------------|
| Kafka | 3 brokers, 24 partitions | 12 brokers, 96 partitions | Increase partitions (keep partition per sec < 5K) |
| Flink | 32 parallel tasks | 128 tasks | Increase parallelism, more task slots |
| Redis | 1 node, 16GB | 6-node cluster, 96GB | Cluster mode, hash slot partitioning |
| Elasticsearch | 3 data nodes | 10 data nodes | Add nodes, reindex with more shards |
| ClickHouse | 2 shards | 8 shards | Add shards, distributed table |
| Dashboard | Single WebSocket server | 5 servers, load balanced | Horizontal scaling, sticky sessions |

**Backpressure handling:**

```python
# Monitor backpressure in Flink
backpressure_stats = flink_api.get_job_backpressure(job_id)
for operator in backpressure_stats:
    if operator.backpressure_level > 0.5:  # 50% backpressured
        print(f"Backpressure detected in {operator.name}")
        print(f"  Level: {operator.backpressure_level}")
        print(f"  Suggested fix: increase parallelism or optimize operator")

# Backpressure causes and fixes:
# Cause 1: Slow sink (e.g., Redis write bottleneck)
#   Fix: Increase Redis cluster shards, batch writes (using pipeline)

# Cause 2: Data skew (some partitions have much more data)
#   Fix: Rebalance key distribution, use salting

# Cause 3: Insufficient parallelism
#   Fix: Increase parallelism, ensure enough task slots

# Cause 4: Source too fast (producer > consumer)
#   Fix: Add Kafka partitions, increase consumer parallelism
```

**Cost management at scale:**
| Component | Current Cost | Scaled Cost | Optimization |
|-----------|-------------|-------------|--------------|
| Kafka | $5K/month | $20K/month | Tiered storage (S3 for older data) |
| Flink | $3K/month | $15K/month | Savepoints, auto-scaling |
| Redis | $2K/month | $8K/month | TTL optimization, eviction policies |
| ClickHouse | $3K/month | $12K/month | Pre-aggregation, partition pruning |
| Elasticsearch | $2K/month | $10K/month | Rollover indices, ILM policies |

**Monitoring for scaling:**
- Kafka consumer lag: alert if > 1 minute
- Flink checkpoint duration: alert if > 30 seconds
- Redis memory usage: alert if > 80%
- Dashboard latency: alert if p99 > 100ms
- Query throughput: per-second query rate per service

---

## Follow-up Questions

**Tumbling window for per-minute revenue:**
```java
DataStream<MinuteRevenue> minuteRevenue = orders
    .keyBy(order -> order.productCategory)
    .window(TumblingEventTimeWindows.of(Duration.ofMinutes(1)))
    .trigger(ContinuousEventTimeTrigger.of(Duration.ofSeconds(5)))  // Early results every 5 sec
    .aggregate(new RevenueAggregator());
```

**Real-time serving comparison:**
| Tool | Query Latency | Ingestion | SQL Support | Cost |
|------|--------------|-----------|-------------|------|
| Redis | < 1ms | Lua scripts | No | Low |
| KTable (Kafka) | < 5ms | Native Kafka | No | Low |
| Pinot | 10-50ms | Kafka integration | Yes | Medium |
| Druid | 10-50ms | Kafka indexing | Limited SQL | Medium |
| ClickHouse | 10-100ms | Kafka engine | Full SQL | Medium |
| Elasticsearch | 50-100ms | Kafka connector | Query DSL | Medium |

**Reconciliation SQL:**
```sql
-- Compare real-time vs batch
WITH real_time AS (
    SELECT date_trunc('hour', window_start) AS hour,
           SUM(revenue) AS rt_revenue,
           COUNT(DISTINCT order_id) AS rt_orders
    FROM real_time_metrics.minute_revenue
    WHERE window_start >= '2024-01-15 12:00:00'
      AND window_start < '2024-01-15 13:00:00'
    GROUP BY 1
),
batch AS (
    SELECT date_trunc('hour', order_date) AS hour,
           SUM(revenue) AS batch_revenue,
           COUNT(DISTINCT order_id) AS batch_orders
    FROM batch_warehouse.fact_orders
    WHERE order_date >= '2024-01-15 12:00:00'
      AND order_date < '2024-01-15 13:00:00'
    GROUP BY 1
)
SELECT rt.hour,
       rt.rt_revenue, b.batch_revenue,
       (rt.rt_revenue - b.batch_revenue) / b.batch_revenue * 100 AS drift_pct,
       rt.rt_orders, b.batch_orders
FROM real_time rt
JOIN batch b ON rt.hour = b.hour;
```

