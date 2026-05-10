package com.learning.lab.module34;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.HashMap;
import java.util.Map;

public class Lab {
    private static final String HOST = "localhost";
    private static final int PORT = 5672;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 34: RabbitMQ Messaging Patterns ===");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);

        workQueuePattern(factory);
        publishSubscribePattern(factory);
        routingPattern(factory);
        topicPattern(factory);
        rpcPattern(factory);
        delayedMessagePattern(factory);
    }

    static void workQueuePattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- Work Queue Pattern ---");
        System.out.println("Distribute tasks among multiple workers");
        System.out.println("Producer: Send tasks to 'task_queue'");
        System.out.println("Consumer: Multiple workers compete for messages");
        System.out.println("Use: fair dispatch (prefetch=1)");
    }

    static void publishSubscribePattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- Publish/Subscribe Pattern ---");
        System.out.println("Fanout exchange sends message to all queues");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("logs", "fanout");
        channel.queueDeclare("log_file", false, true, false, null);
        channel.queueBind("log_file", "logs", "");

        channel.basicPublish("logs", "", null, "Log message".getBytes());
        System.out.println("Published to fanout exchange");

        channel.close();
        connection.close();
    }

    static void routingPattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- Direct Routing Pattern ---");
        System.out.println("Route messages based on routing key");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("direct_logs", "direct");
        channel.queueDeclare("error_queue", false, true, false, null);
        channel.queueBind("error_queue", "direct_logs", "error");

        channel.basicPublish("direct_logs", "error", null, "Error message".getBytes());
        channel.basicPublish("direct_logs", "info", null, "Info message".getBytes());
        System.out.println("Published with routing keys");

        channel.close();
        connection.close();
    }

    static void topicPattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- Topic Pattern ---");
        System.out.println("Use wildcards: * (one word), # (zero or more words)");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("topic_logs", "topic");
        channel.queueDeclare("tech_queue", false, true, false, null);
        channel.queueBind("tech_queue", "topic_logs", "tech.*");
        channel.queueBind("tech_queue", "topic_logs", "dev.#");

        channel.basicPublish("topic_logs", "tech.java", null, "Tech Java message".getBytes());
        channel.basicPublish("topic_logs", "devops.ci", null, "DevOps CI message".getBytes());
        System.out.println("Published to topic exchange");

        channel.close();
        connection.close();
    }

    static void rpcPattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- RPC Pattern ---");
        System.out.println("Request-Reply pattern with correlation ID");
        System.out.println("Client sends message with reply_to queue");
        System.out.println("Server processes and sends response to reply_to queue");
    }

    static void delayedMessagePattern(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- Delayed Message Pattern ---");
        System.out.println("Use dead letter exchange (DLX) for delayed messages");
        System.out.println("Set x-dead-letter-exchange and x-message-ttl on queue");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "delayed_exchange");
        args.put("x-message-ttl", 5000);

        channel.queueDeclare("delayed_queue", false, false, false, args);
        channel.exchangeDeclare("delayed_exchange", "direct");
        System.out.println("Delayed message queue configured");

        channel.close();
        connection.close();
    }
}