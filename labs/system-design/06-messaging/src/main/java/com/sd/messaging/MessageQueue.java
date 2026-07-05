package com.sd.messaging;

import java.util.*;
import java.util.concurrent.*;

public class MessageQueue {

    public static class QueueMessage {
        public final String id;
        public final String payload;
        public final long timestamp;
        public volatile boolean processed;

        public QueueMessage(String id, String payload) {
            this.id = id;
            this.payload = payload;
            this.timestamp = System.currentTimeMillis();
            this.processed = false;
        }
    }

    public static class Queue {
        private final String name;
        private final Queue<QueueMessage> messages = new ConcurrentLinkedQueue<>();
        private final AtomicLong counter = new AtomicLong(0);

        public Queue(String name) { this.name = name; }

        public QueueMessage enqueue(String payload) {
            String id = name + "-" + counter.incrementAndGet();
            QueueMessage msg = new QueueMessage(id, payload);
            messages.add(msg);
            System.out.println("[" + name + "] Enqueued: " + id);
            return msg;
        }

        public QueueMessage dequeue() {
            QueueMessage msg = messages.poll();
            if (msg != null) {
                msg.processed = true;
                System.out.println("[" + name + "] Dequeued: " + msg.id);
            }
            return msg;
        }

        public int size() { return messages.size(); }
        public String getName() { return name; }
    }

    public static class QueueProcessor {
        private final Queue queue;
        private final ExecutorService executor = Executors.newFixedThreadPool(3);

        public QueueProcessor(Queue queue) { this.queue = queue; }

        public void start() {
            executor.submit(() -> {
                while (true) {
                    QueueMessage msg = queue.dequeue();
                    if (msg != null) {
                        System.out.println("  Processing: " + msg.payload);
                    }
                    try { Thread.sleep(200); } catch (InterruptedException e) { break; }
                }
            });
        }

        public void shutdown() { executor.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        Queue queue = new Queue("orders");
        QueueProcessor processor = new QueueProcessor(queue);
        processor.start();

        System.out.println("=== Message Queue ===");
        queue.enqueue("order-1: laptop");
        queue.enqueue("order-2: mouse");
        queue.enqueue("order-3: keyboard");

        Thread.sleep(1000);
        System.out.println("\nQueue size: " + queue.size());
        processor.shutdown();
    }
}
