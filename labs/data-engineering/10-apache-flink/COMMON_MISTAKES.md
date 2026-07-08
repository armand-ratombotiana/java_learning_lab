# Common Mistakes with Apache Flink

1. No Watermark Strategy: Defaults to ProcessingTime, causing incorrect event-time results; always define watermark strategy
2. Large State Without RocksDB: Heap state >1GB causes OOM; switch to RocksDBStateBackend for large state
3. Aligned Checkpoints with Backpressure: Aligned checkpoints increase duration under backpressure; use unaligned checkpoints
4. Global Window Without Trigger: Never emits results; specify a trigger or use a different window type
5. Not Handling Idle Sources: Watermarks stall without data; configure idle source timeout
6. Wrong Parallelism: Too few slots cause backpressure; too many waste resources; match to Kafka partitions
