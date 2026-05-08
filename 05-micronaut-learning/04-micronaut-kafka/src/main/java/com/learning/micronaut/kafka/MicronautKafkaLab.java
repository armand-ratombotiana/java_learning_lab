package com.learning.micronaut.kafka;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class MicronautKafkaLab {

    static class Record<K, V> {
        final K key;
        final V value;
        final String topic;
        final int partition;
        final long offset;

        Record(K key, V value, String topic, int partition, long offset) {
            this.key = key; this.value = value; this.topic = topic;
            this.partition = partition; this.offset = offset;
        }
    }

    static class ProducerRecord<K, V> {
        final K key;
        final V value;
        final String topic;

        ProducerRecord(String topic, K key, V value) {
            this.topic = topic; this.key = key; this.value = value;
        }
    }

    interface Serializer<T> {
        byte[] serialize(T data);
    }

    interface Deserializer<T> {
        T deserialize(byte[] data);
    }

    static class StringSerializer implements Serializer<String> {
        public byte[] serialize(String data) { return data.getBytes(); }
    }

    static class StringDeserializer implements Deserializer<String> {
        public String deserialize(byte[] data) { return new String(data); }
    }

    static class TopicPartition {
        final String topic;
        final int partition;

        TopicPartition(String topic, int partition) {
            this.topic = topic; this.partition = partition;
        }
    }

    static class KafkaCluster {
        private final Map<TopicPartition, List<byte[]>> log = new ConcurrentHashMap<>();
        private final Map<TopicPartition, Long> offsets = new ConcurrentHashMap<>();
        private final AtomicLong globalOffset = new AtomicLong(0);
        private final int partitionCount;

        KafkaCluster(int partitionCount) { this.partitionCount = partitionCount; }

        public long produce(String topic, byte[] key, byte[] value) {
            int partition = Math.abs(Arrays.hashCode(key) % partitionCount);
            TopicPartition tp = new TopicPartition(topic, partition);
            long offset = globalOffset.incrementAndGet();
            log.computeIfAbsent(tp, k -> new CopyOnWriteArrayList<>()).add(value);
            offsets.merge(tp, offset, Math::max);
            return offset;
        }

        public List<byte[]> consume(String topic, int partition, long offset) {
            TopicPartition tp = new TopicPartition(topic, partition);
            List<byte[]> messages = log.get(tp);
            if (messages == null) return List.of();
            int startIdx = (int) Math.min(offset, messages.size() - 1);
            if (startIdx < 0) startIdx = 0;
            return messages.subList(startIdx, messages.size());
        }

        public int getPartitionCount() { return partitionCount; }
    }

    static class KafkaProducer<K, V> {
        private final KafkaCluster cluster;
        private final Serializer<K> keySerializer;
        private final Serializer<V> valueSerializer;
        private final List<Runnable> interceptors = new ArrayList<>();

        KafkaProducer(KafkaCluster cluster, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
            this.cluster = cluster; this.keySerializer = keySerializer;
            this.valueSerializer = valueSerializer;
        }

        public void send(String topic, K key, V value) {
            byte[] keyBytes = keySerializer.serialize(key);
            byte[] valueBytes = valueSerializer.serialize(value);
            long offset = cluster.produce(topic, keyBytes, valueBytes);
            interceptors.forEach(Runnable::run);
            System.out.println("  Produced to " + topic + " [offset=" + offset + "] key=" + key);
        }
    }

    static class KafkaConsumer<K, V> {
        private final KafkaCluster cluster;
        private final Deserializer<K> keyDeserializer;
        private final Deserializer<V> valueDeserializer;
        private final String groupId;
        private final Map<TopicPartition, Long> positions = new ConcurrentHashMap<>();
        private Consumer<Record<K, V>> recordHandler;

        KafkaConsumer(KafkaCluster cluster, Deserializer<K> keyDeserializer,
                      Deserializer<V> valueDeserializer, String groupId) {
            this.cluster = cluster; this.keyDeserializer = keyDeserializer;
            this.valueDeserializer = valueDeserializer; this.groupId = groupId;
        }

        public void subscribe(String topic, Consumer<Record<K, V>> handler) {
            this.recordHandler = handler;
            for (int i = 0; i < cluster.getPartitionCount(); i++) {
                consumeFromPartition(topic, i);
            }
        }

        private void consumeFromPartition(String topic, int partition) {
            TopicPartition tp = new TopicPartition(topic, partition);
            long pos = positions.getOrDefault(tp, 0L);
            List<byte[]> messages = cluster.consume(topic, partition, pos);
            for (byte[] msg : messages) {
                K key = keyDeserializer.deserialize(new byte[0]);
                V value = valueDeserializer.deserialize(msg);
                positions.put(tp, positions.getOrDefault(tp, 0L) + 1);
                if (recordHandler != null) {
                    recordHandler.accept(new Record<>(key, value, topic, partition, pos));
                }
            }
        }

        public void commitSync() {
            System.out.println("  Consumer group '" + groupId + "' committed offsets");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Micronaut Kafka Concepts Lab ===\n");

        producerConsumerDemo();
        partitioning();
        consumerGroups();
        serialization();
        errorHandlingAndRetries();
    }

    static void producerConsumerDemo() {
        System.out.println("--- Kafka Producer & Consumer ---");
        KafkaCluster cluster = new KafkaCluster(3);
        KafkaProducer<String, String> producer = new KafkaProducer<>(cluster,
            new StringSerializer(), new StringSerializer());

        producer.send("orders", "order-1", "{\"id\":1,\"item\":\"laptop\"}");
        producer.send("orders", "order-2", "{\"id\":2,\"item\":\"mouse\"}");
        producer.send("orders", "order-3", "{\"id\":3,\"item\":\"keyboard\"}");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cluster,
            new StringDeserializer(), new StringDeserializer(), "order-processor");
        consumer.subscribe("orders", record ->
            System.out.println("  Consumed: " + record.value() + " from " + record.topic));
        consumer.commitSync();
    }

    static void partitioning() {
        System.out.println("\n--- Partitioning ---");
        KafkaCluster cluster = new KafkaCluster(3);
        KafkaProducer<String, String> producer = new KafkaProducer<>(cluster,
            new StringSerializer(), new StringSerializer());

        for (int i = 1; i <= 6; i++) {
            producer.send("events", "key-" + (i % 3), "data-" + i);
        }
        System.out.println("  (Same key = same partition, ordered per partition)");
    }

    static void consumerGroups() {
        System.out.println("\n--- Consumer Groups ---");
        KafkaCluster cluster = new KafkaCluster(2);
        KafkaProducer<String, String> producer = new KafkaProducer<>(cluster,
            new StringSerializer(), new StringSerializer());

        for (int i = 1; i <= 4; i++) {
            producer.send("tasks", "t-" + i, "task-" + i);
        }

        KafkaConsumer<String, String> consumer1 = new KafkaConsumer<>(cluster,
            new StringDeserializer(), new StringDeserializer(), "workers");
        KafkaConsumer<String, String> consumer2 = new KafkaConsumer<>(cluster,
            new StringDeserializer(), new StringDeserializer(), "workers");

        System.out.println("  Consumer group 'workers' shares partition load");
        System.out.println("  (Micronaut Kafka @KafkaListener handles group coordination)");
    }

    static void serialization() {
        System.out.println("\n--- Serialization ---");
        String original = "{\"userId\":42,\"action\":\"purchase\"}";
        byte[] serialized = new StringSerializer().serialize(original);
        String deserialized = new StringDeserializer().deserialize(serialized);

        System.out.println("  Original: " + original);
        System.out.println("  Serialized bytes: " + serialized.length + " bytes");
        System.out.println("  Deserialized: " + deserialized);
        System.out.println("  (Micronaut Kafka supports JSON/Avro/Protobuf serializers)");
    }

    static void errorHandlingAndRetries() {
        System.out.println("\n--- Error Handling & Retries ---");
        KafkaCluster cluster = new KafkaCluster(2);
        KafkaProducer<String, String> producer = new KafkaProducer<>(cluster,
            new StringSerializer(), new StringSerializer());

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                producer.send("retry-topic", "msg-1", "important-data");
                if (attempt < maxRetries) throw new RuntimeException("Broker unavailable");
                System.out.println("  Sent successfully on attempt " + attempt);
                break;
            } catch (Exception e) {
                System.out.println("  Attempt " + attempt + " failed, retrying...");
                sleep(50);
            }
        }
        System.out.println("  (Micronaut Kafka supports retry and dead letter queues)");
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
