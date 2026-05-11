package com.learning.lab.module22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    void testMessageCreation() {
        Message msg = new Message("body");
        assertNotNull(msg);
    }

    @Test
    void testMessageContent() {
        Message msg = new Message("Hello World");
        assertEquals("Hello World", msg.getContent());
    }

    @Test
    void testMessagePriority() {
        Message msg = new Message("body", 5);
        assertEquals(5, msg.getPriority());
    }

    @Test
    void testMessageDeliveryMode() {
        Message msg = new Message("body");
        assertEquals(2, msg.getDeliveryMode());
    }

    @Test
    void testQueueDeclaration() {
        Queue queue = new Queue("orders-queue");
        assertEquals("orders-queue", queue.getName());
    }

    @Test
    void testQueueDurable() {
        Queue queue = new Queue("orders-queue", true);
        assertTrue(queue.isDurable());
    }

    @Test
    void testExchangeCreation() {
        Exchange exchange = new Exchange("orders-exchange", "topic");
        assertEquals("orders-exchange", exchange.getName());
    }

    @Test
    void testExchangeType() {
        Exchange exchange = new Exchange("orders-exchange", "direct");
        assertEquals("direct", exchange.getType());
    }

    @Test
    void testBinding() {
        Binding binding = new Binding("queue", "exchange", "routing-key");
        assertEquals("routing-key", binding.getRoutingKey());
    }

    @Test
    void testConsumerMessageCount() {
        Consumer consumer = new Consumer();
        consumer.receiveMessage(new Message("test"));
        assertEquals(1, consumer.getMessageCount());
    }

    @Test
    void testPublisherSend() {
        Publisher publisher = new Publisher();
        assertTrue(publisher.publish(new Message("test")));
    }

    @Test
    void testMessageCorrelationId() {
        Message msg = new Message("body");
        msg.setCorrelationId("corr-123");
        assertEquals("corr-123", msg.getCorrelationId());
    }
}

class Message {
    private String content;
    private int priority = 0;
    private int deliveryMode = 2;
    private String correlationId;

    public Message(String content) {
        this.content = content;
    }

    public Message(String content, int priority) {
        this.content = content;
        this.priority = priority;
    }

    public String getContent() {
        return content;
    }

    public int getPriority() {
        return priority;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String id) {
        this.correlationId = id;
    }
}

class Queue {
    private String name;
    private boolean durable;

    public Queue(String name) {
        this(name, false);
    }

    public Queue(String name, boolean durable) {
        this.name = name;
        this.durable = durable;
    }

    public String getName() {
        return name;
    }

    public boolean isDurable() {
        return durable;
    }
}

class Exchange {
    private String name;
    private String type;

    public Exchange(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

class Binding {
    private String queue;
    private String exchange;
    private String routingKey;

    public Binding(String queue, String exchange, String routingKey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}

class Consumer {
    private int messageCount = 0;

    public void receiveMessage(Message message) {
        messageCount++;
    }

    public int getMessageCount() {
        return messageCount;
    }
}

class Publisher {
    public boolean publish(Message message) {
        return true;
    }
}