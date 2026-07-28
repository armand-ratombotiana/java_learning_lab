# Lab 04: Mock Interview — Streaming Platform Engineer

**Interviewer**: "Your Kafka Streams job is experiencing high latency and frequent rebalances. How do you debug and optimize?"

**Candidate**: "First, I'd check the consumer lag metrics to identify if the bottleneck is in consumption or processing. If lag grows steadily, it's a processing bottleneck. I'd check the num.stream.threads config — starting point is matching the number of partitions. Then I'd look at the RocksDB state store: if it's spilling to disk frequently, I'd increase the cache size or enable memory-mapped files."

**Interviewer**: "The cluster has 100 partitions but only 5 nodes. Rebalances are causing 10-second pauses. What do you do?"

**Candidate**: "I'd switch to cooperative rebalancing which only revokes a subset of partitions at a time. I'd also increase session.timeout.ms and heartbeat.interval.ms to reduce false rebalances. If the state stores are large, I'd implement standby replicas to speed up restoration."

**Interviewer**: "You have a hot key — 50% of all traffic goes to one user_id. How do you handle it?"

**Candidate**: "I'd implement the skew-aware partitioner we built in this lab. For the hot key, I'd append a random suffix (#0 through #N) to distribute across partitions. The downstream consumer would need to merge results from all sub-keys. For windowed aggregations, I'd use a separate 'aggregate per sub-key' topology with a final merge step."

**Interviewer**: "How do you ensure exactly-once sink to an external database?"

**Candidate**: "I'd use Kafka Streams' exactly-once semantics (EXACTLY_ONCE_V2) combined with a transactional outbox pattern. The sink writes to the database using an idempotent upsert. If the sink fails and the transaction is aborted, Kafka rolls back the stream offset, and the sink (on retry) sees the same records again but the upsert is idempotent."
