package com.learning.lab.module22;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Lab {
    private static final String QUEUE_NAME = "learning-queue";
    private static final String EXCHANGE_NAME = "learning-exchange";
    private static final String ROUTING_KEY = "learning.routing.key";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 22: RabbitMQ Messaging ===");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        producerDemo(factory);
        consumerDemo(factory);
        exchangeDemo(factory);
    }

    static void producerDemo(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- RabbitMQ Producer ---");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        String message = "Hello RabbitMQ!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

        System.out.println("Sent: " + message);
        channel.close();
        connection.close();
    }

    static void consumerDemo(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- RabbitMQ Consumer ---");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = new String(body);
                System.out.println("Received: " + message);
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    static void exchangeDemo(ConnectionFactory factory) throws IOException, TimeoutException {
        System.out.println("\n--- RabbitMQ Exchange Demo ---");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        channel.queueDeclare("queue1", true, false, false, null);
        channel.queueBind("queue1", EXCHANGE_NAME, ROUTING_KEY);

        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, "Message via exchange".getBytes());
        System.out.println("Published to exchange: " + EXCHANGE_NAME);

        channel.close();
        connection.close();
    }
}