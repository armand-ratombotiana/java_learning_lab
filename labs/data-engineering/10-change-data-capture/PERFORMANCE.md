# Performance

## Batch Size
```java
props.setProperty("max.batch.size", "2048");
props.setProperty("max.queue.size", "8192");
```

## Snapshot
```java
props.setProperty("snapshot.fetch.size", "10000");
props.setProperty("snapshot.locking.mode", "none");
```

## Kafka Producer
```properties
producer.acks=all
producer.linger.ms=50
producer.batch.size=65536
producer.compression.type=snappy
```
