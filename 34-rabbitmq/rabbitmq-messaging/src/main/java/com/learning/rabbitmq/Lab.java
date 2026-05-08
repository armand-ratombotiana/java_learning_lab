package com.learning.rabbitmq;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {
    interface Exchange {
        void publish(String routingKey, String message);
        void bindQueue(String queue, String bindingKey);
    }

    static class DirectExchange implements Exchange {
        Map<String, List<BlockingQueue<String>>> bindings = new ConcurrentHashMap<>();
        Map<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();

        BlockingQueue<String> getOrCreateQueue(String name) {
            return queues.computeIfAbsent(name, k -> new LinkedBlockingQueue<>());
        }

        public void bindQueue(String queue, String bindingKey) {
            bindings.computeIfAbsent(bindingKey, k -> new CopyOnWriteArrayList<>()).add(getOrCreateQueue(queue));
        }

        public void publish(String routingKey, String message) {
            var qs = bindings.get(routingKey);
            if (qs != null) qs.forEach(q -> q.offer(message));
        }

        String consume(String queue, long timeoutMs) throws Exception {
            return getOrCreateQueue(queue).poll(timeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    static class FanoutExchange implements Exchange {
        List<BlockingQueue<String>> allQueues = new CopyOnWriteArrayList<>();
        Map<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();

        BlockingQueue<String> getOrCreateQueue(String name) {
            return queues.computeIfAbsent(name, k -> {
                var q = new LinkedBlockingQueue<String>();
                allQueues.add(q);
                return q;
            });
        }

        public void bindQueue(String queue, String bindingKey) {
            getOrCreateQueue(queue);
        }

        public void publish(String routingKey, String message) {
            allQueues.forEach(q -> q.offer(message));
        }

        String consume(String queue, long timeoutMs) throws Exception {
            return getOrCreateQueue(queue).poll(timeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    static class TopicExchange implements Exchange {
        Map<String, List<BlockingQueue<String>>> bindings = new ConcurrentHashMap<>();
        Map<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();

        BlockingQueue<String> getOrCreateQueue(String name) {
            return queues.computeIfAbsent(name, k -> new LinkedBlockingQueue<>());
        }

        public void bindQueue(String queue, String bindingKey) {
            bindings.computeIfAbsent(bindingKey, k -> new CopyOnWriteArrayList<>()).add(getOrCreateQueue(queue));
        }

        public void publish(String routingKey, String message) {
            for (var e : bindings.entrySet()) {
                if (matchTopic(e.getKey(), routingKey))
                    e.getValue().forEach(q -> q.offer(message));
            }
        }

        boolean matchTopic(String pattern, String key) {
            String[] pParts = pattern.split("\\.");
            String[] kParts = key.split("\\.");
            int pi = 0, ki = 0;
            while (pi < pParts.length && ki < kParts.length) {
                if (pParts[pi].equals("#")) return true;
                if (!pParts[pi].equals("*") && !pParts[pi].equals(kParts[ki])) return false;
                pi++; ki++;
            }
            return pi == pParts.length && ki == kParts.length;
        }

        String consume(String queue, long timeoutMs) throws Exception {
            return getOrCreateQueue(queue).poll(timeoutMs, TimeUnit.MILLISECONDS);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== RabbitMQ Concepts Lab ===\n");

        System.out.println("1. Direct Exchange:");
        DirectExchange direct = new DirectExchange();
        direct.bindQueue("order_errors", "order.error");
        direct.bindQueue("order_info", "order.info");
        direct.bindQueue("payment_errors", "payment.error");

        direct.publish("order.error", "Order #123 failed");
        direct.publish("order.info", "Order #456 created");
        direct.publish("payment.error", "Payment timeout");

        System.out.println("   Routing key 'order.error': " + direct.consume("order_errors", 100));
        System.out.println("   Routing key 'order.info': " + direct.consume("order_info", 100));
        System.out.println("   Routing key 'payment.error': " + direct.consume("payment_errors", 100));

        System.out.println("\n2. Fanout Exchange (broadcast):");
        FanoutExchange fanout = new FanoutExchange();
        fanout.bindQueue("queue_A", "");
        fanout.bindQueue("queue_B", "");
        fanout.bindQueue("queue_C", "");

        fanout.publish("ignored_key", "Broadcast message to all queues");
        System.out.println("   Queue A: " + fanout.consume("queue_A", 100));
        System.out.println("   Queue B: " + fanout.consume("queue_B", 100));
        System.out.println("   Queue C: " + fanout.consume("queue_C", 100));

        System.out.println("\n3. Topic Exchange:");
        TopicExchange topic = new TopicExchange();
        topic.bindQueue("usa_orders", "usa.#");
        topic.bindQueue("europe_orders", "europe.#");
        topic.bindQueue("all_errors", "#.error");

        topic.publish("usa.order.created", "USA order created");
        topic.publish("europe.order.shipped", "Europe order shipped");
        topic.publish("usa.payment.error", "USA payment error");

        System.out.println("   USA orders queue: " + topic.consume("usa_orders", 100));
        System.out.println("   Europe orders queue: " + topic.consume("europe_orders", 100));
        System.out.println("   All errors queue: " + topic.consume("all_errors", 100));

        System.out.println("\n4. Message acknowledgment (ACK/NACK):");
        System.out.println("   Consumer receives message -> processes -> sends ACK");
        System.out.println("   If processing fails -> sends NACK -> message requeued or DLQ");

        System.out.println("\n5. Dead Letter Queue (DLQ):");
        System.out.println("   Failed messages -> DLX exchange -> DLQ for later inspection");
        System.out.println("   Configured via x-dead-letter-exchange arguments");

        System.out.println("\n6. Queue durability & persistence:");
        System.out.println("   durable=true: queue survives broker restart");
        System.out.println("   delivery_mode=2: message persisted to disk");

        System.out.println("\n7. Publisher Confirms:");
        System.out.println("   Publisher sends -> broker confirms receipt");
        System.out.println("   Guarantees at-least-once delivery semantics");

        System.out.println("\n8. Consumer Prefetch (QoS):");
        System.out.println("   basicQos(10): consumer prefetches 10 messages at a time");
        System.out.println("   Prevents consumer overload");

        System.out.println("\n=== Lab Complete ===");
    }
}
