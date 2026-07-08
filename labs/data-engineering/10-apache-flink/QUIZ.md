# Quiz: Apache Flink

1. What mechanism does Flink use for fault tolerance?
   A) Write-ahead log
   B) Distributed snapshots (checkpointing)
   C) Replication
   D) Journaling
2. What does a watermark represent?
   A) Event processing time
   B) Progress in event time
   C) Number of events processed
   D) Resource utilization
3. Which state backend is best for large state (>1GB)?
   A) MemoryStateBackend
   B) FsStateBackend
   C) RocksDBStateBackend
   D) JdbcStateBackend
4. What is the difference between aligned and unaligned checkpoints?
   A) Sync vs async
   B) Blocking vs non-blocking
   C) Wait for all vs proceed immediately
   D) Local vs distributed
5. What does a savepoint enable?
   A) Faster processing
   B) Job upgrades and rescaling
   C) Data compression
   D) Schema evolution

## Answer Key
1: B
2: B
3: C
4: C
5: B
