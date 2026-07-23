package com.leetcode.concurrent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Custom: Producer-Consumer Pattern
 * Demonstrate a bounded buffer with wait/notify.
 *
 * Time Complexity: O(1) per operation
 * Space Complexity: O(capacity)
 */
public class ProducerConsumer {

    private static final int CAPACITY = 5;
    private final Queue<Integer> buffer = new LinkedList<>();

    public synchronized void produce(int value) throws InterruptedException {
        while (buffer.size() == CAPACITY) {
            wait();
        }
        buffer.offer(value);
        System.out.println("Produced: " + value + " (buffer size: " + buffer.size() + ")");
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        int value = buffer.poll();
        System.out.println("Consumed: " + value + " (buffer size: " + buffer.size() + ")");
        notifyAll();
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    pc.produce(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    pc.consume();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Producer-Consumer test complete");
    }
}
