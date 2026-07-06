package com.dsacademy.lab16.concurrent;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;

public class ConcurrentDataStructuresTest {

    @Test
    void testLockFreeStack() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        int threads = 4;
        int opsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                for (int i = 0; i < opsPerThread; i++) stack.push(i);
                latch.countDown();
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        int count = 0;
        while (stack.pop() != null) count++;
        assertEquals(threads * opsPerThread, count);
    }

    @Test
    void testLockFreeQueue() throws InterruptedException {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>();
        int threads = 4;
        int opsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                for (int i = 0; i < opsPerThread; i++) queue.enqueue(i);
                latch.countDown();
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        int count = 0;
        while (queue.dequeue() != null) count++;
        assertEquals(threads * opsPerThread, count);
    }

    @Test
    void testConcurrentHashSet() throws InterruptedException {
        ConcurrentHashSet<Integer> set = new ConcurrentHashSet<>();
        int threads = 4;
        int opsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            int start = t * opsPerThread;
            executor.submit(() -> {
                for (int i = start; i < start + opsPerThread; i++) set.add(i);
                latch.countDown();
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        assertEquals(threads * opsPerThread, set.size());
    }

    @Test
    void testAtomicCounter() throws InterruptedException {
        AtomicCounterExample counter = new AtomicCounterExample();
        int threads = 4;
        int opsPerThread = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                for (int i = 0; i < opsPerThread; i++) counter.increment();
                latch.countDown();
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        assertEquals(threads * opsPerThread, counter.get());
        assertEquals(threads * opsPerThread, counter.getTotalOperations());
    }

    @Test
    void testLockFreeStackOrder() {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertNull(stack.pop());
    }

    @Test
    void testLockFreeQueueOrder() {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    void testConcurrentHashSetBasic() {
        ConcurrentHashSet<Integer> set = new ConcurrentHashSet<>();
        assertTrue(set.add(1));
        assertFalse(set.add(1));
        assertTrue(set.contains(1));
        assertTrue(set.remove(1));
        assertFalse(set.contains(1));
        assertTrue(set.isEmpty());
    }

    @Test
    void testAtomicCounterCompareAndSet() {
        AtomicCounterExample counter = new AtomicCounterExample();
        counter.increment();
        counter.increment();
        assertTrue(counter.compareAndSet(2, 5));
        assertEquals(5, counter.get());
        assertFalse(counter.compareAndSet(2, 10));
        assertEquals(5, counter.get());
    }
}
