# Interview: Event Streaming

## Common Questions

### Q: Design a distributed messaging system like Kafka.
Key features: topic/partition model, sequential disk writes, pull-based consumers, replication with ISR, consumer groups, log compaction. Scale by adding partitions and brokers.

### Q: How does Kafka achieve high throughput compared to traditional message queues?
Sequential disk I/O (not random), zero-copy data transfer (sendfile), batching of records, compression, partitioned parallelism, minimal metadata overhead.

### Q: How do you guarantee exactly-once delivery?
Idempotent producer (producer ID + sequence number), transactional producer (atomic writes to multiple partitions), consumer offset stored in same transaction as processing output.

### Q: How does consumer rebalancing work and how do you minimize its impact?
Consumers join group -> leader elected -> leader computes partition assignment -> assignment distributed. Impact minimized with incremental cooperative rebalancing (sticky assignor, pause partitions individually).

### Q: How would you migrate a topic from 3 to 6 partitions without downtime?
Use a new topic with 6 partitions, dual-write to both topics during migration, backfill old data to new topic, switch consumers to new topic, delete old topic.
