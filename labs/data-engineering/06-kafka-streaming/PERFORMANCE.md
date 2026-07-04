# Performance

## Tuning
```java
props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10L * 1024 * 1024);
```

## RocksDB
```java
props.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG,
    (RocksDBConfigSetter) (db, config) -> {
        db.setOptions(o -> o.setIncreaseParallelism(4));
    }.getClass());
```
