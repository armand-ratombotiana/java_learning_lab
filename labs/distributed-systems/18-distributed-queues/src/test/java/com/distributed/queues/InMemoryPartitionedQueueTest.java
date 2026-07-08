package com.distributed.queues;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryPartitionedQueueTest {

    @Test
    void testSendAndReceive() {
        InMemoryPartitionedQueue<String> queue = new InMemoryPartitionedQueue<>(3);
        queue.send("hello");
        Optional<String> msg = queue.receive("consumer1");
        assertTrue(msg.isPresent());
        assertEquals("hello", msg.get());
    }

    @Test
    void testPartitionDistribution() {
        InMemoryPartitionedQueue<String> queue = new InMemoryPartitionedQueue<>(3);
        for (int i = 0; i < 100; i++) {
            queue.send("msg-" + i);
        }
        assertTrue(queue.getPartitionSize(0) > 0);
        assertTrue(queue.getPartitionSize(1) > 0);
        assertTrue(queue.getPartitionSize(2) > 0);
    }

    @Test
    void testAcknowledge() {
        InMemoryPartitionedQueue<String> queue = new InMemoryPartitionedQueue<>(2);
        String id = queue.send("test");
        assertEquals(1, queue.getSize());
        queue.acknowledge(id);
        assertEquals(0, queue.getSize());
    }

    @Test
    void testNackRequeues() {
        InMemoryPartitionedQueue<String> queue = new InMemoryPartitionedQueue<>(2);
        String id = queue.send("retry");
        queue.receive("c1");
        queue.nack(id);
        Optional<String> msg = queue.receive("c1");
        assertTrue(msg.isPresent());
        assertEquals("retry", msg.get());
    }
}
