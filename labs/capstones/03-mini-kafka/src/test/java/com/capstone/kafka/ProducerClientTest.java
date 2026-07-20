package com.capstone.kafka;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ProducerClientTest {
    private MessageBroker broker;
    private ProducerClient syncProducer;
    private ProducerClient asyncProducer;

    @BeforeEach
    void setUp() {
        broker = new MessageBroker();
        broker.createTopic("test", 1);
        syncProducer = new ProducerClient(broker,
            new ProducerClient.ProducerConfig("sync-prod", false, 0, 0, "1"));
        asyncProducer = new ProducerClient(broker,
            new ProducerClient.ProducerConfig("async-prod", true, 10, 100, "1"));
    }

    @Test void testSyncSend() {
        syncProducer.send("test", "k1".getBytes(), "v1".getBytes());
        var msgs = broker.consume("test", 0, 0, 1);
        assertEquals(1, msgs.size());
    }

    @Test void testAsyncBatching() {
        for (int i = 0; i < 5; i++) {
            asyncProducer.send("test", ("k" + i).getBytes(), ("v" + i).getBytes());
        }
        assertTrue(asyncProducer.pendingCount() <= 10);
    }

    @Test void testFlush() {
        for (int i = 0; i < 5; i++) {
            asyncProducer.send("test", ("k" + i).getBytes(), ("v" + i).getBytes());
        }
        int flushed = asyncProducer.flush();
        assertEquals(5, flushed);
        assertEquals(0, asyncProducer.pendingCount());
        assertEquals(5, broker.consume("test", 0, 0, 10).size());
    }

    @Test void testClose() {
        asyncProducer.send("test", "k".getBytes(), "v".getBytes());
        asyncProducer.close();
        assertEquals(1, broker.consume("test", 0, 0, 10).size());
    }

    @Test void testInvalidConfig() {
        assertThrows(IllegalArgumentException.class,
            () -> new ProducerClient.ProducerConfig("", false, 0, 0, "1"));
    }
}
