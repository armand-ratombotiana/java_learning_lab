package com.javalab.08;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MichaelScottQueue")
class MichaelScottQueueTest {

    private MichaelScottQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new MichaelScottQueue<>();
    }

    @Test
    @DisplayName("new queue is empty")
    void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("peek on empty returns null")
    void peekEmpty() {
        assertNull(queue.peek());
    }

    @Test
    @DisplayName("poll on empty returns null")
    void pollEmpty() {
        assertNull(queue.poll());
    }

    @Test
    @DisplayName("add and poll FIFO order")
    void addAndPoll() {
        queue.add("a");
        queue.add("b");
        queue.add("c");
        assertEquals("a", queue.poll());
        assertEquals("b", queue.poll());
        assertEquals("c", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("peek returns without removing")
    void peek() {
        queue.add("a");
        queue.add("b");
        assertEquals("a", queue.peek());
        assertEquals("a", queue.peek());
        assertEquals(2, iteratorCount());
    }

    @Test
    @DisplayName("interleaved add and poll")
    void interleaved() {
        queue.add("a");
        queue.add("b");
        assertEquals("a", queue.poll());
        queue.add("c");
        assertEquals("b", queue.poll());
        assertEquals("c", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("null element throws NPE")
    void nullElement() {
        assertThrows(NullPointerException.class, () -> queue.add(null));
    }

    @Test
    @DisplayName("concurrent adds do not lose elements")
    void concurrentAdds() throws InterruptedException {
        int threads = 8;
        int addsPerThread = 1_000;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            exec.submit(() -> {
                try {
                    for (int i = 0; i < addsPerThread; i++) {
                        queue.add("item");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        int count = 0;
        while (queue.poll() != null) count++;
        assertEquals(threads * addsPerThread, count);
    }

    @Test
    @DisplayName("concurrent add and poll")
    void concurrentAddAndPoll() throws InterruptedException {
        int items = 5_000;
        ExecutorService exec = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(2);

        exec.submit(() -> {
            try {
                for (int i = 0; i < items; i++) queue.add("v" + i);
            } finally {
                latch.countDown();
            }
        });

        exec.submit(() -> {
            try {
                int polled = 0;
                while (polled < items) {
                    if (queue.poll() != null) polled++;
                }
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        exec.shutdown();
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("iterator traverses snapshot")
    void iterator() {
        queue.add("a");
        queue.add("b");
        queue.add("c");
        StringBuilder sb = new StringBuilder();
        for (String s : queue) sb.append(s);
        assertEquals("abc", sb.toString());
    }

    private int iteratorCount() {
        int count = 0;
        for (String ignored : queue) count++;
        return count;
    }
}
