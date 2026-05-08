package com.learning.vertx.eventbus;

import java.util.concurrent.*;
import java.util.*;
import java.util.function.Consumer;

public class VertxEventBusLab {

    static class Message<T> {
        final String address;
        final T body;
        final String replyAddress;

        Message(String address, T body) {
            this.address = address;
            this.body = body;
            this.replyAddress = null;
        }

        Message(String address, T body, String replyAddress) {
            this.address = address;
            this.body = body;
            this.replyAddress = replyAddress;
        }
    }

    interface MessageConsumer<T> {
        void handle(Message<T> message);
    }

    static class EventBus {
        private final Map<String, List<MessageConsumer<?>>> consumers = new ConcurrentHashMap<>();
        private final Map<String, List<MessageConsumer<?>>> replyConsumers = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final AtomicInteger requestCounter = new AtomicInteger(0);

        public <T> void consumer(String address, MessageConsumer<T> handler) {
            consumers.computeIfAbsent(address, k -> new CopyOnWriteArrayList<>()).add(handler);
        }

        @SuppressWarnings("unchecked")
        public <T> void publish(String address, T body) {
            List<MessageConsumer<?>> handlers = consumers.get(address);
            if (handlers != null) {
                Message<T> msg = new Message<>(address, body);
                for (MessageConsumer<?> h : handlers) {
                    executor.submit(() -> ((MessageConsumer<T>) h).handle(msg));
                }
            }
        }

        public <T> void send(String address, T body) {
            List<MessageConsumer<?>> handlers = consumers.get(address);
            if (handlers != null && !handlers.isEmpty()) {
                MessageConsumer<T> handler = (MessageConsumer<T>) handlers.get(0);
                executor.submit(() -> handler.handle(new Message<>(address, body)));
            }
        }

        @SuppressWarnings("unchecked")
        public <T, R> void request(String address, T body, Consumer<R> replyHandler) {
            String replyAddr = "reply-" + requestCounter.incrementAndGet();
            replyConsumers.computeIfAbsent(replyAddr, k -> new CopyOnWriteArrayList<>())
                .add((MessageConsumer<R>) (MessageConsumer<R>) msg -> replyHandler.accept(msg.body));

            List<MessageConsumer<?>> handlers = consumers.get(address);
            if (handlers != null && !handlers.isEmpty()) {
                MessageConsumer<T> handler = (MessageConsumer<T>) handlers.get(0);
                executor.submit(() -> {
                    Message<T> msg = new Message<>(address, body, replyAddr);
                    handler.handle(msg);
                });
            }
        }

        public void reply(String replyAddress, Object response) {
            List<MessageConsumer<?>> handlers = replyConsumers.get(replyAddress);
            if (handlers != null && !handlers.isEmpty()) {
                MessageConsumer<Object> handler = (MessageConsumer<Object>) handlers.get(0);
                executor.submit(() -> handler.handle(new Message<>(replyAddress, response)));
            }
        }

        public void shutdown() {
            executor.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Vert.x Event Bus Concepts Lab ===\n");

        pointToPointDemo();
        publishSubscribeDemo();
        requestReplyDemo();
        clusteredBusDemo();
    }

    static void pointToPointDemo() throws Exception {
        System.out.println("--- Point-to-Point (send) ---");
        EventBus bus = new EventBus();
        bus.consumer("order.created", (Message<String> msg) -> {
            System.out.println("  Worker-1 received: " + msg.body);
        });
        bus.consumer("order.created", (Message<String> msg) -> {
            System.out.println("  Worker-2 received: " + msg.body);
        });

        bus.send("order.created", "Order-1001");
        bus.send("order.created", "Order-1002");
        Thread.sleep(200);
        System.out.println("  (Only one consumer receives each message in point-to-point)");
    }

    static void publishSubscribeDemo() throws Exception {
        System.out.println("\n--- Publish/Subscribe ---");
        EventBus bus = new EventBus();
        bus.consumer("notifications", (Message<String> msg) -> {
            System.out.println("  Email service: " + msg.body);
        });
        bus.consumer("notifications", (Message<String> msg) -> {
            System.out.println("  SMS service: " + msg.body);
        });
        bus.consumer("notifications", (Message<String> msg) -> {
            System.out.println("  Push service: " + msg.body);
        });

        bus.publish("notifications", "User-42 logged in");
        Thread.sleep(200);
        System.out.println("  (All subscribers received the message)");
    }

    static void requestReplyDemo() throws Exception {
        System.out.println("\n--- Request/Reply ---");
        EventBus bus = new EventBus();
        bus.consumer("user.service", (Message<String> msg) -> {
            String response = "User data for " + msg.body + ": {name:'Alice', role:'admin'}";
            if (msg.replyAddress != null) {
                bus.reply(msg.replyAddress, response);
            }
        });

        bus.request("user.service", "user-42", (String response) -> {
            System.out.println("  Reply received: " + response);
        });
        Thread.sleep(200);
    }

    static void clusteredBusDemo() throws Exception {
        System.out.println("\n--- Cluster Event Bus Concept ---");
        EventBus node1 = new EventBus();
        EventBus node2 = new EventBus();

        node1.consumer("cluster.msg", (Message<String> msg) -> {
            System.out.println("  Node-1 consumed: " + msg.body);
        });
        node2.consumer("cluster.msg", (Message<String> msg) -> {
            System.out.println("  Node-2 consumed: " + msg.body);
        });

        node1.publish("cluster.msg", "Hello from Node-1");
        node2.publish("cluster.msg", "Hello from Node-2");
        Thread.sleep(200);
        System.out.println("  (In a real cluster, messages cross nodes via TCP)");

        node1.shutdown();
        node2.shutdown();
    }
}
