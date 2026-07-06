package com.dsacademy.lab18.circularbuffer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CircularBufferTest {

    @Test
    void testBasicOperations() {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        assertTrue(cb.isEmpty());
        assertTrue(cb.offer(1));
        assertTrue(cb.offer(2));
        assertTrue(cb.offer(3));
        assertTrue(cb.isFull());
        assertFalse(cb.offer(4));
        assertEquals(1, cb.poll());
        assertEquals(2, cb.poll());
        assertEquals(3, cb.poll());
        assertTrue(cb.isEmpty());
    }

    @Test
    void testOverwritePolicy() {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        cb.add(1); cb.add(2); cb.add(3);
        cb.add(4);
        assertEquals(2, cb.poll());
        assertEquals(3, cb.poll());
        assertEquals(4, cb.poll());
        assertTrue(cb.isEmpty());
    }

    @Test
    void testWrapAround() {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        cb.add(1); cb.add(2); cb.add(3);
        cb.poll(); cb.poll();
        cb.add(4); cb.add(5);
        assertEquals(3, cb.poll());
        assertEquals(4, cb.poll());
        assertEquals(5, cb.poll());
    }

    @Test
    void testPeek() {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        assertNull(cb.peek());
        cb.add(1);
        assertEquals(1, cb.peek());
        assertEquals(1, cb.size());
    }

    @Test
    void testClear() {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        cb.add(1); cb.add(2);
        cb.clear();
        assertTrue(cb.isEmpty());
        assertEquals(0, cb.size());
    }

    @Test
    void testBlockingBuffer() throws InterruptedException {
        BlockingCircularBuffer<Integer> bb = new BlockingCircularBuffer<>(2);
        Thread producer = new Thread(() -> {
            try { bb.put(1); bb.put(2); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread consumer = new Thread(() -> {
            try { assertEquals(1, bb.take()); assertEquals(2, bb.take()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        producer.start(); consumer.start();
        producer.join(2000); consumer.join(2000);
    }

    @Test
    void testRingBufferLog() {
        RingBufferLog log = new RingBufferLog(3);
        log.append("a"); log.append("b"); log.append("c");
        log.append("d");
        String[] recent = log.getRecent(3);
        assertArrayEquals(new String[]{"b", "c", "d"}, recent);
    }

    @Test
    void testCircularDeque() {
        CircularDeque<Integer> dq = new CircularDeque<>(5);
        dq.addLast(1); dq.addLast(2); dq.addFirst(0);
        assertEquals(0, dq.peekFirst());
        assertEquals(2, dq.peekLast());
        assertEquals(0, dq.removeFirst());
        assertEquals(2, dq.removeLast());
        assertEquals(1, dq.removeFirst());
        assertTrue(dq.isEmpty());
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CircularBuffer<>(0));
        assertThrows(IllegalArgumentException.class, () -> new CircularDeque<>(0));
    }
}
