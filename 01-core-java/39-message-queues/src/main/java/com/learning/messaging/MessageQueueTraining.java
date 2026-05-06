package com.learning.messaging;

public class MessageQueueTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 39: Message Queues ===");
        demonstrateMessageSystems();
    }

    private static void demonstrateMessageSystems() {
        System.out.println("\n--- Message Systems ---");
        System.out.println("Kafka: High-throughput, distributed, log-based");
        System.out.println("RabbitMQ: Flexible routing, AMQP, traditional MQ");
        System.out.println("ActiveMQ: Enterprise messaging, JMS");
        System.out.println("\nPatterns: Pub/Sub, Point-to-Point, Request-Reply");
    }
}