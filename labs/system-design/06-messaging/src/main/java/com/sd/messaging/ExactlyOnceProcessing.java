package com.sd.messaging;

import java.util.*;
import java.util.concurrent.*;

public class ExactlyOnceProcessing {

    public static class ExactlyOnceConsumer {
        private final String consumerId;
        private final Set<String> processedIds = ConcurrentHashMap.newKeySet();
        private final Map<String, String> results = new ConcurrentHashMap<>();

        public ExactlyOnceConsumer(String consumerId) {
            this.consumerId = consumerId;
        }

        public boolean process(String messageId, String payload) {
            if (processedIds.contains(messageId)) {
                System.out.println("[" + consumerId + "] DUPLICATE detected: " + messageId + " - skipping");
                return true;
            }

            processedIds.add(messageId);
            results.put(messageId, payload);
            System.out.println("[" + consumerId + "] Processed: " + messageId + " -> " + payload);
            return true;
        }

        public boolean isProcessed(String messageId) {
            return processedIds.contains(messageId);
        }

        public int getProcessedCount() { return processedIds.size(); }
    }

    public static class ExactlyOnceProducer {
        private final Set<String> sentIds = ConcurrentHashMap.newKeySet();

        public String produce(String topic, String payload) {
            String id = topic + "-" + UUID.randomUUID();
            sentIds.add(id);
            System.out.println("Produced: " + id + " -> " + payload);
            return id;
        }
    }

    public static class TransactionalOutbox {
        private final Map<String, String> outbox = new LinkedHashMap<>();
        private final Set<String> published = ConcurrentHashMap.newKeySet();

        public synchronized String add(String topic, String payload) {
            String id = UUID.randomUUID().toString();
            outbox.put(id, topic + ":" + payload);
            System.out.println("Outbox: added " + id);
            return id;
        }

        public synchronized void markPublished(String id) {
            published.add(id);
            outbox.remove(id);
        }

        public synchronized List<Map.Entry<String, String>> getUnpublished() {
            List<Map.Entry<String, String>> unpublished = new ArrayList<>();
            for (Map.Entry<String, String> e : outbox.entrySet()) {
                if (!published.contains(e.getKey())) {
                    unpublished.add(e);
                }
            }
            return unpublished;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exactly-Once Processing ===");
        ExactlyOnceConsumer consumer = new ExactlyOnceConsumer("worker-1");

        consumer.process("msg-1", "order-123");
        consumer.process("msg-2", "order-456");
        consumer.process("msg-1", "order-123");
        consumer.process("msg-3", "order-789");

        System.out.println("\nProcessed " + consumer.getProcessedCount() + " unique messages");
        System.out.println("Duplicate msg-1 processed: " + consumer.isProcessed("msg-1"));

        System.out.println("\n=== Transactional Outbox ===");
        TransactionalOutbox outbox = new TransactionalOutbox();
        String id1 = outbox.add("orders", "new-order");
        String id2 = outbox.add("emails", "send-email");
        outbox.markPublished(id1);

        System.out.println("Unpublished: " + outbox.getUnpublished().size());
    }
}
