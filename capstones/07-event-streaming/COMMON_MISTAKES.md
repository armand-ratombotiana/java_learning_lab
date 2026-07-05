# Common Mistakes: Event Streaming

- **Too many partitions**: Each partition has overhead (file handles, memory). Over-partitioning degrades performance.
- **Small segment size**: Lots of small segments increase file handle count and compaction overhead. Default 1GB is good.
- **Not configuring min.insync.replicas**: With replication=3 and min.insync=1, you can lose data on single broker failure. Set to 2.
- **Synchronous produces**: Wait for ack slows throughput. Use async with callback for higher throughput.
- **Ignoring consumer lag**: Monitor consumer group lag. Lag alerting prevents unbounded backlog.
- **No avro/protobuf schema**: Raw JSON serialization lacks schema evolution support. Use Avro with Schema Registry.
- **Single consumer group member**: If one consumer processes all partitions, you don't get parallelism. Scale consumers.
