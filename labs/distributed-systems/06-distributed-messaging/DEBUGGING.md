# Debugging Message Systems

## Common Issues

### Consumer Lag
```java
public class LagMonitor {
    public static Map<Integer, Long> getLag(
            KafkaConsumer<String, String> consumer, String topic) {
        Map<Integer, Long> lag = new HashMap<>();
        Map<TopicPartition, Long> endOffsets = 
            consumer.endOffsets(consumer.assignment());
        Map<TopicPartition, Long> committed = 
            consumer.committed(consumer.assignment());
        
        for (TopicPartition tp : consumer.assignment()) {
            long end = endOffsets.get(tp);
            long comm = committed.getOrDefault(tp, 0L);
            lag.put(tp.partition(), end - comm);
        }
        return lag;
    }
}
```

### Rebalance Detection
Log consumer group metadata to detect frequent rebalances.

### Dead Letter Queue
Route undeliverable messages to a dead letter topic for analysis.
