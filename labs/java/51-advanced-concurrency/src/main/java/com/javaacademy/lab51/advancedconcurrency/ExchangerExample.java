package com.javaacademy.lab51.advancedconcurrency;

import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates Exchanger for pair-wise data exchange between threads.
 * Two threads meet at a synchronization point and swap data structures.
 * Useful for pipeline patterns where one thread produces and another consumes.
 */
public class ExchangerExample {

    private static final int EXCHANGES = 5;
    private static final Exchanger<String> exchanger = new Exchanger<>();
    private static final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Thread producer = Thread.ofVirtual().name("producer").start(() -> {
            try {
                for (int i = 0; i < EXCHANGES; i++) {
                    String data = "data-" + counter.incrementAndGet();
                    System.out.println("Producer sending: " + data);
                    String received = exchanger.exchange(data);
                    System.out.println("Producer received: " + received);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = Thread.ofVirtual().name("consumer").start(() -> {
            try {
                for (int i = 0; i < EXCHANGES; i++) {
                    String data = "result-" + (i * 10);
                    String received = exchanger.exchange(data);
                    System.out.println("Consumer received: " + received + " (sending: " + data + ")");
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        producer.join();
        consumer.join();
        System.out.println("Exchanger demo complete.");
    }
}
