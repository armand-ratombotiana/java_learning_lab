# Flink Internals

## Architecture
```
[JobManager] <-> [TaskManager 1] [TaskManager 2]
                   Task Slots        Task Slots
```

## State Backends
- MemoryStateBackend (dev only)
- FsStateBackend (production, file-based)
- RocksDBStateBackend (large state, disk-based)
