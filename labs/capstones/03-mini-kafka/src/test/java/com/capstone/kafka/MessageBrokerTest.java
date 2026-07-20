package com.capstone.kafka;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageBrokerTest {
    private MessageBroker broker;

    @BeforeEach
    void setUp() { broker = new MessageBroker(); }

    @Test void testCreateTopic() {
        broker.createTopic("test", 3);
        assertEquals(1, broker.topicCount());
        assertEquals(3, broker.getPartitions("test").size());
    }

    @Test void testProduceAndConsume() {
        broker.createTopic("test", 1);
        broker.produce("test", "k1".getBytes(), "v1".getBytes());
        broker.produce("test", "k2".getBytes(), "v2".getBytes());
        var messages = broker.consume("test", 0, 0, 10);
        assertEquals(2, messages.size());
    }

    @Test void testOffsetCommit() {
        broker.createTopic("test", 1);
        broker.produce("test", "k".getBytes(), "v".getBytes());
        broker.commitOffset("g1", "test", 0, 1);
        assertEquals(1, broker.getCommittedOffset("g1", "test", 0));
    }

    @Test void testGroupCoordination() {
        var group = broker.getOrCreateGroup("g1");
        assertNotNull(group);
        assertEquals("g1", group.getGroupId());
    }

    @Test void testProduceWithHeaders() {
        broker.createTopic("test", 1);
        broker.produce("test", "k".getBytes(), "v".getBytes(), Map.of("trace-id", "abc123"));
        var msgs = broker.consume("test", 0, 0, 1);
        assertEquals("abc123", msgs.get(0).headers().get("trace-id"));
    }

    @Test void testReset() {
        broker.createTopic("test", 1);
        broker.reset();
        assertEquals(0, broker.topicCount());
    }
}
