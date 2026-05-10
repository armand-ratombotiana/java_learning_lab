package com.learning.lab.module21;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class Lab {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "learning-topic";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 21: Kafka Producer/Consumer ===");

        producerDemo();
        consumerDemo();
        advancedProducerDemo();
        streamDemo();
    }

    static void producerDemo() throws Exception {
        System.out.println("\n--- Kafka Producer ---");
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 5; i++) {
            String key = "key-" + i;
            String value = "message-" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, key, value);
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Sent: " + value + " to partition " + metadata.partition() + " offset " + metadata.offset());
                } else {
                    System.err.println("Error: " + exception.getMessage());
                }
            });
        }
        producer.flush();
        producer.close();
    }

    static void consumerDemo() throws Exception {
        System.out.println("\n--- Kafka Consumer ---");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "learning-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));
        System.out.println("Listening for messages...");
    }

    static void advancedProducerDemo() throws Exception {
        System.out.println("\n--- Advanced Producer with Callbacks ---");
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        Producer<String, String> producer = new KafkaProducer<>(props);
        String[] messages = {"Hello Kafka", "Streaming Data", "Real-time Processing"};
        for (String msg : messages) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, msg);
            Future future = producer.send(record);
            System.out.println("Sent: " + msg);
        }
        producer.close();
    }

    static void streamDemo() throws Exception {
        System.out.println("\n--- Kafka Streams Demo ---");
        System.out.println("Streams would process: key -> value transformations");
        System.out.println("Example: word count, filtering, aggregation");
    }
}