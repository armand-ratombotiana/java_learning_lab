# Google Data Engineer Interview Guide

## Interview Structure
- Recruiter Screen (30 min): GCP experience, BigQuery, Dataflow, Pub/Sub
- Technical Phone (45 min): BigQuery SQL, Dataflow/Beam, Pub/Sub concepts
- Algorithm/Coding (45 min): LeetCode medium-hard, Python/Java/Go
- System Design (60 min): Data pipelines, analytics platforms
- Googleyness + Leadership (45 min): Culture fit, ambiguity, influence

## Key Topics

### BigQuery
- **Architecture:** Colossus (storage) + Dremel (compute) + Jupiter (network)
- **Partitioning:** Ingestion-time (`_PARTITIONDATE`), column-based (DATE, TIMESTAMP, INT)
- **Clustering:** Up to 4 columns, sorted within partitions
- **Slots:** Compute capacity; on-demand (shared) vs reservations (dedicated)
- **BI Engine:** In-memory acceleration for dashboards
- **Materialized views vs logical views:** Pre-computed vs on-the-fly
- **Omni:** Multi-cloud BigQuery (AWS Azure)

**Optimization:**
- Prune partitions and cluster columns in WHERE clause
- Use `APPROX_COUNT_DISTINCT` instead of `COUNT(DISTINCT)` for large data
- Avoid `SELECT *`; select only needed columns
- Use `WITH` clauses to avoid scanning same data multiple times
- Convert `STRING` joins to `INT64` joins for better performance

### Dataflow / Apache Beam
- **PCollection:** Immutable distributed dataset
- **PTransform:** Parallel transformation
- **Pipeline I/O:** Read from Pub/Sub, GCS, BigQuery, Kafka; Write to same
- **Windowing:** Fixed, sliding, session
- **Watermarks:** Event-time progress tracking
- **Triggers:** Early (speculative), late (on-time), accumulating vs discarding
- **State and Timers:** Per-key state, event-time/processing-time callbacks

**Beam patterns:**
```java
PCollection<Event> events = pipeline.apply("Read", PubsubIO.readAvros(...));
PCollection<SessionStats> sessions = events
    .apply("Window", Window.into(Sessions.withGapDuration(Duration.ofMinutes(30))))
    .apply("GroupByKey", GroupByKey.create())
    .apply("ComputeStats", ParDo.of(new ComputeSessionStats()));
```

### Pub/Sub
- **Push vs pull subscriptions:** push (HTTP endpoint), pull (client pulls)
- **Exactly-once delivery:** Subscription exactly-once delivery flag
- **Dead letter topics:** Failed messages after N retries
- **Retry policy:** Minimum backoff, maximum backoff
- **Ordering keys:** Preserve message order per key
- **Flow control:** `maxOutstandingMessages`, `maxOutstandingBytes`

### Algorithm/Coding
- Focus on: arrays, strings, hash maps, recursion, trees
- Typical topics: merge intervals, top k frequent, sliding window
- Must write clean, efficient code (O(n) or O(n log n))
- Language: Python most common (also Java, Go, C++)

### System Design Focus
- Google Analytics data pipeline
- Real-time anomaly detection on streaming data
- Global data warehouse (multi-region BigQuery)
- YouTube analytics: clickstream → Pub/Sub → Dataflow → BigQuery

### Googleyness
- Ambiguity: How do you handle unclear requirements?
- Collaboration: Cross-functional projects
- Humility: Admitting mistakes and learning
- Passion: Technology interest beyond work
- Data-driven: Using data to make decisions

## Sample Questions
1. "Design a YouTube analytics data pipeline"
2. "How would you optimize a BigQuery query that scans TBs?"
3. "Design a real-time anomaly detection system with Dataflow"
4. "Explain the BigQuery slot architecture"
5. "How do you handle late-arriving data in a streaming pipeline?"

## Resources
- Google Cloud Skills Boost: Data Engineer path
- Professional Data Engineer certification guide
- Google Cloud Blog: BigQuery, Dataflow deep dives
