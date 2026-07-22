# Solution: Kafka Consumer Lag Resolution

## Step 1: Immediate Remediation

### Check Consumer Group Status

```bash
# Describe consumer group lag
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe

# Check consumer group state
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --state

# Get detailed member information
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --members --verbose
```

### Identify Rebalance Frequency

```bash
# Check broker metrics for rebalance count
kafka-run-class.sh kafka.tools.JmxTool \
  --object-name kafka.consumer.group:type=consumer-group-metrics,group=order-processor-group \
  --attributes "num-rebalances-per-hour"
```

## Step 2: Consumer Configuration (Static Group Membership + Cooperative Rebalancing)

```java
package com.acmecorp.orderprocessor.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group.id}")
    private String groupId;

    @Value("${kafka.consumer.instance.id}")
    private String instanceId;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, instanceId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
            "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
            kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(12);
        factory.getContainerProperties().setIdleBetweenPolls(100);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
}
```

## Step 3: Async Processing Pattern

```java
package com.acmecorp.orderprocessor.consumer;

import com.acmecorp.orderprocessor.model.OrderEvent;
import com.acmecorp.orderprocessor.service.OrderEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private static final int BATCH_SIZE = 500;
    private static final int QUEUE_CAPACITY = 10000;
    private static final int FLUSH_INTERVAL_MS = 1000;

    private final OrderEventService orderEventService;
    private final ExecutorService processingExecutor;
    private final ArrayBlockingQueue<OrderEvent> eventQueue;
    private final AtomicInteger processedCount = new AtomicInteger(0);

    public OrderEventConsumer(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
        this.processingExecutor = Executors.newFixedThreadPool(8);
        this.eventQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        startBatchProcessor();
    }

    @KafkaListener(topics = "${kafka.topic.order-events}",
                   groupId = "${kafka.consumer.group.id}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<OrderEvent> events, Acknowledgment acknowledgment) {
        long startTime = System.currentTimeMillis();
        int receivedCount = events.size();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (OrderEvent event : events) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Queue for batch processing instead of immediate DB write
                    if (!eventQueue.offer(event, 100, TimeUnit.MILLISECONDS)) {
                        log.warn("Event queue full, falling back to synchronous processing for event: {}",
                            event.getEventId());
                        orderEventService.processEvent(event);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Interrupted while queueing event: {}", event.getEventId());
                }
            }, processingExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRun(() -> {
                acknowledgment.acknowledge();
                int processed = processedCount.addAndGet(receivedCount);
                long elapsed = System.currentTimeMillis() - startTime;
                log.info("Polled {} events, queued for async processing. Total processed: {} in {}ms",
                    receivedCount, processed, elapsed);
            })
            .exceptionally(throwable -> {
                log.error("Failed to process events batch", throwable);
                acknowledgment.nack(1000); // Re-deliver after 1 second
                return null;
            });
    }

    private void startBatchProcessor() {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> {
                List<OrderEvent> batch = new ArrayList<>(BATCH_SIZE);
                eventQueue.drainTo(batch, BATCH_SIZE);

                if (!batch.isEmpty()) {
                    try {
                        orderEventService.processEventsBatch(batch);
                        log.debug("Batch processed {} events", batch.size());
                    } catch (Exception e) {
                        log.error("Failed to process batch of {} events", batch.size(), e);
                        // Re-queue for retry
                        eventQueue.addAll(batch);
                    }
                }
            }, 0, FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
}
```

## Step 4: Optimized Batch Processing Service

```java
package com.acmecorp.orderprocessor.service;

import com.acmecorp.orderprocessor.model.OrderEvent;
import com.acmecorp.orderprocessor.repository.OrderEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

@Service
public class OrderEventService {

    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private static final int BATCH_SIZE = 500;

    private final OrderEventRepository repository;
    private final DataSource dataSource;
    private final Timer processTimer;

    public OrderEventService(OrderEventRepository repository,
                            DataSource dataSource,
                            MeterRegistry meterRegistry) {
        this.repository = repository;
        this.dataSource = dataSource;
        this.processTimer = Timer.builder("order.event.processing.time")
            .description("Time to process order events")
            .register(meterRegistry);
    }

    @Transactional
    public void processEventsBatch(List<OrderEvent> events) {
        long startTime = System.nanoTime();
        try {
            String sql = "INSERT INTO order_events (event_id, user_id, event_type, payload, created_at) " +
                         "VALUES (?, ?, ?, ?::jsonb, ?) " +
                         "ON CONFLICT (event_id) DO NOTHING";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                int count = 0;
                for (OrderEvent event : events) {
                    ps.setString(1, event.getEventId());
                    ps.setString(2, event.getUserId());
                    ps.setString(3, event.getEventType());
                    ps.setString(4, event.getPayload());
                    ps.setTimestamp(5, java.sql.Timestamp.from(event.getCreatedAt()));
                    ps.addBatch();
                    count++;

                    if (count % BATCH_SIZE == 0) {
                        ps.executeBatch();
                        conn.commit();
                    }
                }
                ps.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            log.error("Batch insert failed for {} events", events.size(), e);
            throw new RuntimeException("Batch insert failed", e);
        } finally {
            long elapsed = System.nanoTime() - startTime;
            processTimer.record(elapsed, TimeUnit.NANOSECONDS);
            log.info("Batch processed {} events in {}ms", events.size(),
                TimeUnit.NANOSECONDS.toMillis(elapsed));
        }
    }

    public void processEvent(OrderEvent event) {
        processTimer.record(() -> {
            repository.save(event);
        });
    }
}
```

## Step 5: JMX Metrics for Consumer Lag Monitoring

```java
package com.acmecorp.orderprocessor.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ConsumerLagMonitor {

    private static final String TOPIC = "order-events";
    private static final long METRICS_INTERVAL_SECONDS = 15;

    private final ConsumerFactory<String, String> consumerFactory;
    private final MeterRegistry meterRegistry;

    public ConsumerLagMonitor(ConsumerFactory<String, String> consumerFactory,
                             MeterRegistry meterRegistry) {
        this.consumerFactory = consumerFactory;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(this::recordLagMetrics,
                0, METRICS_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void recordLagMetrics() {
        try (KafkaConsumer<String, String> consumer =
                 (KafkaConsumer<String, String>) consumerFactory.createConsumer()) {

            Set<TopicPartition> partitions = consumer.partitionsFor(TOPIC).stream()
                .map(pi -> new TopicPartition(pi.topic(), pi.partition()))
                .collect(Collectors.toSet());

            consumer.assign(partitions);
            consumer.seekToEnd(Collections.emptySet());

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            Map<TopicPartition, Long> currentOffsets = consumer.assignment().stream()
                .collect(Collectors.toMap(
                    tp -> tp,
                    tp -> consumer.position(tp)
                ));

            long totalLag = 0;
            for (TopicPartition partition : partitions) {
                long endOffset = endOffsets.getOrDefault(partition, 0L);
                long currentOffset = currentOffsets.getOrDefault(partition, 0L);
                long partitionLag = Math.max(0, endOffset - currentOffset);
                totalLag += partitionLag;

                Gauge.builder("kafka.consumer.lag.partition", partitionLag)
                    .tag("topic", TOPIC)
                    .tag("partition", String.valueOf(partition.partition()))
                    .register(meterRegistry);
            }

            Gauge.builder("kafka.consumer.lag.total", totalLag)
                .tag("topic", TOPIC)
                .register(meterRegistry);

            if (totalLag > 10000) {
                log.warn("Consumer lag is high: {} messages behind", totalLag);
            }
        } catch (Exception e) {
            log.error("Failed to record consumer lag metrics", e);
        }
    }

    private static final org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(ConsumerLagMonitor.class);
}
```

## Step 6: Database Connection Pool Configuration

```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 100
      minimum-idle: 20
      idle-timeout: 300000
      connection-timeout: 5000
      max-lifetime: 600000
      leak-detection-threshold: 60000
      pool-name: OrderProcessorPool
      validation-timeout: 1000
  kafka:
    consumer:
      max-poll-records: 5000
      max-poll-interval-ms: 600000
      session-timeout-ms: 30000
      heartbeat-interval-ms: 10000
      group-instance-id: ${HOSTNAME}
      properties:
        partition.assignment.strategy: |
          org.apache.kafka.clients.consumer.CooperativeStickyAssignor
```

## Step 7: Verification Commands

```bash
# Verify consumer group is healthy
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe

# Check for rebalances in broker logs
kafka-dump-log.sh --files /var/log/kafka/server.log | grep "Rebalance"

# Verify cooperative rebalancing
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --state

# Check lag per partition
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --verbose

# Verify static group membership
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --members --verbose

# Check JMX metrics
curl -s http://localhost:8080/actuator/metrics/kafka.consumer.lag.total

# Verify no connection pool exhaustion
curl -s http://localhost:8080/actuator/health | jq '.components.db'
```
