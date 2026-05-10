package com.learning.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaSolutionTest {

    private KafkaSolution solution;

    @BeforeEach
    void setUp() {
        solution = new KafkaSolution();
    }

    @Test
    void testCreateProducer() {
        KafkaProducer<String, String> producer = solution.createProducer("localhost:9092");
        assertNotNull(producer);
        producer.close();
    }

    @Test
    void testCreateConsumer() {
        KafkaConsumer<String, String> consumer = solution.createConsumer("localhost:9092", "test-group");
        assertNotNull(consumer);
        consumer.close();
    }

    @Test
    void testProduce() {
        KafkaProducer<String, String> mockProducer = mock(KafkaProducer.class);
        solution.produce(mockProducer, "test-topic", "key", "value");
        verify(mockProducer).send(any());
    }

    @Test
    void testSubscribe() {
        KafkaConsumer<String, String> mockConsumer = mock(KafkaConsumer.class);
        solution.subscribe(mockConsumer, "topic1", "topic2");
        verify(mockConsumer).subscribe(any());
    }

    @Test
    void testCreateConfig() {
        Properties config = solution.createConfig("localhost:9092");
        assertEquals("localhost:9092", config.get("bootstrap.servers"));
    }
}