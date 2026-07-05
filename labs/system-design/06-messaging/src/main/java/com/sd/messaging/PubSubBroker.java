package com.sd.messaging;

import java.util.*;
import java.util.concurrent.*;

public class PubSubBroker {

    public interface MessageHandler {
        void onMessage(String topic, String message);
    }

    public static class Broker {
        private final Map<String, List<MessageHandler>> subscriptions = new ConcurrentHashMap<>();
        private final Map<String, List<String>> topicHistory = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newCachedThreadPool();

        public void subscribe(String topic, MessageHandler handler) {
            subscriptions.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
            System.out.println("New subscriber to '" + topic + "'");
        }

        public void unsubscribe(String topic, MessageHandler handler) {
            List<MessageHandler> handlers = subscriptions.get(topic);
            if (handlers != null) handlers.remove(handler);
        }

        public void publish(String topic, String message) {
            topicHistory.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(message);
            List<MessageHandler> handlers = subscriptions.get(topic);
            System.out.println("Published to '" + topic + "': " + message);
            if (handlers != null) {
                for (MessageHandler h : handlers) {
                    executor.submit(() -> h.onMessage(topic, message));
                }
            }
        }

        public List<String> getHistory(String topic) {
            return topicHistory.getOrDefault(topic, List.of());
        }

        public void shutdown() { executor.shutdown(); }
    }

    public static class LoggingHandler implements MessageHandler {
        private final String name;

        public LoggingHandler(String name) { this.name = name; }

        @Override
        public void onMessage(String topic, String message) {
            System.out.println("  [" + name + "] << " + topic + ": " + message);
        }
    }

    public static void main(String[] args) throws Exception {
        Broker broker = new Broker();

        LoggingHandler orderHandler = new LoggingHandler("order-svc");
        LoggingHandler emailHandler = new LoggingHandler("email-svc");
        LoggingHandler analyticsHandler = new LoggingHandler("analytics-svc");

        broker.subscribe("orders", orderHandler);
        broker.subscribe("orders", analyticsHandler);
        broker.subscribe("emails", emailHandler);

        System.out.println("=== Pub/Sub Broker ===");
        broker.publish("orders", "New order #5678");
        broker.publish("emails", "Welcome email sent");

        Thread.sleep(200);

        System.out.println("\nTopic history:");
        System.out.println("  orders: " + broker.getHistory("orders"));
        System.out.println("  emails: " + broker.getHistory("emails"));

        broker.shutdown();
    }
}
