# Lab 13 — Kafka Consumer Lag: Code Examples

## Consumer with Lag Monitoring

```java
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class MonitoredConsumer {
    private final String groupId;
    private final String topic;
    private final AtomicLong totalLag = new AtomicLong(0);
    private final ConcurrentHashMap<Integer, Long> partitionLag = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public MonitoredConsumer(String groupId, String topic) {
        this.groupId = groupId;
        this.topic = topic;
    }

    public void start() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("enable.auto.commit", "false");
        props.put("max.poll.records", "500");
        props.put("max.poll.interval.ms", "300000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));

        // Lag monitoring thread
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> reportLag(consumer), 0, 5, TimeUnit.SECONDS);

        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                if (!records.isEmpty()) {
                    processRecords(records);
                    consumer.commitSync();
                }
            }
        } finally {
            scheduler.shutdown();
            consumer.close();
        }
    }

    void processRecords(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            try {
                // Process message — simulate work
                processMessage(record);
            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
                // Send to DLQ
                sendToDeadLetterQueue(record, e);
            }
        }
    }

    void processMessage(ConsumerRecord<String, String> record) {
        // Business logic
        Thread.sleep(10); // Simulate 10ms processing
    }

    void sendToDeadLetterQueue(ConsumerRecord<String, String> record, Exception e) {
        // Implement DLQ producer logic
    }

    void reportLag(KafkaConsumer<String, String> consumer) {
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumer.assignment());
        Map<TopicPartition, OffsetAndMetadata> committed = consumer.committed(consumer.assignment());

        long total = 0;
        for (TopicPartition tp : consumer.assignment()) {
            long end = endOffsets.getOrDefault(tp, 0L);
            long current = committed.getOrDefault(tp, new OffsetAndMetadata(0)).offset();
            long lag = Math.max(0, end - current);
            partitionLag.put(tp.partition(), lag);
            total += lag;
        }
        totalLag.set(total);

        System.out.printf("[Lag Report] Group: %s, Total Lag: %d%n", groupId, total);
    }

    public long getTotalLag() { return totalLag.get(); }
    public void stop() { running = false; }
}
```

## Lag-Based Auto-Scaler

```java
import java.util.*;
import java.util.concurrent.*;

public class KafkaAutoScaler {
    private final String consumerGroup;
    private final long maxLagThreshold;
    private final int maxConsumers;
    private final LagProvider lagProvider;
    private final ConsumerScaler scaler;
    private int currentConsumerCount;

    public KafkaAutoScaler(String consumerGroup, long maxLagThreshold,
                           int maxConsumers, LagProvider lagProvider,
                           ConsumerScaler scaler, int initialCount) {
        this.consumerGroup = consumerGroup;
        this.maxLagThreshold = maxLagThreshold;
        this.maxConsumers = maxConsumers;
        this.lagProvider = lagProvider;
        this.scaler = scaler;
        this.currentConsumerCount = initialCount;
    }

    public void evaluate() {
        long totalLag = lagProvider.getTotalLag(consumerGroup);
        int targetConsumers = calculateTargetConsumers(totalLag);

        if (targetConsumers > currentConsumerCount && currentConsumerCount < maxConsumers) {
            int scaleUp = Math.min(targetConsumers - currentConsumerCount,
                    maxConsumers - currentConsumerCount);
            System.out.println("Scaling UP by " + scaleUp + " consumers (lag: " +
                    totalLag + ", current: " + currentConsumerCount + ")");
            scaler.scaleUp(scaleUp);
            currentConsumerCount += scaleUp;
        } else if (targetConsumers < currentConsumerCount) {
            int scaleDown = currentConsumerCount - targetConsumers;
            System.out.println("Scaling DOWN by " + scaleDown + " consumers (lag: " +
                    totalLag + ", current: " + currentConsumerCount + ")");
            scaler.scaleDown(scaleDown);
            currentConsumerCount -= scaleDown;
        }
    }

    int calculateTargetConsumers(long totalLag) {
        // Simple proportional scaling: target = current * (lag / maxLag)
        if (totalLag == 0) return Math.max(1, currentConsumerCount / 2);
        double ratio = Math.min((double) totalLag / maxLagThreshold, 2.0);
        return (int) Math.max(1, Math.min(currentConsumerCount * ratio, maxConsumers));
    }

    interface LagProvider { long getTotalLag(String groupId); }
    interface ConsumerScaler { void scaleUp(int count); void scaleDown(int count); }
}
```

## Consumer with Graceful Shutdown and Error Handling

```java
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class ResilientConsumer {
    private final KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;

    public ResilientConsumer(String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("enable.auto.commit", "false");
        props.put("max.poll.interval.ms", "300000");
        props.put("heartbeat.interval.ms", "3000");
        props.put("session.timeout.ms", "45000");
        props.put("partition.assignment.strategy",
                "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
        this.consumer = new KafkaConsumer<>(props);
    }

    public void run() {
        consumer.subscribe(List.of("orders"));
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    processWithRetry(record, 3);
                }
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            // Expected on shutdown
        } finally {
            consumer.close();
            System.out.println("Consumer closed gracefully");
        }
    }

    void processWithRetry(ConsumerRecord<String, String> record, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                processMessage(record);
                return;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    System.err.println("Failed after " + maxRetries + " retries, sending to DLQ");
                    sendToDLQ(record, e);
                } else {
                    try { Thread.sleep(1000L * attempt); } catch (InterruptedException ie) {}
                }
            }
        }
    }

    void processMessage(ConsumerRecord<String, String> record) {
        if (record.value().contains("ERROR")) {
            throw new RuntimeException("Simulated processing error");
        }
    }

    void sendToDLQ(ConsumerRecord<String, String> record, Exception e) {
        // Implement DLQ
    }

    public void shutdown() {
        running = false;
        consumer.wakeup();
    }
}
```
