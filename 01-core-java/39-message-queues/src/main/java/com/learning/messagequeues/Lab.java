package com.learning.messagequeues;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab {

    interface MessageHandler {
        void handle(String message);
    }

    static class InMemoryQueue {
        private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        private volatile boolean running = true;

        void publish(String message) {
            try {
                queue.put(message);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        void subscribe(MessageHandler handler) {
            var t = new Thread(() -> {
                while (running) {
                    try {
                        var msg = queue.poll(1, TimeUnit.SECONDS);
                        if (msg != null) handler.handle(msg);
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                }
            });
            t.setDaemon(true);
            t.start();
        }

        void stop() { running = false; }
    }

    static class Topic {
        private final Set<MessageHandler> subscribers = ConcurrentHashMap.newKeySet();

        void publish(String message) {
            subscribers.forEach(h -> h.handle(message));
        }

        void subscribe(MessageHandler handler) {
            subscribers.add(handler);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Message Queues Lab ===\n");

        pointToPoint();
        pubSub();
        messageOrdering();
        deadLetterQueue();
        competingConsumers();
    }

    static void pointToPoint() throws Exception {
        System.out.println("--- Point-to-Point (Queue) ---");
        var queue = new InMemoryQueue();
        var received = new CopyOnWriteArrayList<String>();

        queue.subscribe(msg -> received.add("Consumer1: " + msg));
        queue.subscribe(msg -> received.add("Consumer2: " + msg));

        queue.publish("OrderPlaced");
        queue.publish("PaymentProcessed");
        queue.publish("InventoryUpdated");

        Thread.sleep(200);
        queue.stop();
        received.forEach(s -> System.out.println("  " + s));
        System.out.println("  Each message consumed by ONE consumer (round-robin)");
    }

    static void pubSub() throws Exception {
        System.out.println("\n--- Publish-Subscribe (Topic) ---");
        var topic = new Topic();
        var logs = new CopyOnWriteArrayList<String>();

        topic.subscribe(msg -> logs.add("ServiceA: " + msg));
        topic.subscribe(msg -> logs.add("ServiceB: " + msg));
        topic.subscribe(msg -> logs.add("AuditLog: " + msg));

        topic.publish("UserRegistered");
        topic.publish("EmailSent");

        logs.forEach(s -> System.out.println("  " + s));
        System.out.println("  Each message delivered to ALL subscribers");
    }

    static void messageOrdering() {
        System.out.println("\n--- Message Ordering ---");
        System.out.println("  Total order: FIFO within a partition");
        System.out.println("  Partial order: order per key (e.g., user_id)");
        System.out.println("  At-least-once: redeliver on ack timeout");
        System.out.println("  At-most-once: no retry, may lose");
        System.out.println("  Exactly-once: dedup + idempotent consumer");
        System.out.println("  Kafka: ordered per partition, partitioned by key");
        System.out.println("  RabbitMQ: ordered per queue with single consumer");
    }

    static void deadLetterQueue() {
        System.out.println("\n--- Dead Letter Queue (DLQ) ---");
        var main = new ArrayDeque<String>();
        var dlq = new ArrayDeque<String>();
        int maxRetries = 3;

        for (int i = 0; i < 5; i++) main.offer("msg-" + i);

        while (!main.isEmpty()) {
            var msg = main.poll();
            boolean success = !msg.equals("msg-2") && !msg.equals("msg-4");
            if (!success && msg.startsWith("dlq:")) {
                dlq.offer(msg);
                System.out.println("  DLQ <- " + msg);
                continue;
            }
            if (!success) {
                String retryMsg = msg;
                int retryCount = 0;
                if (msg.contains(":")) retryCount = Integer.parseInt(msg.split(":")[1]);
                retryCount++;
                if (retryCount <= maxRetries) {
                    main.offer("dlq:" + retryCount + ":" + msg.split(":")[0]);
                    System.out.println("  Retry " + retryCount + "/" + maxRetries + " for " + msg);
                } else {
                    dlq.offer(msg);
                    System.out.println("  DLQ <- " + msg + " (exhausted)");
                }
            } else {
                System.out.println("  Processed: " + msg);
            }
        }
    }

    static void competingConsumers() {
        System.out.println("\n--- Competing Consumers ---");
        var pool = Executors.newFixedThreadPool(3);
        var counter = new AtomicInteger(0);
        int totalMessages = 10;

        System.out.println("  " + totalMessages + " messages, 3 consumers:");
        for (int i = 0; i < totalMessages; i++) {
            int msgId = i;
            pool.submit(() -> {
                var name = Thread.currentThread().getName();
                System.out.println("    " + name + " handled msg-" + msgId);
                counter.incrementAndGet();
            });
        }
        pool.shutdown();
        try { pool.awaitTermination(1, TimeUnit.SECONDS); } catch (InterruptedException e) {}
        System.out.println("  Total processed: " + counter.get());
    }
}
