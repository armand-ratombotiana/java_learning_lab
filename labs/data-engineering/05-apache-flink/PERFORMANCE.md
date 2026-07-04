# Performance

## Buffer Timeout
```java
env.setBufferTimeout(50); // Lower = lower latency, higher CPU
```

## Operator Chaining
```java
env.disableOperatorChaining(); // Debug only
```

## RocksDB Tuning
```java
RocksDBStateBackend backend = new RocksDBStateBackend(uri, true);
backend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH);
env.setStateBackend(backend);
```
