package com.javaacademy.lab51.advancedconcurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Simplified ring-buffer event processor inspired by the LMAX Disruptor pattern.
 * Uses a pre-allocated ring buffer, sequence-based claiming, and batch processing.
 * Eliminates CAS contention compared to traditional bounded queues.
 */
public class DisruptorPattern {

    private static final int RING_SIZE = 1024;
    private static final int EVENTS = 1_000_000;

    private final Event[] ring = new Event[RING_SIZE];
    private final AtomicInteger producerCursor = new AtomicInteger(-1);
    private int consumerCursor = -1;

    record Event(int id, long timestamp, int value) {}

    public DisruptorPattern() {
        for (int i = 0; i < RING_SIZE; i++) {
            ring[i] = new Event(0, 0L, 0);
        }
    }

    public int claim() {
        while (true) {
            int seq = producerCursor.get();
            int next = seq + 1;
            if (next - consumerCursor > RING_SIZE) {
                // Ring full, spin wait
                LockSupport.parkNanos(1);
                continue;
            }
            if (producerCursor.compareAndSet(seq, next)) {
                return next;
            }
        }
    }

    public void publish(int index, Event event) {
        ring[index & (RING_SIZE - 1)] = event;
    }

    public void consume() {
        while (consumerCursor < producerCursor.get()) {
            int next = consumerCursor + 1;
            Event ev = ring[next & (RING_SIZE - 1)];
            consumerCursor = next;
            if (next % (EVENTS / 10) == 0) {
                System.out.println("Consumed: id=" + ev.id() + " value=" + ev.value());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        var disruptor = new DisruptorPattern();

        Thread producer = Thread.ofVirtual().name("producer").start(() -> {
            for (int i = 0; i < EVENTS; i++) {
                int seq = disruptor.claim();
                disruptor.publish(seq, new Event(i, System.nanoTime(), i * 2));
            }
        });

        Thread consumer = Thread.ofVirtual().name("consumer").start(() -> {
            while (disruptor.consumerCursor < EVENTS - 1) {
                disruptor.consume();
                Thread.yield();
            }
        });

        producer.join();
        consumer.join();
        System.out.println("Disruptor pattern demo complete. Total: " + EVENTS + " events.");
    }
}
