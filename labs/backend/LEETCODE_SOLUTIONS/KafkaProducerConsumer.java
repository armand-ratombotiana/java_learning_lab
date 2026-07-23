// KafkaProducerConsumer.java — Apache Kafka Producer and Consumer
// Demonstrates event-driven messaging with Kafka

package com.backend.academy.leetcode;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerConsumer.class);
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "backend-events";
    private static final String GROUP_ID = "backend-consumer-group";

    // --- Producer Configuration ---
    public static Properties producerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");                    // Strongest durability
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);        // Exactly-once semantics
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");     // Compression
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);                   // Batch small messages
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 32 * 1024);          // 32KB batch size
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33_554_432);      // 32MB buffer
        return props;
    }

    // --- Consumer Configuration ---
    public static Properties consumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Start from beginning
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);       // Manual commit
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);          // Max records per poll
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);          // Min fetch size
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);         // Max wait time
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1_048_576); // 1MB
        return props;
    }

    // --- Producer Example ---
    public static class EventProducer {
        private final KafkaProducer<String, String> producer;

        public EventProducer() {
            this.producer = new KafkaProducer<>(producerConfig());
        }

        /**
         * Sends an event to Kafka topic.
         * @param key   Partition key for ordering guarantees
         * @param value Event payload (typically JSON)
         */
        public Future<RecordMetadata> sendEvent(String key, String value) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, value);
            record.headers().add("event-source", "backend-service".getBytes());
            record.headers().add("event-type", "user-action".getBytes());

            return producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Failed to send event: {}", exception.getMessage(), exception);
                } else {
                    log.info("Event sent: partition={}, offset={}, timestamp={}",
                        metadata.partition(), metadata.offset(), metadata.timestamp());
                }
            });
        }

        public void close() {
            producer.flush();
            producer.close();
        }
    }

    // --- Consumer Example ---
    public static class EventConsumer {
        private final KafkaConsumer<String, String> consumer;

        public EventConsumer() {
            this.consumer = new KafkaConsumer<>(consumerConfig());
            this.consumer.subscribe(List.of(TOPIC));
        }

        /**
         * Polls and processes events with manual offset commit.
         */
        public void pollEvents() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            processEvent(record);
                            // Manual offset commit after successful processing
                            consumer.commitSync(java.util.Map.of(
                                record.topicPartition(),
                                new org.apache.kafka.clients.consumer.OffsetAndMetadata(record.offset() + 1)
                            ));
                        } catch (Exception e) {
                            log.error("Failed to process record: partition={}, offset={}",
                                record.partition(), record.offset(), e);
                            // Handle poison pill: send to DLT (dead letter topic)
                            sendToDeadLetterTopic(record);
                        }
                    }
                }
            } finally {
                consumer.close();
            }
        }

        private void processEvent(ConsumerRecord<String, String> record) {
            log.info("Processing event: key={}, value={}, partition={}, offset={}",
                record.key(), record.value(), record.partition(), record.offset());
            // Business logic here
        }

        private void sendToDeadLetterTopic(ConsumerRecord<String, String> failedRecord) {
            log.warn("Sending failed record to DLT: key={}", failedRecord.key());
            // Implementation for dead letter queue
        }
    }

    // --- Kafka Streaming Example (word count) ---
    /*
     * LeetCode-style: Kafka Streams word count implementation
     */
    /*
    import org.apache.kafka.streams.*;
    import org.apache.kafka.streams.kstream.*;

    public class WordCountStream {
        public static void main(String[] args) {
            Properties props = new Properties();
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
            props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
            props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

            StreamsBuilder builder = new StreamsBuilder();
            builder.stream("text-input")
                .flatMapValues(value -> List.of(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .count()
                .toStream()
                .to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

            new KafkaStreams(builder.build(), props).start();
        }
    }
    */

    public static void main(String[] args) {
        // Example usage
        EventProducer producer = new EventProducer();
        producer.sendEvent("user-123", "{\"action\": \"login\", \"timestamp\": 1234567890}");
        producer.close();

        EventConsumer consumer = new EventConsumer();
        // consumer.pollEvents(); // Would run indefinitely
    }
}

// === LeetCode-Style Problems ===

/*
 * LeetCode 622: Design Circular Queue (bounded buffer, like Kafka partitions)
 */
class MyCircularQueue {
    private int[] data;
    private int head;
    private int tail;
    private int size;
    private int capacity;

    public MyCircularQueue(int k) {
        this.data = new int[k];
        this.head = 0;
        this.tail = -1;
        this.size = 0;
        this.capacity = k;
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        tail = (tail + 1) % capacity;
        data[tail] = value;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        head = (head + 1) % capacity;
        size--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : data[head];
    }

    public int Rear() {
        return isEmpty() ? -1 : data[tail];
    }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
}

/*
 * LeetCode 232: Implement Queue using Stacks
 */
class MyQueue {
    private java.util.Stack<Integer> input;
    private java.util.Stack<Integer> output;

    public MyQueue() {
        input = new java.util.Stack<>();
        output = new java.util.Stack<>();
    }

    public void push(int x) { input.push(x); }

    public int pop() {
        if (output.isEmpty()) transfer();
        return output.pop();
    }

    public int peek() {
        if (output.isEmpty()) transfer();
        return output.peek();
    }

    public boolean empty() { return input.isEmpty() && output.isEmpty(); }

    private void transfer() {
        while (!input.isEmpty()) {
            output.push(input.pop());
        }
    }
}
