# Apache Flink Internals

## Runtime Architecture
JobManager (Master): schedules tasks, coordinates checkpoints, handles failure recovery. TaskManagers (Workers): execute tasks in slots; each slot = one thread; network buffers for data exchange between tasks.

## Checkpointing Algorithm (Chandy-Lamport)
1. JM injects checkpoint barrier into sources. 2. Barriers propagate through operators. 3. Each operator snapshots state upon receiving barrier. 4. Aligned: wait for all input barriers. 5. Unaligned: proceed immediately (for high backpressure).

## State Backends
HashMapStateBackend: state in JVM heap, checkpoint to DFS — fast but limited to ~1GB. RocksDBStateBackend: state in RocksDB (disk), checkpoint to DFS — more memory-efficient, slower access, handles >1GB.

## Network Stack
Credit-based flow control. Buffer pools per TaskManager. Backpressure propagates upstream automatically. Configurable network buffer sizes and fractions.
