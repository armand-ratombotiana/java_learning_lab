package com.learning.lab.module22.solution;

import java.util.*;

public class Solution {

    // Connection
    public interface Connection {
        Channel createChannel();
        void close();
    }

    public static class ConnectionFactory {
        private String host = "localhost";
        private int port = 5672;
        private String username = "guest";
        private String password = "guest";
        private String virtualHost = "/";

        public void setHost(String host) { this.host = host; }
        public void setPort(int port) { this.port = port; }
        public void setUsername(String username) { this.username = username; }
        public void setPassword(String password) { this.password = password; }
        public void setVirtualHost(String vhost) { this.virtualHost = vhost; }

        public Connection newConnection() {
            return new RabbitConnection(host, port, username, password, virtualHost);
        }
    }

    public static class RabbitConnection implements Connection {
        private final String host;
        private final int port;
        private boolean open = true;

        public RabbitConnection(String host, int port, String username, String password, String vhost) {
            this.host = host;
            this.port = port;
            System.out.println("Connected to RabbitMQ at " + host + ":" + port);
        }

        @Override
        public Channel createChannel() {
            return new RabbitChannel();
        }

        @Override
        public void close() {
            open = false;
            System.out.println("Connection closed");
        }

        public boolean isOpen() { return open; }
    }

    // Channel
    public interface Channel {
        void queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> args);
        void exchangeDeclare(String exchange, String type, boolean durable);
        void queueBind(String queue, String exchange, String routingKey);
        void basicPublish(String exchange, String routingKey, byte[] body);
        void basicConsume(String queue, DeliverCallback callback);
        void close();
    }

    public interface DeliverCallback {
        void handle(String consumerTag, Message message);
    }

    public static class RabbitChannel implements Channel {
        private final Map<String, Queue> queues = new HashMap<>();
        private final Map<String, Exchange> exchanges = new HashMap<>();

        @Override
        public void queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> args) {
            Queue q = new Queue(queue, durable, exclusive, autoDelete, args);
            queues.put(queue, q);
            System.out.println("Queue declared: " + queue);
        }

        @Override
        public void exchangeDeclare(String exchange, String type, boolean durable) {
            Exchange ex = new Exchange(exchange, type, durable);
            exchanges.put(exchange, ex);
            System.out.println("Exchange declared: " + exchange + " (" + type + ")");
        }

        @Override
        public void queueBind(String queue, String exchange, String routingKey) {
            Queue q = queues.get(queue);
            if (q != null) {
                q.bind(exchange, routingKey);
            }
            System.out.println("Bound " + queue + " to " + exchange + " with key: " + routingKey);
        }

        @Override
        public void basicPublish(String exchange, String routingKey, byte[] body) {
            System.out.println("Publishing to " + exchange + "/" + routingKey + ": " + new String(body));
        }

        @Override
        public void basicConsume(String queue, DeliverCallback callback) {
            System.out.println("Starting consumer on: " + queue);
        }

        @Override
        public void close() {
            System.out.println("Channel closed");
        }
    }

    // Queue
    public static class Queue {
        private final String name;
        private final boolean durable;
        private final boolean exclusive;
        private final boolean autoDelete;
        private final Map<String, Object> arguments;
        private final List<Binding> bindings = new ArrayList<>();

        public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> args) {
            this.name = name;
            this.durable = durable;
            this.exclusive = exclusive;
            this.autoDelete = autoDelete;
            this.arguments = args;
        }

        public String getName() { return name; }
        public boolean isDurable() { return durable; }

        public void bind(String exchange, String routingKey) {
            bindings.add(new Binding(exchange, routingKey));
        }

        public List<Binding> getBindings() { return bindings; }
    }

    // Exchange
    public static class Exchange {
        private final String name;
        private final String type;
        private final boolean durable;

        public Exchange(String name, String type, boolean durable) {
            this.name = name;
            this.type = type;
            this.durable = durable;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public boolean isDurable() { return durable; }
    }

    // Binding
    public static class Binding {
        private final String exchange;
        private final String routingKey;

        public Binding(String exchange, String routingKey) {
            this.exchange = exchange;
            this.routingKey = routingKey;
        }

        public String getExchange() { return exchange; }
        public String getRoutingKey() { return routingKey; }
    }

    // Message
    public static class Message {
        private final byte[] body;
        private final Map<String, Object> properties;

        public Message(byte[] body, Map<String, Object> properties) {
            this.body = body;
            this.properties = properties;
        }

        public byte[] getBody() { return body; }
        public Map<String, Object> getProperties() { return properties; }

        public static Message withBody(byte[] body) {
            return new Message(body, new HashMap<>());
        }

        public static Message withProperties(byte[] body, Map<String, Object> props) {
            return new Message(body, props);
        }
    }

    // Dead Letter Queue (DLQ)
    public static class DeadLetterQueue {
        private final String queueName;
        private final String exchangeName;
        private final String routingKey;

        public DeadLetterQueue(String queueName, String exchangeName, String routingKey) {
            this.queueName = queueName;
            this.exchangeName = exchangeName;
            this.routingKey = routingKey;
        }

        public String getQueueName() { return queueName; }
        public String getExchangeName() { return exchangeName; }
        public String getRoutingKey() { return routingKey; }

        public static DeadLetterQueue create(String baseQueue) {
            return new DeadLetterQueue(baseQueue + ".dlq", baseQueue + ".dlx", baseQueue);
        }
    }

    // Message Properties
    public static class MessageProperties {
        public static final String CONTENT_TYPE = "content_type";
        public static final String DELIVERY_MODE = "delivery_mode";
        public static final String PRIORITY = "priority";
        public static final String CORRELATION_ID = "correlation_id";
        public static final String REPLY_TO = "reply_to";
        public static final String EXPIRATION = "expiration";
        public static final String MESSAGE_ID = "message_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String TYPE = "type";
        public static final String USER_ID = "user_id";
    }

    // Exchange Types
    public static class ExchangeType {
        public static final String DIRECT = "direct";
        public static final String FANOUT = "fanout";
        public static final String TOPIC = "topic";
        public static final String HEADERS = "headers";
    }

    // Consumer
    public static class Consumer {
        private final String queueName;
        private final boolean autoAck;
        private DeliverCallback callback;

        public Consumer(String queueName, boolean autoAck) {
            this.queueName = queueName;
            this.autoAck = autoAck;
        }

        public void setDeliverCallback(DeliverCallback callback) {
            this.callback = callback;
        }

        public void start(Channel channel) {
            channel.basicConsume(queueName, callback);
        }
    }

    // Producer
    public static class Producer {
        private final Channel channel;
        private final String defaultExchange;

        public Producer(Channel channel, String defaultExchange) {
            this.channel = channel;
            this.defaultExchange = defaultExchange;
        }

        public void send(String queue, String message) {
            channel.basicPublish(defaultExchange, queue, message.getBytes());
        }

        public void send(String exchange, String routingKey, String message) {
            channel.basicPublish(exchange, routingKey, message.getBytes());
        }

        public void sendWithProperties(String queue, String message, Map<String, Object> props) {
            channel.basicPublish("", queue, MessageProperties.withProperties(message.getBytes(), props).getBody());
        }
    }

    // Retry Logic
    public static class RetryPolicy {
        private int maxAttempts = 3;
        private long initialInterval = 1000;
        private double multiplier = 2.0;
        private long maxInterval = 10000;

        public void setMaxAttempts(int max) { this.maxAttempts = max; }
        public void setInitialInterval(long ms) { this.initialInterval = ms; }
        public void setMultiplier(double mult) { this.multiplier = mult; }
        public void setMaxInterval(long ms) { this.maxInterval = ms; }

        public int getMaxAttempts() { return maxAttempts; }
        public long getInitialInterval() { return initialInterval; }
        public double getMultiplier() { return multiplier; }
        public long getMaxInterval() { return maxInterval; }

        public long getNextInterval(int attempt) {
            long interval = (long) (initialInterval * Math.pow(multiplier, attempt - 1));
            return Math.min(interval, maxInterval);
        }
    }

    public static void demonstrateRabbitMQ() {
        System.out.println("=== Connection Factory ===");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();

        System.out.println("\n=== Channel & Exchange ===");
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("orders", ExchangeType.DIRECT, true);
        channel.exchangeDeclare("notifications", ExchangeType.TOPIC, false);

        System.out.println("\n=== Queues ===");
        channel.queueDeclare("order-queue", true, false, false, null);
        channel.queueDeclare("notification-queue", true, false, false, null);
        channel.queueDeclare("order-dlq", true, false, false, null);

        System.out.println("\n=== Bindings ===");
        channel.queueBind("order-queue", "orders", "order.created");
        channel.queueBind("notification-queue", "notifications", "order.#");

        System.out.println("\n=== Dead Letter Queue ===");
        DeadLetterQueue dlq = DeadLetterQueue.create("order-queue");
        System.out.println("DLQ: " + dlq.getQueueName());
        System.out.println("DLX: " + dlq.getExchangeName());

        System.out.println("\n=== Producer ===");
        Producer producer = new Producer(channel, "");
        producer.send("order-queue", "Order created: 12345");
        producer.send("orders", "order.created", "New order placed");

        System.out.println("\n=== Consumer ===");
        Consumer consumer = new Consumer("order-queue", false);
        consumer.setDeliverCallback((tag, msg) -> {
            System.out.println("Received: " + new String(msg.getBody()));
        });
        consumer.start(channel);

        System.out.println("\n=== Retry Policy ===");
        RetryPolicy retry = new RetryPolicy();
        retry.setMaxAttempts(3);
        retry.setInitialInterval(1000);
        System.out.println("Retry attempt 1 interval: " + retry.getNextInterval(1) + "ms");
        System.out.println("Retry attempt 2 interval: " + retry.getNextInterval(2) + "ms");

        channel.close();
        connection.close();
    }

    public static void main(String[] args) {
        demonstrateRabbitMQ();
    }
}